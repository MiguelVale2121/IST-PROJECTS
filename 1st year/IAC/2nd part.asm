; **********************************************************************
; * Grupo 24
; *
; * 99059: Carlota Alves Tracana
; * 99068: Francisco Martins
; * 99078: Guilherme Carabalone
; * 99113: Miguel Vale 
; **********************************************************************

;displays
DISPLAYS   EQU 0A000H  			; endereço dos displays de 7 segmentos (periférico POUT-1)

;teclado
TEC_LIN    EQU 0C000H  			; endereço das linhas do teclado (periférico POUT-2)
TEC_COL    EQU 0E000H  			; endereço das colunas do teclado (periférico PIN)
ULTIMA_LINHA EQU 8H				; indica a última linha do teclado (dependendo do teclado podemos alterar aqui)
VAR_LOOP_SIMPLES EQU 5H		; controla o ritmo do incremento/decremento contínuo

NAVE_DISPARA_MISSIL        EQU 6
; #########
ESTADO_DISPARA_MISSIL             EQU 0            ; Estado do processo dispara_missil
ESTADO_MOVER_MISSIL                EQU 1            ; Estado do processo dispara_missil
ESTADO_LIMITE_MISSIL            EQU 2             ; Estado do processo dispara_missil
DISTANCIA_MAX                     EQU 0CH            ; distancia máxima que o míssil pode ficar da nave (12 em decimal)
; teclado
ESTADO_ESPERA_TECLA EQU 0        ; Estado do processo teclado
ESTADO_HA_TECLA     EQU 1        ; Estado do processo teclado
DISPARA_MISSIL_TECLA     EQU 0DH

;estados dos processos
;estados processo nave
ENERGIA_PERIODICA   	 	EQU  0  		; Estado do processo nave
MOVER_DIREITA_NAVE    		EQU  1      	; Estado do processo nave
MOVER_ESQUERDA_NAVE    		EQU  2   		; Estado do processo nave
;estados processo ovni
MOVER_ONDE_OVNI				EQU 0			; Estado do processo ovni
COLIDE_OVNI_MISSIL			EQU 1			; Estado do processo ovni
;estados processo teclado
INCREMENTO_POR_CLIQUE_T  	EQU 3    		; Estado do processo teclado
DECREMENTO_POR_CLIQUE_T  	EQU 4			; Estado do processo teclado
HA_TECLA_T					EQU 5			; Estado do processo teclado
;estados processo controlo
Estado_inicial 	EQU 0
INICIAR 		EQU 1
PAUSA			EQU 2
FIM_JOGO  		EQU 3

;ovnis
; nº de pixeis que move de cada vezes
N_PIXEIS EQU 2
; movimentos ovni
NAO_MOVER_OVNI              EQU 0
MOVER_BAIXO_OVNI			EQU 1
MOVER_DIAGONAL_DIR_OVNI		EQU 2
MOVER_DIAGONAL_ESQ_OVNI		EQU 3

; que tecla faz que funcionalidade (pode-se facilmente alterar)
; 0 reinicilaiza todas as variáveis
Tecla_inicio 	EQU 0000H
Tecla_pausa 	EQU 0001H
Tecla_fim_jogo  EQU 0002H
MOVER_DIREITA EQU 0EH       
INCREMENTO_POR_CLIQUE EQU 0BH
MOVER_ESQUERDA EQU 0CH
DECREMENTO_POR_CLIQUE EQU 0FH

;media center
APAGAR_PIXEL_ECRA EQU 6002H
DEFINE_ECRA 	EQU 6004H	   ; endereço do comando para definir o nº do ecrã em que se escreve 
DEFINE_LINHA    EQU 600AH      ; endereço do comando para definir a linha
DEFINE_COLUNA   EQU 600CH      ; endereço do comando para definir a coluna
DEFINE_PIXEL    EQU 6012H      ; endereço do comando para escrever um pixel
Adicionar_fundo			EQU 6042H
Adicionar_video_som 	EQU 605AH 
Adicionar_video_loop	EQU 605CH
Selecionar_video_som    EQU 6048H
Termina_video           EQU 6066H
ECRA_OVNI 		EQU 1
ECRA_MISSIL		EQU 2
ECRA_NAVE		EQU 3
; ligeiramente após linha máxima do media center
LINHA_MAX_MEDIA_CENTER EQU 20H	; 32
; coluna média do media center
COLUNA_MEDIA_CENTER EQU 20H		; 32
Imagem_0 				EQU 0
Imagem_1				EQU 1
Imagem_2				EQU 2

Video_0					EQU 0
Som_0					EQU 1

	
; cores
azul_e EQU 0F369H
azul_c EQU 0F9BEH
vermelho EQU 0FF00H
vermelho_2 EQU 3F00H  
vermelho_3 EQU 1F00H
preto EQU 0F000H
amarelo EQU 0FFF0H
verde EQU 0F0C5H
    

; *********************************************************************************
; * Dados 
; *********************************************************************************
PLACE     1000H
pilha:    TABLE 100H          ; espaço reservado para a pilha 
                              ; (200H bytes, pois são 100H words)
SP_inicial:                   ; este é o endereço (1200H) com que o SP deve ser 
                              ; inicializado. O 1.º end. de retorno será 
                              ; armazenado em 11FEH (1200H-2)
                              
; Tabela das rotinas de interrupção
tab:	
	WORD rot_int_0      	; rotina de atendimento da interrupção energia
	WORD rot_int_2			; rotina de atendimento da interrupção ovnis
	WORD rot_int_1
	
; eventos
evento_int:					; eventos associados às interrupções
    WORD 0              	; se 1, indica que a interrupção 0 ocorreu
    WORD 0              	; se 1, indica que a interrupção 1 ocorreu
    WORD 0              	; se 1, indica que a interrupção 2 ocorreu
		  
; teclado
TECLA_ATUAL:
	WORD 10H						; inicializa a tecla a 10H, ou seja sem tecla
LINHA_ATUAL:
	WORD 1H

contador_atraso:
	WORD VAR_LOOP_SIMPLES

;display
valor_displays:
	WORD 64H

;Processo controlo

variavel_inicial:
	WORD Estado_inicial

; missil
PODE_DISPARAR:
    WORD 0H             ; valor que vai ser atualizado pelo processo nave quando o processo míssil puder dispará-lo
estado_missil:
    WORD ESTADO_DISPARA_MISSIL
LINHA_MISSIL:
    WORD 0H                ; valor arbitrário que vai ser trocado depois no processo do míssil
COLUNA_MISSIL:
    WORD 0H                ; valor arbitrário que vai ser trocado depois no processo do míssil
; )
; *
; * MÍSSIL (2x4)
; *
H_MISSIL EQU 1H
LA_MISSIL EQU 1H

DISTANCIA:
    WORD 0H

MISSIL:

    ; 1 linha
    WORD vermelho

; ##########################################################################

; nave
estado_nave:
	WORD ENERGIA_PERIODICA	
;desenho personagem nave
LA_NAVE			EQU 5H      	  ; porque a nave é 5x5 não é preciso definir separadamente largura e altura (têm o mesmo valor)
LINHA_NAVE:
    WORD 1BH          ; linha em que o pixel vai ser desenhado (26 em decimal)
