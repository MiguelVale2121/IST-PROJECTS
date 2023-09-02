# Miguel Vale ist199113

"""
TAD posicao
Representacao interna: string("a1","b1","c1","a2","b2","c2","a3","b3","c3")
cria_posicao: string x string -> posicao
cria_copia_posicao: posicao->posicao
obter_pos_c: posicao -> string
obter_pos_l: posicao -> string
eh_posicao: universal-> booleano
posicoes_iguais: posicao x posicao -> booleano
posicao_para_str: posicao->str
posicao_inteiro: posicao -> inteiro
int_posicao_str_c: inteiro -> string
int_posicao_str_l: inteiro -> string
obter_posicoes_adjacentes: posicao -> tuplo de posicoes
"""

def cria_posicao(c,l):
#parametro c: coluna
#parametro l: linha
#return str
# cria uma posicao atraves da soma do elemento da coluna com o da linha
    colunas = ["a","b","c"]
    linhas = ["1","2","3"]
    if type(c)!= str or type(l)!=str or c not in colunas or l not in linhas:
        raise ValueError("cria_posicao: argumentos invalidos")
    return c + l

def cria_copia_posicao(p):
# parametro p:posicao
# return posicao
# cria copia da posicoa fornecida como parametro
    return cria_posicao(p[0],p[1])

def obter_pos_c(p):
# parametro p:posicao
# return str
# obtem a coluna
    return p[0]

def obter_pos_l(p):
# parametro p:posicao
# return str
# obtem a linha
    return p[1]

def eh_posicao(arg):
# parametro arg: argumento
# return booleano
# verifica os argumentos para verificar se e uma posicao

    return type(arg)==str and len(arg)==2 and arg[0] in ["a","b","c"] and arg[1] in ["1","2","3"]

def posicoes_iguais(p1,p2):
#parametro p1: posicao 1
#parametro p2: posicao 2
#return booleano
#verifica se as duas posicoes sao iguais
    return eh_posicao(p1) and eh_posicao(p2) and obter_pos_c(p1)==obter_pos_c(p2) and obter_pos_l(p1)==obter_pos_l(p2)

def posicao_para_str(p):
#parametro p = posicao
#return string
#transforma a posicao dada numa stirng
    return p  #como a posicao dada ja e uma string devolve a posicao dada

def posicao_inteiro(p):
#parametro p = posicao
#return inteiro
#transforma uma posicao num inteiro conforme o tavbuleiro escolhido
    if p == cria_posicao("a","1"):
        return 0
    elif p == cria_posicao("b","1"):
        return 1
    elif p == cria_posicao("c","1"):
        return 2
    elif p == cria_posicao("a","2"):
        return 3
    elif p == cria_posicao("b","2"):
        return 4
    elif p == cria_posicao("c","2"):
        return 5
    elif p == cria_posicao("a","3"):
        return 6
    elif p == cria_posicao("b","3"):
        return 7
    elif p == cria_posicao("c","3"):
        return 8

def int_posicao_str_c(int):
#parametro int: inteiro
#return stirng
#transforma um inteiro numa string sendo essa a coluna
    if int == 0:
        return "a"
    elif int == 1:
        return "b"
    elif int == 2:
        return "c"

def int_posicao_str_l(int):
# parametro int: inteiro
# return stirng
# transforma um inteiro numa string sendo essa a coluna
    if int==0:
        return "1"
    elif int == 1:
        return "2"
    elif int == 2:
        return "3"

#Alto nível posicao
posicoes_tab = ("a1","b1","c1","a2","b2","c2","a3","b3","c3")

def posicao_a1(p):
#parametro p: posicao
#return tuplo de posicoes
# afirma quais sao as posicoes adjacentes da posicao a1
    if p == "a1":
        return (posicoes_tab[1],posicoes_tab[3],posicoes_tab[4])
