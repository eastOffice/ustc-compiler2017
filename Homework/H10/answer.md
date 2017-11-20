# HW10-2

PB15111610 张一卓

## 1.

观察如下 C 程序：（程序在/test/test1.c）

```c
#include <stdio.h>

int n;

int f()
{
	n = n-1;
	return n;
}
int main()
{
	n = 1;
 	printf("%d  %d\n", n, f()); 
} 
```

(1)  在``gcc version 5.3.1 20160413 (Ubuntu 5.3.1-14ubuntu2) ``环境下编译的输出结果为：

```
0	0
```

请解释为什么。

(2) 在``Apple LLVM version 9.0.0 (clang-900.0.38) MacOS 10.12.6``下用``clang``编译的输出结果为：

`````
1	0
`````

参考使用``clang -S`` 的部分输出：（完整输出在/test/test1.s）

```assembly
Lcfi2:
	.cfi_def_cfa_register %rbp
	movq	_n@GOTPCREL(%rip), %rax
	movl	(%rax), %ecx
	subl	$1, %ecx
	movl	%ecx, (%rax)
	movl	(%rax), %eax
	popq	%rbp
	retq
	.cfi_endproc

	.globl	_main
	.p2align	4, 0x90
_main:                                  ## @main
	.cfi_startproc
## BB#0:
	pushq	%rbp
Lcfi3:
	.cfi_def_cfa_offset 16
Lcfi4:
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
Lcfi5:
	.cfi_def_cfa_register %rbp
	subq	$16, %rsp
	movq	_n@GOTPCREL(%rip), %rax
	movl	$1, (%rax)
	movl	(%rax), %esi
	movl	%esi, -4(%rbp)          ## 4-byte Spill
	callq	_f
	leaq	L_.str(%rip), %rdi
	movl	-4(%rbp), %esi          ## 4-byte Reload
	movl	%eax, %edx
	movb	$0, %al
	callq	_printf
	xorl	%edx, %edx
	movl	%eax, -8(%rbp)          ## 4-byte Spill
	movl	%edx, %eax
	addq	$16, %rsp
	popq	%rbp
	retq
	.cfi_endproc
```

解释为什么。

答：

（1）因为在调用printf时，参数是逆序入栈的，所以函数f的结果先入栈，n的值在函数f中先被修改成0，然后再把n进栈，得到的结果是0。

考察点：C语言函数参数进栈顺序

（2）编译器发现函数f会对n的值（在%rax寄存器中）作出修改，所以在``callq  _f``之前，保护了n的值:

```assembly
	movl	(%rax), %esi
	movl	%esi, -4(%rbp)          ## 4-byte Spill
	callq	_f
	leaq	L_.str(%rip), %rdi
	movl	-4(%rbp), %esi          ## 4-byte Reload
```

​	因此在第二次入栈时，入栈的n值还是1，所以输出n的值还是1。

考察点：汇编程序

# 2

观察如下C语言程序：（程序在/test/test2.c）

```c
#include <stdio.h>

int f(unsigned int n)
{
	if(n < 0)
		return 1;
	else 
		return n + f(n-1);
}
int main()
{
	int n = 5;
	printf("%d\n", f(n));
} 
```

在``Apple LLVM version 9.0.0 (clang-900.0.38) MacOS 10.12.6``下编译运行结果为

```
Segmentation fault: 11
```

请解释为什么。

答：把类型为``int``的参数传入``f``时，以及``int``和``unsigned int``相加时，会把``int``隐式转换成``unsigned int``，所以在判断``n < 0``时，条件永远为假，所以会无限递归，知道活动记录栈溢出，报错``Segmentation fault``。

​	事实上现在``clang``会报``warning : comparison of unsigned expression < 0 is always false``。

考察点：类型转换