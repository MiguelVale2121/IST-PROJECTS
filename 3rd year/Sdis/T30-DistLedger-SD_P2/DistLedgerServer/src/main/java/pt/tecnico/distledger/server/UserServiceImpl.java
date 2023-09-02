package pt.tecnico.distledger.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.exception.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.NamingServer;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import static io.grpc.Status.*;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private final NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;
    private final ServerState serverState;

    public UserServiceImpl(ServerState serverState) {
        this.serverState = serverState;
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:5001").usePlaintext().build();
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

    public void sendState(String serverQualifier, DistLedgerCommonDefinitions.LedgerState newState )
            throws ServerEntryNotFound , StatusRuntimeException {
        NamingServer.ServerEntry server = searchForServer(serverQualifier);

        ManagedChannel tmpChannel = ManagedChannelBuilder.forTarget(
                server.getHost() + ":" + server.getPort()).usePlaintext().build();

        DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub tmpStub =
                DistLedgerCrossServerServiceGrpc.newBlockingStub(tmpChannel);

        tmpStub.propagateState(
                CrossServerDistLedger.PropagateStateRequest.newBuilder().setState(newState).build());

        tmpChannel.shutdownNow();
    }

    @Override
    public synchronized void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
        if (!serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        if (!serverState.isPrimary()) {
            responseObserver.onError(OUT_OF_RANGE.withDescription("Server is not primary.").asRuntimeException());
            return;
        }

        String account = request.getUserId();

        try {
            serverState.validOpCreateAccount(account);

            DistLedgerCommonDefinitions.LedgerState newState = DistLedgerCommonDefinitions.LedgerState.newBuilder()
                    .addLedger(
                            DistLedgerCommonDefinitions.Operation.newBuilder()
                                    .setType(DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT)
                                    .setUserId(account)
                                    .build())
                    .build();

            sendState("B" , newState);

            serverState.createAccount(account);

            CreateAccountResponse response = CreateAccountResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (StatusRuntimeException | ServerEntryNotFound e) {
            responseObserver.onError(OUT_OF_RANGE.withDescription(e.getMessage()).asRuntimeException());
        } catch (CannotCreateBrokerException | UserAlreadyExistsException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized void deleteAccount(DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver) {
        if (!this.serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        if (!serverState.isPrimary()) {
            responseObserver.onError(OUT_OF_RANGE.withDescription("Server is not primary.").asRuntimeException());
            return;
        }

        String userId = request.getUserId();

        try {
            serverState.validOpDeleteAccount(userId);

            DistLedgerCommonDefinitions.LedgerState newState = DistLedgerCommonDefinitions.LedgerState.newBuilder()
                    .addLedger(
                            DistLedgerCommonDefinitions.Operation.newBuilder()
                                    .setType(DistLedgerCommonDefinitions.OperationType.OP_DELETE_ACCOUNT)
                                    .setUserId(userId)
                                    .build())
                    .build();

            sendState("B" , newState);

            this.serverState.deleteAccount(userId);

            DeleteAccountResponse response = DeleteAccountResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (StatusRuntimeException | ServerEntryNotFound e) {
            responseObserver.onError(OUT_OF_RANGE.withDescription(e.getMessage()).asRuntimeException());
        } catch (CannotDeleteBrokerException | NoSuchUserException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (BalanceNotZeroException e) {
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        if (!this.serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        String account = request.getUserId();

        try {
            int result = this.serverState.balance(account);
            BalanceResponse response = BalanceResponse.newBuilder().setValue(result).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (NoSuchUserException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()); 
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public synchronized void transferTo(TransferToRequest request, StreamObserver<TransferToResponse> responseObserver) {
        if (!this.serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        if (!serverState.isPrimary()) {
            responseObserver.onError(OUT_OF_RANGE.withDescription("Server is not primary.").asRuntimeException());
            return;
        }

        String fromAccount = request.getAccountFrom();
        String destAccount = request.getAccountTo();
        int amount = request.getAmount();

        try {
            serverState.validOpTransferTo(fromAccount, destAccount, amount);

            DistLedgerCommonDefinitions.LedgerState newState = DistLedgerCommonDefinitions.LedgerState.newBuilder()
                    .addLedger(
                            DistLedgerCommonDefinitions.Operation.newBuilder()
                                    .setType(DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO)
                                    .setUserId(fromAccount)
                                    .setDestUserId(destAccount)
                                    .setAmount(amount)
                                    .build())
                    .build();

            sendState("B" , newState);

            serverState.transferTo(fromAccount,destAccount,amount);

            TransferToResponse response = TransferToResponse.newBuilder().build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException | ServerEntryNotFound e) {
            responseObserver.onError(OUT_OF_RANGE.withDescription(e.getMessage()).asRuntimeException());
        } catch (NoSuchUserException | InsufficientFundsException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

