; **********************************************************************
; * Grupo 24
; *
; * 99068: Francisco Martins
; * 99078: Guilherme Carabalone
; * 99113: Miguel Vale 
; **********************************************************************

; **********************************************************************
; * Constantes
; **********************************************************************
DISPLAYS   EQU 0A000H  			; endereço dos displays de 7 segmentos (periférico POUT-1)
TEC_LIN    EQU 0C000H  			; endereço das linhas do teclado (periférico POUT-2)
TEC_COL    EQU 0E000H  			; endereço das colunas do teclado (periférico PIN)
ULTIMA_LINHA EQU 8H				; indica a última linha do teclado (dependendo do teclado podemos alterar aqui)
VAR_LOOP_SIMPLES EQU 4000H		; controla o ritmo do incremento/decremento contínuo
; que tecla faz que funcionalidade (pode-se facilmente alterar)
; 0 reinicilaiza todas as variáveis
INCREMENTO_CONTINUO EQU 0AH       
INCREMENTO_POR_CLIQUE EQU 0BH
DECREMENTO_CONTINUO EQU 0EH
DECREMENTO_POR_CLIQUE EQU 0FH
; **********************************************************************
; * Código
; **********************************************************************
PLACE	1000H
inicio_pilha:	TABLE	100H	; reserva espaço para a pilha (256 words)
SP_inicial:

PLACE	0
inicio:	
	;inicializações
	MOV	SP, SP_inicial	; inicializa SP
    MOV  R2, TEC_LIN   	; endereço do periférico das linhas
    MOV  R3, TEC_COL   	; endereço do periférico das colunas
    MOV  R4, DISPLAYS  	; endereço do periférico dos display

; corpo principal do programa
ciclo_principal:       			; varrimento do teclado
    MOV  R1, 0         
    MOV [R4], R1       			; escreve linha e coluna a zero nos displays
	MOV R8, 0          			; zera o registo que conta, em hexadecimal, o valor do display

inicializacoes:
	CALL converter_decimal		; converte e escreve no display
	MOV R1, 1           		; inicialização da linha
	MOV R9, VAR_LOOP_SIMPLES    ; inicializa a variável do loop_simples_decrementacao que controla o ritmo do incremento/decremento contínuo	
		
espera_tecla:          			; neste ciclo espera-se até uma tecla ser premida enquanto varre o teclado
    MOVB [R2], R1      			; escreve no periférico de saída (linhas)
    MOVB R0, [R3]      			; ler do periférico de entrada (colunas)
    CMP  R0, 0         			; há tecla premida?
	JZ   muda_linha             ; se nenhuma tecla premida, muda para a próxima linha
					            
    MOV R5, R1                  ; faz uma cópia da linha porque vai ser alterada na rotina conversao_linhas_colunas_em_hexadecimal
    CALL conversao_linhas_colunas_em_hexadecimal	; executa esta rotina para descobrir o valor da tecla premida
tecla_premida:                  ; procura que tecla foi premida para saber que função executar
	MOV R6,0H                   ; verifica se 0 foi premido               
	CMP R1, R6
	JEQ ciclo_principal         ; se 0 foi premido reinicializa todas as variáveis
	MOV R6, INCREMENTO_CONTINUO	; verifica se outras teclas foram premidas e dependendo da tecla premida incrementa/decrementa por clique/continuamente                 
	CMP R1, R6
	JEQ incrementar_continuo
	MOV R6, INCREMENTO_POR_CLIQUE
	CMP R1, R6
	JEQ incrementar_por_clique
	MOV R6, DECREMENTO_CONTINUO
	CMP R1, R6
	JEQ decrementar_continuo
	MOV R6, DECREMENTO_POR_CLIQUE
	CMP R1, R6
	JEQ decrementar_por_clique
	JMP inicializacoes    		; se a tecla premida não foi nenhuma destas então repete o ciclo      			
    
muda_linha:                     ; enquanto nehuma tecla é premida continuar a varrer uma nova linha
    MOV R7, ULTIMA_LINHA        ; valor da última linha (neste caso é 8)
	CMP R1, R7        			; não permite que passe da linha 3 (verifica se a próxima linha existe)
	JGT inicializacoes              
	ADD R1, R1                  ; como é inferior ou igual a 8 muda para a próxima linha
	JMP espera_tecla            ; volta a procurar se alguma tecla foi premida na linha

ha_tecla:              			; neste ciclo espera-se até NENHUMA tecla estar premida
    MOV  R1, R5        			; recupera o valor da linha (R1 tinha sido alterado)
    MOVB [R2], R1      			; escrever no periférico de saída (linhas)
    MOVB R0, [R3]      			; ler do periférico de entrada (colunas)
    CMP  R0, 0         			; há tecla premida?
    JNZ  ha_tecla      			; se ainda houver uma tecla premida, espera até não haver
	JMP inicializacoes			; repete o ciclo

; funcionalidades específicas (incrementar/decrementar por clique/continuamente)

incrementar_continuo:
	ADD R8, 1						; incrementa o registo que conta, em hexadecimal, o valor do display  
	JMP ha_tecla             		; vai ser incrementado por clique pois espera até a tecla não estar premida para voltar ao ciclo