COLUNA_NAVE:
    WORD 1CH          ; coluna em que o pixel vai ser desenhado (31 em decimal)
NAVE:  
	; 1ª linha
	WORD  0000H	  ; cor dos pixeis que fazem a nave   
	WORD  0000H 
	WORD  azul_e     
	WORD  0000H      
	WORD  0000H
	; 2ª linha
	WORD  0000H     
	WORD  0000H 
	WORD  azul_e    
	WORD  0000H      
	WORD  0000H
	; 3ª linha
	WORD  0000H     
	WORD  azul_c
	WORD  azul_e    
	WORD  azul_c     
	WORD  0000H
	; 4ª linha
	WORD  azul_c     
	WORD  azul_c
	WORD  azul_e   
	WORD  azul_c     
	WORD  azul_c	
	; 5ª linha
	WORD  vermelho_3    
	WORD  vermelho
	WORD  vermelho_2   
	WORD  vermelho
	WORD  vermelho_3	

; ovnis
;variável do gerador que determina o movimento do ovni
contador_gerador:
	WORD 0			; gerador aleatório do movimento do ovni
	WORD 0			; gerador aleatório do tipo do ovni

tipo_gerador:
	WORD tabela_presente
	
movimento_gerador:
	WORD NAO_MOVER_OVNI

estado_processo_ovni:
	WORD MOVER_ONDE_OVNI
tipo_ovni:
	WORD tabela_presente
	WORD tabela_presente
	WORD tabela_presente
	WORD tabela_presente
movimento_ovni:
	WORD NAO_MOVER_OVNI
	WORD NAO_MOVER_OVNI
	WORD NAO_MOVER_OVNI
	WORD NAO_MOVER_OVNI
linha_ovni:
	WORD 20H				; assume que o ovni começa fora do ecrã
	WORD 20H				; assume que o ovni começa fora do ecrã
	WORD 20H				; assume que o ovni começa fora do ecrã
	WORD 20H				; assume que o ovni começa fora do ecrã
coluna_ovni:
	WORD COLUNA_MEDIA_CENTER				; assume que o ovni começa a meio do ecrã
	WORD COLUNA_MEDIA_CENTER				; assume que o ovni começa a meio do ecrã
	WORD COLUNA_MEDIA_CENTER				; assume que o ovni começa a meio do ecrã
	WORD COLUNA_MEDIA_CENTER				; assume que o ovni começa a meio do ecrã
	
tabela_presente:
	WORD OVNI_PEQUENO
	WORD OVNI_MEDIO_PRETO
	WORD OVNI_PEQUENO_PRESENTE
	WORD OVNI_MEDIO_PRESENTE
	WORD OVNI_PRESENTE
	
tabela_pacman:	
	WORD OVNI_PEQUENO
	WORD OVNI_MEDIO_PRETO
	WORD OVNI_PAC_MAN_PEQUENO
	WORD OVNI_PAC_MAN_MEDIO
	WORD OVNI_PAC_MAN
	
;desenho personagem ovnis	
;********************************************************************************
;********************************************************************************
; Ovni pequeno (1x1)

OVNI_PEQUENO:
LA_OVNI_PEQUENO_PRETO:
	WORD 1H 
	WORD 1H
; desenho
	WORD preto

;********************************************************************************
;********************************************************************************
; Ovni médio preto (2x2)
OVNI_MEDIO_PRETO: 
LA_OVNI_MEDIO_PRETO:
    WORD 2H
	WORD 2H 
; desenho
	; 1ª linha
	WORD preto
	WORD preto
    ; 2ª linha
	WORD preto
	WORD preto
	
;********************************************************************************
;********************************************************************************
; Ovni pequeno presente (2x2) (vermelho)
OVNI_PEQUENO_PRESENTE:
LA_OVNI_PEQUENO_PRESENTE:
	WORD 2H
	WORD 2H 
; desenho
	; 1ª linha
	WORD vermelho
	WORD vermelho
    ; 2ª linha
	WORD vermelho
	WORD vermelho
	
;********************************************************************************
;********************************************************************************
; OVNI MEDIO PRESENTE (3X3)
OVNI_MEDIO_PRESENTE:
LA_OVNI_MEDIO_PRESENTE:
	WORD 3H
	WORD 3H 
; desenho
	; 1 linha
	WORD vermelho
	WORD verde
	WORD vermelho
	; 2 linha
	WORD verde
	WORD verde
	WORD verde
	; 3 linha
	WORD vermelho
	WORD verde
	WORD vermelho

;********************************************************************************
;********************************************************************************
; OVNI PRESENTE (5X8) 
OVNI_PRESENTE:
LA_OVNI_PRESENTE:
	WORD 5H
	WORD 8H 	
;desenho
	; 1 linha
	WORD  0000H
	WORD verde
	WORD  0000H
	WORD verde
	WORD  0000H
	; 2 linha
	WORD verde
	WORD  0000H
	WORD verde
	WORD  0000H
	WORD verde
	; 3 linha
	WORD  0000H
	WORD verde
	WORD verde
	WORD verde
	WORD  0000H
	; 4 linha
	WORD vermelho
	WORD vermelho
	WORD verde
	WORD vermelho
	WORD vermelho
	; 5 linha
	WORD vermelho
	WORD vermelho
	WORD verde
	WORD vermelho
	WORD vermelho
	; 6 linha 
	WORD verde
	WORD verde
	WORD verde
	WORD verde
	WORD verde
	; 7 linha 
	WORD vermelho
	WORD vermelho
	WORD verde
	WORD vermelho
	WORD vermelho
	; 8 linha
	WORD vermelho
	WORD vermelho
	WORD verde
	WORD vermelho
	WORD vermelho
                        
;********************************************************************************
;********************************************************************************
; OVNI PAC MAN PEQUENO (3x3)
OVNI_PAC_MAN_PEQUENO:
LA_OVNI_PAC_MAN_PEQUENO:
	WORD 3H
	WORD 3H 
; desenho
	; 1 linha
	WORD amarelo
	WORD amarelo
	WORD amarelo
	; 2 linha
	WORD amarelo
	WORD amarelo
	WORD amarelo
	; 3 linha
	WORD amarelo
	WORD 0000H
	WORD amarelo

;********************************************************************************
;********************************************************************************
; OVNI PAC MAN MEDIO (5x5)
OVNI_PAC_MAN_MEDIO:
LA_OVNI_PAC_MAN_MEDIO:
	WORD 5H
	WORD 5H 
; desenho
	; 1 linha
	WORD 0000H
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD 0000H
	; 2 linha
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	; 3 linha
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD preto
	WORD amarelo
	; 4 linha
	WORD amarelo
	WORD amarelo
	WORD 0000H
	WORD amarelo
	WORD amarelo
	; 5 linha
	WORD amarelo
	WORD 0000H
	WORD 0000H
	WORD 0000H
	WORD amarelo

;********************************************************************************
;********************************************************************************
; OVNI PAC MAN (7x7)
OVNI_PAC_MAN:
LA_OVNI_PAC_MAN:
	WORD 7H
	WORD 7H 	
