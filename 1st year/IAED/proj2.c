/*---------------------------------
    Segundo projeto IAED: proj2.c

    Miguel Vale ist199113

---------------------------------*/


#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define INST_LIMIT 65537 /* Numero maximo de instrucoes.*/
#define CARACT_BRANCOS 3 /* Numero de caracteres brancos*/

/* Estrutura */
typedef struct node {
    char *ultimo;  /* Ultimo membro do Caminho */
    char *name;    /* Caminho */
    char *value;   /* Valor associado ao caminho */
    struct node *subnode;  /* filho */
    struct node *nextnode; /* irmao */
} *link;


void merge(char **v,int l,int m, int r){
/*------------------------------------------

    Funcao que auxilia a funcao merge sort

    Input : char **v-> elementos que vao ser ordenados
            inteiro l -> Id do primeiro elemento a ser ordenado
            inteiro m -> Id do elemento do meio
            inteiro r -> Id do ultimo elemento a ser ordenado

    Output : ()

------------------------------------------*/
    char *auxiliar[sizeof(v)];
    int i,j,k;
    for(i = m+1; i>l; i--){
        auxiliar[i-1] = v[i-1];
    }
    for(j = m; j<r; j++){
        auxiliar[r+m-j] = v[j+1];
    }
    for(k = l; k <= r; k++){
        if (strcmp(auxiliar[j],auxiliar[i])<0)
            v[k]= auxiliar[j--];
        else
            v[k]= auxiliar[i++];
    }
}

void mergesort(char **v, int l, int r){
/*------------------------------------------

    Funcao mergesort que ordena elementos por ordem alfabetica

    Input : char **v -> elementos que vao ser ordenados
            inteiro l -> Id do primeiro elemento a ser ordenado
            inteiro r -> Id do ultimo elemento a ser ordenado

    Output : ()

------------------------------------------*/

    int m = (r+l)/2;
    if (r <= l) return;
    mergesort(v,l,m);
    mergesort(v, m+1,r);
    merge(v,l,m,r);

}


/* Funcoes auxiliares */
void no_memory(link x){
/*------------------------------------------

    Funcao no memory

    Invoca a mensagem No memory 
    quando o programa excede o numero maximo de intrucoes

    input : link x
    output : ()

------------------------------------------*/
    if(!x){
        printf("No memory.\n");
        exit(-1);
    }
}

void all_free(link node){
/*------------------------------------------
    Funcao all free

    Da free a todos os nodes da estrutura 
    e desta forma liberta a memoria

    input : link node -> nos da estrutura
    output : ()

 -----------------------------------------*/
    link subnode;
    link tmp;

    subnode = node->subnode; 
    while(subnode != NULL){ /* Enquanto o subnode tiver 
                                        preenchido continua a invocar a funcao*/
        tmp = subnode->nextnode;
        all_free(subnode);
        subnode = tmp; 
        
    } 
    free(node->value);  
    free(node->name);
    free(node->ultimo);
    free(node);
}

char *concatenar(char *a, char *b, char *c){
/*------------------------------------------
    Funcao concatenar

    Concatena strings usada para corrigir o input do search()

    input : char *a -> primeira string
            char *b -> segunda string
            char *c -> terceira string
    
    output : str -> strings concatenadas

-----------------------------------------*/
    int size = strlen(a)+strlen(b)+strlen(c)+1;
    char *str = malloc(size);
    strcpy(str, a);   /* Copia para str a sting a */
    strcat(str, b);   /* Concatena a str a string b */
    strcat(str, c);   /* Concatena a str a string c */
    return str;
}


link novo(char *oldnode_2, char *ultimo){
/*------------------------------------------
    Funcao novo

    Cria nos na estrutura

    input : char *oldnode_2 -> o no anterior ao no atual
            char *ultimo -> ultimo no da estrutura
            
    
    output : x -> no criado

-----------------------------------------*/
    link x = (link)malloc( sizeof(struct node)); /* Reserva espaco para o no */
    no_memory(x);

    if(!oldnode_2){
        x->name = (char*)malloc(CARACT_BRANCOS); /* Espaco ocupado por carateres brancos */
        no_memory(x);
        strcpy(x->name, "/");
        return x;
    }
    x->name = (char*)malloc(strlen(oldnode_2)+1+strlen(ultimo)+1); /* Espaco reservado para o no*/
    no_memory(x);
    strcpy(x->name,oldnode_2);
    if(strcmp(oldnode_2,"/")!=0){
        strcat(x->name, "/");
    }
    strcat(x->name,ultimo); /* concatena no final de uma string */
    x->ultimo = (char*)malloc(strlen(ultimo)+1); /* Espaco reservado para o ultimo no*/
    no_memory(x);
    strcpy(x->ultimo,ultimo);
    return x;
    
}

