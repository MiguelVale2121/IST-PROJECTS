package pt.tecnico.distledger.userclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.distledger.userclient.exception.ServerEntryNotFound;
import pt.ulisboa.tecnico.distledger.contract.NamingServer;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

public class UserService {
    private ManagedChannel channel;
    private NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;


    public UserService() {
        this.channel = ManagedChannelBuilder.forTarget("localhost:5001").usePlaintext().build();
        this.stub = NamingServerServiceGrpc.newBlockingStub(channel);
    }

    public NamingServer.ServerEntry searchForServer(String serverQualifier) throws StatusRuntimeException, ServerEntryNotFound {
        NamingServer.LookupResponse response =
                this.stub.lookup(NamingServer.LookupRequest.newBuilder()
                        .setServiceName("DistLedger")
                        .setServerQualifier(serverQualifier)
                        .build());

        if (response.getRetrievedServersCount() == 0)
            throw new ServerEntryNotFound(serverQualifier);

        return response.getRetrievedServers(0);
    }

    public void createAccount(String serverQualifier, String account) throws StatusRuntimeException, ServerEntryNotFound {
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() + ":" + server.getPort()).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub tmpStub = UserServiceGrpc.newBlockingStub(tmpChannel);

        tmpStub.createAccount(UserDistLedger.CreateAccountRequest.newBuilder()
                .setUserId(account).build());

        tmpChannel.shutdownNow();
    }

    public void deleteAccount(String serverQualifier, String account) throws StatusRuntimeException, ServerEntryNotFound {
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() + ":" + server.getPort()).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub tmpStub = UserServiceGrpc.newBlockingStub(tmpChannel);

        tmpStub.deleteAccount(UserDistLedger.DeleteAccountRequest.newBuilder()
                .setUserId(account).build());

        tmpChannel.shutdownNow();
    }

    public int balance(String serverQualifier, String account) throws StatusRuntimeException, ServerEntryNotFound {
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() + ":" + server.getPort()).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub tmpStub = UserServiceGrpc.newBlockingStub(tmpChannel);

        Integer balance = tmpStub.balance(
                UserDistLedger.BalanceRequest.newBuilder().setUserId(account).build()).getValue();

        tmpChannel.shutdownNow();

        return balance;
    }

    public void transferTo(String serverQualifier, String fromAccount, String destAccount, int amount) throws StatusRuntimeException, ServerEntryNotFound {
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() + ":" + server.getPort()).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub tmpStub = UserServiceGrpc.newBlockingStub(tmpChannel);

        tmpStub.transferTo(UserDistLedger.TransferToRequest.newBuilder()
                .setAccountFrom(fromAccount)
                .setAccountTo(destAccount)
                .setAmount(amount).build());

        tmpChannel.shutdownNow();
    }

    public void shutDownUserService() {
       this.channel.shutdownNow();
    }

}
