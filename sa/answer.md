# Clang Static Analyzer Report

PB15111610 张一卓

## 3.1

1. 简要说明`test.c`、`AST.svg`、`CFG.svg`和`ExplodedGraph.svg`之间的联系与区别

   AST 图描述的就是一棵抽象语法树，它从``CompoundStmt``开始，在``test.c``中，遇到``int i = 0; int j = 0;``生成两个对应的``DeclStmt``结点；遇到接下来的``for``循环则生成一个``ForStmt``结点，最后是``return 0 ``，生成``ReturnStmt``结点。之后就继续展开这四个结点，和之前实验中实现的非常类似，就不赘述了。

   CFG 图就是``Control Flow Graph``，它的原型就是书上的流图，由基本块组成，并且描述了基本块之间的逻辑转换关系。根据``test.c``，由``B7 ENTRY``作为入口基本块，``B6``是对 i , j 声明，赋初值的两条语句，``B5``是对for循环的判断语句，``B2, B3, B4``构成循环内的语句，包含一个 if 语句的判断和跳转，最后``B2, B1``是 return 语句和出口。

   ExplodedGraph 是扩展图。在 CFG 的基础上，把边作为 Basicblock，结点作为程序的状态，形成带状态的CFG。更进一步，如果把经不同的控制结构到达的相同结点作为不同的状态（Program Point 相同，State 不同），那么就得到扩展图 ExplodedGraph。

## 3.2

1. Checker 对于程序的分析主要在 AST 上还是在 CFG 上进行？

   首先，AST 和 CFG 是紧密相连的，CFG 的构建需要 AST，并且在源码中，CFGElement都是AST的结点基础上套上图的成员。所以回答这个问题很纠结，遍历 CFG 其实遍历的是 AST 的结点。而且基类的名字叫做``ASTConsumer``。

   Checker 提供了两种分析方法，一种是遍历 AST 进行 Syntax 报错，另一种是借助 CFG 进行路径敏感的分析。其中，路径敏感的分析是 Checker 的主要工作。由此来看，主要在 CFG 上进行。

2. Checker 在分析程序时需要记录程序状态，这些状态一般保存在哪里？

   这些状态一般保存在``ProgramState``类中，主要成员如下：

   ​	Environment: 源码表达式到符号值的映射。

   ​	Store：存储地址到符号值的映射。

   ​	GenericDataMap：符号值的限制（Constraints）。	

   另外还有部分信息在``ProgramPoint``中，比如关于一个程序状态何时/怎样加入等等。

3. 简要解释分析器在分析下面程序片段时的过程，在过程中产生了哪些symbolic values? 它们的关系是什么？

一段程序：

```c
int x = 3, y = 4;
int *p = &x;
int z = *(p + 1);
```

​	第一行：3和4都是``ConcreteInt``，不是 symbol；对x和y，分别新建两个 SVal，由于他们是左值，所以属于 ``MemRegion``，并且在分别把3，4的值绑定给它们。

​	第二行：&x 是对x地址的右值引用，因此会有一个``SymExpr``来代表它。p 是左值，因此是``MemRegion``，并且把代表了&x的``SymExpr``的值绑定给它。

​	第三行：首先是对p的右值引用，会新建一个SVal，把p的左值变成右值，因此是``SymExpr``；然后是1，它是一个``ConcreteInt``;接着计算``*(p+1)``，新建一个SVal，是``SymExpr``;最后是z的左值引用，新建一个``MemRegion``，并且把之前计算的``SymExpr``绑定给它。

## 3.3

