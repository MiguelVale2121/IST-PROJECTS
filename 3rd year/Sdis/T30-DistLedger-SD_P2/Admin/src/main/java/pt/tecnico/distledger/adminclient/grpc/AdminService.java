package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc.*;

public class AdminService {

    private final ManagedChannel channel;
    private final AdminServiceBlockingStub stub;

    public AdminService(String target) {
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        this.stub = AdminServiceGrpc.newBlockingStub(channel);
    }


    public void activate() throws StatusRuntimeException {
        this.stub.activate(ActivateRequest.newBuilder().build());
    }

    public void deactivate() throws StatusRuntimeException {
        this.stub.deactivate(DeactivateRequest.newBuilder().build());
    }

    public LedgerState getLedgerState() throws StatusRuntimeException {
        return (this.stub.getLedgerState(getLedgerStateRequest.newBuilder().build())).getLedgerState();
    }

}
