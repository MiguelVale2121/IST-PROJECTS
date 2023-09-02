# Miguel Vale ist199113 LEIC-T

def eh_tabuleiro(tab):
    """eh_tabuleiro:
    Recebe um tabuleiro e verifica se esse tabuleiro corresponde ao do jogo para isso ve se o tabuleiro e um tuplo e se
    contem tres elementos, depois ve se esses elementos tambem sao tuplos e que os elementos dentro deles tem 3
    elementos por fim, ve se esses elementos sao inteiros e iguais aos numeros 1,-1,0.

    recebe:tab,universal
    devolve:boleano, True or False
    """
    if type(tab)!= tuple or len(tab)!=3:
        return False
    for i in tab:
        if type(i) != tuple or len(i)!=3:
           return False
        for j in i:
            if type(j) != int or j not in (1, -1, 0):
                return False
    return True


def eh_posicao(posicao):
    """eh_posicao:
    Recebe uma posicao e verifica se essa posicao e um numero inteiro que pode ter os meus valores das posicoes do
    tabuleiro(1,2,3,4,5,6,7,8,9).

    recebe:posicao,universal
    devolve:boleano,True or False
    """
    if type(posicao)!= int:
        return False
    if posicao<1 or posicao>9:
        return False
    else:
        return True


def obter_coluna(tab,inteiro):
    """obter_coluna:
    Recebe um tabuleiro e um inteiro que corresponde ao numero de colunas, e devolve nos um tuplo correspondete a coluna
    que selecionamos.

    recebe:tab e inteiro,universal
    devolve:vector,tuplo
    """
    if not(eh_tabuleiro(tab)) or type(inteiro)!=int or inteiro<1 or inteiro>3:
        raise ValueError("obter_coluna: algum dos argumentos e invalido")
    vector=()
    for i in tab:
        vector = vector + (i[inteiro-1],)
    return vector


def obter_linha(tab,inteiro):
    """obter_linha:
       Recebe um tabuleiro e um inteiro que corresponde ao numero de linhas, e devolve nos um tuplo correspondete a
       linha que selecionamos.

       recebe:tab e inteiro,universal
       devolve:vector,tuplo
    """
    if not(eh_tabuleiro(tab)) or type(inteiro)!=int or inteiro<1 or inteiro>3:
        raise ValueError("obter_linha: algum dos argumentos e invalido")
    return tab[inteiro-1]


def obter_diagonal(tab,inteiro):
    """obter_diagonal:
       Recebe um tabuleiro e um inteiro que corresponde ao numero de diagonais, e devolve nos um tuplo correspondete a
       diagonal que selecionamos.

       recebe:tab e inteiro,universal
       devolve:vector,tuplo
    """
    if not(eh_tabuleiro(tab)) or type(inteiro)!=int or inteiro<1 or inteiro>2:
        raise ValueError("obter_diagonal: algum dos argumentos e invalido")
    if inteiro == 1:
        return tab[0][0],tab[1][1],tab[2][2],
    if inteiro == 2:
        return tab[2][0],tab[1][1],tab[0][2],


def tabuleiro_str(tab):
    """tabuleiro_str:
    Recebe um tabuleiro e devolve o mesmo em formato de string, o numero 1 representa X o numero -1 representa O.

    recebe: tab,universal
    devolve: cadeia de caracteres, string
    """
    if not(eh_tabuleiro(tab)):
        raise ValueError("tabuleiro_str: o argumento e invalido")
    tabuleiro=""
    contador=1
    contador2=0
    for linha in tab:
        contador2=contador2+1
        for posicao in linha:
            if contador % 3 == 0:
                if posicao == 1:
                    contador = contador + 1
                    tabuleiro = tabuleiro + " X "
                elif posicao == -1:
                    contador = contador + 1
                    tabuleiro = tabuleiro + " O "
                else:
                    contador = contador + 1
                    tabuleiro = tabuleiro + "   "
            else:
                if posicao==1:
                    contador= contador + 1
                    tabuleiro= tabuleiro + " X |"
                elif posicao==-1:
                    contador = contador + 1
                    tabuleiro= tabuleiro + " O |"
                else:
                    contador = contador + 1
                    tabuleiro = tabuleiro + "   |"
        if contador2<=2:
            tabuleiro = tabuleiro + "\n-----------\n"

    return tabuleiro


