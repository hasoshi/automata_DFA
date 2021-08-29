#include <iostream>
#include <vector>
#include <map>
#include <algorithm>
#include <string>
#include <fstream>
#include <queue>
#include <sstream>
#include <utility>

#define breakline() cout<<"------------------------------------\n"
using namespace std;

//Xoa cac trang thai khong can thiet (khong the di toi) cua DFA
void remove_states(map<string, vector<string> >& table, vector<string>& nofinal_states, vector<string>& final_states) {
    map<string, bool> reached;
    int i, j;
    for (i = 0; i < nofinal_states.size(); i++) {
        reached.insert(pair<string, bool>(nofinal_states[i], 0));
    }
    for (i = 0; i < final_states.size(); i++) {
        reached.insert(pair<string, bool>(final_states[i], 0));
    }

    queue<string> q;
    string t;
    reached[nofinal_states[0]] = 1;
    for (i = 0; i < table[nofinal_states[0]].size(); i++) {
        q.push(table[nofinal_states[0]][i]);
    }

    //Su dung tim kiem chieu rong BFS de theo doi cac trang thai da truy cap hay chua
    while (!q.empty()) {
        t = q.front();
        if (!reached[t]) {
            reached[t] = 1;
            for (i = 0; i < table[t].size(); i++) {
                q.push(table[t][i]);
            }
        }
        q.pop();
    }
    for (map<string, bool>::iterator r = reached.begin(); r != reached.end(); r++) {
        if (!r->second) {
            string t = (string)r->first;
            table.erase(t);
            vector<string>::iterator it1, it2;
            it1 = find(nofinal_states.begin(), nofinal_states.end(), t);
            it2 = find(final_states.begin(), final_states.end(), t);
            if (it1 != nofinal_states.end()) {
                //xoa khoi tap cac cap khong co dinh ket
                nofinal_states.erase(it1, it1 + 1);
            }
            else if (it2 != final_states.end()) {
                //xoa khoi tap co dinh ket
                final_states.erase(it2, it2 + 1);
            }

        }
    }
}

void update_set(vector<vector<string> >& set, map<string, int>& m, int max_num_set, int& curr_num_set) {
    int i, j;
    set.resize(max_num_set);
    for (i = 0; i < curr_num_set; i++) {
        for (j = 0; j < set[i].size(); j++) {
            if (m[set[i][j]] != i) {
                set[m[set[i][j]]].push_back(set[i][j]);
                set[i].erase(set[i].begin() + j, set[i].begin() + j + 1);
                j--;
            }
        }
    }
    curr_num_set = max_num_set;
}
//Update lai bang chuyen trang thai
void update_table_states(vector<string>& comb_set, map<string, int>& m, map<string, vector<string> >& table, vector<string>& nofinal_states, vector<string>& final_states) {
    int i, j;
    map<string, vector<string> > newtable;
    for (map<string, vector<string> >::iterator it = table.begin(); it != table.end(); it++) {
        newtable[comb_set[m[it->first]]];
        for (j = 0; j < it->second.size(); j++) {
            if (newtable[comb_set[m[it->first]]].size() < it->second.size())
                newtable[comb_set[m[it->first]]].push_back(comb_set[m[it->second[j]]]);
        }
    }
    table.clear();
    //update bang chuyen trang thai
    table = newtable;

    for (i = 0; i < nofinal_states.size(); i++) {
        if ((comb_set[m[nofinal_states[i]]].size()) > 0) {
            string t = comb_set[m[nofinal_states[i]]];
            comb_set[m[nofinal_states[i]]].clear();
            nofinal_states[i] = t;
        }
        else {
            nofinal_states.erase(nofinal_states.begin() + i, nofinal_states.begin() + i + 1);
            i--;
        }
    }

    for (i = 0; i < final_states.size(); i++)
    {
        if ((comb_set[m[final_states[i]]].size()) > 1)
        {
            string t = comb_set[m[final_states[i]]];
            comb_set[m[final_states[i]]].clear();
            final_states[i] = t;
        }
        else {
            final_states.erase(final_states.begin() + i, final_states.begin() + i + 1);
            i--;
        }
    }
}

