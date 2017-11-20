	.section	__TEXT,__text,regular,pure_instructions
	.macosx_version_min 10, 12
	.globl	_f
	.p2align	4, 0x90
_f:                                     ## @f
	.cfi_startproc
## BB#0:
	pushq	%rbp
Lcfi0:
	.cfi_def_cfa_offset 16
Lcfi1:
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
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

	.comm	_n,4,2                  ## @n
	.section	__TEXT,__cstring,cstring_literals
L_.str:                                 ## @.str
	.asciz	"%d  %d\n"


.subsections_via_symbols