/* Funcoes principais */
void help();
void quit(link root);
void set(link root, char *name, char *value);
void print(link root);
void find(link root, char *name);
void list(link root, char *name);
int search(link root, char *name);
void delete(link root, char *name);

int main(){
    char comandos[INST_LIMIT], *comando, *name, *value, *head;
    link root = novo(NULL, NULL);

    while(1){
        fgets(comandos, INST_LIMIT, stdin);
        comandos[strcspn(comandos, "\n")] = '\0';
        comando = strtok(comandos, " ");
        name = strtok(NULL, " ");
        value = strtok(NULL, "");
        if (strcmp(comando, "help") == 0){ 
            help();
        }
        if (strcmp(comando, "quit") == 0){ 
            quit(root);
        }
        if (strcmp(comando,  "set") == 0){
            set(root, name, value);
        }
        if (strcmp(comando,  "print") == 0){
            print(root);
        }
        if (strcmp(comando,  "find") == 0){ 
            find(root, name);
        }
        if (strcmp(comando, "list")==0){
            list(root,name);
        }
        if (strcmp(comando,  "search") == 0){
            head = concatenar(name, " ", value);
            search(root, head);
        }
        if(strcmp(comando, "delete")==0){
            delete(root,name);
        }
    }
    return 0;
}

/* Funcoes principais */
void help(){
/*------------------------------------------
    Funcao help

    Imprime os comandos disponiveis

    input : ()
            
    output : ()

-----------------------------------------*/
    printf("help: Imprime os comandos disponíveis.\n");
    printf("quit: Termina o programa.\n");
    printf("set: Adiciona ou modifica o valor a armazenar.\n");
    printf("print: Imprime todos os caminhos e valores.\n");
    printf("find: Imprime o valor armazenado.\n");
    printf("list: Lista todos os componentes imediatos de um sub-caminho.\n");
    printf("search: Procura o caminho dado um valor.\n");
    printf("delete: Apaga um caminho e todos os subcaminhos.\n");    
}

void quit(link root){
/*------------------------------------------
    Funcao quit

    Termina o programa

    input : link root -> nos da estrutura
            
    output : ()

-----------------------------------------*/
    all_free(root);
    exit(0);
}
void set(link root, char *name, char *value){
/*------------------------------------------
    Funcao set

    Adiciona ou modifica o valor a armazenar

    input : link root -> nos da estrutura
            char *name -> caminho 
            char *value -> valor associado ao caminho
            
    output : ()

-----------------------------------------*/
    
    link node = root; 
    link subnode; 
    link nextnode; 
    link oldnode = root;
    char *passeio;

    for(passeio = strtok(name, "/"); passeio != NULL; passeio = strtok(NULL, "/")){
/* Enquanto que os elementos do caminho nao forem NULL*/    
        nextnode = NULL; 
        oldnode = node;  
        subnode = node->subnode;
        while(subnode != NULL){
        /* Enquanto que subnode nao for NULL*/
            if (strcmp(passeio, subnode->ultimo)==0){
            /* Se o ultimo elemento do caminho for igual ao ultimo no */
                node = subnode;
                break;
            }
            nextnode = subnode; 
            subnode = subnode->nextnode;
        }

        if(node != subnode){ 
        /* Se o no for diferente da sua ramificacao cria um novo no*/
            node = novo(oldnode->name, passeio);
            if(nextnode == NULL){
            /* Se o proximo no for NULL */
                oldnode->subnode = node;
            }
            else {
            /* Se o proximo no nao for NULL */
                nextnode->nextnode = node;
            }           
        } 
           
    }
    node->value = (char *)malloc(strlen(value)+1); /* Reserva memoria para o valor do caminho*/
    no_memory(node);
    strcpy(node->value,value);
}

