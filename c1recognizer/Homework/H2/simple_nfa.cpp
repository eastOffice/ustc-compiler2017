#include <iostream>
#include <string>
#include <queue>

using namespace std;

int state_trans_table[5][5];
queue<int> current_state;

void table_init()
{
    for (int i = 0; i < 5; i++)
        for (int j = 0; j < 5; j++)
            state_trans_table[i][j] = -1;					// -1表示两顶不相连
    state_trans_table[0][1] = state_trans_table[0][3] = 0;	//两顶由空串相连
    state_trans_table[1][2] = state_trans_table[2][2] = 1;	//两顶由a相连
    state_trans_table[3][4] = state_trans_table[4][4] = 2;	//两顶由b相连
}

void queue_init()
{
    while (!current_state.empty())
    {
        current_state.pop();
    }
    current_state.push(0);
}

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

void print_queue()
{
    queue<int> temp = current_state;
    while (!temp.empty())
    {
        cout << temp.front() << " ";
        temp.pop();
    }
    cout << endl;
}

int main()
{
    table_init();
    while (1)
    {
        queue_init();
        string test_string; cin >> test_string;
        if (test_string[0] == 'q') break;
        int start_index = 0, end_index = 0;
        int len = test_string.length();
        while (end_index < len)
        {
            //先从空串开始找
            table_search(0);
            //print_queue();
            //接下来读一个字母
            char c = test_string[end_index++];
            if (c == 'a') {
                table_search(1);
                //print_queue();
            }
            else if (c == 'b') {
                table_search(2);
                //print_queue();
            }
            if (current_state.size() == 0)
            {
                //cout << "Does not match.\n";
                end_index = end_index -1;
                cout << test_string.substr(start_index, end_index-start_index) << "  " << "start: " << start_index <<
                "   end: " << end_index - 1 << endl;
                start_index = end_index;
                current_state.push(0);
            }
        }
        cout << test_string.substr(start_index, end_index - start_index) << "  start: " << start_index << "   end: " << end_index-1 << endl;
    }
    return 0;
}