; desenho
	; 1 linha
	WORD 0000H
	WORD 0000H
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD 0000H
	WORD 0000H
	; 2 linha
	WORD 0000H
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD 0000H
	; 3 linha
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	; 4 linha
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD preto
	WORD amarelo
	; 5 linha
	WORD amarelo
	WORD amarelo
	WORD amarelo
	WORD 0000H
	WORD amarelo
	WORD amarelo
	WORD amarelo
	; 6 linha
	WORD amarelo
	WORD amarelo
	WORD 0000H
	WORD 0000H
	WORD 0000H
	WORD amarelo
	WORD amarelo
	; 7 linha
	WORD 0000H
	WORD amarelo
	WORD 0000H
	WORD 0000H
	WORD 0000H
	WORD amarelo
	WORD 0000H
	
; *********************************************************************************
; * Código
; *********************************************************************************
; corpo principal do programa
PLACE   0	                    ; o código tem de começar em 0000H
inicio:
    MOV BTE, tab            	; inicializa BTE (registo de Base da Tabela de Exceções)
    MOV SP, SP_inicial      	; inicializa SP 
	MOV R8, valor_displays      ; inicializa, em hexadecimal, o registo que é usado na função de escrever no display
	MOV R8, [R8]
	CALL escrever_display
	CALL desenhar_nave	
	pre_jogo:
	CMP R9,Estado_inicial			; estamos no pré-jogo?
	JNE rotina_jogo_iniciar			; se não, vamos para iniciar o jogo 
	CALL rotina_fundo_inicial		; chama a rotina que faz com que aparece a imagem do pré-jogo
	MOV R9,INICIAR
	MOV [R8],R9						; atualiza a variável de estado
	JMP sair_rotina

	EI0                      	; permite interrupções 0
	EI1							; permite interrupções 1
    EI                       	; permite interrupções (geral)
	
; ciclo dos processos
ciclo:	
    CALL teclado             ; verifica se alguma tecla foi carregada
	CALL processo_nave       ; executa as funções da nave
	CALL proceso_missil
	CALL processo_ovni       ; executa as funções do ovni
	CALL processo_gerador	 ; executa as funções do gerador
	CALL processo_controlo	 ; executa as funções do controlo
    JMP  ciclo
	
; **********************************************************************
; Processos 
; **********************************************************************

; **********************************************************************
; TECLADO - Processo que deteta quando se carrega numa tecla do teclado
; e varre continuamente o teclado 
; **********************************************************************
teclado:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4	
	PUSH R5
	PUSH R7
	MOV R2, TEC_LIN   	  		; endereço do periférico das linhas
    MOV R3, TEC_COL   	  		; endereço do periférico das colunas
	MOV R4, LINHA_ATUAL
	MOV R5, TECLA_ATUAL
	MOV R1, 10H					
	MOV [R5], R1				; assume que nehuma tecla está premida (tecla 10H não existe mas dependendo do tamanho do teclado poder-se-ia escolher outro valor facilmente)
	MOV R1, 1           		; inicialização da linha

espera_tecla:          			; neste ciclo espera-se até uma tecla ser premida enquanto varre o teclado
	MOVB [R2], R1      			; escreve no periférico de saída (linhas)
    MOVB R0, [R3]      			; ler do periférico de entrada (colunas)
    CMP  R0, 0         			; há tecla premida?
	JZ   muda_linha             ; se nenhuma tecla premida, muda para a próxima linha
	
    MOV [R4], R1                 					; faz uma cópia da linha para usar mais tarde em ha_tecla
    CALL conversao_linhas_colunas_em_hexadecimal	; executa esta rotina para descobrir o valor da tecla premida
	MOV [R5], R1									; atualiza o valor da linha atual
	JMP sai_teclado

muda_linha:                     ; enquanto nehuma tecla é premida continuar a varrer uma nova linha
    MOV R7, ULTIMA_LINHA        ; valor da última linha (neste caso é 8)
	CMP R1, R7        			; não permite que passe da linha 3 (verifica se a próxima linha existe)
	JGT sai_teclado              
	SHL R1, 1                  ; como é inferior ou igual a 8 muda para a próxima linha
	JMP espera_tecla            ; volta a procurar se alguma tecla foi premida na linha
sai_teclado:
	POP R7
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0
	RET
	
; **********************************************************************
; PROCESSO_NAVE - Processo que altera o estado da nave e realiza ações de acordo 
; com que tecla foi premida
; **********************************************************************	
processo_nave:
	PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
	PUSH R4
	PUSH R5
	PUSH R6
	MOV R0, estado_nave
	MOV R2, [R0]						; obtém o estado atual do processo_nave
	MOV R3, TECLA_ATUAL
	MOV R1, [R3]						; obtém em R1 a tecla premida

nave_energia_periodica:				    ; periodicamente de acordo com a interrupção
	CMP  R2, ENERGIA_PERIODICA
    JNE  nave_mover_direita
; executa estado ENERGIA_PERIODICA
	CALL altera_display		 			 ; altera periodicamente a energia da nave
	MOV  R2, MOVER_DIREITA_NAVE          ; muda para o próximo estado
    MOV  [R0], R2                     	 ; atualiza a variável de estado
    JMP sai_processo_nave	
	

nave_mover_direita:	
	CMP  R2, MOVER_DIREITA_NAVE
    JNE  nave_mover_esquerda
; executa estado MOVER_DIREITA_NAVE	
	MOV R6, MOVER_DIREITA			                
	CMP R1, R6
	JNE sai_nave_mover_direita
	CALL desenhar_nave       
sai_nave_mover_direita:	
	MOV  R2, MOVER_ESQUERDA_NAVE          ; muda para o próximo estado
    MOV  [R0], R2                     	  ; atualiza a variável de estado 
	JMP sai_processo_nave				  ; na próxima execução já irá para o novo estado 
		

nave_mover_esquerda:
	CMP  R2, MOVER_ESQUERDA_NAVE
    JNE  sai_nave_mover_esquerda
; executa estado MOVER_ESQUERDA_NAVE	
	MOV R6, MOVER_ESQUERDA		                
	CMP R1, R6
	JNE sai_nave_mover_esquerda
	CALL desenhar_nave       
sai_nave_mover_esquerda:	
	MOV  R2, ENERGIA_PERIODICA            ; muda para o próximo estado
    MOV  [R0], R2                     	  ; atualiza a variável de estado 
	JMP sai_processo_nave				  ; na próxima execução já irá para o novo estado 

sai_processo_nave:	 	
	POP R6
	POP R5
	POP R4
    POP R3
    POP R2
    POP R1
    POP R0
	RET
	
; **
; * Rotina: desenha o míssil numa dada posição
; * Entradas:
; * Saídas: 
; **

desenhar_missil:
    PUSH R0
    PUSH R1
    PUSH R2
    PUSH R4
    PUSH R7
    PUSH R8
    PUSH R9
    MOV R0, LINHA_MISSIL
    MOV R1, [R0]        ; define a linha inicial do míssil
    MOV R0, COLUNA_MISSIL
    MOV R2, [R0]        ; define a coluna inicial do míssil
    MOV R7, LA_MISSIL    ; dimensões (comprimento do míssil)
    ADD R7, R1             ; define linha máx do missil
    MOV R8, H_MISSIL    ; dimensões (linha do míssil)
    CALL rotina_mover_missil
