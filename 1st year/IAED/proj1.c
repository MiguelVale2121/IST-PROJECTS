/*
    Primeiro projeto IAED: proj1.c

    Miguel Vale ist199113

*/


#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MAXTASK 10000
#define MAXSTR1 51
#define MAXSTR2 21
#define MAXUTL 50
#define MAXATV 10
#define ATV_DO "TO DO"
#define ATV_PROGRESS "IN PROGRESS"
#define ATV_DONE "DONE"

/* Variaveis globais */
int contador = 0;      
int utilizadores = 0;
int atividades = 3; 
int tempo=0;
char nome_utl[MAXUTL][MAXSTR2], nome_atv[MAXATV][MAXSTR2];   

/* Estrutura */
typedef struct tarefa{
    int id;
    char desc[MAXSTR1];
    char user[MAXSTR2];
    char atv[MAXSTR2];
    int dur;
    int inst;
    int atv_gasto;
}tarefa;

/* Variaveis globais */
tarefa task[MAXTASK],auxiliar[MAXTASK],ordena[MAXTASK],ordena2[MAXTASK];


/* Funcoes de ordenacao */
void merge(tarefa v[],int l,int m, int r){
    /* 

    Funcao que auxilia a funcao merge sort

    Input : tarefa vetor[]-> elementos que vao ser ordenados
            inteiro l -> Id do primeiro elemento a ser ordenado
            inteiro m -> Id do elemento do meio
            inteiro r -> Id do ultimo elemento a ser ordenado

    Output : ()

    */

    int i,j,k;
    for(i = m+1; i>l; i--){
        auxiliar[i-1] = v[i-1];
    }
    for(j = m; j<r; j++){
        auxiliar[r+m-j] = v[j+1];
    }
    for(k = l; k <= r; k++){
        if (strcmp(auxiliar[j].desc,auxiliar[i].desc)<0)
            v[k]= auxiliar[j--];
        else
            v[k]= auxiliar[i++];
    }
}

void mergesort(tarefa v[], int l, int r){
    /* 

    Funcao mergesort que ordena elementos por ordem alfabetica

    Input : tarefa vetor[]-> elementos que vao ser ordenados
            inteiro l -> Id do primeiro elemento a ser ordenado
            inteiro r -> Id do ultimo elemento a ser ordenado

    Output : ()

    */

    int m = (r+l)/2;
    if (r <= l) return;
    mergesort(v,l,m);
    mergesort(v, m+1,r);
    merge(v,l,m,r);
}

void SelectionSort(tarefa v[], int l, int r){
    /*

    Funcao SelectionSort que ordena elementos por ordem crescente

    Input : tarefa vetor[]-> elementos que vao ser ordenados
            inteiro l -> Id do primeiro elemento a ser ordenado
            inteiro r -> Id do ultimo elemento a ser ordenado

    Output : ()
    */

    int i,j;
    for(i = l; i < r;i++){
        int min=i;
        tarefa aux;
        for (j = i+1;j<=r;j++)
            if (v[j].inst< v[min].inst){
                min = j;
            }
        aux = v[i]; v[i]=v[min];v[min]=aux;

    }
}

/* Verificacoes */
int verificacao_u(char novo[]){
    /*
    
    Verifica se existe um utilizador no vetor com os utilizadores

    Input : char vetor -> Dos utilizadores que vamos inserir para comparar com os existentes

    Output : 1 -> Se sao iguais o utilizador que inserirmos e aquele que ja esta guardado
             0 -> Se nao sao iguais

    */
    int i=0;

    while(i<utilizadores){
        if(strcmp(nome_utl[i],novo)==0){
            return 1;
        }
        i++;
    }
    return 0;
}

int verificacao_a(char novo[]){
    /*
    
    Verifica se existe uma atividade no vetor com as atividades

    Input : char vetor -> Das atividades que vamos inserir para comparar com as existentes

    Output : 1 -> Se sao iguais a atividade que inserirmos com aquela que ja esta guardado
             0 -> Se nao sao iguais

    */
    int i=0;

    while(i<atividades){
        if(strcmp(nome_atv[i],novo)==0){
            return 1;
        }
        i++;
    }
    return 0;
}