incrementar_por_clique:
	ADD R8, 1						; incrementa o registo que conta, em hexadecimal, o valor do display  
	JMP loop_simples_decrementacao  ; incrementa continuamente mas primeiro salta para um loop para atrasar a incrementação contínua
decrementar_continuo:
	SUB R8, 1						; decrementa o registo que conta, em hexadecimal, o valor do display		
	JMP ha_tecla             		; vai ser incrementado por clique pois espera até a tecla não estar premida para voltar ao ciclo	
decrementar_por_clique:
	SUB R8, 1						; decrementa o registo que conta, em hexadecimal, o valor do display	
	JMP loop_simples_decrementacao	; decrementa continuamente mas primeiro salta para um loop para atrasar a decrementação contínua 
loop_simples_decrementacao:			; apenas procura nova tecla após 5000 decrementações para atrasar a alteração contínua 
	SUB R9, 1						; decrementa a variável de decrementação
	JNZ loop_simples_decrementacao	; enquanto não zerar o variável repete o ciclo
    JMP inicializacoes				; repete o ciclo

; **********************************************************************
; * Rotina: converte para o valor da tecla (ou seja, linhas e colunas no hexadecimal correspondente)
; * Entradas: A linha e coluna da tecla premida (R0, R1)  
; * Saídas: O valor em hexadecimal da tecla (R1)
; **********************************************************************	
conversao_linhas_colunas_em_hexadecimal:	
	PUSH R9								; guarda o valor da variável loop_simples
	MOV R9, 0               			; coloca o valor do contador das colunas em 0
	MOV R10, 0							; coloca o valor do contador das linhas em 0
converter_linha:            			; função que converte para o número da linha (0,1,2 ou 3)
	SHR R1, 1               			; procura a posição do bit a 1 na linha (anteriormente fizemos a cópia da linha para este registo) 
	CMP R1, 0                			; quando chega a zero para o ciclo
	JZ converter_coluna					; quando para o ciclo salta para a próxima tarefa
	ADD R10, 1            	    		; incrementa o contador que indica, no final, qual é o bit a 1
	JMP converter_linha					; repete o ciclo até o 1 "sair" (tecnicamente passar para a flag carry) 
converter_coluna:	   					; função que converte para o número da coluna (0,1,2 ou 3)
	SHR R0, 1               			; procura a posição do bit a 1 ao contar o número de vezes que se tem de shiftar para a direita até o 1 do resultado "sair" (tecnicamente passar para a flag carry)
	CMP R0, 0                			; quando chega a zero para o ciclo
	JZ converter_hexadecimal			; quando para o ciclo salta para a próxima tarefa
	ADD R9,1                			; incrementa o contador que indica, no final, qual é o bit a 1
	JMP converter_coluna				; repete o ciclo até o 1 "sair" (tecnicamente passar para a flag carry) 
converter_hexadecimal:	 	    		; converte de número da coluna e número da linha para o hexadecimal correspondente à tecla premida
	MOV R11, 4                  		; para utilizar na multiplicação abaixo
	MUL R10, R11                 		; multiplica linhas por 4
	ADD R10, R9                  		; soma o quádruplo das linhas às colunas
	MOV R1, R10							; transfere o valor para o registo que estava a guardar o valor da linha
sai_hex:	
	POP R9								; recupera o valor da variável do loop simples
	RET
	

; **********************************************************************
; * Rotina: converter número em decimal e passá-lo para o display
; * Entradas: O número que queremos converter e escrever no display (R8)  
; * Saídas: Não tem
; **********************************************************************
converter_decimal:              
	CMP R8, 0
	JN ciclo_principal				; não permite números negativos e caso os encontre reinicializa as variáveis
	MOV R10, 3E8H
	CMP R8, R10                     ; se chegar a 1000 reinicializa todas as variáveis e dá a volta ao contador do display
	JEQ ciclo_principal
	PUSH R8							; guarda o valor do registo que conta em hexadecimal, para mais tarde
	MOV R9, 0         		    	; inicializa o resultado final em decimal
	MOV R10, 3E8H      		    	; inicializa o factor em hexadecimal (já tinha este valor de 1000 é só para reforçar a ideia)
	MOV R11, 0AH        	    	; constante de valor 10 pela qual se vai obter os dígitos do numero em decimal e dividir o factor
ciclo_dec:            
	MOD R8, R10         		    ; remove o dígito de maior peso
	DIV R10, R11        		    ; divide o fator por 10 para o referir ao próximo dígito decimal
	MOV R6, R8         		    	; inicializa o dígito 
	DIV R6, R10                  	; obtém o dígito decimal seguinte
	SHL R9, 4          	    		; move os dígitos decimais um byte para a esquerda para poder acrescentar o próximo dígito na posição vaga (no final ficam todos na posição certa)
	OR  R9, R6           	    	; coloca o último dígito obtido  na casa de menor peso
	CMP R10, 1		  		    	 
	JGT ciclo_dec					; repete o ciclo se o fator ainda não chegou a 1  
    MOV [R4], R9			    	; escreve o valor em decimal nos displays
sai_dec:
    POP R8							; recupera o valor, em hexadecimal, do display
	RET
	
	
	
	
	
	
	
	
	