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