1. LLVM 大量使用了 C++11/14的智能指针，请简要描述几种智能指针的特点、使用场合，如有疑问也可以记录在报告中。

   **unique_ptr:**

   unique_ptr 是一个独享所有权的智能指针，它提供了严格意义上的所有权，包括：

   1、拥有它指向的对象

   2、无法进行复制构造，无法进行复制赋值操作。即无法使两个unique_ptr指向同一个对象。但是可以进行移动构造和移动赋值操作

   3、保存指向某个对象的指针，当它本身被删除释放的时候，会使用给定的删除器释放它指向的对象

   unique_ptr 可以实现如下功能：

   1、为动态申请的内存提供异常安全

   2、讲动态申请的内存所有权传递给某函数

   3、从某个函数返回动态申请内存的所有权

   4、在容器中保存指针

   unique_ptr 的使用场合一定是程序不需要多个指针指向同一个对象的时候，因为它独享所有权。

   **share_ptr:**

   share_ptr 指向对象可以被多个指针共享，它使用计数机制来表明资源被几个指针共享。可以通过成员函数use_count()来查看资源的所有者个数。它可以通过new来构造，还可以通过传入auto_ptr, unique_ptr, weak_ptr来构造。当调用析构函数 release() 时，当前指针会释放资源所有权，计数减一。当计数等于0时，资源会被释放。

   使用场合是在程序需要多个指针指向同一对象的时候，比如：

   - 有一个指针数组，并使用一些辅助指针来标示特定的元素，如最大的元素和最小的元素；
   - 两个对象包含都指向第三个对象的指针；
   - STL容器包含指针。

   **weak_ptr:**

   weak_ptr 是用来解决 shared_ptr 相互引用时的死锁问题：如果说两个 shared_ptr 相互引用，那么这两个指针的引用计数永远不可能下降为0，资源永远不会释放。它是对对象的一种弱引用，不会增加对象的引用计数，和shared_ptr之间可以相互转化，shared_ptr 可以直接赋值给它，它可以通过调用 lock 函数来获得shared_ptr。

   weak_ptr 的使用场合上面已经有说：解决 shared_ptr 相互引用时的死锁问题。

   **疑问：**看到一种说法，不能用 weak_ptr 调用对象的方法，要先用 lock 函数转换成 shared_ptr，为什么？

2. LLVM 不使用 C++ 的运行时类型推断（RTTI），理由是什么？LLVM 提供了怎样的机制来代替它？

   不使用 C++ 的 RTTI 的理由：

   - 为了减少代码和可执行文件的大小
   - 认为 C++ 的 RTTI 违反了 C++ ” you only pay for what you use" 的原则，因为即使在代码中从不使用RTTI，也会使可执行文件大小膨胀。
   - ``dynamic_cast``仅适用于有``v-table``的类。

   替代机制：

   用例如``isa<>, cast<>, dyn_cast<>``的模板，手动实现了 RTTI，这样的 RTTI 可以加到任意类上。

3. 如果你想写一个函数，它的参数既可以是数组，也可以是std::vector，那么你可以声明该参数为什么类型？如果你希望同时接受 C 风格字符串和 std::string 呢？

   同时接受数组和 std::vector:

   使用 llvm::ArrayRef Class。

   同时接受 C 风格字符串和 std::string：

   使用 llvm::StringRef Class。

4. 你有时会在cpp文件中看到匿名命名空间的使用，这是出于什么考虑？

   匿名的 namespace 在编译时会被分配唯一的内部名称，并且它的名称作用域被限制在当前文件中（有点像static 的作用）。这样做一方面可以保护命名空间，防止被命名冲突，另一方面，由于该命名空间只在当前编译单位内可见，那么编译器就可以做更激进的优化。

## 3.4

1. 这个 checker 对于什么对象保存了哪些状态？保存在哪里？

   StreamState:

   ​	它的打开/关闭状态：``enum Kind { Opened, Closed } K;``

2. 状态在哪些时候会发生变化？

   有两种状态的变化：

   第一种是在 checkPostCall 和 chekcPreCall 中，会对打开/关闭 Stream 做出状态的改变：

   ```c++
   State = State->set<StreamMap>(FileDesc, StreamState::getOpened()); // getClosed either
   ```

   第二种是从当前 State 的 Map 中去掉 Stream 的 Symbol，语句都是一样的：

   ```c++
   State = State->remove<StreamMap>(Sym);
   ```

   出现在``VisitSymbol``（已经访问过过这个Symbol），``checkDeadSymbols``（去掉已经失效的Symbol），``checkPointerEscape``（Pointer Escaped，假设在别处关闭了Stream）。

