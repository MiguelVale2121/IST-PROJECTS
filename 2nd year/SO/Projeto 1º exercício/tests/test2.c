#include "fs/operations.h"
#include <pthread.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>

#define ITERATIONS (10)
#define ACTV_DELAY (100000)
#define COUNT 80
#define SIZE 256

char input[SIZE]; 
char generic_buffer[ITERATIONS][SIZE];

void *test_func1(void *none){

    char *path = "/Vale"; 
    
    memset(input, 'V', SIZE);

    int f;

    f = tfs_open(path, TFS_O_CREAT);
    assert(f != -1);

    for (int i = 0; i < COUNT; i++) {
        assert(tfs_write(f, input, SIZE) == SIZE);
    }
    assert(tfs_close(f) != -1);

    /* Open again to check if contents are as expected */
    f = tfs_open(path, 0);
    assert(f != -1 );

    /*Active Waiting*/
    for (int i = 0 ; i < ACTV_DELAY ; i++){
        for (int j = 0 ; j < ACTV_DELAY ; j++){
            for (int k = 0 ; k < ACTV_DELAY ; k++){
                //do nothing 
            }
        }
    }

    for (int i = 0; i < COUNT; i++) {
        assert(tfs_read(f, generic_buffer[f], SIZE) == SIZE);
        assert (memcmp(input, generic_buffer[f], SIZE) == 0);
    }

    assert(tfs_close(f) != -1);

    return none;
}


int main() {
    char cmp[SIZE*COUNT];

    pthread_t threads[ITERATIONS];

    assert(tfs_init() != -1);

    for (int i = 0 ; i < ITERATIONS ; i++) {
        pthread_create(&threads[i], NULL, test_func1, NULL);
    }

    for(int i = 0 ; i < ITERATIONS ; i++) {
        pthread_join(threads[i], NULL);
    }

    for (int i = 0 ; i < COUNT ; i++) {
        strcpy(cmp,"V");
    }    

    for (int i = 0 ; i < ITERATIONS ; i++) {
        assert(strcmp(generic_buffer[i], cmp) == 0);
    }

    printf("Successful test.\n");

    return 0;
}