def eh_posicao_livre(tab,posicao):
    """eh_posicao_livre:
    Recebe um tabuleiro e uma posicao, e verifica que a posicao dada corresponde a a uma posicao livre no tabuleiro dado.

    recebe: tab e posicao, universal
    devolve: boleano, True or False
    """
    variavel = 0
    if not(eh_tabuleiro(tab)) or not(eh_posicao(posicao)):
        raise ValueError("eh_posicao_livre: algum dos argumentos e invalido")
    for linha in range(len(tab)):
        for casa in range(len(tab)):
            variavel = variavel + 1
            if posicao == variavel:
                if tab[linha][casa] == 0:
                    return True

    return False


def obter_posicoes_livres(tab):
    """obter_posicoes_livres:
    Recebe um tabuleiro e devolve um vector ordenado das posicoes livres do tabuleiro dado.

    recebe:tab,universal
    devolve:vectro,tuplo
    """
    if not(eh_tabuleiro(tab)):
        raise ValueError("obter_posicoes_livres: o argumento e invalido")
    vector=()
    variavel=0
    for linha in range(len(tab)):
        for casa in range(len(tab)):
            variavel = variavel + 1
            if tab[linha][casa]==0:
                vector = vector + (variavel,)

    return vector


def jogador_ganhador(tab):
    """jogador_ganhador:
    Recebe um tabuleiro e devolve um numero inteiro que corresponde ao simbulo do jogador vencedor X=1,O=-1 ou 0 se nao
    ganhou ninguem.

    recebe:tab,universal
    devolve:numero,inteiro
    """
    if not(eh_tabuleiro(tab)):
        raise ValueError("jogador_ganhador: o argumento e invalido")
    for linha in range(1,4):
        if obter_linha(tab,linha)==(1,1,1) or obter_coluna(tab,linha)==(1,1,1):
            return 1
        elif obter_linha(tab,linha)==(-1,-1,-1) or obter_coluna(tab,linha)==(-1,-1,-1):
            return -1
        elif linha<=2:
            if obter_diagonal(tab,linha)==(1,1,1):
                return 1
            elif obter_diagonal(tab,linha)==(-1,-1,-1):
                return -1
    return 0


def marcar_posicao(tab,inteiro,posicao):
    """marcar_posicao:
    Recebe um tabuleiro, um inteiro que corresponde ao jogador X=1 e O=-1 , uma posicao livre do tabuleiro e devolve
    esse mesmo tabuleiro só que a posicao livre escolhida e trocada no tabuleiro pelo valor colocado no inteiro que
    representa qual jogador jogou nessa posicao.

    recebe:tab,inteiro,posicao , universal
    devolve:tabuleiro, tuplo
    """
    if not(eh_tabuleiro(tab)) or type(inteiro)!=int or inteiro>1 or inteiro<-1 or inteiro==0 or not(eh_posicao(posicao)) or not(eh_posicao_livre(tab,posicao)):
        raise ValueError("marcar_posicao: algum dos argumentos e invalido")
    lista=[]
    tuplo_principal=()
    tuplo_temporario=()
    for linha in tab:
        for casa in linha:
            lista = lista + [casa]
    lista[posicao-1] = inteiro
    for i in range(len(lista)):
        if i % 3 ==0 and i > 0:
            tuplo_principal = tuplo_principal + (tuplo_temporario,)
            tuplo_temporario = ()
        tuplo_temporario = tuplo_temporario + (lista[i],)
    tuplo_principal = tuplo_principal + (tuplo_temporario,)
    return tuplo_principal

