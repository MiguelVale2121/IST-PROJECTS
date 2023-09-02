package pt.tecnico.distledger.adminclient.grpc;

import java.util.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.distledger.contract.NamingServer.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc.*;

public class NamingService {

    private final ManagedChannel channel;
    private final NamingServerServiceBlockingStub stub;

    public NamingService() {
        this.channel = ManagedChannelBuilder.forTarget("localhost:5001").usePlaintext().build();
        this.stub = NamingServerServiceGrpc.newBlockingStub(channel);
    }   

    public List<ServerEntry> lookup(String service, String qualifier) throws StatusRuntimeException {
        return (this.stub.lookup(LookupRequest.newBuilder().setServiceName(service).setServerQualifier(qualifier).build())).getRetrievedServersList();
    }

    public void shutDownNamingService() {
        this.channel.shutdownNow();
    }
}
