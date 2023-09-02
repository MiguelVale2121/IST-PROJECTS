# takuzu.py: Template para implementação do projeto de Inteligência Artificial 2021/2022.
# Devem alterar as classes e funções neste ficheiro de acordo com as instruções do enunciado.
# Além das funções e classes já definidas, podem acrescentar outras que considerem pertinentes.

# Grupo 24:
# 99113 Miguel Vale
# 99057 Bernardo Melo

import sys

from sys import stdin
from search import (
    Problem,
    Node,
    astar_search,
    breadth_first_tree_search,
    depth_first_tree_search,
    greedy_search,
    compare_searchers,
    recursive_best_first_search,
)
import numpy as np

class TakuzuState:
    state_id = 0

    def __init__(self, board):
        self.board = board
        self.id = TakuzuState.state_id
        TakuzuState.state_id += 1

    def __lt__(self, other):
        return self.id < other.id

    # TODO: outros metodos da classe


class Board:
    """Representação interna de um tabuleiro de Takuzu."""
    def __init__(self, board_init, numbers, tamanho):
        self.board = board_init
        self.test = numbers
        self.size = tamanho

    def __str__(self):
        boardStr = ""
        for line in self.board:
            for col in line:
                boardStr += str(col)+"\t"
            boardStr = boardStr[:-1]
            boardStr += "\n"
        boardStr = boardStr[:-1]
        return boardStr
    

    def get_number(self, row: int, col: int) -> int:
        """Devolve o valor na respetiva posição do tabuleiro."""
        # TODO
        return self.board[row][col]

    

    def adjacent_vertical_numbers(self, row: int, col: int):
        """Devolve os valores imediatamente abaixo e acima,
        respectivamente."""
        # TODO
        vertical = [0, 0]
        if (row + 1 > len(self.board) - 1):
            vertical[0] = None

        else:
            vertical[0] = self.board[row + 1][col]

        if (row - 1 < 0):
            vertical[1] = None

        else:
            vertical[1] = self.board[row - 1][col]

        return (vertical[0], vertical[1])

    def adjacent_horizontal_numbers(self, row: int, col: int):
        """Devolve os valores imediatamente à esquerda e à direita,
        respectivamente."""
        # TODO
        horizontal = [0, 0]
        if (col - 1 < 0):
            horizontal[0] = None

        else:
            horizontal[0] = self.board[row][col - 1]

        if (col + 1 > len(self.board) - 1):
            horizontal[1] = None

        else:
            horizontal[1] = self.board[row][col + 1]

        return (horizontal[0], horizontal[1]) 

    @staticmethod
    def parse_instance_from_stdin():
        """Lê o test do standard input (stdin) que é passado como argumento
        e retorna uma instância da classe Board.

        Por exemplo:
            $ python3 takuzu.py < input_T01

            > from sys import stdin
            > stdin.readline()
        """
        # TODO
        buffer = []
        run = True
        contador = 0
        while run:
            line = stdin.readline()
            if line == "":
                run = False
            else:
                new_list = []
                for elem in line.split():
                    elem = int(elem)
                    new_list.append(elem)
                    if elem == 2:
                        contador += 1
                buffer.append(new_list)
        size = buffer[0][0]
        buffer = buffer[1:]
        return Board(np.array(buffer),contador,size)

    # TODO: outros metodos da classe