def posicao_b1(p):
# parametro p: posicao
# return tuplo de posicoes
# afirma quais sao as posicoes adjacentes da posicao b1
    if p == "b1":
        return (posicoes_tab[0],posicoes_tab[2],posicoes_tab[4])
def posicao_c1(p):
# parametro p: posicao
# return tuplo de posicoes
# afirma quais sao as posicoes adjacentes da posicao c1
    if p == "c1":
        return (posicoes_tab[1],posicoes_tab[4],posicoes_tab[5])
def posicao_a2(p):
# parametro p: posicao
# return tuplo de posicoes
# afirma quais sao as posicoes adjacentes da posicao a2
    if p == "a2":
        return (posicoes_tab[0],posicoes_tab[4],posicoes_tab[6])
def posicao_b2(p):
# parametro p: posicao
# return tuplo de posicoes
# afirma quais sao as posicoes adjacentes da posicao b2
    if p == "b2":
        return (posicoes_tab[0],posicoes_tab[1],posicoes_tab[2],posicoes_tab[3],posicoes_tab[5],posicoes_tab[6],
                posicoes_tab[7],posicoes_tab[8])
def posicao_c2(p):
# parametro p: posicao
# return tuplo de posicoes
# afirma quais sao as posicoes adjacentes da posicao c2
    if p== "c2":
        return (posicoes_tab[2],posicoes_tab[4],posicoes_tab[8])
def posicao_a3(p):
# parametro p: posicao
# return tuplo de posicoes
# afirma quais sao as posicoes adjacentes da posicao a3
    if p== "a3":
        return (posicoes_tab[3],posicoes_tab[4],posicoes_tab[7])
def posicao_b3(p):
# parametro p: posicao
# return tuplo de posicoes
# afirma quais sao as posicoes adjacentes da posicao b3
    if p== "b3":
        return (posicoes_tab[4],posicoes_tab[6],posicoes_tab[8])
def posicao_c3(p):
# parametro p: posicao
# return tuplo de posicoes
# afirma quais sao as posicoes adjacentes da posicao c3
    if p=="c3":
        return (posicoes_tab[4],posicoes_tab[5],posicoes_tab[7])


def obter_posicoes_adjacentes(p):
#parametro p: posicao
#return tuplo de posicoes
# afirma quais sao as posicoes adjacentes a cada posicao do tabuleiro
    if posicao_a1(p)!=None:
        return posicao_a1(p)
    elif posicao_a2(p)!=None:
        return posicao_a2(p)
    elif posicao_a3(p)!=None:
        return posicao_a3(p)
    elif posicao_b1(p)!=None:
        return posicao_b1(p)
    elif posicao_b2(p)!=None:
        return posicao_b2(p)
    elif posicao_b3(p)!=None:
        return posicao_b3(p)
    elif posicao_c1(p)!=None:
        return posicao_c1(p)
    elif posicao_c2(p)!=None:
        return posicao_c2(p)
    elif posicao_c3(p)!=None:
        return posicao_c3(p)



"""
TAD peca
Representacao interna: String ("X","O"," ")
cria_peca: str->peca
cria_copia_peca: peca->peca
eh_peca: universal -> booleano
pecas_iguais: peca x peca -> booleano
peca_para_str: peca -> string
inteiro_para_peca: inteiro->peca
peca_para_inteiro: peca -> inteiro
"""
def cria_peca(s):
#parametro s: string
#return peca
#atraves de uma string cria a nossa representacao da peca
    peca=["X","O"," "]
    if s not in peca or type(s)!=str:
        raise ValueError("cria_peca: argumento invalido")
    return s

def cria_copia_peca(j):
#parametro j: peca
#return peca
#copia a peca dada como argumento
    return cria_peca(j)

def eh_peca(arg):
#parametro arg: argumento
#return booleano
#valida os argumentos de acordo com a peca
    return type(arg)==str and len(arg)==1 and arg in ["X","O"," "]

def pecas_iguais(j1,j2):
#parametro j1: peca 1
#parametro j2: peca 2
#return booleano
#verifica se as pecas sao iguais
    return eh_peca(j1) and eh_peca(j2) and cria_peca(j1)==cria_peca(j2)

