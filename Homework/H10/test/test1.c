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