fim_desenhar_missil:
    MOV R0, LINHA_MISSIL    ; atualiza a linha do míssil (não é preciso atualizar a coluna pois ela é sempre a mesma)
    MOV [R0], R1
    MOV R7, H_MISSIL
    ADD R7, R1                ; nova linha máxima do míssil
    MOV R4, MISSIL            ; objeto a ser desenhado
    CALL desenhar_objeto
sai_desenhar_missil:
    POP R9
    POP R8
    POP R7
    POP R4
    POP R2
    POP R1
    POP R0
    RET

; ***
; * Rotina: move a posição do míssil no media center e apaga o míssil corrente
; * Entradas: --
; * Saídas: linha do míssil com a coordenada nova
; **

rotina_mover_missil:
     PUSH R3
     MOV R0, estado_missil
     MOV R3, [R0]
     CMP R3, ESTADO_MOVER_MISSIL        ; se o estado for o de mover o míssil, então a rotina subtrai 1 a linha do míssil (move o míssil para cima)
     JNE sair_mover_missil                ; caso não simplesmente retorna sem alterar a posição

mover_missil_baixo:
    SUB R1, 1                            ; atualização da posição do míssil

sair_mover_missil:
    CALL apagar_objeto                    ; apaga o objeto anterior para que na próxima vez seja desenhado na nova posição
    POP R3
    RET

	
; **********************************************************************
; PROCESSO_OVNI - Processo que altera o estado do OVNI e realiza ações 
; de acordo com o estado do jogo
; **********************************************************************	
processo_ovni:
	PUSH R0
    PUSH R1
    PUSH R2
    PUSH R3
	PUSH R4
	PUSH R5
	PUSH R6
	PUSH R7
	PUSH R8
	PUSH R11
	MOV R0, estado_processo_ovni
	MOV R2, [R0]						; obtém o estado atual do processo_ovni


ovni_mover:	
	CMP  R2, MOVER_ONDE_OVNI
    JNE  sai_processo_ovni
; executa estado MOVER_ONDE_OVNI 
    MOV  R5, evento_int
    MOV  R2, [R5+2]    	    				; valor da variável que diz se houve uma interrupção de ovni
    CMP  R2, 0
    JZ   sai_ovni_mover	    				; se não houve interrupção, vai-se embora
    MOV  R2, 0
    MOV  [R5+2], R2     	    			; coloca a zero o valor da variável que diz se houve uma interrupção (consome evento)
	MOV R3, 0
ciclo_mover_ovnis:
	MOV R11, linha_ovni
	MOV R7, [R11+R3]
	MOV R2, LINHA_MAX_MEDIA_CENTER
	CMP R7, R2								; verifica se existe o ovni no ecrã
	JLE ovni_correntemente_no_ecra
	MOV R7, 0								; vai desenhar na primeira linha do media center
	MOV [R11+R3], R7						; escreve na linha correspondente ao nº do ovni indicado por R3
	MOV R11, coluna_ovni
	MOV R7, COLUNA_MEDIA_CENTER				; vai desenhar no centro do media center
	MOV [R11+R3], R7
	MOV R11, tipo_ovni
	MOV R7, tipo_gerador					; seleciona um tipo de ovni aleatório
	MOV [R11+R3], R7
	MOV R11, movimento_ovni					
	MOV R7, movimento_gerador				; seleciona um movimento de ovni aleatório
	MOV [R11+R3], R7
ovni_correntemente_no_ecra:		
	CALL desenhar_ovni
	ADD R3, 2								; pois as words têm dois bytes
	CMP R3, 6								; pois são 4 ovnis e as words têm dois bytes e começa do 0
	JLE ciclo_mover_ovnis
sai_ovni_mover:	
	MOV  R2, COLIDE_OVNI_MISSIL			    ; muda para o próximo estado
    MOV  [R0], R2                        	; atualiza a variável de estado 
	JMP sai_processo_ovni				 	; na próxima execução já irá para o novo estado 


deteta_colisao_missil:
	CMP  R2, COLIDE_OVNI_MISSIL
    JNE  sai_processo_ovni
; executa estado COLIDE_OVNI_MISSIL
	MOV R3, MISSIL							; regista a posicao atual do missil
	MOV R1, [R3]							; guarda no registo o valor da variavel posicao_missil
	MOV R3, SELECIONA_PIXEL					; seleciona o pixel a desenhar
	MOV [R3], R1 							; coloca a posicao do missil no local do pixel
	MOV R3, 0                           	; seleciona o ecra 0
	MOV R1, SELECIONA_ECRA					; seleciona o endereço do ecra 
	MOV [R1], R3 							; coloca o ecra 0 no ecra 
	MOV R1, ESTADO_PIXEL 					; regista o estado onde se encontra o pixel
	MOV R3, [R1]                         	; coloca o valor do estado do pixel no ecra selecionado
	CMP R3, 0                            	; compara se o valor do estado do pixel e se for igual a 0 nao existe colisao
	JZ nao_colisao
	MOV R3, houve_colisao                	; seleciona a word com o valor de houve_colisao
	MOV R1, 0                            	; quando e zero existe colisao 
	MOV [R3], R1                         
nao_colisao:
	MOV R3, houve_colisao
	MOV R1, 1
	MOV [R3], R1
sai_deteta_colisao_missil:
	MOV  R2, MOVER_ONDE_OVNI			    ; muda para o próximo estado
    MOV  [R0], R2                        	; atualiza a variável de estado 
	JMP sai_processo_ovni				 	; na próxima execução já irá para o novo estado 


sai_processo_ovni:	 		
	POP R11
	POP R8
	POP R7
	POP R6
	POP R5
	POP R4
    POP R3
    POP R2
    POP R1
    POP R0
	RET	

; **********************************************************************
; PROCESSO_GERADOR - Processo que gera um numero aleatorio que seleciona 
; o movimento do ovni e o tipo do ovni
; **********************************************************************	
processo_gerador:
	PUSH R6
	MOV R6, contador_gerador
	CALL escolha_tipo
	CALL escolha_movimento
	POP R6
	RET
	
; **********************************************************************
; PROCESSO_CONTROLO - Processo que controla o correr do jogo
; **********************************************************************
rotina_jogo_controlo:
	PUSH R7
	PUSH R8
	PUSH R9
	PUSH R10
	PUSH R11
	MOV R8,variavel_inicial			; obtém o estado atual da variavel_inicial
	MOV R9,[R8]
	MOV R10,TECLA_ATUAL
	MOV R11,[R10]				; obtém em R11 a tecla premida

rotina_jogo_iniciar:
	CMP R9,INICIAR				; estamos no jogo?
	JNE rotina_jogo_pausar		; se não, o jogo está em pausa?
	MOV R7,Tecla_inicio 
	CMP R11,R7				; foi premida a tecla do inicio do jogo?
	JNE sair_rotina_jogo_iniciar		; se não, vai atualizar a variável de estado
	CALL rotina_fundo_video_jogo		; se foi premida a tecla do inicio do jogo aparece o fundo do jogo

sair_rotina_jogo_iniciar:
	MOV R9,PAUSA
	MOV [R8],R9				; atualiza a variável de estado
	JMP sair_rotina