def peca_para_str(j):
#parametro j:peca
#return str
#transforma a nossa peca em uma string corresponde ao dono da peca("[X]", "[O]", "[ ]").
    return "["+ str(cria_peca(j)) +"]"

def inteiro_para_peca(j):
#parametro j: peca
#return str
# transforma a noss peca inteira em uma peca em string
    if j==1:
        return "X"
    elif j==-1:
        return "O"
    elif j==0:
        return " "


#Alto nivel peca
def peca_para_inteiro(j):
#parametro j:peca
#return inteiro
#transforma a nossa peca string nuam peca inteiro
    if j == "X":
        return 1
    elif j == "O":
        return -1
    elif j == " ":
        return 0


"""
TAD tabuleiro
Representacao interna: lista com tres listas inseridas nela ([[" "," "," "],[" "," "," "],[" "," "," "]])
cria_tabuleiro: {} -> tabuleiro
cria_copia_tabuleiro : tabuleiro -> tabuleiro
tabuleiro_singular: tabuleiro -> uma lista com as posicoes do tabuleiro
obter_peca: tabuleiro x posicao -> peca
obter_vetor : tabuleiro x str -> tuplo de pecas
coloca_peca: tabuleiro x peca x posicao -> tabuleiro
remove_peca: tabuleiro x posicao -> tabuleiro
move_peca: tabuleiro x posicao x posicao -> tabuleiro
eh_tabuleiro: universal -> booleano
eh_posicao_livre: tabuleiro x posicao -> booleano
tabuleiro_iguais: tabuleiro x tabuleiro -> booleano
tabuleiro_para_str : tabuleiro -> str
tuplo_para_tabuleiro: tuplo ->tabuleiro
obter_ganhador: tabuleiro -> peca
obter_posicoes_livres: tabuleiro -> tuplo de posicoes
obter_posicoes_jogador: tabuleiro x peca - > tuplo de posicoes
"""
def cria_tabuleiro():
#return tabuleiro
#cria um tabuleiro vazio
    return [[" "," "," "],[" "," "," "],[" "," "," "]]

def cria_copia_tabuleiro(t):
#parametro t: tabuleiro
#return tabuleiro
# cria uma copia do tabuleiro fornecido
    return [[t[0][0],t[0][1],t[0][2]],[t[1][0],t[1][1],t[1][2]],[t[2][0],t[2][1],t[2][2]]]

def tabuleiro_singular(t):
#parametro t: tabuleiro
#return lista
# transforma o nosso tabuleiro numa lista
    return [t[0][0],t[0][1],t[0][2],t[1][0],t[1][1],t[1][2],t[2][0],t[2][1],t[2][2]]

def obter_peca(t,p):
#parametro t: tabuleiro
#parametro p: posicao
#return peca
#obtem a peca que se encontra na posicoa que desejamos do tabuleiro
    for i in range(len(tabuleiro_singular(t))):
        if i == posicao_inteiro(p):
            return tabuleiro_singular(t)[i]


def obter_vetor(t,s):
# parametro t: tabuleiro
# parametro s: string
#return tuplo de pecas
# da nos o vetor de linha ou coluna que pedirmos com as respetivas pecas inseridas
    if s in ["a", "b", "c"]:
        return ((obter_peca(t, cria_posicao(s, "1"))), (obter_peca(t, cria_posicao(s, "2"))),
                (obter_peca(t, cria_posicao(s, "3"))))
    elif s in ["1", "2", "3"]:
        return ((obter_peca(t, cria_posicao("a", s))), (obter_peca(t, cria_posicao("b", s))),
                (obter_peca(t, cria_posicao("c", s))))

def coloca_peca(t,j,p):
#parametro t: tabuleiro
#parametro j: peca
#parametro p: posicao
#return tabuleiro
#modifica o tabuleiro primeiramente inserido com a peca que queremos colocar
    contador=0
    for i in range(len(t)):
        for e in range(len(t)):
            if posicao_inteiro(p)==contador:
                t[i][e]=j
            contador = contador + 1
    return t