class Takuzu(Problem):
    def __init__(self, board: Board):
        """O construtor especifica o estado inicial."""
        # TODO
        self.initial = TakuzuState(board)
        
    def EqualNumbersRow(self, state: TakuzuState):
        board = state.board.board
        size = state.board.size
        for line in board:
            num_zeros = np.count_nonzero(line==0)
            num_ones = np.count_nonzero(line==1)
            if size % 2 ==0 and num_ones == num_zeros :  
                pass
            elif (size // 2) + 1 == num_ones and (size // 2) == num_zeros:
                pass
            elif (size // 2) + 1 == num_zeros and (size // 2) == num_ones:
                pass
            else:
                return False
        return True

    def EqualNumbersColumn(self, state: TakuzuState):
        board = state.board.board
        size = state.board.size
        i=0
        while i <= size-1:
            line = board[:,i]
            num_zeros = np.count_nonzero(line==0)
            num_ones = np.count_nonzero(line==1)
            if size % 2 ==0 and num_ones == num_zeros :  
                pass
            elif (size // 2) + 1 == num_ones and (size // 2) == num_zeros:
                pass
            elif (size // 2) + 1 == num_zeros and (size // 2) == num_ones:
                pass
            else:
                return False
            i+=1
        return True

    def DifferentRows(self, state: TakuzuState):
        board = state.board.board
        size = state.board.size
        j = 1
        for i in range(0,size):
            while j <= size-1: 
                if np.array_equal(board[i],board[j]) == True:
                    return False
                j += 1
            j = i + 2
        return True

    def DifferentColumn(self, state: TakuzuState):
        board = state.board.board
        new_board = np.transpose(board)
        size = state.board.size
        j = 1
        for i in range(0,size):
            while j <= size-1:  
                if np.array_equal(new_board[i],new_board[j]) == True:
                    return False
                j += 1
            j = i + 2
        return True

    def check_adjs(self, state: TakuzuState):
        size = state.board.size
        for row in range(size):
            for col in range(size):
                adjs_v = state.board.adjacent_vertical_numbers(row, col)
                adjs_h = state.board.adjacent_horizontal_numbers(row, col)
                number = state.board.get_number(row,col)           
                if (number == adjs_v[0] == adjs_v[1] or number == adjs_h[0]==adjs_h[1]):
                    return False

        return True

    def actions(self, state: TakuzuState):
        """Retorna uma lista de ações que podem ser executadasself.board = board a
        partir do estado passado como argumento."""
        # TODO
        actions = []
        size = state.board.size
        board = state.board.board
        i=0
        for line in board:
            j = 0
            
            for elem in line:
                
                if elem == 2:
                    linev=board[:,j]
                    num_zeros = np.count_nonzero(line==0)
                    num_ones = np.count_nonzero(line==1)
                    num_zerosv = np.count_nonzero(linev==0)
                    num_onesv = np.count_nonzero(linev==1)
                    adjs_v = state.board.adjacent_vertical_numbers(i, j)
                    adjs_h = state.board.adjacent_horizontal_numbers(i, j)
                    #Adjacentes verticais iguais
                    if(adjs_v[0]==adjs_v[1] and adjs_v[0]!=2):
                        return [[i,j,(adjs_v[0]-1)/(-1)]]

                    #Adjacentes horizontais
                    if(adjs_h[0]==adjs_h[1] and adjs_h[0]!=2):
                        return [[i,j,(adjs_h[0]-1)/(-1)]]

                    #dois iguais a Esquerda
                    if (j-2>=0 and board[i][j-1]==board[i][j-2] and board[i][j-1]!= 2):
                        return [[i,j,(adjs_h[0]-1)/(-1)]]
                        
                    #dois iguais a Direita
                    if (j+2<=size-1 and board[i][j+1]==board[i][j+2] and board[i][j+1]!=2):
                        return [[i,j,(adjs_h[1]-1)/(-1)]]
                    #dois iguais a Cima
                    if (i-2>=0 and board[i-1][j]==board[i-2][j] and board[i-1][j]!= 2):
                        return [[i,j,(board[i-1][j]-1)/(-1)]]
                    #dois iguais a Baixo
                    if (i+2<=size-1 and board[i+1][j]==board[i+2][j] and board[i+1][j]!= 2):
                        return [[i,j,(board[i+1][j]-1)/(-1)]]
                    #Metade de 1 nas linhas
                    if (size%2==0 and size/2 == num_ones) or (size%2!=0 and size//2+1 == num_ones):
                        return [[i,j,0]]                   
                    #Metade de 0 nas linhas
                    if (size%2==0 and size/2 == num_zeros) or (size%2!=0 and size//2+1 == num_zeros):
                        return [[i,j,1]]                    
                    #Metade de 1 nas colunas
                    if (size%2==0 and size/2 == num_onesv) or (size%2!=0 and size//2+1 == num_onesv):
                        return [[i,j,0]]
                    
                    #Metade de 0 nas colunas
                    if (size%2==0 and size/2 == num_zerosv) or (size%2!=0 and size//2+1 == num_zerosv):
                        return [[i,j,1]]
                                     
                    if(len(actions)==0):
                        actions.append([i,j,1])
                        actions.append([i,j,0])
                j+=1
            i+=1
        
        return actions
                    
    def result(self, state: TakuzuState, action):
        """Retorna o estado resultante de executar a 'action' sobre
        'state' passado como argumento. A ação a executar deve ser uma
        das presentes na lista obtida pela execução de
        self.actions(state)."""
        # TODO
        
        boardc = state.board.board.copy()
        boardc[action[0]][action[1]] = action[2]
        tab = Board(boardc,state.board.test-1,state.board.size)
        return TakuzuState(tab)

    def goal_test(self, state: TakuzuState):
        """Retorna True se e só se o estado passado como argumento é
        um estado objetivo. Deve verificar se todas as posições do tabuleiro
        estão preenchidas com uma sequência de números adjacentes."""
        # TODO
        #print(state.board.test)
        if state.board.test != 0:
            return False
        if problem.EqualNumbersRow(state) == False:
            return False
        if problem.DifferentRows(state)==False:
            return False
        if problem.check_adjs(state)==False:
            return False
        if problem.EqualNumbersColumn(state)==False:
            return False
        if problem.DifferentColumn(state)==False:
            return False
        return True

    def h(self, node: Node):
        """Função heuristica utilizada para a procura A*."""
        # TODO
        actions =0
        size = node.state.board.size
        board = node.state.board.board
        i=0
        for line in board:
            j = 0            
            for elem in line:                
                if elem == 2:
                    linev=board[:,j]
                    num_zeros = np.count_nonzero(line==0)
                    num_ones = np.count_nonzero(line==1)
                    num_zerosv = np.count_nonzero(linev==0)
                    num_onesv = np.count_nonzero(linev==1)
                    adjs_v = node.state.board.adjacent_vertical_numbers(i, j)
                    adjs_h = node.state.board.adjacent_horizontal_numbers(i, j)
                    #Adjacentes verticais iguais
                    if(adjs_v[0]==adjs_v[1] and adjs_v[0]!=2):
                        actions +=1
                    #Adjacentes horizontais
                    if(adjs_h[0]==adjs_h[1] and adjs_h[0]!=2):
                        actions +=1
                    #dois iguais a Esquerda
                    if (j-2>=0 and board[i][j-1]==board[i][j-2] and board[i][j-1]!= 2):
                        actions +=1                      
                    #dois iguais a Direita
                    if (j+2<=size-1 and board[i][j+1]==board[i][j+2] and board[i][j+1]!=2):
                        actions +=1
                    #dois iguais a Cima
                    if (i-2>=0 and board[i-1][j]==board[i-2][j] and board[i-1][j]!= 2):
                        actions +=1
                    #dois iguais a Baixo
                    if (i+2<=size-1 and board[i+1][j]==board[i+2][j] and board[i+1][j]!= 2):
                        actions +=1
                    #Metade de 1 nas linhas
                    if (size%2==0 and size/2 == num_ones) or (size%2!=0 and size//2+1 == num_ones):
                        actions +=1                 
                    #Metade de 0 nas linhas
                    if (size%2==0 and size/2 == num_zeros) or (size%2!=0 and size//2+1 == num_zeros):
                        actions +=1                  
                    #Metade de 1 nas colunas
                    if (size%2==0 and size/2 == num_onesv) or (size%2!=0 and size//2+1 == num_onesv):
                        actions +=1                    
                    #Metade de 0 nas colunas
                    if (size%2==0 and size/2 == num_zerosv) or (size%2!=0 and size//2+1 == num_zerosv):
                        actions +=1                                     
                    if(len(actions)==0):
                        return sys.maxsize * 2 + 1
                j+=1
            i+=1
        return actions
    # TODO: outros metodos da classe


if __name__ == "__main__":
    import time
    start_time = time.time()
    # TODO:
    # Imprimir para o standard output no formato indicado.
    board = Board.parse_instance_from_stdin()
    # Criar uma instância de Takuzu:
    problem = Takuzu(board)
    #goal_node_dfs= depth_first_tree_search(problem)
    #goal_node_Astar = astar_search(problem,problem.h)
    #goal_node_bfs = breadth_first_tree_search(problem)
    #goal_node_greedy = greedy_search(problem,problem.h)
    #print(goal_node_dfs.state.board)
    print(compare_searchers([problem], ["algoritmo", "resultados"]))
    #print("--- %s seconds ---" % round(time.time() - start_time, 6))