# Lab2 Report

PB15111610 张一卓

## 实验内容

在本实验中，将通过实现一个 syntax tree visitor，为 C1 语言构建 LLVM IR 代码生成器：

- `assembly_builder.cpp` 中的所有空白 `visit` 方法。
- 在 repository 中建立 `test` 文件夹，其中存放你的测试样例。请确保你的程序至少在你的测试样例下工作正常，并尽可能让你的测试样例覆盖面更广。

## 实验分析

###概述

经过了预热实验之后，我对 LLVM IR 及其生成代码的接口有了一定的了解。在本次实验中，就是要根据lab 1 生成的语法树，来构建自动生成 IR 的代码。实现 syntax tree visitor 的过程大致就是一边更新 builder 中的成员变量（比如维护函数表，变量符号表，全局和常量属性等等），另一边在合适的地方插入生成 LLVM IR 的代码：builder.CreateXxx。期间经历各种艰难险阻，问题和解决方案就在下面一个一个说。

### 1.函数和语句块

这是最简单的几个函数，在一开始就可以写好。首先是``visit(assembly &node)``，核心就是访问其中的每一条语句：

```c
for(auto def : node.global_defs)
{
    def->accept(*this);
}
```

我在预热实验的时候就读过``syntax_tree_builder.h``的代码，所以对``accept``的用法并不陌生，它是访问者模式中对每一个子类进行访问的函数，接收的参数是``syntax_tree_visitor &visitor)``，在这里就是``*this``。``block``的处理与之几乎一样，就不赘述了。

接下来是对于函数处理：``func_def & func_call``，这两个函数就要更新 builder 的成员变量：``funcdef``中要检查声明是否重复，并把新函数加到函数表，``func_call``中要检查函数是否已经声明等等。另外还要写生成 IR 的代码，在预热实验中已写过函数声明和调用的代码，并不困难。

### 2. 表达式，条件和赋值语句

首先说二元表达式，它最简单，只要理解了``value_result``和``const_result``就行。基本过程就是先后访问二元表达式的左部和右部，保存下返回值，根据操作符计算出结果，把它返回到正确的地方。根据上下文，在``constexpr_expected``为``true``的时候，对``node.lhs & node.rhs``访问后的结果就会保存在``const_result``中，反之，则保存在``value_result``。同理，保存的结果也是这样。只需要注意``value_result``是``Value *``类型。对前一种情形，代码例如这样：

```c++
if(node.op == binop::plus)
            value_result = builder.CreateNSWAdd(_lhs, _rhs);
```

对于后一种情形，代码例如这样：

```c++
if(node.op == binop::plus)
            const_result = _lhs + _rhs;
```

与之非常像的是``visit(cond_syntax &node)``，不同点在于，**条件语句的左部和右部都是变量**，因此返回值只能保存到``result_value``中，这一点在处理控制结构的时候需要注意。条件语句的代码生成例如这样：

```c++
if(node.op == relop::equal)
        value_result = builder.CreateICmpEQ(_lhs, _rhs);
```

生成函数的接口查一下就行。``literal_syntax``则不需任何处理，直接判断一下``const``然后返回到相应位置即可。

对于一元表达式，只需要处理操作符是``minus``即可，如果是常量那就把``const_result``变成相反数，如果是变量的情况我当时还卡了好一会，一直在找一种方法可以把``value_result``里面的值换成 int 取出来，后来才终于开窍，**做个减法**就可以了：

```c++
Value* const_int32_0 = ConstantInt::get(Type::getInt32Ty(context), 0);
value_result = builder.CreateNSWSub(const_int32_0, value_result);
```

赋值语句的核心就是先后访问``value & target``，然后``CreateStore``，但是，还不能忘记在访问前要设置正确的上下文：访问``target``前，``lval_as_rval = false``，因为是变量作为左值引用，访问``value``前，``lval_as_rval = true``，因为变量作为右值引用。另外和条件语句一样，左值右值都是变量。

### 3.控制结构语句