def escolher_posicao_manual(tab):
    """escolher_posicao_manual:
    Recebe um tabuleiro e devolve um a mesma posicao inserida por nos se essa casa estiver livre.

    recebe:tab,universal
    devolve:posicao,inteiro
    """
    if not(eh_tabuleiro(tab)):
        raise ValueError("escolher_posicao_manual: o argumento e invalido")
    posicao_manual = int(input("Turno do jogador. Escolha uma posicao livre: "))
    if not (eh_posicao(posicao_manual)) or not (eh_posicao_livre(tab, posicao_manual)):
        raise ValueError("escolher_posicao_manual: a posicao introduzida e invalida")

    return posicao_manual


def criterio_5(tab):
    """criterio_5:
    Recebe um tabuleiro e se a posicao central desse tabuleiro estiver livre devolve essa posicao.

    recebe:tab,universal
    devolve:posicao,inteiro
    """
    if eh_posicao_livre(tab,5):
        return 5


def criterio_7(tab):
    """criterio_7:
    Recebe um tabuleiro e se os cantos estiverem vazios devolve a posicao do primerio canto livre que encontrar.

    recebe:tab,universal
    devolve:posicao,inteiro
    """
    cantos=[1,3,7,9]
    for posicao in cantos:
        if eh_posicao_livre(tab,posicao):
            return posicao


def criterio_8(tab):
    """criterio_8:
    Recebe um tabuleiro e se as posicoes laterais estiverem livres devolve a primeira posicao lateral livre que
    encontrar.

    recebe:tab,universal
    devolve:posicao,inteiro
    """
    laterais=[2,4,6,8]
    for posicao in laterais:
        if eh_posicao_livre(tab,posicao):
            return posicao


def criterio_1(tab,inteiro):
    """criterio_1:
    Recebe um tabuleiro e um inteiro correspondente ao jogador e verifica se o jogador tiver duas pecas juntas deve
    colocar a proxima peca de forma a obter vitoria e devolve essa posicao.

    recebe:tab e inteiro,universal
    devolve:posicao,inteiro
    """
    posicoes=obter_posicoes_livres(tab)
    for posicao in posicoes:
        if jogador_ganhador(marcar_posicao(tab,inteiro,posicao))== 1 or jogador_ganhador(marcar_posicao(tab,inteiro,posicao))== -1:
            return posicao


def criterio_2(tab,inteiro):
    """criterio_2:
    Recebe um tabuleiro e inteiro correspondente ao jogador e verifica se o jogador tiver duas pecas juntas deve colucar
    a proxima peca de forma a bloquear a vitoria do adversario e devolve essa posicao.

    recebe:tab e inteiro,universal
    devolve:posicao,inteiro
    """
    return criterio_1(tab,inteiro*-1)


def criterio_6(tab,inteiro):
    """criterio_6:
    Recebe um tabuleiro e um inteiro correspondente ao jogador e verifica se o jogador jogou num canto o adversario joga
    automaticamente no canto oposto e devolve essa posicao.

    recebe:tab e inteiro,universal
    devolve:posicao,inteiro
    """
    for posicao_diagonal in range(1,3):
        if obter_diagonal(tab,posicao_diagonal)[0]==-1*inteiro and obter_diagonal(tab,posicao_diagonal)[2]==0:
            if posicao_diagonal==1:
                return 9
            elif posicao_diagonal==2:
                return 3
        elif obter_diagonal(tab,posicao_diagonal)[2]==-1*inteiro and obter_diagonal(tab,posicao_diagonal)[0]==0:
            if posicao_diagonal==1:
                return 1
            elif posicao_diagonal==2:
                return 7




def basico(tab):
    """basico:
    Recebe um tabuleiro e devolve a dificuldade basica do cpu

    recebe:tab,universal
    devolve:um estilo de jogo
    """
    if criterio_5(tab) != None:
        return 5
    elif criterio_7(tab) != None:
        return criterio_7(tab)
    elif criterio_8(tab) != None:
        return criterio_8(tab)


def normal(tab,inteiro):
    """basico:
    Recebe um tabuleiro e devolve a dificuldade normal do cpu

    recebe:tab,universal
    devolve:um estilo de jogo
    """
    if criterio_1(tab, inteiro) != None:
        return criterio_1(tab, inteiro)
    elif criterio_2(tab, inteiro) != None:
        return criterio_2(tab, inteiro)
    elif criterio_5(tab) != None:
        return 5
    elif criterio_6(tab, inteiro) != None:
        return criterio_6(tab, inteiro)
    elif criterio_7(tab) != None:
        return criterio_7(tab)
    elif criterio_8(tab) != None:
        return criterio_8(tab)


