#include "operations.h"
#include <assert.h>
#include <errno.h>
#include <fcntl.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <pthread.h>

#define S 4 //max number of clients
#define MAX_RW 1024 //max bytes to read or write


typedef struct {
    char name[40];
    int session_id;
    int fclient;
    int free;
} session_t;


static pthread_t thread_ids[S];
static session_t sessions[S];
static int fserver;

int receive_message(char *buffer, ssize_t size) {
    ssize_t ret;

    while(1) {
        if ((ret = read(fserver, buffer, (size_t)size)) != -1)
            return (int)ret;
    }
}

void thread_work(){}

int main(int argc, char **argv) {

    int fclient;
    char name[40];
    char op_code[sizeof(char)];
    ssize_t r, ret;

    memset(name, '\0', 40);

    for (int i = 0; i < S; i++) {
        sessions[i].free = 1;
    }

    if (argc < 2) {
        printf("Please specify the pathname of the server's pipe.\n");
        return 1;
    }

    for (int i = 0 ; i < S ; i++) {
        if (pthread_create(&thread_ids[i], NULL, (void*)&thread_work, (void*)(&sessions[i])) != 0)
            return -1;
    }

    char *pipename = argv[1];
    printf("Starting TecnicoFS server with pipe called %s\n", pipename);

    // remove pipe if it does not exist
    if (unlink(pipename) != 0 && errno != ENOENT) {
        fprintf(stderr, "[ERR]: unlink(%s) failed: %s\n", pipename,
                strerror(errno));
        exit(EXIT_FAILURE);
    }

    // create pipe
    if (mkfifo(pipename, 0640) != 0) {
        fprintf(stderr, "[ERR]: mkfifo failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    // open pipe for writing
    // this waits for someone to open it for reading
    fserver = open(pipename, O_RDONLY | O_NONBLOCK);
    if (fserver == -1) {
        fprintf(stderr, "[ERR]: open failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    while (true) {       

        r = receive_message(op_code, sizeof(char));
        if (r == 0) {
            if (close(fserver) == -1) return -1;
            if ((fserver = open(pipename, O_RDONLY )) == -1) return -1;
            
        } else if (r == -1) {
            // ret == -1 signals error
            fprintf(stderr, "[ERR]: read failed: %s\n", strerror(errno));
            exit(EXIT_FAILURE);
        }

        int opcode = atoi(op_code);

        if (opcode == TFS_OP_CODE_MOUNT) {

            int session_id;

            // reading from client
            while(1) {
                ret = read(fserver, name, 40);
                if (ret == 40)
                    break;
            }

            for (int i = 0; i < S; i++) {
                if (sessions[i].free) {
                    session_id = i;
                    break;
                }
            }            

            sessions[session_id].free = 0;
            memset(sessions[session_id].name, '\0', 40);
            memcpy(sessions[session_id].name, name, 40);
            sessions[session_id].session_id = session_id;

            while(1) {
                if ((fclient = open(name, O_WRONLY | O_NONBLOCK)) != -1)
                    break;
            }

            sessions[session_id].fclient = fclient;

            // answering
            ret = write(fclient, &session_id, sizeof(int)); 
            if (ret == -1) return -1;       
        }
        else if (op_code == TFS_OP_CODE_UNMOUNT) {
            int session_id;
            char buffer[4];

            memset(buffer, '\0', 4);

            receive_message(buffer, 4);

            memcpy(&session_id, buffer, sizeof(int));

            ret = write(sessions[session_id].fclient, &session_id, sizeof(int)); 
            if (ret == -1) return -1;

        }
        else if (op_code == TFS_OP_CODE_OPEN) {

            int session_id;
            int flags;
            char n[40];
            char buffer[49];

            memset(buffer, '\0', 49);
            memset(n, '\0', 40);

            ret = receive_message(buffer, 49);

            memcpy(n, buffer + 1 + sizeof(int), 40);
            memcpy(&flags, buffer + 1 + sizeof(int) + 40, sizeof(int));

            int fhandle = tfs_open(n, flags);

            //idk y we send back the session_id
            ret = write(sessions[session_id].fclient, &fhandle, sizeof(int)); 
            if (ret == -1) return -1;

        }
        else if (op_code == TFS_OP_CODE_CLOSE) {

            int session_id;
            int fhandle;
            char buffer[8];

            memset(buffer, '\0', 8);

            receive_message(buffer, 8);

            memcpy(&session_id, buffer, sizeof(int));
            memcpy(&fhandle, buffer + 4, sizeof(int));

            tfs_close(fhandle);

            //idk y we send back the session_id
            ret = write(sessions[session_id].fclient, &session_id, sizeof(int)); 
            if (ret == -1) return -1;

        }
        else if (op_code == TFS_OP_CODE_WRITE) {

            
            int session_id;
            int fhandle;
            size_t len;
            char buffer[8+sizeof(size_t)+MAX_RW];

            memset(buffer, '\0', 8+sizeof(size_t)+MAX_RW);
            memset(buffer, '\0', MAX_RW);

            receive_message(buffer, 8+sizeof(size_t)+MAX_RW);

            memcpy(&session_id, buffer, sizeof(int));
            memcpy(&fhandle, buffer + 4, sizeof(int));
            memcpy(len, buffer + 8, sizeof(size_t));

            char buf[len];
            memcpy(buf, buffer + 8 + sizeof(size_t), len);

            tfs_write(fhandle, buf, len);

            //idk y we send back the session_id
            ret = write(sessions[session_id].fclient, &session_id, sizeof(int)); 
            if (ret == -1) return -1;

        }
        else if (op_code == TFS_OP_CODE_READ) {

            int session_id;
            int fhandle;
            size_t len;
            char buffer[8+sizeof(size_t)];

            memset(buffer, '\0', 8+sizeof(size_t));
            //memset(buf, '\0', len);

            receive_message(buffer, 8+sizeof(size_t));

            memcpy(&session_id, buffer, sizeof(int));
            memcpy(&fhandle, buffer + 4, sizeof(int));
            memcpy(len, buffer + 8, sizeof(size_t));

            char read_buf[len];
            memset(read_buf, '\0', len);

            int bytes_read = (int)tfs_read(fhandle, read_buf, len);

            char return_buf[sizeof(int)+len];
            memset(return_buf, '\0', sizeof(int)+len);

            memcpy(return_buf, &bytes_read, sizeof(int));

            memcpy(return_buf, &read_buf, len);

            ret = write(sessions[session_id].fclient, return_buf, sizeof(int)+len); 
            if (ret == -1) return -1;

        }
        else if (op_code == TFS_OP_CODE_SHUTDOWN_AFTER_ALL_CLOSED) {
            int session_id;
            char buffer[4];

            memset(buffer, '\0', 4);

            receive_message(buffer, 4);

            memcpy(&session_id, buffer, sizeof(int));

            tfs_destroy_after_all_closed();

            //idk y we send back the session_id
            ret = write(sessions[session_id].fclient, &session_id, sizeof(int)); 
            if (ret == -1) return -1;

        }
        

    }

    return 0;
}