实验过程中，控制结构语句是最后写的（因为以为很难），但是这里先讲，因为复杂程度远远没有下面两个函数大。

#### if_stmt_syntax

首先要规划要用几个``Basicblock``，对于 if 语句，可以把条件``pred``放在当前的 bb 里，``then_body``肯定要一个 bb，对于有``else_body``的情况，``else_body``要一个bb，并且还要一个作为 if 语句结束的插入点；对于没有``else_body``的情况，那么只再要一个 bb 作为 if 结束的插入点即可。

以有 else 的情况为例，先访问``node.pred``，结果返回在``value_result``中。接下来生成条件跳转：

```c++
builder.CreateCondBr(if_cond, if_then, if_else);
```

在 if_then 的基本块下访问``then_body``：

```c++
builder.SetInsertPoint(if_then);
node.then_body->accept(*this);
builder.CreateBr(if_next);
```

**切记最后要跳转到 if 结束的地方！！**测试的时候补了好几个这样的错误。

else_body 与之类似。

#### while_stmt_syntax

整个过程与上面相似，主要是 bb 的设计不一样而已，需要三个基本快：``while_loop, while_true, while_next``，跳转的逻辑就直接给出代码：

```c++
	builder.CreateBr(while_loop); // !!!
    builder.SetInsertPoint(while_loop);
    node.pred->accept(*this);
    auto while_cond = value_result;
    builder.CreateCondBr(while_cond, while_true, while_next);
    builder.SetInsertPoint(while_true);
    node.body->accept(*this);
    builder.CreateBr(while_loop);
    builder.SetInsertPoint(while_next);
```

注释中带感叹号的那句话也是测试时发现漏掉的。调试 while 语句的时候总是段错误，以为是别的地方有问题，最后用``-emit-llvm``输出中间代码发现有一个 bb 结束后没有跳转，所以出现了段错误。

###4. lval_syntax

####实现过程

