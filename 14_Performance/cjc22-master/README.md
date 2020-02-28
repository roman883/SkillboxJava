# CJC II - task 22

- We have a chessboard with specified ```width``` and ```height```. Width indicates the amount of columns. (```always 1 <= width <= 1000```). Height indicates the amount of rows. (```always 1 <= height <= 1000```)

- Given the initial ```start``` and ```end``` position of the KNIGHT, each consists of two variables - ```start```: startCol, startRow, ```end``` endRow, endCol

Arguments are always correct.


Calculate the minimum number of moves in which the KNIGHT will reach the end position.

```code
         1   2   3   4   5   6   7   8      1000 (Col)
       + - + - + - + - + - + - + - + - +...+
1      |   | S |   |   |   |   |   |   |   |
       + - + - + - + - + - + - + - + - +...+
2      |   |   |   |   |   |   |   |   |   |
       + - + - + - + - + - + - + - + - +...+
3      |   |   | X |   |   |   |   |   |   |
       + - + - + - + - + - + - + - + - +...+
4      |   |   |   |   | X |   |   |   |   |
       + - + - + - + - + - + - + - + - +...+
5      |   |   |   |   |   |   | E |   |   |
       + - + - + - + - + - + - + - + - +...+
6      |   |   |   |   |   |   |   |   |   |
       + - + - + - + - + - + - + - + - +...+
7      |   |   |   |   |   |   |   |   |   |
       + - + - + - + - + - + - + - + - +...+
8      |   |   |   |   |   |   |   |   |   |
       + - + - + - + - + - + - + - + - +...+
       .   .   .   .   .   .   .   .   .   .
1000   |   |   |   |   |   |   |   |   |   |
(Row)  + - + - + - + - + - + - + - + - +...+
```

```S =  START position = 2, 1```

```E =  END position = 7, 5```

- In this situation minimum moves = 3

1 move = 2, 1 -> 3, 3

2 move = 3, 3 -> 5, 4

3 move = 5, 4 -> 7, 5


If the end position is not reachable return ```-1```.

Good luck!

