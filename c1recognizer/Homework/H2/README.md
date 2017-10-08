# README

## 概述与使用

源代码simple_nfa.cpp实现了课件29页的nfa，能够自动识别一串字符中的多个满足nfa的子串。

输入：由字母a和b组成的任意字符串。

输出：满足指定nfa的多个字符串，以及它们分别的起始下标，终止下标。

可以循环输入字符串，输入字母q结束程序。

## 代码结构

1. **nfa转换图的表示**

   这个nfa比较简单，只有5个状态，所以可以用邻接矩阵来表示nfa的转换图。邻接矩阵是5*5的二维数组，下标正好对应状态的编号。矩阵中-1代表两顶不相连，0代表由空串相连，1代表由a相连，2代表由b相连。比如（3，4）= 2，代表状态3可以由输入b转换到状态4。

   ```c++
   int state_trans_table[5][5];
   void table_init()
   {
       for (int i = 0; i < 5; i++)
           for (int j = 0; j < 5; j++)
               state_trans_table[i][j] = -1;					// -1表示两顶不相连
       state_trans_table[0][1] = state_trans_table[0][3] = 0;	//两顶由空串相连
       state_trans_table[1][2] = state_trans_table[2][2] = 1;	//两顶由a相连
       state_trans_table[3][4] = state_trans_table[4][4] = 2;	//两顶由b相连
   }
   ```

2. **状态的表示**

   程序中用了一个单向队列来表示目前的状态集合。

   ```c++
   queue<int> current_state;
   ```

3. **nfa转换图的搜索**

   字符串在nfa转换图中的匹配过程就是转换图的路径搜索过程，所以采取了类似广搜的算法。

```c++
void table_search(int search_type)
{
    int n = current_state.size();
    for (int i = 0; i < n; i++)
    {
        int temp = current_state.front(); current_state.pop();
        if (search_type == 0)
            current_state.push(temp);
        for (int j = 0; j < 5; j++)
        {
            if (state_trans_table[temp][j] == search_type)
                current_state.push(j);
        }
    }
}
```

​	参数search_type表示搜索的条件，0表示搜索由空串连接的状态，1，2分别表示由a，b连接的状态。每次搜索取队列头上的状态，把搜索到的状态加入队列尾。需要注意的是搜索空串时队列头的状态需要重新加入队列尾。

```c++
		while (end_index < len)
        {
            //先从空串开始找
            table_search(0);
            //接下来读一个字母，按字母查找
            char c = test_string[end_index++];
            if (c == 'a') {
                table_search(1);
            }
            else if (c == 'b') {
                table_search(2);
            }
            if (current_state.size() == 0)
            {
                //遇到了不匹配的字母
                end_index = end_index -1;
                cout << test_string.substr(start_index, end_index-start_index) << "  " << "start: " << start_index <<"   end: " << end_index - 1 << endl;
                start_index = end_index;
                current_state.push(0);
              	//重新开始
            }
```

​	这是匹配nfa字符串的核心循环。第一步，从空串开始搜索，把所有能由空串连接的状态都加入队列。第二步，读入一个字母，按这个字母进行搜索。第三步，如果这是状态队列为空，那么说明目前的字符让字符串不匹配nfa了，则输出目前满足匹配的字符串，并且从当前字符重新开始搜索。

​	字符串aaab的状态转换是0 -> 1 ->2 ->2 ->2, 0-> 3->4,在程序中状态集合的变化为 {0} -> {0,1,3} -> {2}-> {2}-> {2} -> {0} -> {0,1,3} -> {4}。

## 结果展示

```
aaa
aaa  start: 0   end: 2
aabbaa
aa  start: 0   end: 1
bb  start: 2   end: 3
aa  start: 4   end: 5
aabbbab
aa  start: 0   end: 1
bbb  start: 2   end: 4
a  start: 5   end: 5
b  start: 6   end: 6
```

