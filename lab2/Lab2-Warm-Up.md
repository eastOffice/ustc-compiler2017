# Lab2 Warm Up

PB15111610 张一卓

## 1.人工编写LLVM IR

将指定的代码人工翻译成 LLVM IR，可以参考 clang 的输出。

### 观察 clang 的输出

首先可以看到 IR 中对一个全局函数的定义：

```
define i32 @fib(i32) #0 {...}
```

中间的局部变量定义：

```
%2 = alloca i32, align 4
```

``%``开头定义的是局部变量，经过探索，定义变量的时候如果用数字命名，需要按照顺序来使用。在``main``中，即使``%1``没有使用，也定义并分配了空间，在``fib``函数中，``%1``没有出现。并且观察返回值，可以发现都是以``%2``来存放返回值，以``%0``作为参数的输入。

对于标号``label``，在 clang 的输出中都使用的是数字标号，比如``label %10``，每一块 basic block 就用两个``label``标识出来。在自己编写 IR 的时候，为了方便知道标号的含义，可以重新命名。

自动生成的 LLVM IR 有不少冗余的地方，比如：

```
  %3 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  %4 = load i32, i32* %3, align 4
```

可以直接使用``%0``，而不是分配，赋值再调用。

### 编写LLVM IR

自己编写 LLVM IR 的时候，参考了 clang 输出的 basic block 的框架，去除了一些冗余的语句，对``main``函数的标号进行了一点改动。

对于局部变量命名，使用了数字命名的方式。对于标号，为了方便知道重要标号的含义，进行了重新命名，比如：

```
return:
    %14 = load i32, i32* %2, align 4
    ret i32 %14
```

在``main``函数中，把``for loop``循环作为一个 basic block进行处理：

```
loop:                                     
  %4 = load i32, i32* %3, align 4
  %5 = icmp slt i32 %4, 10                      ; if i < 10
  br i1 %5, label %6, label %return

; <label>:6:                                      
  %7 = load i32, i32* %3, align 4
  %8 = call i32 @fib(i32 %7)
  %9 = load i32, i32* %2, align 4
  %10 = add nsw i32 %9, %8
  store i32 %10, i32* %2, align 4                                
  %11 = add nsw i32 %7, 1                      ; i = i+1
  store i32 %11, i32* %3, align 4
  br label %loop
```

源代码有注释，这个程序也十分简单，很好看懂。

### 运行结果

在命令行``cd``到``fib.ll``的目录下，然后：

```shell
lli fib.ll
echo $?
```

就可以看到输出结果为 88。与 clang 的输出一致，结果正确。

## 2.使用 IR Builder 构建 LLVM IR

编写一个 `gen_fib.cpp`，使用 IRBuilder 构建上一题中所编写的 LLVM IR 并打印。

### 实验过程

编写实验时主要参考了 LLVM Reference Manual, IRBuilder.h 和助教写的 example。

首先是全局函数 fib, main 的定义，根据助教写的样例，只需要稍加修改（函数类型和 fib 的参数）就可以了。

根据预热实验1的经验，每个basic block 由一个label 来标识。所以在使用 IR Builder 的时候，每次遇到一个标识就创建一个新的 bb， 并且``setInsertPoint``到当前的bb，开始插入每一条语句。

对于具体指令的插入，都是通过调用``builder.CreateXxx()``来实现，这些 Create 函数只需要在 IRBuilder.h 里面查看接口，如果没有看懂就到 Reference Manual 中寻找相应的 example，整个过程并不困难，遇到的问题在下面有讲。

### 实验结果

首先``cd``到``gen_fib.cpp``的目录下面，然后：

```shell
c++ gen_fib.cpp `llvm-config --cxxflags --ldflags --system-libs --libs` -o gen_fib
./gen_fib
```

就可以看到输出的 LLVM IR。这个程序的输出结果和上题中自己编写的 IR 完全一样（ 包括变量的命名，bb 的顺序）。

### 问题处理

1. 在遇到``icmp``指令的时候出现了立即数（比如0），当时就直接写了：

   ```c++
   builder.CreateICmpEQ(fib_i0, 0)
   ```

   结果报错，这个函数并没有重载一个能够接受``int``的接口，把``int``自动转换成``Value*``，所以所有的立即数要专门定义，比如：

   ```c++
   Value* const_int32_0 = ConstantInt::get(Type::getInt32Ty(context), 0);
   ```

2. 在对函数 fib 的参数处理的时候遇到了一点困难，我一开始是这样写的：

   ```c++
   std::map<std::string, Value*> FibArg;
   for (auto &Arg : func_fib->args())
   {   
          //Arg.setName("n");
          FibArg["n"] = &Arg;
    }
   ```

   没有``setName``是因为自己写的 IR 中使用了默认的参数名``%0``。但是在使用``FibArg["n"]``时却报错：

   ```shell
   Segmentation fault: 11
   ```

   我使用``printf``开始检查，本以为是``map``的使用不对，但是经过调试发现这一部分没有问题。后来发现我的``fib``函数的定义是这样的：

   ```c++
   auto func_fib = Function::Create(FunctionType::get(Type::getInt32Ty(context), 			   std::vector<Type *>(), false),
   GlobalValue::LinkageTypes::ExternalLinkage,
   "fib", module);
   ```

   传入参数显然不对，当初写定义太草率，没有注意每个参数的含义。把``std::vector<Type *>()``改成``Type::getInt32Ty(context)``，就没有问题了。