int ha_minusculas(char atividade[]){
    /*

    Verifica se existe alguma letra minuscula na atividade que inserimos

    Input: char vetor -> A atividade que queremos inserir

    Output: 1 -> Se existem letras minusculas
            0 -> Se nao existirem

    */
    int i;

    for (i=0;atividade[i] != '\0';i++){
        if (islower(atividade[i])){
            return 1;
        }

    }
    return 0;
}

/* Funcoes kanban */
void t(){
    /*

    Funcao t() adiciona uma nova tarefa ao sistema

    Erros:  too many tasks
            duplicate description
            invalid duration

    Input: ()

    Output: ()

    */
    int d,erro=0,i;
    char str[MAXSTR1];
    scanf(" %d %50[^\n]", &d,str);

    for (i=0;i<contador;i++){
        if (strcmp(task[i].desc,str)==0){
            erro = 1;
            break;
        }
    }
    if (contador+1>MAXTASK){
        printf("too many tasks\n");
    }
    else if (erro){
        printf("duplicate description\n");
    }

    else if (d<=0){
        printf("invalid duration\n");
    }
        else{
        task[contador].id=contador + 1;
        strcpy(task[contador].desc,str);
        task[contador].dur = d;
        strcpy(task[contador].atv,ATV_DO);
        task[contador].atv_gasto = 0;
        contador = contador + 1;
        printf("task %d\n", task[contador-1].id );
    }
}

void l(){
    /*

    Funcao l() lista as tarefas:  Se o comando for invocado sem argumentos, todas as tarefas são listadas por ordem alfabética da descrição.
                                  Se o comando for invocado com uma lista de <id>s, as tarefas devem ser listadas pela ordem dos respetivos <id>s.

    Erros: no such task

    Input: ()

    Output: ()

    */
    int i,id,i2; 

    if (getchar()==' '){
        while (scanf(" %d", &id)){
            if (id >= contador + 1 || id <= 0){
                printf("%d: no such task\n",id);
            }
            else{
                printf("%d %s #%d %s\n", task[id-1].id,task[id-1].atv,task[id-1].dur,task[id-1].desc);
            }
        }
    }
    else{
        for (i2 = 0; i2< contador + 1;i2++){
            ordena[i2] = task[i2];          /* Guarda os valores ordenados noutro vetor para nao corromper a tarefa*/
        }
        mergesort(ordena,0,contador-1);   /* Funcao de ordenacao */
        for (i=0;i<contador;i++){
           printf("%d %s #%d %s\n", ordena[i].id,ordena[i].atv,ordena[i].dur,ordena[i].desc);
        }
    }  
}

void n(){
    /*

    Funcao n() avança o tempo do sistema

    Erros: invalid time

    Input : ()

    Output: ()

    */
    int duracao;
    char decimal;
    scanf(" %d%c", &duracao,&decimal);

    if (duracao<0||decimal!='\n'){
        printf("invalid time\n");
    }
    else{
        tempo += duracao;
        printf("%d\n", tempo);
    }
}

void u(){
    /*

    Funcao u() adiciona um utilizador ou lista todos os utilizadores
    
    Erros: user already exists
           too many users

    Input: ()

    Output: ()

    */
    int i=0;
    char nome[MAXSTR2];
  
    if(getchar()!=' '){
        while (i<utilizadores){         
            printf("%s\n", nome_utl[i]);
            i++; 
        }
    }

    else{
        scanf(" %20s", nome);
        
        if (verificacao_u(nome)){ /*Funcao que verifica se o utilizador ja existe*/
            printf("user already exists\n");
        }
        else if(utilizadores > MAXUTL - 1){
            printf("too many users\n");
        }   
        
        else {
            utilizadores = utilizadores + 1;
            strcpy(nome_utl[utilizadores-1],nome);
       }
    }
}

