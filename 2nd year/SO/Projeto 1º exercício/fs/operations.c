# include "operations.h"
# include <stdbool.h>
# include <stdio.h>
# include <stdlib.h>
# include <string.h>
# include <assert.h>
# include <math.h>
# include <threads.h>
# include <pthread.h>

pthread_mutex_t mutex_open = PTHREAD_MUTEX_INITIALIZER;

int tfs_init() {
    state_init();

    /* create root inode */
    int root = inode_create(T_DIRECTORY);
    if (root != ROOT_DIR_INUM) {
        return -1;
    }

    return 0;
}

int tfs_destroy() {
    
    state_destroy();
    return 0;
}

/*sees if a string is a valid path*/
static bool valid_pathname(char const *name) {

    return name != NULL && strlen(name) > 1 && name[0] == '/';

}

/*searches for a file in the file system*/
int tfs_lookup(char const *name) {

    if (!valid_pathname(name)) {
        return -1;
    }

    // skip the initial '/' character
    name++;

    return find_in_dir(ROOT_DIR_INUM, name);
}


int tfs_open(char const *name, int flags) {
    int inum;
    size_t offset;

    pthread_mutex_lock(&mutex_open);

    /* Checks if the path name is valid */
    if (!valid_pathname(name)) {
        pthread_mutex_unlock(&mutex_open);
        return -1;
    }

    /*Sees if the file is in the system*/
    inum = tfs_lookup(name);

    if (inum >= 0) {

        /*The file already exists*/
        inode_t *inode = inode_get(inum);
        if (inode == NULL) {
            pthread_mutex_unlock(&mutex_open);
            return -1;
        }

        /*Trucate (if requested)*/
        if (flags & TFS_O_TRUNC) {

            if (inode->i_size > 0) {

                if (data_block_free(inode->i_data_block[0]) == -1) {
                    pthread_mutex_unlock(&mutex_open);
                    return -1;
                }
                inode->i_size = 0;

            }
        }
        /*Determine initial offset*/
        if (flags & TFS_O_APPEND) {
            offset = inode->i_size;
        } 
        
        else {
            offset = 0;
        }
    } 
    
    else if (flags & TFS_O_CREAT) {

        /*The file doesn't exist; the flags specify that it should be created*/
        /*Create inode*/
        inum = inode_create(T_FILE);
        if (inum == -1) {
            pthread_mutex_unlock(&mutex_open);
            return -1;
        }
        /* Add entry in the root directory */
        if (add_dir_entry(ROOT_DIR_INUM, inum, name + 1) == -1) {
            inode_delete(inum);
            pthread_mutex_unlock(&mutex_open);
            return -1;
        }
        offset = 0;

    } 
    
    else {

        pthread_mutex_unlock(&mutex_open);
        return -1;
    }

    pthread_mutex_unlock(&mutex_open);

    /* Finally, add entry to the open file table and
     * return the corresponding handle */
    return add_to_open_file_table(inum, offset);

    /* Note: for simplification, if file was created with TFS_O_CREAT and there
     * is an error adding an entry to the open file table, the file is not
     * opened but it remains created */
}

/*Closes the system*/
int tfs_close(int fhandle) { return remove_from_open_file_table(fhandle); }