def remove_peca(t,p):
# parametro t: tabuleiro
# parametro p: posicao
# return tabuleiro
# modifica o tabuleiro primeiramente inserido retirando a peca da posicao desejada
    contador = 0
    for i in range(len(t)):
        for e in range(len(t)):
            if posicao_inteiro(p) == contador:
                t[i][e] = cria_peca(" ")
            contador = contador + 1
    return t


def move_peca(t,p1,p2):
# parametro t: tabuleiro
# parametro p1: posicao1
# parametro p2: posicao2
# return tabuleiro
# modifica o tabuleiro primeiramente inserido mudando a peca da posicao 1 para a posicao 2
    if cria_copia_posicao(p1) != cria_copia_posicao(p2):
        coloca_peca(t,obter_peca(t,p1),p2)
        remove_peca(t,p1)
    return t

def eh_tabuleiro(arg):
#parametro arg: argumento
#return booleano
#verifica os argumentos para que um tabuleiro seja verdadeiro
    contador_X = 0
    contador_O = 0
    if type(arg)!=list or len(arg)!=3:
        return False
    for i in arg:
        if type(i) != list or len(i) != 3:
            return False
        contador_X = contador_X + i.count(cria_peca("X"))
        contador_O = contador_O + i.count(cria_peca("O"))
        for j in i:
            if type(j) != str or j not in [cria_peca("X"),cria_peca("O"),cria_peca(" ")]:
                return False
    if contador_X>3 or contador_O>3 or abs(contador_X-contador_O)>1:
        return False
    for i in ["a","b","c","1","2","3"]:
        if obter_vetor(arg,i) == (cria_peca("X"),cria_peca("X"),cria_peca("X")):
            for e in ["a","b","c","1","2","3"]:
                if obter_vetor(arg, e) == (cria_peca("O"), cria_peca("O"), cria_peca("O")):
                    return False
    return True

def eh_posicao_livre(t,p):
#parametro t: tabuleiro
#parametro p: posicao
#return boolenao
# verifica se no tabuleiro dado a posicoa dada e livre
    return obter_peca(t,p)==" "

def tabuleiros_iguais(t1,t2):
#parametro t1: tabuleiro1
#parametro t2: tabuleiro2
#return booleano
#verifica se os tabuleiros dados sao iguais
    return eh_tabuleiro(t1) and eh_tabuleiro(t2) and cria_copia_tabuleiro(t1)==cria_copia_tabuleiro(t2)

def tabuleiro_para_str(t):
#parametro t:tabuleiro
#return string
#devolve uma string no formatos do tabuleiro
    return ("   a   b   c\n1 ["+t[0][0]+"]-["+t[0][1]+"]-["+t[0][2]+"]\n   | \\ | / |\n2 ["+t[1][0]+"]-["+t[1][1]+"]-["+t[1][2]+"]\n   | / | \\ |\n3 ["+t[2][0]+"]-["+t[2][1]+"]-["+t[2][2]+"]")

def tuplo_para_tabuleiro(t):
#parametro t : tuplo
#return tabuleiro
#transforma um tuplo no nosso tabuleiro
    lista=[]
    tabuleiro_final=[]
    tabuleiro=[]
    for linha in t:
        for elemento in linha:
            if elemento == 1:
                lista = lista + ["X"]
            elif elemento == -1:
                lista = lista + ["O"]
            elif elemento == 0:
                lista = lista + [" "]
    for i in range(len(lista)):
        if i % 3 ==0 and i>0:
            tabuleiro_final = tabuleiro_final + [tabuleiro]
            tabuleiro = []
        tabuleiro = tabuleiro + [lista[i]]
    tabuleiro_final = tabuleiro_final + [tabuleiro]
    return tabuleiro_final

#Alto nivel tabuleiro