def perfeito(tab,inteiro):
    """basico:
    Recebe um tabuleiro e devolve a dificuldade basica do cpu

    recebe:tab,universal
    devolve:um estilo de jogo
    """
    if criterio_1(tab, inteiro) != None:
        return criterio_1(tab, inteiro)
    elif criterio_2(tab, inteiro) != None:
        return criterio_2(tab, inteiro)
    elif criterio_5(tab) != None:
        return 5
    elif criterio_6(tab, inteiro) != None:
        return criterio_6(tab, inteiro)
    elif criterio_7(tab) != None:
        return criterio_7(tab)
    elif criterio_8(tab) != None:
        return criterio_8(tab)


def escolher_posicao_auto(tab,inteiro,dificuldade):
    """escolher_posicao_auto:
    Recebe um tabuleiro, um inteiro correspondente ao jogador e a dificuldade correspondente ao nivel que o cpu vai
    jogar e devolve a posicao certa onde o cpu deve jogar.

    recebe:tab,inteiro,dificuldade, universal
    devolve:posicao,inteiro
    """
    if not(eh_tabuleiro(tab)) or type(inteiro)!=int or inteiro>1 or inteiro<-1 or inteiro==0 or type(dificuldade)!=str or dificuldade not in ("basico","normal","perfeito"):
        raise ValueError("escolher_posicao_auto: algum dos argumentos e invalido")
    if dificuldade=="basico":
        if basico(tab):
            return basico(tab)
    elif dificuldade=="normal":
        if normal(tab,inteiro):
            return normal(tab,inteiro)
    elif dificuldade=="perfeito":
        if perfeito(tab,inteiro):
            return perfeito(tab,inteiro)


def jogo_do_galo(jogador,dificuldade):
    """jogo_do_galo:
    Recebe um jogador e tambem a dificuldade e consequentemente devolve um cadeia de caracteres com as respetivas
    posicoes inseridas.

    recebe:jogador,dificuldade, string
    devolve:cadeia de caracteres, string
    """
    if type(jogador)!= str or type(dificuldade)!= str:
        raise ValueError("jogo_do_galo: algum dos argumentos e invalido")
    print("Bem-vindo ao JOGO DO GALO.")
    print("O jogador joga com "+repr(jogador)+".")
    tab= ((0,0,0),(0,0,0),(0,0,0))
    while obter_posicoes_livres(tab)!=():
        if jogador_ganhador(tab)==0:
            if jogador=="X":
                posicao=escolher_posicao_manual(tab)
                tab = marcar_posicao(tab,1,posicao)
                print(tabuleiro_str(tab))
                if jogador_ganhador(tab)==0 and obter_posicoes_livres(tab)!=():
                    print("Turno do computador ("+dificuldade+"):")
                    posicao=escolher_posicao_auto(tab,-1,dificuldade)
                    tab= marcar_posicao(tab,-1,posicao)
                    print(tabuleiro_str(tab))

            elif jogador== "O":
                print("Turno do computador (" + dificuldade + "):")
                posicao = escolher_posicao_auto(tab, 1, dificuldade)
                tab = marcar_posicao(tab, 1, posicao)
                print(tabuleiro_str(tab))
                if jogador_ganhador(tab) == 0 and obter_posicoes_livres(tab) != ():
                    posicao = escolher_posicao_manual(tab)
                    tab = marcar_posicao(tab, -1, posicao)
                    print(tabuleiro_str(tab))

        elif jogador_ganhador(tab) == 1:
            return "X"
        elif jogador_ganhador(tab) == -1:
            return "O"
    if jogador_ganhador(tab)==1:
        return "X"
    elif jogador_ganhador(tab)==-1:
        return "O"

    return "EMPATE"

print(jogo_do_galo("X","normal"))