ssize_t tfs_write(int fhandle, void const *buffer, size_t to_write) {

    /*Gets the pointer to the wanted file*/
    open_file_entry_t *file = get_open_file_entry(fhandle);
    if (file == NULL) return -1;

    /*Holds the index of the current block*/
    int block_index = (int)floor((int)(file->of_offset)/BLOCK_SIZE);

    /*Holds the position of the last relevant byte of the file*/
    int block_position = (int)file->of_offset - block_index*BLOCK_SIZE;

    /*From the open file table entry, we get the inode*/
    inode_t *inode = inode_get(file->of_inumber);
    if (inode == NULL) {
        return -1;
    }

    pthread_mutex_lock(&inode->mutex_inode_struct);

    /*Holds the number of blocks we need to add*/
    int block_qt = (int)floor(((int)to_write + block_position)/BLOCK_SIZE);


    if (inode->i_data_block[0] == -1) inode->i_data_block[0] = data_block_alloc();  

    /* Allocates blocks */
    for (int i = 1 ; i <= block_qt ; i++) {

        /*Allocates memory for up to 11 blocks*/
        if ((block_index + i) <= BLOCK_QT) {
            inode->i_data_block[block_index + i] = data_block_alloc();    
        }

        else {

            /*If the block we want to write exceeds the maximum number of blocks break*/
            if ((block_index + i - BLOCK_QT) > BLOCK_SIZE/sizeof(int)) { break; }

            /*Allocates memory for the blocks after 10*/
            int *extra_block = data_block_get(inode->i_data_block[BLOCK_QT]);
            if (extra_block == NULL) {
                pthread_mutex_unlock(&inode->mutex_inode_struct);
                return -1;
            }

            extra_block[block_index + i - BLOCK_QT] = data_block_alloc();

        }
    }

    /*Counts the number of itterations of the while loop*/
    int while_counter = 0;

    /*Holds the written bytes*/
    int written = 0;

    int extra = 0;
    
    /*If we only have to write one block*/
    if (block_qt == 0) {
        extra = block_position;
    }

    /*Writes in all the blocks we want to except the last*/
    while (block_qt > 0) {

        if (!while_counter) {

            if (block_index < BLOCK_QT) {

                void *block = data_block_get(inode->i_data_block[block_index]);
                if (block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    return -1;
                }

                memcpy(block + block_position, buffer, BLOCK_SIZE - (size_t)block_position);
                written += BLOCK_SIZE-block_position;

            }

            else {

                int *extra_block = data_block_get(inode->i_data_block[BLOCK_QT]);
                if (extra_block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    return -1;
                }

                void *block = data_block_get(extra_block[block_index - BLOCK_QT]);
                if (block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    return -1;
                }

                memcpy(block + block_position, buffer, BLOCK_SIZE - (size_t)block_position);
                written += BLOCK_SIZE - block_position;

            }
        }
        else {

            if (block_index + while_counter < BLOCK_QT) {

                void *block = data_block_get(inode->i_data_block[block_index + while_counter]);
                if (block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    return -1;
                }

                memcpy(block, buffer + written, BLOCK_SIZE);
                written += BLOCK_SIZE;

            }

            else {

                int *extra_block = data_block_get(inode->i_data_block[BLOCK_QT]);
                if (extra_block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    return -1;
                }

                void *block = data_block_get(extra_block[block_index + while_counter - BLOCK_QT]);
                if (block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    return -1;
                }

                memcpy(block, buffer + written, BLOCK_SIZE);
                written += BLOCK_SIZE;

            }
        }
        while_counter += 1;

        /*Updates the number of blocks left to write*/
        block_qt -= 1;
    }

    /*Holds the number of bytes left to write*/
    int bytes_left = (int)to_write - written;

    /*Writes in the last block*/
    if (block_index + while_counter < BLOCK_QT) {

        void *block = data_block_get(inode->i_data_block[block_index + while_counter]);
        if (block == NULL) {
            pthread_mutex_unlock(&inode->mutex_inode_struct);
            return -1;
        }

        memcpy(block + extra, buffer + written, (size_t)bytes_left);
        written += bytes_left;

    }

    else {

        int *extra_block = data_block_get(inode->i_data_block[BLOCK_QT]);
        if (extra_block == NULL) {
            pthread_mutex_unlock(&inode->mutex_inode_struct);
            return -1;
        }

        void *block = data_block_get(extra_block[block_index + while_counter - BLOCK_QT]);
        if (block == NULL) {
            pthread_mutex_unlock(&inode->mutex_inode_struct);
            return -1;
        }

        memcpy(block + extra, buffer + written, (size_t)bytes_left);
        written += bytes_left;

    }

    /*Updates the file ofset*/
    file->of_offset += (size_t)written;

    /*If the ofset is larger then the size, update the size*/
    if (file->of_offset > inode->i_size) {
        inode->i_size = file->of_offset;
    }

    pthread_mutex_unlock(&inode->mutex_inode_struct);

    /*Returns the number of bytes written*/
    return (ssize_t)written;

}