def obter_ganhador(t):
#parametro t:tabuleiro
#return peca
# devolve uma peca do jogador que tenha as suas 3 pecas em linha na vertical ou na horizontal no tabuleiro
    ganhador= (cria_peca(" "))
    for s in ["a","b","c","1","2","3"]:
        if obter_vetor(t,s) == ((cria_peca("X")), (cria_peca("X")), (cria_peca("X"))):
            ganhador = (cria_peca("X"))
        elif obter_vetor(t,s) == ((cria_peca("O")), (cria_peca("O")), (cria_peca("O"))):
            ganhador = (cria_peca("O"))
    return ganhador


def obter_posicoes_livres(t):
#parametor t: tabuleiro
#return tuplo de posicoes
# devolve um tuplo com as posicoes livres do tabuleiro
    vector = ()
    contador_c=0
    contador_l=0
    if eh_tabuleiro(t):
        for linha in range(len(t)):
            for casa in range(len(t)):
                if t[linha][casa] == cria_peca(" "):
                    vector = vector + (cria_posicao(int_posicao_str_c(contador_c),int_posicao_str_l(contador_l)),)
                contador_c = contador_c + 1
            contador_l = contador_l + 1
            contador_c = 0
        return vector



def obter_posicoes_jogador(t,j):
#parametro t:tabuleiro
#parametro j: peca
#return tuplo de posicoes
#devolve o tuplo com as posicoes ocupadas pelas pecas do jogador
    vector = ()
    contador_c = 0
    contador_l = 0
    if eh_tabuleiro(t):
        for linha in range(len(t)):
            for casa in range(len(t)):
                if t[linha][casa] == cria_peca(j):
                    vector = vector + (cria_posicao(int_posicao_str_c(contador_c),int_posicao_str_l(contador_l)),)
                contador_c = contador_c + 1
            contador_l = contador_l + 1
            contador_c = 0
        return vector

"""
Funcoes adicionais
obter_movimento_manual: tabuleiro x peca -> tuplo de posicoes
centro: tabuleiro -> str
canto_vazio: tabuleiro -> str
lateral_vazio: tabuleiro -> str
vitoria: tabuleiro x peca -> str
inverte_peca: peca -> peca
bloqueio: tabuleiro x peca -> str
obter_movimento_auto: tabuleiro x peca x str -> tuplo de posicoes
minimax: tabuleiro x peca x profundidade x seq_movimentos -> tuplo
moinho: str x str -> str
"""

def obter_movimento_manual(t,j):
#parametro t :tabuleiro
#parametro j: peca
#return tuplo de posicoes
#devolve um tuplo com a posicao ou as posicoes que inserimos manualmente
    a_posicoes_livres=0
    if len(obter_posicoes_jogador(t,j))<3:
        colocacao = str(input("Turno do jogador. Escolha uma posicao: "))
        x = cria_posicao(colocacao[0],colocacao[1])
        if len(colocacao)!=2 or x[0] not in ["a","b","c"] or x[1] not in ["1","2","3"] \
        or x not in obter_posicoes_livres(t):
            raise ValueError("obter_movimento_manual: escolha invalida")
        return x,
    elif len(obter_posicoes_jogador(t,j))==3:
        movimento = str(input("Turno do jogador. Escolha um movimento: "))
        y = cria_posicao(movimento[0],movimento[1])
        z = cria_posicao(movimento[2],movimento[3])
        if len(movimento)!=4 or movimento[0] not in ["a","b","c"] or movimento[2] not in ["a","b","c"] \
        or movimento[1] not in ["1","2","3"]\
        or movimento[3] not in ["1", "2", "3"] or obter_peca(t,y)!= j or \
        obter_peca(t,z)!= cria_peca(" ")  or z not in obter_posicoes_adjacentes(y):
            raise ValueError("obter_movimento_manual: escolha invalida")
        for i in obter_posicoes_jogador(t,j):
            for e in obter_posicoes_adjacentes(i):
                if e in obter_posicoes_livres(t):
                    a_posicoes_livres = a_posicoes_livres + 1
        if a_posicoes_livres!=0:
            if y == z:
                raise ValueError("obter_movimento_manual: escolha invalida")

        return (y,z)