rotina_jogo_pausar:
	CMP R9,PAUSA				; estamos em pausa?
	JNE rotina_jogo_game_over		; se não, será que foi game over?
	MOV R7,Tecla_pausa			
	CMP R11,R7				; foi premida a tecla de pausa?
	JNE sair_rotina_pausa			; se não, vai atualizar a variável de estado
	DI0					; durante a pausa ocorre o disable das interrupções todas
	DI
	CALL rotina_fundo_pausa			; se foi premida a tecla de pausa do jogo aparece o fundo da pausa

	JMP sair_pausa

sair_pausa:
	CMP R9,PAUSA				; ainda estamos em pausa?
	JNE rotina_jogo_game_over		; se não, será que foi game over?	
	MOV R7,Tecla_pausa
	CMP R11,R7				; foi premida a tecla de pausa?
	JNE sair_pausa				; se não continua no ciclo até ser premida novamente a tecla de pausa
	EI0					; se foi premida novamente dá enable das interrupções todas
	EI
	CALL rotina_fundo_video_jogo		; volta a aparecer o fundo do jogo
	JMP sair_rotina_jogo_pausa

sair_rotina_jogo_pausa:
	MOV R9,FIM_JOGO
	MOV [R8],R9				; atualiza a variável de estado
	JMP sair_rotina

rotina_jogo_game_over:
	CMP R9,FIM_JOGO						; foi game over?
	JNE sair_rotina_jogo_game_over		; se não, vai atualizar a variável de estado
	CALL rotina_termina_video			; termina o fundo do jogo
	MOV R7,Tecla_fim_jogo
	CMP R11,R7							; foi premida a tecla do fim do jogo? 
	JNE sair_rotina_jogo_game_over		; se não, vai atualizar a variável de estado
	CALL rotina_fundo_game_over			; aparece o fundo do game over

sair_rotina_jogo_game_over:
	MOV R9,Estado_inicial
	MOV [R8],R9				; atualiza a variável de estado
	JMP sair_rotina	

sair_rotina:
	POP R11
	POP R10
	POP R9
	POP R8
	POP R7
	RET
	
; ***********************************************************************
; rotinas auxiliares
; ***********************************************************************

; **********************************************************************
; * escolha_tipo: rotina que seleciona o tipo de ovni de acordo com o valor do gerador
; * Entradas: contador_gerador (R6) 
; * Saídas: Não tem
; **********************************************************************
escolha_tipo:
	PUSH R6
	PUSH R7
	PUSH R8
	MOV R7, tipo_gerador
	MOV R6, [R6+2]				; seleciona o (numero que está no) gerador do tipo de ovni
	MOV R8, 4
	MOD R6, R8					; tem a possibilidade de gerar 4 numeros diferentes (0,1,2 e 3) (25% de probabilidade)
	JZ gera_presente
; gera pacman
	MOV R6, tabela_pacman
	MOV [R7], R6
gera_presente:
	MOV R6, tabela_presente
	MOV [R7], R6
	POP R8
	POP R7
	POP R6
	RET

; **********************************************************************
; * escolha_tipo: rotina que seleciona o movimento de ovni de acordo com o valor do gerador
; * Entradas: contador_gerador (R6) 
; * Saídas: Não tem
; **********************************************************************
escolha_movimento:
	PUSH R5
	PUSH R6
	PUSH R7
	PUSH R8
	MOV R7, movimento_gerador
	MOV R6, [R6]						; seleciona o (numero que está no) gerador do movimento de ovni
	MOV R8, 3
	MOD R6, R8							; gera 3 numeros aleatórios
	CMP R6, 0	
	JEQ mover_ovni_baixo
	CMP R6, 1
	JEQ mover_ovni_diagonal_dir
	CMP R6, 2
	JEQ mover_ovni_diagonal_esq 			
mover_ovni_baixo:				
	MOV R6, MOVER_BAIXO_OVNI
	JMP sai_escolha_movimento		
mover_ovni_diagonal_dir:	
	MOV R6, MOVER_DIAGONAL_DIR_OVNI					    
	JMP sai_escolha_movimento		
mover_ovni_diagonal_esq:		
	MOV R6, MOVER_DIAGONAL_ESQ_OVNI				
	JMP sai_escolha_movimento	
sai_escolha_movimento:
	MOV [R7], R6
	POP R8
	POP R7
	POP R6
	POP R5	
	RET

; **********************************************************************
; * altera_display: rotina que altera periodicamente o valor dos displays 
; * e qunado ocorrem certos eventos (destruir pacmans, se calhar apanhar presentes 
; * mas o jogo até fica melhor se apenas aumentar a energia se se destruir pacmans) 
; * Entradas: Não tem 
; * Saídas: Não tem
; **********************************************************************			
altera_display:
	PUSH R0
    PUSH R1
	PUSH R4
	PUSH R8
	MOV R0, evento_int
	MOV R1, [R0]
    CMP R1, 1 
	JNE sai_altera_display
	MOV R4, valor_displays
	MOV R8, [R4]
	SUB R8, 5	
	CALL escrever_display
	MOV [R4], R8
	MOV R1, 0
	MOV [R0], R1
sai_altera_display:
	POP R8
	POP R4
    POP R1
    POP R0
	RET
	
; **********************************************************************
; * Rotina: passa valor em decimal para o display
; * Entradas: Valor em hexadecimal (R8)  
; * Saídas: Não tem
; **********************************************************************
escrever_display:
	PUSH R4
	PUSH R9
	MOV R4, DISPLAYS  	  		; endereço do periférico dos display
	CALL converter_decimal		; converte para decimal
	MOV [R4], R9			    ; escreve o valor em decimal nos displays
	POP R9
	POP R4
	RET

		
; **********************************************************************
; ROT_INT_0 - Rotina de atendimento da interrupção ovnis
;             Assinala o evento na componente 0 da variável evento_int
; **********************************************************************
rot_int_0:
    PUSH R0
    PUSH R1
    MOV  R0, evento_int
    MOV  R1, 1               ; assinala que houve uma interrupção 0
    MOV  [R0], R1            ; na componente 0 da variável evento_int
    POP  R1
    POP  R0
    RFE                 
	
; **********************************************************************
; ROT_INT_1 - Rotina de atendimento da interrupção ovnis
;             Assinala o evento na componente 1 da variável evento_int
; **********************************************************************
rot_int_2:
     PUSH R0
     PUSH R1
     MOV  R0, evento_int
     MOV  R1, 1               	; assinala que houve uma interrupção 1
     MOV  [R0+2], R1            ; na componente 1 da variável evento_int
     POP  R1
     POP  R0
     RFE	
	 
; **********************************************************************
; * Rotina: converte para o valor da tecla (ou seja, linhas e colunas no hexadecimal correspondente)
; * Entradas: A linha e coluna da tecla premida (R1, R0)  
; * Saídas: O valor em hexadecimal da tecla (R1)
; **********************************************************************	
conversao_linhas_colunas_em_hexadecimal:	
	PUSH R9							; guarda o valor da variável loop_simples
	PUSH R10
	PUSH R0                
	MOV R9, 0               		; coloca o valor do contador das colunas em 0
	MOV R10, 0						; coloca o valor do contador das linhas em 0
converter_linha:            		; função que converte para o número da linha (0,1,2 ou 3)
	SHR R1, 1               		; procura a posição do bit a 1 na linha (anteriormente fizemos a cópia da linha para este registo) 
	CMP R1, 0                		; quando chega a zero para o ciclo
	JZ converter_coluna				; quando para o ciclo salta para a próxima tarefa
	ADD R10, 1           	    	; incrementa o contador que indica, no final, qual é o bit a 1
	JMP converter_linha				; repete o ciclo até o 1 "sair" (tecnicamente passar para a flag carry) 
