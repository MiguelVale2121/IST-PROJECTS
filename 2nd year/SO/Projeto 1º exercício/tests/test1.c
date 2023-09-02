#include "fs/operations.h"
#include <pthread.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>

#define ITERATIONS (10)
#define ACTV_DELAY (100000)

char generic_buffer[ITERATIONS][40];

void *test_func(void *none){

    char *path = "/Vasco";
    char *string = "O Vasco e fixe";

    int f;
    ssize_t r;

    f = tfs_open(path, TFS_O_CREAT);
    assert(f != -1);

    r = tfs_write(f, string, strlen(string));
    assert(r == strlen(string));

    assert(tfs_close(f) != -1);

    f = tfs_open(path, 0);
    assert(f != -1);

    /*Active Waiting*/
    for (int i = 0 ; i < ACTV_DELAY ; i++){
        for (int j = 0 ; j < ACTV_DELAY ; j++){
            for (int k = 0 ; k < ACTV_DELAY ; k++){
                //do nothing 
            }
        }
    }

    r = tfs_read(f, generic_buffer[f], sizeof(generic_buffer[f]) - 1);
    assert(r == strlen(string));

    generic_buffer[f][r] = '\0';
    assert(strcmp(generic_buffer[f], string) == 0);

    return none;
}


int main() {

    pthread_t threads[ITERATIONS];

    assert(tfs_init() != -1);

    for (int i = 0 ; i < ITERATIONS ; i++) {
        pthread_create(&threads[i], NULL, test_func, NULL);
    }

    for(int i = 0 ; i < ITERATIONS ; i++) {
        pthread_join(threads[i], NULL);
    }

    for (int i = 0 ; i < ITERATIONS ; i++) {
        assert(strcmp(generic_buffer[i], "O Vasco e fixe") == 0);
    }

    printf("Successful test.\n");

    return 0;
}
