# Parallel-Mine-Sweeper-Solver
A basic minesweeper game written in Java, which can solve itself in parallel using recursive fork-join tasks. 

The backend for the game is done using bitwise operations instead of objects to reduce runtime while using the parallel solver. 
There are three bitsets which represent the entire board: One for the location of the mines, one for where the user has clicked, and one final bitset
to keep track of where the user marks mines.

The solver algorithm, works by creating a graph of every possible sequence of moves. Each node in the graph contains the current game state, and a 
reference to the previous node. 

A backpointer is used to find the best possible solution to the board, by using dijksta's shortest path algorithm to find the solution with the least
number of moves
