package pt.tecnico.distledger.server;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.tecnico.distledger.server.exception.ServerAlreadyActiveException;
import pt.tecnico.distledger.server.exception.ServerAlreadyInactiveException;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc.*;

import java.util.List;

import static io.grpc.Status.*;
import static pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType.*;

public class AdminServiceImpl extends AdminServiceImplBase {
    private final ServerState serverState;

    public AdminServiceImpl(ServerState serverState) {
        this.serverState = serverState;
    }

    @Override
    public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
        try {
            serverState.changeModeToActive();

            ActivateResponse response = ActivateResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerAlreadyActiveException e) {
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
        try {
            serverState.changeModeToInactive();

            DeactivateResponse response = DeactivateResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerAlreadyInactiveException e) {
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getLedgerState(getLedgerStateRequest request, StreamObserver<getLedgerStateResponse> responseObserver) {
        List<Operation> ledger = serverState.getLedger();
        DistLedgerCommonDefinitions.LedgerState.Builder ledgerState =
                DistLedgerCommonDefinitions.LedgerState.newBuilder();

        try {
            for (Operation op : ledger) {
                DistLedgerCommonDefinitions.Operation.Builder opContent = DistLedgerCommonDefinitions.Operation.newBuilder();
                opContent.setUserId(op.getAccount());

                switch (op.getClass().getSimpleName()) {
                    case "CreateOp":
                        opContent.setType(OP_CREATE_ACCOUNT);

                        break;
                    case "DeleteOp":
                        opContent.setType(OP_DELETE_ACCOUNT);

                        break;
                    case "TransferOp":
                        opContent.setType(OP_TRANSFER_TO);

                        opContent.setDestUserId(((TransferOp) op).getDestAccount());
                        opContent.setAmount(((TransferOp) op).getAmount());

                        break;
                }

                ledgerState.addLedger(opContent.build());
            }

            getLedgerStateResponse response = getLedgerStateResponse.newBuilder().setLedgerState(ledgerState).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
