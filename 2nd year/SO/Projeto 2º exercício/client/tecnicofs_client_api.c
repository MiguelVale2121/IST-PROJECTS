#include "tecnicofs_client_api.h"
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

static int session_id;
static int fserv;
static int fcli;
// helper function to send messages
// retries to send whatever was not sent in the begginning
void send_msg(int tx, char const *str, ssize_t len) {
    ssize_t ret = write(tx, str, (size_t)len);
    if (ret < 0) {
        fprintf(stderr, "[ERR]: write failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }
}

int tfs_mount(char const *client_pipe_path, char const *server_pipe_path) {

    fserv = open(server_pipe_path, O_WRONLY);

    if (fserv == -1) return -1;

    char buffer[41];

    char op_code = TFS_OP_CODE_MOUNT + '0';

    memcpy(buffer, &op_code, sizeof(char));

    memcpy(buffer + 1, client_pipe_path, 40);
    
    send_msg(fserv, buffer, 41);
    
    // remove pipe if it does not exist
    if (unlink(client_pipe_path) != 0 && errno != ENOENT) {
        fprintf(stderr, "[ERR]: unlink(%s) failed: %s\n", client_pipe_path,
                strerror(errno));
        exit(EXIT_FAILURE);
    }

    // create pipe
    if (mkfifo(client_pipe_path, 0640) != 0) {
        fprintf(stderr, "[ERR]: mkfifo failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }    

    // open pipe for writing
    // this waits for someone to open it for reading
    // receive from workers
    fcli = open(client_pipe_path, O_RDONLY);
    if (fcli == -1) {
        fprintf(stderr, "[ERR]: open failed: %s\n", strerror(errno));
        exit(EXIT_FAILURE);
    }

    while(1) {
        if (read(fcli,&session_id,sizeof(int))!=-1)
            break;
    }
    

    return 0;
}

int tfs_unmount() {
    /* TODO: Implement this */
    char buffer[5];
    memset(buffer, '\0', 5);

    buffer[0] = (char)(TFS_OP_CODE_UNMOUNT + '0');
    memcpy(buffer + 1, &session_id, sizeof(int));

    send_msg(fserv, buffer, 5);

    while(1) {
        if (read(fcli,&session_id,sizeof(int)) != -1)
            break;
    }

    close(fcli);

    if (unlink(fcli) != 0 && errno != ENOENT) {
        fprintf(stderr, "[ERR]: unlink(%s) failed: %s\n", fcli,
                strerror(errno));
        exit(EXIT_FAILURE);
    }

    return 0;
}

int tfs_open(char const *name, int flags) {
    /* TODO: Implement this */

    char buffer[49];
    memset(buffer, '\0', 49);

    char op_code = TFS_OP_CODE_OPEN + '0';

    memcpy(buffer, &op_code, sizeof(char));

    memcpy(buffer + 1, &session_id, sizeof(int));

    memcpy(buffer + 5, name, 40);

    memcpy(buffer + 45, &flags, sizeof(int));
    
    send_msg(fserv, buffer, sizeof(buffer));

    while(1) {
        if (read(fcli,&session_id,sizeof(int)) != -1)
            break;
    }

    return 0;
}

int tfs_close(int fhandle) {
    /* TODO: Implement this */

    char buffer[9];
    memset(buffer, '\0', 9);

    buffer[0] = (char)(TFS_OP_CODE_CLOSE + '0');

    memcpy(buffer + 1, &session_id, sizeof(int));

    memcpy(buffer + 4, &fhandle, sizeof(int));

    send_msg(fserv, buffer, 9);

    while(1) {
        if (read(fcli,&session_id,sizeof(int)) != -1)
            break;
    }

    return 0;
}

ssize_t tfs_write(int fhandle, void const *buffer, size_t len) {
    /* TODO: Implement this */

    char buf[9+sizeof(size_t)+len];
    memset(buf, '\0', 9+sizeof(size_t)+len);
    char op_code = TFS_OP_CODE_WRITE;
    memcpy(buf, &op_code, 1);

    memcpy(buf + 1, &session_id, sizeof(int));

    memcpy(buf + 5, &fhandle, sizeof(int));

    memcpy(buf + 9, len, sizeof(size_t));

    memcpy(buf + 9 + sizeof(size_t), buffer, len);

    send_msg(fserv, buf, 9+sizeof(size_t)+len);

    while(1) {
        if (read(fcli,&session_id,sizeof(int)) != -1)
            break;
    }

    return 0;
}

ssize_t tfs_read(int fhandle, void *buffer, size_t len) {
    /* TODO: Implement this */

    char buf[9+sizeof(size_t)+len];
    memset(buffer, '\0', 9+sizeof(size_t));

    char op_code = TFS_OP_CODE_READ;
    memcpy(buf, &op_code, 1);

    memcpy(buf + 1, &session_id, sizeof(int));

    memcpy(buf + 5, &fhandle, sizeof(int));

    memcpy(buf + 9, len, sizeof(size_t));

    send_msg(fserv, buf, 9+sizeof(size_t));

    char receive_buf[sizeof(int)+len];
    memset(receive_buf, '\0', sizeof(int)+len);

    while(1) {
        if (read(fcli,receive_buf,sizeof(int)+len) != -1)
            break;
    }

    ssize_t leste = (ssize_t)(*receive_buf);
    memcpy(buffer,receive_buf + sizeof(ssize_t), len);

    
    return leste;
}

int tfs_shutdown_after_all_closed() {
    /* TODO: Implement this */

    char buffer[5];
    memset(buffer, '\0', 5);

    buffer[0] = (char)(TFS_OP_CODE_SHUTDOWN_AFTER_ALL_CLOSED + '0');
    memcpy(buffer + 1, &session_id, sizeof(int));

    send_msg(fserv, buffer, 5);

    while(1) {
        if (read(fcli,&session_id,sizeof(int)) != -1)
            break;
    }

    return 0;
}