3. 在哪些地方有对状态的检查？

   - 在 checkPreCall 中，

     ```c++
      const StreamState *SS = State->get<StreamMap>(FileDesc);
       if (SS && SS->isClosed()) {
         reportDoubleClose(FileDesc, Call, C);
         return;
       }
     ```

     如果发现当前Stream已经关闭但是还在State 的Map里面声明关闭，那么就报重复关闭的错误。

   - 在判断 isLeaked 的时候，有对Stream是否成功打开的检查

     ```c++
     if (IsSymDead && SS.isOpened()) {
         // If a symbol is NULL, assume that fopen failed on this path.
         // A symbol should only be considered leaked if it is non-null.
         ...
      }
     ```

4. 函数`SimpleStreamChecker::checkPointerEscape`的逻辑是怎样的？实现了什么功能？用在什么地方？

   逻辑：首先，

   ```c++
     if (Kind == PSK_DirectEscapeOnCall && guaranteedNotToCloseFile(*Call)) {
       return State;
     }
   ```

   如果当前的 call 已经被确定不会关闭文件，那么直接返回传进来的 Program State，不用做修改。

   接着，

   ```c++
     for (InvalidatedSymbols::const_iterator I = Escaped.begin(),
                                             E = Escaped.end();
                                             I != E; ++I) {
       SymbolRef Sym = *I;
       State = State->remove<StreamMap>(Sym);
     }
   ```

   对于 Escaped 里面的每一个 Symbol，作乐观的假设：它们对应的文件会被别处关闭。因此，从 State 里面移除 Symbol。

   实现功能：对文件变量 Escape 之后的处理。

   用在何处：当分析器遇到指针丢失的情况时调用这个函数。

5. 根据以上认识，你认为这个简单的checker能够识别出怎样的bug？又有哪些局限性？请给出测试程序及相关的说明。

   能够识别出文件流的重复关闭（DoubleClose）和文件不关闭造成泄露（Leaked）。

   局限性：

   1. 在Mac OS 上，文件重复关闭的报错不支持。在 Linux 上可以。

   2. 测试程序：

      ```c
      #include<stdio.h>
      void main(){
      	FILE *fp1 = fopen("test1.txt", "r");
      	fp1 = fopen("test2.txt", "r");
      	fclose(fp1);
      }
      ```

      在没有关闭``test1.txt``的情况下，直接覆盖文件指针，这个 Checker 的 warning 是：

      ```
      Value stored to 'fp1' during its initialization is never read
      ```

      这是因为这个 Checker 在遇到 Pointer Escaped 的情况下（比如文件指针被覆盖），会假设这个文件在别处被关闭，因此不报文件未关闭的警告（Leak）。但是，这样的做法（比如测试程序），是有内存泄漏的。

## 3.5

1. 增加一个checker需要增加哪些文件？需要对哪些文件进行修改？

   需要增加checker的实现文件，比如``SimpleChecker.cpp``，放到``clang/lib/StaticAnalyzer/Checkers/``目录下。需要修改``include/clang/StaticAnalyzer/Checkers/Checkers.td``，把新的checker声明到checkers表中；还要把源文件放到Cmake中：``lib/StaticAnalyzer/Checkers/CMakeLists.txt``。

2. 阅读[`clang/include/clang/StaticAnalyzer/Checkers/CMakeLists.txt`](https://github.com/llvm-mirror/clang/blob/release_40/include/clang/StaticAnalyzer/Checkers/CMakeLists.txt)，解释其中的 clang_tablegen 函数的作用。

   用 Checker.td 生成 TARGET ClangSACheckers 的 C++ 源文件。

3. `.td`文件在clang中出现多次，比如这里的`clang/include/clang/StaticAnalyzer/Checkers/Checkers.td`。这类文件的作用是什么？它是怎样生成C++头文件或源文件的？这个机制有什么好处？

   ``.td``文件包含了``TableGen``函数，它是一种解释性的语言，用于描述特定领域的信息记录。在 llvm 和 clang 中，用于生成 llvm/clang/C/C++ 源码。经过tablegen工具批量生成C++源文件，它的好处就是使得 llvm 移植更加容易，因为这个机制大量减少了必要的 C++ 编写的代码量。另外，改变代码也更容易，比如可以只更改一个地方（tblgen）就可以更改所有目标到一个新的接口。