def centro(t):
#parametro t: tabuleiro
#return str
#devolve a posicao central do tabuleiro
    if eh_posicao_livre(t,"b2"):
        return "b2"

def canto_vazio(t):
# parametro t: tabuleiro
# return str
# devolve um canto vazio do tabuleiro
    cantos=["a1","c1","a3","c3"]
    for posicao in cantos:
        if eh_posicao_livre(t,posicao):
            return posicao

def lateral_vazio(t):
# parametro t: tabuleiro
# return str
# devolve uma lateral vazia do tabuleiro
    laterais=["b1","a2","c2","b3"]
    for posicao in laterais:
        if eh_posicao_livre(t,posicao):
            return posicao

def vitoria(t,j):
#parametro t:tabuleiro
#parametro j:peca
#return str
#devolve a posicao que vai obter vitoria ao jogador
    posicoes=obter_posicoes_livres(t)
    for posicao in posicoes:
        t2 = cria_copia_tabuleiro(t)
        if obter_ganhador(coloca_peca(t2,j,posicao))== (cria_peca("X")) \
        or obter_ganhador(coloca_peca(t2,j,posicao))== (cria_peca("O")):
            return posicao

def inverte_peca(j):
#parametro j: peca
#return peca
#inverte as pecas
    if j == cria_peca("X"):
        return cria_peca("O")
    elif j == cria_peca("O"):
        return cria_peca("X")

def bloqueio(t,j):
#parametro t:tabuleiro
#parametro j:peca
#return str
#vai bloquear a vitoria do adversario
   return vitoria(t,inverte_peca(j))

def minimax(t, j, profundidade, seq_movimentos):
#parametro t: tabuleiro
#parametro j: peca
#parametro profundidade: inteiro
#parametro seq_movimentos: tuplo
#return tuplo
#devolve a melhor jogada possivel efetuada pelo computador dependendo do nivel da funcao recursiva
    if (obter_ganhador(t) in (cria_peca("X"),cria_peca("O"))) or profundidade == 0:
        return peca_para_inteiro(obter_ganhador(t)), seq_movimentos
    melhor_resultado = -peca_para_inteiro(j)
    melhor_seq_movimentos = None
    for p in obter_posicoes_jogador(t, j):
        for posicao in obter_posicoes_adjacentes(p):
            if eh_posicao_livre(t, posicao):
                novo_tabuleiro = cria_copia_tabuleiro(t)
                move_peca(novo_tabuleiro, p, posicao)
                novo_resultado, nova_seq_movimentos = minimax(novo_tabuleiro, inteiro_para_peca(melhor_resultado),
                                                              profundidade - 1,seq_movimentos + (p, posicao))
                if (melhor_seq_movimentos is None) or (j == cria_peca("X") and novo_resultado > melhor_resultado)\
                or (j == cria_peca("O") and novo_resultado < melhor_resultado):
                    melhor_resultado, melhor_seq_movimentos = novo_resultado, nova_seq_movimentos
    return melhor_resultado, melhor_seq_movimentos

def obter_movimento_auto(t,j,dificuldade):
#parametro t: tabuleiro
#parametro j:peca
#parametro dificuldade : str
#return tuplo de posicoes
# esta funcao vai conter todas as opcoes de movimento que o computador tem
    if len(obter_posicoes_jogador(t, j)) < 3:
        if vitoria(t,j):
            return vitoria(t,j),
        elif bloqueio(t,j):
            return bloqueio(t,j),
        elif centro(t):
            return "b2",
        elif canto_vazio(t):
            return canto_vazio(t),
        elif lateral_vazio(t):
            return lateral_vazio(t),
    else:
        if dificuldade == "facil":
            for posicao in ["a1","b1","c1","a2","b2","c2","a3","b3","c3"]:
                if obter_peca(t,posicao)==j:
                    for i in obter_posicoes_adjacentes(posicao):
                        if eh_posicao_livre(t,i):
                            return (posicao,i)
        elif dificuldade == "normal":
            return minimax(t,j,1,())[1][0], minimax(t,j,1,())[1][1]
        elif dificuldade == "dificil":
            return minimax(t,j,5,())[1][0], minimax(t,j,5,())[1][1]



