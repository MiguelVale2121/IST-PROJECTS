# Operating Systems Project: TecnicoFS

This repository contains the code and documentation for the Operating Systems project, which consists of two parts. The project focuses on implementing a simplified user-mode file system called TecnicoFS.

## Part 1: Base Project (Starting Point)

The base project is TecnicoFS, a simplified user-mode file system. It is implemented as a library that can be used by any client process that wants to have a private instance of a file system in which it can store its data.

### Key Features (Part 1):
- Implementation of a user-mode file system.
- Access through a library.
- Support for basic file system operations, such as file creation, reading, writing, and deletion.
- Operation in single-client mode.

## Part 2: Extension for Concurrent Operation

The second exercise of the Operating Systems project aims to extend the TecnicoFS file system with the capability to serve multiple client processes concurrently. For this purpose, TecnicoFS will no longer be a simple library but will become an autonomous server process to which different client processes can connect and send messages with operation requests.

### Key Features (Part 2):
- Transformation of TecnicoFS into an autonomous server.
- Support for multiple client processes, allowing concurrent operations.
- Interprocess communication to handle operation requests.

## Grade Received: 17.26