converter_coluna:	   				; função que converte para o número da coluna (0,1,2 ou 3)
	SHR R0, 1              			; procura a posição do bit a 1 ao contar o número de vezes que se tem de shiftar para a direita até o 1 do resultado "sair" (tecnicamente passar para a flag carry)
	CMP R0, 0              			; quando chega a zero para o ciclo
	JZ converter_hexadecimal		; quando para o ciclo salta para a próxima tarefa
	ADD R9,1               			; incrementa o contador que indica, no final, qual é o bit a 1
	JMP converter_coluna			; repete o ciclo até o 1 "sair" (tecnicamente passar para a flag carry) 
converter_hexadecimal:	 	   		; converte de número da coluna e número da linha para o hexadecimal correspondente à tecla premida
	SHL R10, 2                		; multiplica linhas por 4
	ADD R10, R9                  	; soma o quádruplo das linhas às colunas
	MOV R1, R10						; transfere o valor para o registo que estava a guardar o valor da linha
sai_hex:	
	POP R0
	POP R10
	POP R9							; recupera o valor da variável do loop simples
	RET

; **********************************************************************
; * Rotina: converter número em decimal e passá-lo para o display
; * Entradas: O número que queremos converter e escrever no display (R8)  
; * Saídas: Valor em decimal (R9)
; **********************************************************************
converter_decimal:              
	PUSH R8							; guarda o valor do registo que conta em hexadecimal, para mais tarde
	PUSH R6
	PUSH R10
	PUSH R11
	MOV R9, 0         		    	; inicializa o resultado final em decimal
	MOV R10, 3E8H      		    	; inicializa o factor em hexadecimal (já tinha este valor de 1000 é só para reforçar a ideia)
	MOV R11, 0AH        	    	; constante de valor 10 pela qual se vai obter os dígitos do numero em decimal e dividir o factor
ciclo_dec:            
	MOD R8, R10         		    ; remove o dígito de maior peso
	DIV R10, R11        			; divide o fator por 10 para o referir ao próximo dígito decimal
	MOV R6, R8         		    	; inicializa o dígito 
	DIV R6, R10                  	; sobtém o dígito decimal seguinte
	SHL R9, 4          	    		; move os dígitos decimais um byte para a esquerda para poder acrescentar o próximo dígito na posição vaga (no final ficam todos na posição certa)
	OR  R9, R6           	    	; coloca o último dígito obtido  na casa de menor peso
	CMP R10, 1		  		    	 
	JGT ciclo_dec					; repete o ciclo se o fator ainda não chegou a 1  
sai_dec:
    POP R11
	POP R10
	POP R6
	POP R8							; recupera o valor, em hexadecimal, do display
	RET

; **********************************************************************
; * Rotina: desenha o objeto pretendido (aquele dos quais se vai buscar os argumentos) no media center
; * Entradas: linha inicial(R1), coluna inicial(R2), desenho (R4), dimensões em termos de linha máxima e coluna máxima (R7 e R8)
; * Saídas: Não tem
; **********************************************************************	
desenhar_objeto:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4	
	PUSH R5
	PUSH R6
	PUSH R7
	PUSH R8
	PUSH R9
	MOV R0, DEFINE_LINHA
    MOV [R0], R1           	    ; seleciona a linha 
	MOV R9, R2					; guarda coluna inicial 
	MOV R5, 0					; inicializa o indice
ciclo_desenho:    
	MOV R3, [R4+R5]				; valor do pixel do desenho que vai ser escrito
	CALL escreve_pixel     		; escreve o pixel no ecrã
	ADD R5, 2					; incrementa o indice para poder escrever o próximo pixel
	ADD R2, 1					; incrementa a coluna para escrever à direita do anterior
	CMP R2, R8					; verifica se a coluna passou da largura (coluna máxima) do desenho
	JLT ciclo_desenho			; se não: repete o ciclo, se sim: muda para a linha abaixo
; muda de linha	    
	MOV R2, R9		     		; recupera o valor inicial da coluna para voltar a escrever no inicio da linha
	ADD R1, 1					; escreve na próxima linha
	MOV  R0, DEFINE_LINHA
    MOV  [R0], R1           	; seleciona a linha 
	CMP R1, R7
	JLT ciclo_desenho 
fim_desenho_nave:	
	POP R9
	POP R8
	POP R7
	POP R6
	POP R5  
	POP R4
	POP R3
	POP R2
	POP R1
	RET	

; **********************************************************************
; * Rotina: desenha o objeto pretendido (aquele dos quais se vai buscar os argumentos) no media center
; * Entradas: linha inicial(R1), coluna inicial(R2), dimensões (R7 e R8)
; * Saídas: Não tem
; **********************************************************************
apagar_objeto:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4	
	PUSH R6
	PUSH R7
	PUSH R8
	PUSH R9
	MOV  R0, DEFINE_LINHA
    MOV  [R0], R1           	; seleciona a linha 
	MOV R9, R2					; guarda coluna inicial 
	MOV R3, 0					; inicializa o pixel que vai ser escrito (0 para apagar o objeto)
ciclo_apaga:    
	CALL escreve_pixel     		; escreve o pixel no ecrã
	ADD R2, 1					; incrementa a coluna para escrever à direita do anterior
	CMP R2, R8					; verifica se a coluna passou da largura (coluna máxima) do desenho
	JLT ciclo_apaga				; se não: repete o ciclo, se sim: muda para a linha abaixo
;muda de linha	    
	MOV R2, R9		     		; recupera o valor inicial da coluna para voltar a escrever no inicio da linha
	ADD R1, 1					; escreve na próxima linha
	MOV  R0, DEFINE_LINHA
    MOV  [R0], R1           	; seleciona a linha 
	CMP R1, R7
	JLT ciclo_apaga 
fim_apaga_nave:	
	POP R9
	POP R8
	POP R7
	POP R6 
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0
	RET

; **********************************************************************
; ESCREVE_PIXEL - Rotina que escreve um pixel na linha e coluna indicadas.
; Entradas: linha (R1), coluna (R2), pixel a desenhar (R3), ecrã a desenhar (R6)
;**********************************************************************
escreve_pixel:
    PUSH R0 
	PUSH R7
    MOV R7, 0					 ; vai alterar o gerador do movimento do ovni
	CALL incrementa_gerador
	MOV R7, DEFINE_ECRA
	MOV [R7], R6				; seleciona o ecrã
	MOV R0, DEFINE_COLUNA
    MOV [R0], R2       	    	; seleciona a coluna   
    MOV R0, DEFINE_PIXEL
    MOV [R0], R3      		    ; altera a cor do pixel na linha e coluna selecionadas  
    POP R7
	POP R0
    RET