def moinho(j,dificuldade):
#parametro j: peca
#parametro dificuldade: str
#return str
#funcao responsavel para podermos jogar o jogo do moinho
    if type(j)!=str or type(dificuldade)!=str or j not in ["[X]","[O]"]\
    or dificuldade not in ["facil", "normal", "dificil"]:
        raise ValueError("moinho: argumentos invalidos")
    if j == "[X]":
        jog = cria_peca("X")
    else:
        jog = cria_peca("O")
    print("Bem-vindo ao JOGO DO MOINHO. Nivel de dificuldade " +dificuldade+ ".")
    t = cria_tabuleiro()
    print(tabuleiro_para_str(t))
    while len(obter_posicoes_livres(t))!=0 and obter_ganhador(t)==cria_peca(" "):
        if jog==(cria_peca("X")):
            if len(obter_posicoes_jogador(t,cria_peca("X")))<3 or len(obter_posicoes_jogador(t,cria_peca("O")))<3:
                posicao = obter_movimento_manual(t,jog)
                t = coloca_peca(t,cria_peca("X"),posicao[0])
                print(tabuleiro_para_str(t))
                if obter_ganhador(t)==cria_peca(" ") and obter_posicoes_livres(t)!=[]:
                    print("Turno do computador (" +dificuldade+ "):")
                    posicao = obter_movimento_auto(t, cria_peca("O"), dificuldade)
                    t = coloca_peca(t, cria_peca("O"),posicao[0])
                    print(tabuleiro_para_str(t))
            else:
                posicao = obter_movimento_manual(t, jog)
                t = move_peca(t, posicao[0], posicao[1])
                print(tabuleiro_para_str(t))
                if obter_ganhador(t) == cria_peca(" ") and obter_posicoes_livres(t) != []:
                    print("Turno do computador (" + dificuldade + "):")
                    posicao = obter_movimento_auto(t, cria_peca("O"), dificuldade)
                    t = move_peca(t, posicao[0], posicao[1])
                    print(tabuleiro_para_str(t))
        elif jog==cria_peca("O"):
            if len(obter_posicoes_jogador(t, cria_peca("X"))) < 3 or len(obter_posicoes_jogador(t, cria_peca("O"))) < 3:
                print("Turno do computador (" + dificuldade + "):")
                posicao = obter_movimento_auto(t, cria_peca("X"), dificuldade)
                t = coloca_peca(t, cria_peca("X"), posicao[0])
                print(tabuleiro_para_str(t))
                if obter_ganhador(t) == cria_peca(" ") and obter_posicoes_livres(t) != []:
                    posicao = obter_movimento_manual(t, jog)
                    t = coloca_peca(t, cria_peca("O"), posicao[0])
                    print(tabuleiro_para_str(t))
            else:
                print("Turno do computador (" + dificuldade + "):")
                posicao = obter_movimento_auto(t, cria_peca("X"), dificuldade)
                t = move_peca(t, posicao[0], posicao[1])
                print(tabuleiro_para_str(t))
                if obter_ganhador(t) == cria_peca(" ") and obter_posicoes_livres(t) != []:
                    posicao = obter_movimento_manual(t, jog)
                    t = move_peca(t, posicao[0], posicao[1])
                    print(tabuleiro_para_str(t))
    if obter_ganhador(t)==cria_peca("X"):
        return "[X]"
    elif obter_ganhador(t)==cria_peca("O"):
        return "[O]"


print(moinho("[X]","dificil"))