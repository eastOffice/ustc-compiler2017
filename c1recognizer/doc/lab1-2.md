# Lab 1-2 Report

PB15111610 张一卓

## 实验目标

根据 C1语言 的 EBNF 描述，修改并完善课程实验软件包中 C1 的语法描述文件c1recognizer/grammar/C1Parser.g4，利用 ANTLR v4 节介绍的 antlr4 和 grun 工具编译和测试你的分析器。请编写若干个正确的和错误的 C1 程序作为测试程序，来测试你所构造的 C1 语法分析器。

## 实验分析与设计

在课程主页上有用 EBNF 表示的C1语法，只需要在``C1Parser.g4``中把EBNF表示的语法转换成 antlr 支持的文法就可以了。举一个简单的例子来说，如下一条 EBNF 语法：

```EBNF
ConstDef    → ident '=' Exp | ident '[' [ Exp ] ']' '=' '{' Exp { ',' Exp } '}'
```

转换成

```antlr
constdef: 
    Identifier Assign exp 
    |Identifier LeftBracket exp? RightBracket Assign LeftBrace exp (Comma exp)* RightBrace;
```

只需要注意例如 ``Identifier``这样的符号都必须是在``C1Lexer.g4``中定义过的。带单引号的符号都是``token``，不带单引号的大括号和中括号则分别代表着可重复任意次和可选项，并且分别用``*``和``?``来处理。

这个过程并不困难，就是要比较细心，比如注意``Comma``和``SemiColon``的区别，``Assign``和``Equal``的区别；另外，第一条语法：

```
CompUnit    → [ CompUnit ] ( Decl | FuncDef ) 
```

是 antlr 所不支持的左递归文法，需要修改。我用的方法是直接把中括号展开：

```
compilationUnit:
    (decl | funcdef)
    |compilationUnit (decl | funcdef);
```

这样就是一条 antlr 支持的左递归文法了。最后的结果见 c1recognizer/grammar/C1Parser.g4 .

## 实验编译与结果

编译的时候需要把 Lexer 和 Parser 文件一起编译，在 grammer 目录下，使用 simple.c1 进行测试：

```
antlr4 *.g4
javac *.java
grun C1 compilationUnit -gui ../test/test_cases/simple.c1
```

产生的语法树如下：

![simple_tree](simple_tree.png)

用 declarations.c1 进行测试（我注释掉了其中的 main 函数）：

![declarations_tree](/Users/eastOffice/.ssh/PB15111610/c1recognizer/doc/declarations_tree.png)

对于错误的语句：（见 test.c1）

```
int a = 5
int b == 4
```

也有报错：

```
line 2:0 mismatched input 'int' expecting {',', ';'}
line 2:6 mismatched input '==' expecting {',', ';'}
```

可以看到实验结果符合预期。