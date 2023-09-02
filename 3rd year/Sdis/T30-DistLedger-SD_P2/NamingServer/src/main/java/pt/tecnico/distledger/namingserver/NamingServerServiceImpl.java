package pt.tecnico.distledger.namingserver;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.namingserver.domain.NamingServerState;
import pt.tecnico.distledger.namingserver.domain.ServerEntry;
import pt.tecnico.distledger.namingserver.exception.NotPossibleToRemove;
import pt.tecnico.distledger.namingserver.exception.ServerAlreadyRegistered;
import pt.ulisboa.tecnico.distledger.contract.NamingServer;
import pt.ulisboa.tecnico.distledger.contract.NamingServer.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc.*;

import static io.grpc.Status.FAILED_PRECONDITION;
import java.util.*;
import java.util.stream.Collectors;


public class NamingServerServiceImpl extends NamingServerServiceImplBase  {

    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

    private final NamingServerState namingServerState;

    public NamingServerServiceImpl(NamingServerState namingServerState) {
        this.namingServerState = namingServerState;
    }

    public static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.println(debugMessage);
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        NamingServerServiceImpl.debug("Call register.");

        String serviceName = request.getServiceName();
        String serverHost = request.getServerHost();
        String serverPort = request.getServerPort();
        String serverQualifier = request.getServerQualifier();

        NamingServerServiceImpl.debug("Received:\n" +
                "   service name: " + serviceName + "\n" +
                "   server host: " + serverHost + "\n" +
                "   server port: " + serverPort + "\n" +
                "   server qualifier: " + serverQualifier);

        try {
            this.namingServerState.registerServer(serviceName,
                    new ServerEntry(serverHost, serverPort, serverQualifier));

            RegisterResponse response = RegisterResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            NamingServerServiceImpl.debug("Successfully registered.");
        } catch (ServerAlreadyRegistered e) {
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void lookup(LookupRequest request, StreamObserver<LookupResponse> responseObserver) {
        NamingServerServiceImpl.debug("Call lookup.");

        String serviceName = request.getServiceName();
        String serverQualifier = request.getServerQualifier();

        NamingServerServiceImpl.debug("Received:\n" +
                "   service name: " + serviceName + "\n" +
                "   server qualifier: " + serverQualifier);

        List<ServerEntry> retrievedServers = namingServerState.lookupServer(serviceName, serverQualifier);

        NamingServer.LookupResponse response = NamingServer.LookupResponse.newBuilder()
                .addAllRetrievedServers(retrievedServers.stream().map(ServerEntry::proto).collect(Collectors.toList()))
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public synchronized void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver){
        NamingServerServiceImpl.debug("Call delete.");

        String serviceName = request.getServiceName();
        String host = request.getServerHost();
        String port = request.getServerPort();

        try {
            this.namingServerState.deleteServer(serviceName,host,port);

            DeleteResponse response = DeleteResponse.newBuilder().build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (NotPossibleToRemove e){
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
