package pt.tecnico.distledger.userclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

public class UserService {
    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub stub;

    public UserService(String target) {
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        this.stub = UserServiceGrpc.newBlockingStub(channel);
    }

    public void createAccount(String account) throws StatusRuntimeException  {
        this.stub.createAccount(CreateAccountRequest.newBuilder()
                .setUserId(account).build());
    }

    public void deleteAccount(String account) throws StatusRuntimeException {
        this.stub.deleteAccount(DeleteAccountRequest.newBuilder()
                .setUserId(account).build());
    }

    public int balance(String account) throws StatusRuntimeException {
        return this.stub.balance(BalanceRequest.newBuilder().setUserId(account).build()).getValue();
    }

    public void transferTo(String fromAccount, String destAccount, int amount) throws StatusRuntimeException {
        this.stub.transferTo(TransferToRequest.newBuilder()
                .setAccountFrom(fromAccount)
                .setAccountTo(destAccount)
                .setAmount(amount).build());
    }

    public void shutDownUserService() {
       this.channel.shutdownNow();
    }

}
