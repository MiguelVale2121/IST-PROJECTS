# Analysis and Synthesis of Algorithms Projects

This repository contains two projects related to algorithm analysis and synthesis.

## Project 1: Longest Strictly Increasing Subsequence

### Problem Description

Given a sequence ~x = h x0, x1, ..., xk i of integers, the goal is to calculate the size of the longest strictly increasing subsequence of ~x, as well as the number of strictly increasing subsequences of maximum size.

For example, for the sequence ~x = h1, 2, 6, 3, 7i, there are two strictly increasing subsequences of maximum size, which are ~s1 = h1, 2, 6, 7i and ~s2 = h1, 2, 3, 7i.

### Input

The input file contains information about the problem to solve and the corresponding sequences of integers. It is defined as follows:
- One line containing an integer 'n' indicating the problem number (1 for this problem).
- 'n' lines, each containing a sequence of integers separated by a single space and ending with a newline character.

### Output

The program should write two integers 't' and 'c' separated by a space, where 't' corresponds to the size of the longest subsequence that meets the problem's constraints, and 'c' corresponds to the number of subsequences of maximum size.

### Example

Input:
1
1 2 6 3 7

Output:
4 2


## Project 2: Closest Common Ancestors in a Genealogical Tree

### Problem Description

A genealogical tree is a directed graph in which each node represents a person, and the direct neighbors of a node correspond to their children, with the possibility of orphan nodes, nodes with one parent, and nodes with two parents. Given two nodes P1 and P2 in a genealogical tree:
- P1 is considered an ancestor of P2 if it is possible to reach P2 from P1 in the genealogical tree.
- P3 is considered the closest common ancestor of P1 and P2 if it is an ancestor of both P1 and P2, and there is no descendant P4 of P3 that is also an ancestor of P1 and P2.

This definition allows multiple closest common ancestors for two nodes. For example, in the graph below, v2 and v4 are both closest common ancestors of v5 and v6.

Given a directed graph G = (V, E) and two vertices v1 and v2 ∈ V, the goal is to:
1. Determine if G forms a valid genealogical tree.
2. Find the set of closest common ancestors between v1 and v2.

### Input

The input file contains information about the genealogical tree to be processed and the vertices v1 and v2 for which the closest common ancestors should be calculated. It is defined as follows:
- One line containing the identifiers of vertices v1 and v2.
- One line containing two integers: the number 'n' of vertices (n ≥ 1) and the number 'm' of edges (m ≥ 0).
- A list of 'm' lines, each containing the identifiers of vertices 'x' and 'y', indicating that 'y' is the child of 'x'.
- The identifiers of vertices are integers between 1 and 'n'.

### Output

The program should write "0" to the output if the graph does not form a valid genealogical tree. Otherwise, it should write the sequence of identifiers of all closest common ancestors, sorted in ascending order and separated by a space. If no closest common ancestor exists, it should write "-".

### Example

Input:
5 6
8 9
1 2
1 3
2 6
2 7
3 8
4 3
4 5
7 5
8 6

Output:
2 4

## Grading

- Project 1 Grade: 19.75
- Project 2 Grade: 16.50


