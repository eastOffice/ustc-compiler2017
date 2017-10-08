# Homework 1

PB15111610 张一卓

## 源代码

```c
#include <stdio.h>
int main()
{
	int i = 0;
	for(i = 0; i < 5; i++)
	{
		printf("hello world!\n");
	}
	return 0;
}
```

## -S

Only run preprocess and compilation steps

#### -m64 (default)

```assembly
	.section	__TEXT,__text,regular,pure_instructions
	.macosx_version_min 10, 11
	.globl	_main
	.align	4, 0x90
_main:                                  	## @main
	.cfi_startproc
## BB#0:
	pushq	%rbp						  ## %rbq into stack
Ltmp0:
	.cfi_def_cfa_offset 16
Ltmp1:
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp					   ## init the stack
Ltmp2:
	.cfi_def_cfa_register %rbp
	subq	$16, %rsp					   ## move the stack pointer, make space for i
	movl	$0, -4(%rbp)
	movl	$0, -8(%rbp)
	movl	$0, -8(%rbp)				   ## initial: i = 0
LBB0_1:                          ## =>This Inner Loop Header: Depth=1
	cmpl	$5, -8(%rbp)
	jge	LBB0_4					## if i >= 5, then jump to LBB04, out of for loop
## BB#2:                         ##   in Loop: Header=BB0_1 Depth=1
	leaq	L_.str(%rip), %rdi			    ## load 'hello world'
	movb	$0, %al
	callq	_printf
	movl	%eax, -12(%rbp)         	    ## 4-byte Spill
## BB#3:                                     ##   in Loop: Header=BB0_1 Depth=1
	movl	-8(%rbp), %eax				   ## i'(%eax) = i
	addl	$1, %eax					  ## i' = i' + 1
	movl	%eax, -8(%rbp)				   ## i = i'
	jmp	LBB0_1
LBB0_4:									## out of for loop
	xorl	%eax, %eax					## clear %eax
	addq	$16, %rsp					## regain the stack pointer
	popq	%rbp						## %rbq out of stack
	retq
	.cfi_endproc

	.section	__TEXT,__cstring,cstring_literals
L_.str:                                 ## @.str
	.asciz	"hello world!\n"


.subsections_via_symbols
```

#### -m32

```assembly
	.section	__TEXT,__text,regular,pure_instructions
	.macosx_version_min 10, 11
	.globl	_main
	.align	4, 0x90
_main:                                     ## @main
## BB#0:
	pushl	%ebp						## %ebp (i) into stack
	movl	%esp, %ebp					## init
	subl	$24, %esp					## allocate space
	calll	L0$pb
L0$pb:
	popl	%eax
	movl	$0, -4(%ebp)
	movl	$0, -8(%ebp)
	movl	$0, -8(%ebp)				## i = 0
	movl	%eax, -12(%ebp)         ## 4-byte Spill
LBB0_1:                           ## =>This Inner Loop Header: Depth=1
	cmpl	$5, -8(%ebp)
	jge	LBB0_4						## if i >= 5, jump to LBB04
## BB#2:                            ##   in Loop: Header=BB0_1 Depth=1
	movl	-12(%ebp), %eax         ## 4-byte Reload
	leal	L_.str-L0$pb(%eax), %ecx			## load "hello world!\n"
	movl	%ecx, (%esp)
	calll	_printf
	movl	%eax, -16(%ebp)         ## 4-byte Spill
## BB#3:                                ##   in Loop: Header=BB0_1 Depth=1
	movl	-8(%ebp), %eax			## i'(%ebp) = i
	addl	$1, %eax				## i' = i' + 1
	movl	%eax, -8(%ebp)			## i = i'
	jmp	LBB0_1
LBB0_4:
	xorl	%eax, %eax				## clear %eax
	addl	$24, %esp				## regain stack pointer
	popl	%ebp					## %ebp out of stack
	retl

	.section	__TEXT,__cstring,cstring_literals
L_.str:                                 ## @.str
	.asciz	"hello world!\n"


.subsections_via_symbols
```







