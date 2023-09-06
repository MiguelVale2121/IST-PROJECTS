#include <vector>
#include <stdio.h>
#include <ostream>
#include <iostream>
#include <algorithm>

using namespace std;

int matrix_size;
vector<vector<int>> adj;
vector<int> ancestors;

void initializeAdj() 
{   
    /*initializes adj with quantity of nodes empty vectors*/
    vector<int> v(0);

    for (int i = 0 ; i < matrix_size ; i++) 
    {
        adj.push_back(v);
    }
}

void addEdge(int parent, int child)
{
    /*the vector in the child-1 position of adj will have 
      all of the child's parents*/
    adj[child-1].push_back(parent);
}

void getAncestors(int vertex)
{   
    for (int p : adj[vertex-1]) 
    {
        /*add every parent of vertex to ancestors*/
        ancestors.push_back(p);

        /*repeat the process until reaching node with no parents*/
        getAncestors(p);
    }
}

vector<int> getLowestCommonAncestor(int v1, int v2) {

    /*gets all ancestors of v1 including itself*/
    getAncestors(v1);
    vector<int> v1_ancestors = ancestors;
    v1_ancestors.push_back(v1);

    ancestors.clear();

    /*gets all ancestors of v2 including itself*/
    getAncestors(v2);
    vector<int> v2_ancestors = ancestors;
    v2_ancestors.push_back(v2);

    ancestors.clear();

    /*sorts both ancestor vectors so they can be input in set_intersection(...)*/
    sort(v1_ancestors.begin(), v1_ancestors.end());
    sort(v2_ancestors.begin(), v2_ancestors.end());

    /*creates a vector with all the common ancestors of v1 and v2*/
    vector<int> v1_v2_ancestors;

    set_intersection(v1_ancestors.begin(), v1_ancestors.end(), v2_ancestors.begin(), v2_ancestors.end(), back_inserter(v1_v2_ancestors));
         
    /*deletes any vertex with a descendant that is also a common ancestor 
      of v1 and v2 from the final vector*/
    for (int va : v1_v2_ancestors)
    {
        getAncestors(va);
        for (int v : ancestors) 
        {
            if (count(v1_v2_ancestors.begin(), v1_v2_ancestors.end(), v)){
                v1_v2_ancestors.erase(remove(v1_v2_ancestors.begin(), v1_v2_ancestors.end(), v), v1_v2_ancestors.end());
            }
        }
        ancestors.clear();
        
    }

    for (int va : v1_v2_ancestors)
    {
        getAncestors(va);
        for (int v : ancestors) 
        {
            if (count(v1_v2_ancestors.begin(), v1_v2_ancestors.end(), v)){
                v1_v2_ancestors.erase(remove(v1_v2_ancestors.begin(), v1_v2_ancestors.end(), v), v1_v2_ancestors.end());
            }
        }
        ancestors.clear();
        
    }

    for (int va : v1_v2_ancestors)
    {
        getAncestors(va);
        for (int v : ancestors) 
        {
            if (count(v1_v2_ancestors.begin(), v1_v2_ancestors.end(), v)){
                v1_v2_ancestors.erase(remove(v1_v2_ancestors.begin(), v1_v2_ancestors.end(), v), v1_v2_ancestors.end());
            }
        }
        ancestors.clear();
        
    }
    

    /*sort the LCA vector increasingly*/
    sort(v1_v2_ancestors.begin(), v1_v2_ancestors.end());

    

    /*removes duplicates from the vector*/
    auto last = unique(v1_v2_ancestors.begin(), v1_v2_ancestors.end());
    v1_v2_ancestors.erase(last, v1_v2_ancestors.end());

    vector<int> auxilary_vector;
    if(v1==v2){

        auxilary_vector.push_back(v2);
        return auxilary_vector;
    }

    for(int i : v1_ancestors){
        if(i == v2){
            auxilary_vector.push_back(v2);
            return auxilary_vector;
        }
    }
    
    for(int i : v2_ancestors){
        if(i == v1){
            auxilary_vector.push_back(v1);
            return auxilary_vector;
        }
    }

    

    return v1_v2_ancestors;
}

bool isTree()
{
    for (size_t c = 0 ; c < adj.size() ; c++)
    {
        if (adj[c].size() > 2) return 0;
        for (int p : adj[c]) 
        {
            if (count(adj[p-1].begin(), adj[p-1].end(), c+1)) return 0;
        }
    }

    return 1;

}

int main() 
{
    int v1, v2;
    int n, m;
    int parent, child;
    
    scanf("%d %d", &v1, &v2);
    scanf("%d %d", &n, &m);

    matrix_size = n;

    initializeAdj(); 

    /*construct the tree*/
    for (int i=0 ; i<m ; i++)
    {
        scanf("%d %d", &parent, &child);
        addEdge(parent, child);
    }

    /*if it's not a valid tree, print "0" and stop the program*/
    if (!isTree()) 
    {
        cout << "0" << endl;
        return 0;
    }


    /*if it's valid, get the LCA between the two specified vertexes*/
    vector<int> LCA = getLowestCommonAncestor(v1, v2);

    /*if the vector is not empty print it, else print "-"*/
    if (LCA.size()) for (int x : LCA) cout << x << " ";
    else cout << "-";
    cout << endl;

    return 0;
}