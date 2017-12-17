# Analysis

PB15111610  张一卓

## 4.1

1. clang 还有的一些缺陷：

   - new 的当前表示并不能为分析器建模一个调用内存分配函数（operator new）的简单方法，然后用构造函数调用初始化结果。

   - delete 的表示不包括对析构函数的调用。对如下程序：

     ```c++
       #include <stdlib.h>

       class Test {
       public:
         Test() {}
         __attribute__((noreturn)) ~Test(){
           abort();
         }
       };

       __attribute__((noreturn)) void test () {
         abort();
       }

       void myfunc(int * t) {
         if (t == nullptr) {
           Test();  // causes null dereference warning below on '*t = 4'
           //test();  // works
         }
         *t = 4;
       }
     ```

     会有对 null dereference 的一个 false positive warning。

     这个程序在 clang —analyze 命令下已经不会报 warning 了，根据最新的 comment，应该是已经被解决了。

   - （未实现）DynamicTypePropagation checker 负责根据代码执行的操作来推断区域的动态类型。 Casts 是当前分析器忽略的丰富的类型信息资源。它们很难被正确的得到，但是会非常有用。

   - （未实现）alpha-remaining: 如果两个符号值经比较确定为一样的时候，沿执行路径同一它们。这可以减少 false positive，并为更高级的分析打下基础，比如 summary-based 过程间分析和交叉编译单元分析。

   - 对 execptions 的处理：目前异常被当做黑洞，异常处理控制结构的建模效果不好。

2. 这部分围绕  [StreamChecker.cpp](https://github.com/llvm-mirror/clang/blob/release_40/lib/StaticAnalyzer/Checkers/StreamChecker.cpp) 展开回答。

   - 在 Mac OS High Sierra 上，之前使用 alpha.unix.SimpleStream 没有报错的 double close 错误，用alpha.unix.Stream 就可以检查出来，用的例子是助教给的：

     ```shell
     warning: Try to close a file Descriptor already closed. Cause undefined behaviour
     ```

     对于之前提到的文件指针覆盖的问题，使用 alpha.unix.Stream 可以检查出内存泄漏：

     ```
     warning: Opened File never closed. Potential Resource leak
     ```

   - 简单说一下实现机制：为什么可以检查出 SimpleStreamChecker 检查不出的内存泄漏的问题。

     这在于 StreamChecker 中没有 CheckerEscapePoint 这个函数，也就没有了那个假设：当文件指针丢失以后，会乐观假设它在别处被关闭，总的来讲，就是丢了就不管了。但是在 StreamChecker 中就很严格，只有一个 CheckerDeadSimbols 函数：

     ```c++
     void StreamChecker::checkDeadSymbols(SymbolReaper &SymReaper,
                                          CheckerContext &C) const {
       // TODO: Clean up the state.
       for (SymbolReaper::dead_iterator I = SymReaper.dead_begin(),
              E = SymReaper.dead_end(); I != E; ++I) {
         SymbolRef Sym = *I;
         ProgramStateRef state = C.getState();
         const StreamState *SS = state->get<StreamMap>(Sym);
         if (!SS)
           continue;

         if (SS->isOpened()) {
           ExplodedNode *N = C.generateErrorNode();
           if (N) {
             if (!BT_ResourceLeak)
               BT_ResourceLeak.reset(new BuiltinBug(
                   this, "Resource Leak",
                   "Opened File never closed. Potential Resource leak."));
             C.emitReport(llvm::make_unique<BugReport>(
                 *BT_ResourceLeak, BT_ResourceLeak->getDescription(), N));
           }
         }
       }
     }
     ```

     可以看到，只要在``SymReaper``中（无论是变量作用域到了还是指针丢失），只要文件没关闭（!BT_ResourceLeak）就会报 warning。

     另外，StreamCheck 还增加了对 null stream 的检查：

     ```c++
     ProgramStateRef StreamChecker::CheckNullStream(SVal SV, ProgramStateRef state,
                                         CheckerContext &C) const {
       Optional<DefinedSVal> DV = SV.getAs<DefinedSVal>();
       if (!DV)
         return nullptr;

       ConstraintManager &CM = C.getConstraintManager();
       ProgramStateRef stateNotNull, stateNull;
       std::tie(stateNotNull, stateNull) = CM.assumeDual(state, *DV);

       if (!stateNotNull && stateNull) {
         if (ExplodedNode *N = C.generateErrorNode(stateNull)) {
           if (!BT_nullfp)
             BT_nullfp.reset(new BuiltinBug(this, "NULL stream pointer",
                                            "Stream pointer might be NULL."));
           C.emitReport(llvm::make_unique<BugReport>(
               *BT_nullfp, BT_nullfp->getDescription(), N));
         }
         return nullptr;
       }
       return stateNotNull;
     }
     ```

     在 [stream.c](https://github.com/llvm-mirror/clang/blob/master/test/Analysis/stream.c) 中有对 StreamChecker 比较完整的测试。

     它的结果：

     ```shell
     /Users/eastOffice/.ssh/PB15111610/sa/stream.c:19:3: warning: Stream pointer
           might be NULL
       fread(buf, 1, 1, p); // expected-warning {{Stream pointer might be NULL}}
       ^~~~~~~~~~~~~~~~~~~
     /Users/eastOffice/.ssh/PB15111610/sa/stream.c:25:3: warning: Stream pointer
           might be NULL
       fseek(p, 1, SEEK_SET); // expected-warning {{Stream pointer might be NULL}}
       ^~~~~~~~~~~~~~~~~~~~~
     /Users/eastOffice/.ssh/PB15111610/sa/stream.c:31:3: warning: Stream pointer
           might be NULL
       ftell(p); // expected-warning {{Stream pointer might be NULL}}
       ^~~~~~~~
     /Users/eastOffice/.ssh/PB15111610/sa/stream.c:37:3: warning: Stream pointer
           might be NULL
       rewind(p); // expected-warning {{Stream pointer might be NULL}}
       ^~~~~~~~~
     /Users/eastOffice/.ssh/PB15111610/sa/stream.c:46:3: warning: The whence argument
           to fseek() should be SEEK_SET, SEEK_END, or SEEK_CUR
       fseek(p, 1, 3); // expected-warning {{The whence argument to fseek() s...
       ^~~~~~~~~~~~~~
     /Users/eastOffice/.ssh/PB15111610/sa/stream.c:53:3: warning: Try to close a file
           Descriptor already closed. Cause undefined behaviour
       fclose(p); // expected-warning {{Try to close a file Descriptor alread...
       ^~~~~~~~~
     /Users/eastOffice/.ssh/PB15111610/sa/stream.c:58:3: warning: Stream pointer
           might be NULL
       ftell(p); // expected-warning {{Stream pointer might be NULL}}
       ^~~~~~~~
     /Users/eastOffice/.ssh/PB15111610/sa/stream.c:65:5: warning: Opened File never
           closed. Potential Resource leak
         return; // expected-warning {{Opened File never closed. Potential Re...
         ^~~~~~
     8 warnings generated.
     ```

     共8个warning，与预期的一致。