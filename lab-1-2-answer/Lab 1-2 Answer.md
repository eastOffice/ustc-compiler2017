# Lab 1-2 Answer

PB15111610 张一卓

## 依赖环境

java 1.8

antlr 4.7

python 3.x

## 测试程序说明

编写用于测试左递归的文件如下：

```
- c1recognizer
	| lab1-2-answer
		| MultFirst.g4
		| MultFirst.py
		| PlusFirst.g4
		| PlusFirst.py
		| UnsupportedLeftRecursive.g4
```

编译``.g4``文件时产生的衍生文件没有列出，但是测试时也是必需的文件。

###.g4文件说明

- 测试时，把 lexer 和 parser 的部分合在了一起，更加方便。

- 两个文件定义的``tokens``都是一样的，只有加法，乘法，Number，以及空白符：

  ```
  Number: [1-9] [0-9]*;
  Plus: '+';
  Multiply: '*';
  WhiteSpace: [ \t\n\r]+ -> skip;
  ```

- 在 ``MultFirst.g4``中，文法为：

  ```
  e:
      e Multiply e # Mult
      | e Plus e   # Plus
      | Number # Num
      ;
  ```



- 在``PlusFirst.g4``中，文法为：

  ```
  e:
      e Plus e # Plus
      | e Multiply e   # Mult
      | Number # Num
      ;
  ```


- 第一行文法

  ```
  s : e EOF;
  ```

  定义了一个起始状态，是为了处理一开始测试的时候会有一个``EOF``的报错。

- ``UnsupportedLeftRecursive.g4``中是不被支持的左递归语法的一个例子，放到后面再讲。

### .py文件说明

- 这两个python代码都是模仿仓库中的范例改写的，根据观察，只需要修改一些变量名称即可，接下来以

  ``MultFirst.py``为例。

- 首先需要修改``import``部分：

  ```
  import MultFirstLexer
  import MultFirstListener
  import MultFirstParser
  ```

  都是编译上述``.g4``文件产生的包。

- 接下来把所有引用它们的地方都改正确


- 经过试探性的运行和观察编译``.g4``产生的``.py``文件，发现需要修改一些函数名称，比如

  ``exprParser.NumContext``需要修改成``MultFirstParser.EContext``，另外，由于定义的``tokens``中没有``Identifier``和``Bracket``，所以有两段处理它们的代码就不需要了。

- 为了更直观的表示运算过程，为每个方法加上了一个``string``属性，用括号表现优先级。

## 编译与使用方式

首先，需要在``lab1-2-answer``文件夹下编译两个``.g4``文件：

```shell
antlr4 -Dlanguage=Python3 MultFirst.g4
antlr4 -Dlanguage=Python3 PlusFirst.g4
```

成功产生衍生文件后（提交时已经编译过了），用相应的``.py``文件进行测试：

```shell
python3 MultFirst.py
python3 PlusFirst.py
```

程序开始运行后，会在命令行等待输入一个**由数字，‘+’，‘*’ 构成**的计算表达式：

```
Please enter an expression:
```

结束后按下回车，就会得到这个表达式的值。根据这个表达式计算值，我们就可以知道文法的计算优先级。

## 测试结果与分析

首先观察``MultFirst.g4``和``PlusFirst.g4``两个文件的区别，发现仅仅是加法和乘法在文法语句中的顺序不一样而已，所以很容易猜测是运算优先级的区别。所以在测试的时候，输入同一个语句，比如：

```
1*2+3
```

``MultFirst.g4``的结果为：

```
Please enter an expression: 1*2+3
((1 * 2) + 3)
7
```

``PlusFirst.g4``的结果为：

```
Please enter an expression: 1*2+3
(1 * (2 + 3))
9
```

再试一下``1*2+3``,得到的结果都是5.

多试几条语句，发现之前的猜想得到了印证，这两个文法的区别就在于乘法和加法的优先级不同。哪一条产生式写在``|``前面，哪一条产生式的优先级就越高。

接下来发现这两条文法都是明显的左递归，而且是直接左递归，antlr都可以处理。所以猜测 antlr 会利用这样的文法规则的优先级，来保证每次规约的时候都有唯一的规则；用类似课本上的方法，重写直接左递归的规则，转换成非左递归的文法。那么对于间接左递归的文法， antlr 是否还支持呢？

在``UnsupportedLeftRecursive.g4``中，我写了一个间接左递归的文法：

```
S : A | End;
A : B | End;
B : S | End;
```

进行编译：

```
antlr4 UnsupportedLeftRecursive.g4
```

直接报错：

```
error(119): UnsupportedLeftRecursive.g4::: The following sets of rules are mutually left-recursive [S, B, A]
```

发现不支持。经过查找资料确认，antlr 的确不支持间接左递归的文法。

## 结论

- ANTLR 容许哪些类型的左递归？

  ANTLR 容许所有直接左递归，不支持间接左递归。注意到 C1 中一条语法：``CompUnit -> [ CompUnit ] ( Decl | FuncDef`` 不被支持，其实这类语法（第一项是可选项）可以算作间接左递归，因为ANTLR 无法预测可选项是否出现，来判断是否需要进行左递归的优化。

- ANTLR 对所支持的左递归如何处理？

  ANTLR 利用文法规则的优先级（规则越前，优先级越高），并对直接左递归进行重写，使之成为非左递归的形式。

- 给出ANTLR 不支持的左递归文法的例子并尝试分析原因。

  例子已经在上文给出。在网上查找资料之后，尝试分析一下：

  把直接左递归转换成非左递归的算法已经有了，比较简单。但是对于间接的左递归，若要能够转化成直接左递归式，要求文法不存在环和ε产生式，而并非所有的间接左递归文法都能满足这样的性质。而且，要判断是否满足这样的条件并不容易。

