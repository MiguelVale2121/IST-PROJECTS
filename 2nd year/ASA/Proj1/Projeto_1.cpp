
#include <vector>
#include <stdio.h>
#include <ostream>
#include <iostream>
#include <algorithm>
#include <iterator>
#include <chrono>

using namespace std;


/*---------------------------------AUX FUNCTIONS---------------------------------*/

int maxInt(int a, int b) {

    if (a<b)
        return b;

    else
        return a;

}

bool inSequence(vector<int> X, int num) {

    if (count(X.begin(), X.end(), num)) return 1;
    return 0;

}

vector<int> intersection(vector<int> X, vector<int> Y) {

    vector<int> intersected;

    sort(X.begin(),X.end());
    sort(Y.begin(),Y.end());

    set_intersection(X.begin(),X.end(),Y.begin(),Y.end(),back_inserter(intersected));

    return intersected;

}

vector<int> filterVector(vector<int> toFilter, vector<int> intersection) {

    vector<int> filtered;

    for(long unsigned i=0 ; i<toFilter.size() ; i++) {

            if(inSequence(intersection,toFilter[i]))
                filtered.push_back(toFilter[i]);
            
        }

    return filtered;

}

/*---------------------------------PROBLEM 1---------------------------------*/

void LIS(vector<int> X) {
    int x = X.size();
    
    int length[x];
    int maxNum[x];
    int latest1 = 0;
    int latest2 = 0;
    int latest3 = 0;

    for (int i = 0; i < x; i++){
            length[i] = 1;
            maxNum[i] = 1;
        }
    
    for (int i = 0; i < x; i++) {
        
        latest1 = X[i];
        latest2= length[i];
        latest3= maxNum[i];

        for (int j = 0; j < i; j++) {
        
            if  (latest1>X[j] && length[j] + 1 > latest2) {

                latest2 = length[j] + 1;
                latest3 = maxNum[j];

            }

            else if (length[j] + 1 == latest2)
                latest3 += maxNum[j];

        }
        X[i]=latest1;
        length[i]=latest2;
        maxNum[i]=latest3;
        
    }

    int highest = 0;
    
    for (int i : length)
        highest = maxInt(i,highest);
    
    int count = 0;
    
    for(int i = 0; i < x; i++) {

        if (length[i] == highest)
        count += maxNum[i];

    }

    cout << highest << " " << count << endl;
    
}

/*---------------------------------PROBLEM 2---------------------------------*/

int LCIS(vector<int> X, vector<int> Y) { 

    int y = Y.size();
    int x = X.size();
    int table[y];

    for (int j=0; j<y; j++)
        table[j] = 0;

    for (int i=0; i<x; i++) {

        int current = 0;
 
        for (int j=0; j<y; j++) {

            if (X[i] == Y[j])
                if (current + 1 > table[j])
                    table[j] = current + 1;

            if (X[i] > Y[j])
                if (table[j] > current)
                    current = table[j];
        }
    }

    int result = 0;

    for (int i=0; i<y; i++)
        if (table[i] > result)
           result = table[i];
  
    return result;

}

int main() {
    
    char character;

    int type, num, num2,last = -1;


    vector<int> X, Y, Xfilt, Xfiltered, Yfiltered, intersected;

    cin >> type;
    
    if (type == 1) {

        while(cin >> num) {

            X.push_back(num);

            if ((character = getchar()) == '\n' || character== EOF) {

                LIS(X);
                break; 

            }
        }
    }

    if (type == 2) {

        while(cin >> num) {

            if(num != last)
                Xfilt.push_back(num);

            last = num;

            X.push_back(num);

            if (getchar() == '\n') break;

        }

        while(cin >> num2) {

            Y.push_back(num2);

            if ((character = getchar()) == '\n' || character== EOF)
                break;
            
        }

        intersected = intersection(Xfilt,Y);

        Xfiltered = filterVector(Xfilt, intersected);
        Yfiltered = filterVector(Y, intersected);

    

        cout << LCIS(Xfiltered, Yfiltered)<< endl; 

    }  

    return 0;   

}