void m(){
    /*

    Funcao m() move uma tarefa de uma atvidade para outra

    Erros: no such task
           task already started
           no such user
           no such activity

    Input: ()

    Output:()

    */
    int id, duration, slack;
    char utilizador[MAXSTR2], atividade[MAXSTR2];
    scanf(" %d %s %20[^\n]", &id , utilizador, atividade);

    
    if (id > contador ){
        printf("no such task\n");
    }
    else if (strcmp(ATV_DO,atividade)==0 && task[id-1].atv_gasto != 0){
        printf("task already started\n");
    }
    else if (!verificacao_u(utilizador)){ /*Funcao que verifica se o utilizador ja existe*/
        printf("no such user\n");
    }
    else if (!verificacao_a(atividade)){ /*Funcao que verifica se a atividade ja existe*/
        printf("no such activity\n");
    }


    else{
        task[id-1].atv_gasto++;
        strcpy(task[id-1].atv,atividade);

        if(task[id-1].atv_gasto == 1){
            task[id-1].inst = tempo;
        }

        if (strcmp(ATV_DONE,atividade)==0){
            duration = tempo - task[id-1].inst;
            slack = duration - task[id-1].dur;
            printf("duration=%d slack=%d\n",duration,slack);
        }
    }
}

void d(){
    /*

    Funcao d() lista todas as tarefas que estejam numa dada atividade 

    Erros: no such activity

    Input: ()

    Output:()

    */
    char atividade[MAXSTR2];
    int id,i;
    scanf(" %20[^\n]", atividade);

    if (!verificacao_a(atividade)){ /*Funcao que verifica se a atividade ja existe*/
        printf("no such activity\n");
    }

    else{
        for (i=0;i < contador + 1;i++){
            ordena2[i]=task[i];  /* Guarda os valores ordenados noutro vetor para nao corromper a tarefa*/
        }
        mergesort(ordena2,0,contador-1); /* Funcao que ordena alfabeticamente */
        SelectionSort(ordena2,0,contador-1);/* Funcao que ordena por ordem crescente*/
        for(id=0;id<contador;id++){
            if(strcmp(task[id].atv,atividade)==0){
                printf("%d %d %s\n", ordena2[id].id , ordena2[id].inst, ordena2[id].desc);
            }
        }
    }
}

void a(){
    /*

    Funcao a() adiciona uma atividade ou lista todas as atividades

    Erros: duplicate activity
           invalid description
           too many activities

    Input: ()

    Output: ()

    */
    int i=0;
    char atividade[MAXSTR2];
  
    if(getchar()!=' '){
        while (i<atividades){         
            printf("%s\n", nome_atv[i]);
            i++; 
        }
    }

    else{
        scanf(" %20[^\n]", atividade);
      
        if (verificacao_a(atividade)){ /*Funcao que verifica se a atividade ja existe*/
            printf("duplicate activity\n");
        } 
        else if(ha_minusculas(atividade)){ /*Funcao que verifica se existe alguma letra minuscula*/
            printf("invalid description\n");
        }
        else if(atividades > MAXATV - 1){
            printf("too many activities\n");
        }   
        else {
            atividades = atividades + 1;
            strcpy(nome_atv[atividades-1],atividade);
       }
    }
}

int main(){
    char comando;
    comando = getchar();
    strcpy(nome_atv[0], ATV_DO);
    strcpy(nome_atv[1], ATV_PROGRESS);
    strcpy(nome_atv[2], ATV_DONE);


    while(comando != 'q'){

        if (comando == 't'){
            t();
        }
        if (comando == 'l'){
            l();
        }
        if (comando == 'n'){
            n();
        }
        if (comando == 'u'){
            u();
        }
        if (comando == 'm'){
            m();
        }
        if (comando == 'd'){
            d();
        }
        if (comando == 'a'){
            a();
        }
        comando = getchar();
    }

    return 0;
}