; **********************************************************************
; * Rotina: desenha a nave numa dada posição e move-a se for preciso
; * Entradas: 
; * Saídas: 
; **********************************************************************
desenhar_nave:	
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R4	
	PUSH R5
	PUSH R6
	PUSH R7
	PUSH R8
	PUSH R9
	MOV R0, LINHA_NAVE
	MOV R1, [R0]          		; define linha inicial da nave
	MOV R0, COLUNA_NAVE			
    MOV R2, [R0]				; define coluna inicial da nave
	MOV R7, LA_NAVE				
	ADD R7, R1					; define linha máxima da nave
	MOV R8, LA_NAVE				
	ADD R8, R2					; define coluna máxima da nave
	MOV R6, ECRA_NAVE
	CALL mover_nave
fim_desenhar_nave:	
	MOV R0, COLUNA_NAVE	
	MOV [R0], R2
	MOV R8, LA_NAVE				
	ADD R8, R2					; define nova coluna máxima da nave
	MOV R4, NAVE				; todos os pixeis que fazem o desenho da nave
	CALL desenhar_objeto
sai_desenhar_nave:	
	POP R9
	POP R8
	POP R7
	POP R6
	POP R5
	POP R4
	POP R2
	POP R1
	POP R0
	RET

; **********************************************************************
; * Rotina: move a posição da nave no media center e apaga a nave corrente
; * Entradas: modo - esquerda ou direita ou nenhum deles
; * Saídas: 
; **********************************************************************
mover_nave:	
	PUSH R3
	PUSH R4
	MOV R0, estado_nave
	MOV R4, [R0]
	CMP R4, MOVER_DIREITA_NAVE					; define o que mover_nave faz (move para a esquerda ou move para a direita, senão apenas desenha a nave na posição indicada)
	JEQ mover_direita
	CMP R4, MOVER_ESQUERDA_NAVE
	JEQ mover_esquerda
	JMP sai_mover_nave						; desenha sem mover nada
mover_esquerda:
	MOV R3, -2
	JMP mover_esquerda_ou_direita
mover_direita:
    MOV R3, 2	
mover_esquerda_ou_direita:
	CALL atraso              				; espera tempo
	CMP  R9, 0               				; tempo de espera chegou ao fim?
    JNZ  sai_mover_nave	
	CALL apagar_objeto
	ADD R2, R3								; atualiza a coluna da nave
sai_mover_nave:
	POP R4 
	POP R3
	RET

; **********************************************************************
; ATRASO - Atrasa iterações de forma não bloqueante.
; Argumentos: Nenhum
; Saídas:     R9 - Se 0, o atraso chegou ao fim
; **********************************************************************
atraso:
    PUSH R2
    PUSH R3
	PUSH R4
	PUSH R5
	PUSH R7
    MOV R7, 2					   ; vai alterar o gerador do tipo do ovni
	CALL incrementa_gerador
	MOV R3, contador_atraso        ; contador, cujo valor vai ser mostrado nos displays
    MOV R9, [R3]                   ; obtém valor do contador do atraso
    SUB R9, 1
    MOV [R3], R9                   ; atualiza valor do contador do atraso
	JNZ sai_atraso
    MOV R2, VAR_LOOP_SIMPLES
    MOV [R3], R2                   ; volta a colocar o valor inicial no contador do atraso
sai_atraso:
	POP R7
    POP R5
	POP R4
    POP R3
    POP R2
    RET	

; **********************************************************************
; * Rotina: desenha o ovni numa dada posição e move-a se for preciso
; * Entradas: nº do ovni (R3), tipo de ovni - pacman ou presente ([tipo_ovni]),  
; * Saídas: 
; **********************************************************************
desenhar_ovni:	
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4	
	PUSH R6
	PUSH R7
	PUSH R8
	PUSH R9
	PUSH R11
	MOV R11, tipo_ovni
	MOV R0, [R11+R3]
	MOV R11, linha_ovni			; define a característica do ovni a que se vai aceder à informação
	MOV R1, [R11+R3]          	; define linha inicial do ovni			
    MOV R11, coluna_ovni		; define a característica do ovni a que se vai aceder à informação
	MOV R2, [R11+R3]			; define coluna inicial do ovni
	CALL select_tamanho_ovni
	ADD R4, R0					; seleciona o tamanho certo
	MOV R0, [R4]
	MOV R7, [R0+2]				; move a altura do desenho (uma das dimensões definidas)		
	ADD R7, R1					; define linha máxima do ovni
	MOV R8, [R0]				; move a largura do desenho (uma das dimensões definidas)
	ADD R8, R2					; define coluna máxima do ovni
	MOV R6, ECRA_OVNI 
	CALL mover_ovni
fim_desenhar_ovni:	
	MOV R11, linha_ovni			; define a característica do ovni a que se vai aceder à informação
	MOV [R11+R3], R1          		; atualiza linha do ovni			
    MOV R11, coluna_ovni		; define a característica do ovni a que se vai aceder à informação
	MOV [R11+R3], R2				; atualiza a coluna do ovni
	CALL select_tamanho_ovni
	MOV R11, tipo_ovni
	MOV R0, [R11+R3]
	ADD R4, R0					; seleciona o tamanho certo
	MOV R0, [R4]
	MOV R7, [R0+2]				; move a altura do desenho (uma das dimensões definidas)		
	ADD R7, R1					; define linha máxima do ovni
	MOV R8, [R0]				; move a largura do desenho (uma das dimensões definidas)
	ADD R8, R2					; define coluna máxima do ovni
	MOV R3, 4
	ADD R3, R0					; seleciona o desenho do ovni
	MOV R4, R3					; todos os pixeis que fazem o desenho do ovni
	CALL desenhar_objeto
sai_desenhar_ovni:	
	POP R11
	POP R9
	POP R8
	POP R7
	POP R6
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0
	RET		

; **********************************************************************
; * Rotina: move a posição do ovni no media center e apaga o ovni corrente
; * Entradas: linha do ovni(R1)
; * Saídas:  valor do offset que se acrescenta ao indice de memória para obter o tamnaho do ovni certo (R4)
; **********************************************************************	
select_tamanho_ovni:
	PUSH R0
	MOV R0, N_PIXEIS				; por acaso N_PIXEIS é dois o que permite truques mas vamos fazer o método geral
	MOV R4, R1						; move a linha do ovni atual para o registo que vai guardar o valor do offset para obter o tamnaho do ovni certo
	DIV R4, R0						; porque começa da linha 0 e anda sempre N_PIXEIS
	CMP R4, 4
	JLE sai_select_tamanho_ovni		; não pode ser superior a 4 pois esse é o numero de tamanhos dos ovnis que existe -1 (pois começa do 0)
	MOV R4, 4						; se a linha for maior é para selecionar o último tamanho
sai_select_tamanho_ovni:
	SHL R4, 1						; multiplica por dois pois são words
	POP R0
	RET

; **********************************************************************
; * Rotina: move a posição do ovni no media center e apaga o ovni corrente
; * Entradas: linha do ovni(R1), coluna do ovni(R2), nº do ovni (R3), modo - baixo ou para uma das diagonal ou nenhum deles([movimento_ovni])
; * Saídas:  novos valores da linha e da coluna (R1 e R2)
; **********************************************************************
mover_ovni:	
	PUSH R0
	PUSH R3
	PUSH R4
	PUSH R5
	MOV R0, movimento_ovni
	MOV R4, [R0]
	MOV R5, N_PIXEIS						; nº de movimentos que o ovni faz por iteração
	MOV R3, 0								; inicializa o valor do movimento relativo (+N_PIXEIS, -N_PIXEIS ou +0)	
	CMP R4, MOVER_BAIXO_OVNI				; define o que mover_ovni faz (move para baixo ou para uma das diagonal senão apenas desenha o ovni na posição indicada)
	JEQ mover_baixo_ovni
	CMP R4, MOVER_DIAGONAL_DIR_OVNI
	JEQ mover_diagonal_dir_ovni
	CMP R4, MOVER_DIAGONAL_ESQ_OVNI
	JEQ mover_diagonal_esq_ovni
	JMP sai_mover_ovni						; desenha sem mover nada