void print(link root){
/*------------------------------------------
    Funcao print

    Imprime todos os caminhos e valores

    input : link root -> nos da estrutura
            
    output : ()

-----------------------------------------*/
    link subnode;
    if(root->value){
        printf("%s %s\n", root->name, root->value);
    }
    subnode = root->subnode;
    while(subnode != NULL ){
    /* Se o subnode nao for NULL continua a percorrer a funcao */
        print(subnode);
        subnode = subnode->nextnode;
    }
}

void find(link root, char *name){
/*------------------------------------------
    Funcao find

    Imprime o valor armazenado de um caminho

    input : link root -> nos da estrutura
            char *name -> caminho
            
    output : ()

-----------------------------------------*/
    char *passeio;
    link node = root;
    link subnode;

    for(passeio = strtok(name, "/"); passeio != NULL;){
        subnode = node->subnode;
        while( subnode != NULL){ /* Percorre a arvore enquanto os elementos forem diferentes de null*/
            if (strcmp(passeio, subnode->ultimo)==0){
                node = subnode;
                break;
            }
            subnode = subnode->nextnode;
        }
        if(node != subnode){ 
           printf("not found\n");
           return;
        }
        passeio = strtok(NULL, "/");
    }
    if (!node -> value){
        printf("no data\n");
        return; 
    }
    printf("%s\n", node->value);
}

void list(link root, char *name){
/*------------------------------------------
    Funcao list

    Lista todos os componentes imediatos de um sub-caminho

    input : link root -> nos da estrutura
            char *name -> caminho
            
    output : ()

-----------------------------------------*/
    int i = 0, x=0;
    char *passeio;
    char **passeios = NULL;
    link node = root; 
    link subnode;
    int contador = 0;
    
    if (name){
        passeio = strtok(name, "/");
    } 
    else{
        passeio = NULL;
    }

    while(passeio != NULL){/* Percorre a arvore enquanto os elementos forem diferentes de null*/
        subnode = node->subnode;
        while(subnode != NULL){    
            if (strcmp(passeio, subnode->ultimo)==0){
                node = subnode;
                break;
            }
            subnode = subnode->nextnode;
        }
        if(node != subnode){
            printf("not found\n");
            return;
        }

        passeio = strtok(NULL, "/");
    }
    subnode = node->subnode;
    while(subnode != NULL){
        passeios = realloc(passeios, ++i * sizeof(char*));
        passeios[i-1] = subnode->ultimo;
        subnode = subnode->nextnode;
        contador++;
    }
    mergesort(passeios, 0, contador - 1);
    while (x < i){
        printf("%s\n", passeios[x]);
        x++;
    }
    free(passeios);
}
int search(link root, char *value){
/*------------------------------------------
    Funcao search

    Procura o caminho dado um valor

    input : link root -> nos da estrutura
            char *value -> valor de um caminho
            
    output : ()

-----------------------------------------*/
    link subnode;
    if(root->value && strcmp(root->value, value)==0){
        printf("%s\n", root->name );
        return 1;
    }

    subnode = root->subnode;
    while( subnode != NULL ){
    /* Enquanto que o subnode nao for NULL percorre a funcao */
       if(search(subnode, value)){
        return 1;
       }
       subnode = subnode->nextnode;
    }
    if(strcmp(root->name, "/")==0){
        printf("not found\n");
    }
    return 0;
}

void delete(link root, char *name){
/*------------------------------------------
    Funcao delete

    Apaga todos os caminhos de um sub-caminho

    input : link root -> nos da estrutura
            char *name -> Caminho
            
    output : ()

-----------------------------------------*/
    char *passeio;
    link node = root, subnode, nextnode, oldnode;
    
    if (name)
        passeio = strtok(name, "/"); /* Elimina as / do input */
        
    else
        passeio = NULL;

    while(passeio != NULL){ /* Percorre a arvore enquanto os elementos forem diferentes de null*/
        nextnode = NULL;
        oldnode = node;
        subnode = node->subnode;
        while(subnode){   
            if (strcmp(passeio, subnode->ultimo)==0){
                node = subnode;
                break;
            }
            nextnode = subnode;
            subnode = subnode->nextnode;
        }
        
        if(node != subnode){
            printf("not found\n");
            return;
        }

        passeio = strtok(NULL, "/");
    }
    if (!nextnode){ 
        oldnode->subnode = node->nextnode;       
    }
    else{
        nextnode->nextnode = node->nextnode;
    }
    all_free(node);
}