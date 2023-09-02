package pt.tecnico.distledger.server;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.distledger.server.domain.ServerState;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        // receive and print arguments
        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }

        // check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            return;
        }

        final int port = Integer.parseInt(args[0]);
        ServerState serverState = new ServerState();
        final BindableService userImpl = new UserServiceImpl(serverState);
        final BindableService adminImpl = new AdminServiceImpl(serverState);

        // Create a new server to listen on port
        Server server = ServerBuilder.forPort(port).addService(userImpl).addService(adminImpl).build();

        // Start the servers
        server.start();

        // Server threads are running in the background.
        System.out.println("Server started");

        // Do not exit the main thread. Wait until server is terminated.
        server.awaitTermination();
    }

}