//ham toi thieu hoa DFA
void Minimize_DFA(map<string, vector<string> >& table, vector<string>& nofinal_states, vector<string>& final_states, int input_symbol) {

    remove_states(table, nofinal_states, final_states); //xoa cac trang thai khong su dung
    int i, j;
    map<string, int> m;
    vector<vector<string> > set; //tao set
    vector<string> t, curr_set;
    int s, curr_num_set, max_num_set, eq_set;
    bool change = 1, flag = 0;

    for (i = 0; i < nofinal_states.size(); i++) {
        m[nofinal_states[i]] = 0;
        t.push_back(nofinal_states[i]);
    }
    set.push_back(t);
    t.clear();
    for (i = 0; i < final_states.size(); i++) {
        m[final_states[i]] = 1;
        t.push_back(final_states[i]);
    }
    set.push_back(t);
    t.clear();

    //bat dau toi thieu hoa
    curr_num_set = 2; //so luong dinh cua vecto
    max_num_set = 2; //so luong dinh cua vector ban dau + cac dinh moi tao thanh
    while (change) { //vong lap tim cac trang thai phan biet
        for (s = 0; s < input_symbol; s++) {
            change = 0;
            for (i = 0; i < curr_num_set; i++) {
                flag = 0;
                curr_set = set[i];
                string p = table[curr_set[0]][s];

                eq_set = m[table[curr_set[0]][s]];
                for (j = 1; j < curr_set.size(); j++) {
                    if (m[table[curr_set[j]][s]] != eq_set) {
                        change = 1;
                        flag = 1;
                        m[curr_set[j]] = max_num_set;
                        p = curr_set[j];
                    }
                }
                if (flag) {
                    max_num_set++;
                    flag = 0;
                }
            }
            if (change) {
                update_set(set, m, max_num_set, curr_num_set);
            }
        }
    }

    vector<string> comb_set;
    string temp;
    for (i = 0; i < set.size(); i++) {
        temp.clear();
        for (j = 0; j < set[i].size(); j++) {
            temp += set[i][j];
        }
        comb_set.push_back(temp);
    }
    set.clear();
    //update cac trang thai moi cua DFA
    update_table_states(comb_set, m, table, nofinal_states, final_states);
}
//in cac du lieu
void print(map<string, vector<string> >& table, vector<string>& nofinal_states, vector<string>& final_states, vector<string>& input_symbols) {
    int i, j;
    breakline();
    cout << "Cac trang thai khong co trang thai ket: \n";
    for (i = 0; i < nofinal_states.size(); i++) {
        cout << nofinal_states[i] << "  ";
    }
    cout << endl;
    cout << "Trang thai ket: \n";
    for (i = 0; i < final_states.size(); i++) {
        cout << final_states[i] << "  ";
    }
    cout << endl;
    //thuc hien doc ki tu dau tien trong bang chuyen trang thai
    //ki tu dau tien day cung chinh la ki tu bat dau trang thai otomat (start states)
    cout << "Ki hieu vao (" << input_symbols[0] << ") & Bang chuyen trang thai:\n";
    for (i = 0; i < input_symbols.size(); i++) {
        cout << input_symbols[i] << "\t";
    }
    cout << endl;
    for (map<string, vector<string> >::iterator it = table.begin(); it != table.end(); it++) {
        cout << it->first << "\t";
        for (j = 0; j < it->second.size(); j++) {
            cout << it->second[j] << "\t";
        }
        cout << endl;
    }
    breakline();
}

int main() {
    vector<string> final_states; 
    map<string, vector<string> >  table; //bang trang thai dung map
    vector<string> nofinal_states; 
    vector<string> input_symbols; 

    string line, ch, t;
    int i, j;
    bool  flag = 0;
    //nhap du lieu tu file txt
    ifstream fin;
    fin.open("dfa_input.txt");
    if (!fin) {
        cerr << "Load du lieu khong thanh cong!\n";
        exit(1);
    }
    while (getline(fin, line)) {
        istringstream ss(line);
        ss >> ch;
        if (ch.compare("NF") == 0) { 
            ss >> ch;
            while (ss >> ch) {
                nofinal_states.push_back(ch);
            }
        }else if (ch.compare("F") == 0 && !flag) {
            ss >> ch;
            while (ss >> ch) {
                final_states.push_back(ch);
            }
        }else if (ch.compare("S") == 0) { //sigma
            input_symbols.push_back(ch);
            while (ss >> ch) {
                input_symbols.push_back(ch);
            }
            flag = 1;
        }else {
            t = ch;
            while (ss >> ch) {
                table[t].push_back(ch);
            }
        }
    }
    cout << "Nhap DFA ban dau:\n";
    print(table, nofinal_states, final_states, input_symbols);
    Minimize_DFA(table, nofinal_states, final_states, input_symbols.size() - 1);
    cout << "DFA sau khi toi thieu hoa:\n";
    print(table, nofinal_states, final_states, input_symbols);
    return 0;
}