ssize_t tfs_read(int fhandle, void *buffer, size_t len) {

    /*Gets the pointer to the wanted file*/
    open_file_entry_t *file = get_open_file_entry(fhandle);
    if (file == NULL) return -1;

    pthread_mutex_lock(&file->mutex_of);

    /*Holds the index of the last written block*/
    int block_index = (int)floor((int)(file->of_offset)/BLOCK_SIZE);

    /*Holds the position of the last relevant byte of the file*/
    int block_position = (int)file->of_offset - block_index*BLOCK_SIZE;

    /*From the open file table entry, we get the inode*/
    inode_t *inode = inode_get(file->of_inumber);
    if (inode == NULL) {
        pthread_mutex_unlock(&file->mutex_of);
        return -1;
    }

    pthread_mutex_lock(&inode->mutex_inode_struct);

    size_t to_read = inode->i_size - file->of_offset;
    if (to_read > len) {
        to_read = len;
    }

    /*Holds the number of blocks we need to read*/
    int block_qt = (int)floor(((int)to_read + block_position)/BLOCK_SIZE);

    /*Counts the number of itterations of the while loop*/
    int while_counter = 0;

    /*Holds the written bytes*/
    int read = 0;

    int extra = 0;

    /*If we only have to write one block*/
    if (block_qt == 0) {
        extra = block_position;
    }


    /*Reads all the blocks we want to except the last*/
    while (block_qt > 0) {

        if (!while_counter) {

            if (block_index < BLOCK_QT) {

                void *block = data_block_get(inode->i_data_block[block_index]);
                if (block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    pthread_mutex_unlock(&file->mutex_of);
                    return -1;
                }

                memcpy(buffer , block + block_position, BLOCK_SIZE - (size_t)block_position);
                read += BLOCK_SIZE-block_position;

            }

            else {

                int *extra_block = data_block_get(inode->i_data_block[BLOCK_QT]);
                if (extra_block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    pthread_mutex_unlock(&file->mutex_of);
                    return -1;
                }

                void *block = data_block_get(extra_block[block_index - BLOCK_QT]);
                if (block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    pthread_mutex_unlock(&file->mutex_of);
                    return -1;
                }

                memcpy(buffer, block + block_position, BLOCK_SIZE - (size_t)block_position);
                read += BLOCK_SIZE - block_position;

            }
        }
        else {

            if (block_index + while_counter < BLOCK_QT) {

                void *block = data_block_get(inode->i_data_block[block_index + while_counter]);
                if (block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    pthread_mutex_unlock(&file->mutex_of);
                    return -1;
                }

                memcpy(buffer + read, block, BLOCK_SIZE);
                read += BLOCK_SIZE;

            }

            else {

                int *extra_block = data_block_get(inode->i_data_block[BLOCK_QT]);
                if (extra_block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    pthread_mutex_unlock(&file->mutex_of);
                    return -1;
                }

                void *block = data_block_get(extra_block[block_index + while_counter - BLOCK_QT]);
                if (block == NULL) {
                    pthread_mutex_unlock(&inode->mutex_inode_struct);
                    pthread_mutex_unlock(&file->mutex_of);
                    return -1;
                }

                memcpy(buffer + read, block , BLOCK_SIZE);
                read += BLOCK_SIZE;

            }
        }
        while_counter += 1;

        /*Updates the number of blocks left to read*/
        block_qt -= 1;
    }

    /*Holds the number of bytes left to read*/
    int bytes_left = (int)to_read - read;

    if (block_index + while_counter < BLOCK_QT) {

        void *block = data_block_get(inode->i_data_block[block_index + while_counter]);
        if (block == NULL) {
            pthread_mutex_unlock(&inode->mutex_inode_struct);
            pthread_mutex_unlock(&file->mutex_of);
            return -1;
        }

        memcpy(buffer + read, block + extra, (size_t)bytes_left);
        read += bytes_left;

    }
    else {

        int *extra_block = data_block_get(inode->i_data_block[BLOCK_QT]);
        if (extra_block == NULL) {
            pthread_mutex_unlock(&inode->mutex_inode_struct);
            pthread_mutex_unlock(&file->mutex_of);
            return -1;
        }

        void *block = data_block_get(extra_block[block_index + while_counter - BLOCK_QT]);
        if (block == NULL) {
            pthread_mutex_unlock(&inode->mutex_inode_struct);
            pthread_mutex_unlock(&file->mutex_of);
            return -1;
        }

        memcpy(buffer + read, block + extra, (size_t)bytes_left);
        read += bytes_left;

    }

    /*Updates the file ofset*/
    file->of_offset += (size_t)read;

    pthread_mutex_unlock(&inode->mutex_inode_struct);
    pthread_mutex_unlock(&file->mutex_of);

    /*Returns the number of bytes read*/
    return (ssize_t)read;

}



int tfs_copy_to_external_fs(char const *source_path, char const *dest_path) {

    /*Opens the destination file*/
    FILE *dest_file = fopen(dest_path,"w");
    if (dest_file == NULL) return -1; 

    /*Buffer variable that holds the data of the source file*/
    char buffer[200];

    /*Opens the source file in the tfs*/
    int fhandle = tfs_open(source_path, 0);
    if (fhandle == -1) {
        fclose(dest_file);
        return -1;
    }

    /*Gets the pointer to the source file*/
    open_file_entry_t *file = get_open_file_entry(fhandle);
    if (file == NULL) {
        tfs_close(fhandle);
        fclose(dest_file);
        return -1;
    }

    /*Holds the number of read bytes in each iteration of the while loop*/
    ssize_t bytes_read;

    /*Holds the total bytes read;
     *To use in the termination condition of the while loop*/
    ssize_t n = 0;

    /*Holds the return value of fwrite(...);
     *To see if the writing succeeded*/
    size_t fflag = 0;

    /*From the open file table entry, we get the inode*/
    inode_t *node = inode_get(file->of_inumber);
    if (node == NULL) {
        tfs_close(fhandle);
        fclose(dest_file);
        return -1;
    }
    
    /*Holds the size of the file to copy*/
    size_t file_size = node->i_size;

    /*Copies 200 bytes at a time until all bytes are copied*/
    do{

        /*Reads 200 bytes from the source file and stores them in the buffer*/
        bytes_read = tfs_read(fhandle, buffer, sizeof(buffer));
        if (bytes_read < 0) {
            tfs_close(fhandle);
            fclose(dest_file);
            return -1;
        }

        /*Writes the read bytes in the destination file*/
        fflag = fwrite(buffer, sizeof(char), (size_t)bytes_read, dest_file);
        if (fflag == -1) {
            tfs_close(fhandle);
            fclose(dest_file);
            return -1;
        }

        /*Increments n with the bytes read*/
        n += bytes_read;

    } while (n < file_size);

    /*Closes both files*/
    tfs_close(fhandle);
    fclose(dest_file);

    return 0;
}