mover_baixo_ovni:							
	JMP mover_baixo_ou_diagonal				; não move para os lados (não vai alterar a coluna subsequentemente)
mover_diagonal_dir_ovni:
	ADD R3, R5								; vai mover para a esquerda (alterar coluna por -2 de onde está)
	JMP mover_baixo_ou_diagonal				
mover_diagonal_esq_ovni:	
	SUB R3, R5								; vai mover para a direita (altera coluna por +2 de onde está)
	JMP mover_baixo_ou_diagonal
mover_baixo_ou_diagonal:
	CALL apagar_objeto
	ADD R2, R3								; atualiza a coluna para mover para os lados (ou possivelmente não mover)	
	ADD R1, R5								; move para baixo, atualizando a linha dois para baixo 	
sai_mover_ovni:
	POP R5
	POP R4 
	POP R3
	POP R0
	RET	

; **********************************************************************
; * Rotina: incrementa o gerador de numeros aleatorios
; * Entradas:  (R7) qual contador incrementar - 0, tipo de nave; 2, movimento da nave
; * Saídas:  alteração na memória do contador_gerador
; **********************************************************************
incrementa_gerador:	 				; é posta em sitios frequentados do código para gerar numeros aleatorios
	PUSH R4
	PUSH R5
	MOV R4, contador_gerador
	MOV R5, [R4+R7]					; para gerar um numero aleatorio que selecione o tipo do ovni
	ADD R5, 1
	MOV [R4+R7], R5
	POP R4 
	POP R5
	RET
	

rotina_game_over:
	MOV R1,APAGAR_PIXEL_ECRA	; guarda o valor do endereco que apaga os pixeis do ecra 
	MOV R0,0					; seleciona o ecra que se quer apagar
	MOV [R1],R0 				; apaga-se os pixeis desejados

	CALL rotina_fundo_game_over
	CALL rotina_jogo_iniciar	

rotina_fundo_inicial:
	PUSH R7
	PUSH R6
	MOV R7,Adicionar_fundo		; seleciona o fundo
	MOV R6,Imagem_0          	; imagem inicial do fundo do jogo
	MOV [R7],R6					; Definir a imagem inicial no fundo do jogo
	POP R6
	POP R7
	RET

rotina_fundo_pausa:
	PUSH R7
	PUSH R6
	MOV R7,Adicionar_fundo		; seleciona o fundo
	MOV R6,Imagem_1          	; imagem da pausa no fundo do jogo
	MOV [R7],R6					; Definir a imagem da pausa no fundo do jogo
	POP R6
	POP R7
	RET

rotina_fundo_game_over:
	PUSH R7
	PUSH R6
	MOV R7,Adicionar_fundo		; seleciona o fundo
	MOV R6,Imagem_2         	; imagem do game over do fundo do jogo
	MOV [R7],R6					; Definir a imagem do game over no fundo do jogo
	POP R6
	POP R7
	RET

rotina_fundo_video_jogo:
	PUSH R7
	PUSH R6
	PUSH R5
	MOV R7,Selecionar_video_som	; seleciona o fundo
	MOV R6,Video_0         		; Video do jogo do fundo do jogo
	MOV [R7],R6					; Definir o video do jogo no fundo do jogo
	MOV R5,Adicionar_video_loop	; Adiciona o video ao background
	MOV [R5],R6
	POP R5
	POP R6
	POP R7
	RET

rotina_som:
	PUSH R7
	PUSH R6
	PUSH R5
	MOV R7,Adicionar_video_som 	; seleciona o som
	MOV R6,Som_0				; Som do tiro
	MOV [R7],R6					; Define o som
	MOV R5,Adicionar_video_som 	; Adiciona o som ao jogo
	MOV [R5],R6
	POP R5
	POP R6
	POP R7
	RET
	
rotina_termina_video:
	PUSH R7
	PUSH R6
	PUSH R5
	MOV R7,Selecionar_video_som	; seleciona o fundo
	MOV R6,Video_0         		; Video do jogo do fundo do jogo
	MOV [R7],R6					; Definir o video do jogo no fundo do jogo
	MOV R5,Termina_video 		; Termina o video
	MOV [R5],R6
	POP R5
	POP R6
	POP R7
	RET

; **********************************************************************
; * Rotina: desenha o míssil numa dada posição
; * Entradas:
; * Saídas: 
; **********************************************************************	

desenhar_missil:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R4	
	PUSH R7
	PUSH R8
	PUSH R9
	MOV R0, LINHA_MISSIL
	MOV R1, [R0]		; define a linha inicial do míssil
	MOV R0, COLUNA_MISSIL
	MOV R2, [R0]		; define a coluna inicial do míssil
	MOV R7, LA_MISSIL	; dimensões (comprimento do míssil)
	ADD R7, R1 			; define linha máx do missil
	MOV R8, H_MISSIL	; dimensões (linha do míssil)
	CALL rotina_mover_missil
fim_desenhar_missil:
	MOV R0, LINHA_MISSIL	; atualiza a linha do míssil (não é preciso atualizar a coluna pois ela é sempre a mesma)
	MOV [R0], R1
	MOV R7, H_MISSIL
	ADD R7, R1				; nova linha máxima do míssil
	MOV R4, MISSIL			; objeto a ser desenhado
	CALL desenhar_objeto
sai_desenhar_missil:
	POP R9
	POP R8
	POP R7
	POP R4
	POP R2
	POP R1
	POP R0
	RET
	
; **********************************************************************
; * Rotina: move a posição do míssil no media center e apaga o míssil corrente
; * Entradas: --
; * Saídas: linha do míssil com a coordenada nova
; **********************************************************************
	
rotina_mover_missil:
	 PUSH R3
	 MOV R0, estado_missil
	 MOV R3, [R0]
	 CMP R3, ESTADO_MOVER_MISSIL		; se o estado for o de mover o míssil, então a rotina subtrai 1 a linha do míssil (move o míssil para cima)
	 JNE sair_mover_missil				; caso não simplesmente retorna sem alterar a posição
	 
mover_missil_baixo:
	SUB R1, 1							; atualização da posição do míssil

sair_mover_missil:
	CALL apagar_objeto					; apaga o objeto anterior para que na próxima vez seja desenhado na nova posição
	POP R3
	RET
	
; **********************************************************************
; ROT_INT_1 - Rotina de atendimento da interrupção 1 (missil)
; **********************************************************************
rot_int_1: 
	PUSH R0
    PUSH R1
    MOV  R0, evento_int
   	MOV  R1, 1               	; assinala que houve uma interrupção 1
    MOV  [R0+2], R1         	; na 2° componente da variável evento_int
    POP  R1						; Usa-se 2 porque cada word tem 2 bytes
    POP  R0
    RFE
	
	
	