引用一个值得时候首先去符号表里面检查，是否声明，如果已经声明那么函数```look_up_variable``返回一个三元组，包含值，是否常量，是否数组的信息。然后，对是否是数组进行分类处理：

1. 如果不是数组（记得类型检查），那么对左值引用和右值引用进行分类：左值的话首先要判断如果``constexpr_expected``为真，那么报错，因为常量不能作为左值，然后直接把得到的值赋给``value_result``即可。如果是右值，那么直接把得到的值``CreateLoad``给``value_result``即可。

2. 如果是数组，（同样要类型检查），流程和上面一样，只不过多了一个取数组元素的过程。这个助教在习题课上讲了两个方法，我之前一开始是用简单那一种实现的，但是后来和 clang 比对以为自己错了，于是就用了clang的实现方式：创建一个 vector<Value *> index， 先 push 一个0，作为索引到数组头的元素，再访问并push ``array_index`` ，最后使用：

   ```c++
   Value * element = builder.CreateGEP(lval, index);
   ```

   得到的 element 就是数组的元素。

#### 遇到的问题

在完成实验后看来，lval 并没有那么复杂，但是调试的时候却遇到了很多的问题，反思之后觉得是当时混淆了``is_const``和``constexpr_expected``的含义，**前者表示的是 lval 本身是否是常量**，**而后者表示的是接下来你想得到的数据是不是应该是常量**，理解这一点非常关键，在 var_def 中也有这个问题。

比如``int i = 0; int k = i;``这条语句，之前我没有搞清以上含义，结果对 k 的赋值会报错`` const value cannot be lval``。原因就是理解问题导致的报错的逻辑不对。

### 5. var_def_stmt_syntax

#### 实现过程

首先根据 node 的成员设置好变量名，是否是数组两个参数。然后根据是否是全局变量分类处理：

1. 全局变量。全局变量的特点就是两个，一个是只能用常量初始化，所以``constexpr_expected``一定是真，另一个是初始化的时候直接在声明语句中完成。对于非数组，声明加初始化如下：

   ```c++
   var_ptr = new GlobalVariable(*module,
                               Type::getInt32Ty(module->getContext()),
                               node.is_constant,
                               GlobalValue::ExternalLinkage,
   				  ConstantInt::get(Type::getInt32Ty(module->getContext()),const_result),
                                var_name);
   ```

   其中const_result 是访问 initializers[0] 得到的结果，对于数组，接口都是一样的，只不过需要修改Type类型为``ArrayType::get(Type::getInt32Ty(context), temp_array_len),``（第二个参数），另外第5个参数是初始化，需要和 lval 一样建立一个 vector， 把 initializers 里面的元素依次访问并 push 进去即可。对于没有初始化的全局变量，都用0 来初始化。

2. 局部变量。局部变量和全局变量正好相反，初始化的值全是用变量处理，所以``const_expected``一定为假，另外初始化方式是先声明，再通过 Store 的方式初始化。只给出数组的声明：

   ```c++
   builder.CreateAlloca(ArrayType::get(Type::getInt32Ty(context), temp_array_len), nullptr, var_name);
   ```

   初始化的方式就是通过 Store 指令，没有什么难点。要注意初始化长度小于数组长度的时候，要在后面补0。值得一提的是要报 error 的地方有：声明数组长度小于0，数组初始化长度大于数组长度。另外对于声明长度为0的数组，报了warning处理。

最后不要忘记维护符号表，使用``declare_variable``函数，如果不成功就报 error。

#### 遇到的问题

```c++
const int i = 3;
int a[3] = {1, 2, i}; // 都是局部变量
```

对于这段程序，我一开始输出a[2] 的值是2， 而不是3。因为我一开始没有意识到，**局部变量的初始化都是按变量处理的** ，也就是说，返回值都在``value_result``中，而我之前写的时候，初始化都是按常量处理，所以a[2] 初始化的时候还到``const_result``里面找，结果是之前设置的2。

这个问题的背后依然是没有理解好``constexpr_expected``的含义，导致第一次写的时候没有正确设置它在上下文中的值。

### 5.其它问题

在测试error 的时候有一个程序：

```c
void main()
{
  i = 0;
}
```

它会在给出``using of undeclared variable``之后报段错误，我感到非常不解，于是用 printf 进行报错的跟踪，发现在进入 assign_stmt 之后就没有出来。于是我看了 assign_stmt 的代码：

```c++
node.target->accept(*this);
auto _target = value_result;
```

在报error之后就直接返回了，所以 value_result 就没有值进去。由于这个程序只有一条语句，所以 value_result 之前也没有被修改过，所以，这是由于 value_result 没有初值而导致的段错误。我在 assembly 中给它赋初值0，就解决了这个问题。

之后另外一个问题：

```c
void main()
{
  int a[3] = {0};
  int i = 0;
  while(i < 3)
  {
    a[i] = i;
    output_var = a[i];
    output();
    i = i+1;
  }
}
```

一开始会报一个 Abort 的错误，发现是对于 a[i] 作为左值的时候，对索引 i 的解析有问题。发现这个错误的时候报告已经写得差不多了，所以对 lval_as_rval 比较熟悉，想到是对 i 进行了左值引用，没有把 i 的值 Load 出来，所以只要加上如下代码：

```c++
        int temp_lval = lval_as_rval;
        lval_as_rval = true;
        node.array_index->accept(*this);
		lval_as_rval = temp_lval;
```

就是在处理数组的 index 之前保存下 lval_as_rval，用右值的方式访问 index，然后恢复即可。这个问题比较隐蔽，但是解决比较快。

## 实验编译与结果

编译：

```shell
cd build
make
```

测试文件位于 /test_cases/ 下：（文件内容比较长，就不在这粘贴了）

final_test.c1 : 测试大部分正确的语句，输出结果是1-28顺序打印。

fact.c1 ：测试递归函数调用。

test_error.c1 : 测试报错。

测试结果都是符合预期的，其它两个文件 test.c1 和 test2.c1 用来专门调试一些语句。



