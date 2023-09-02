package pt.tecnico.distledger.server;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.exception.BalanceNotZeroException;
import pt.tecnico.distledger.server.exception.CannotCreateBrokerException;
import pt.tecnico.distledger.server.exception.CannotDeleteBrokerException;
import pt.tecnico.distledger.server.exception.InsufficientFundsException;
import pt.tecnico.distledger.server.exception.NoSuchUserException;
import pt.tecnico.distledger.server.exception.UserAlreadyExistsException;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import static io.grpc.Status.*;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private final ServerState serverState;

    public UserServiceImpl(ServerState serverState) {
        this.serverState = serverState;
    }

    @Override
    public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
        if (!this.serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        String account = request.getUserId();

        try {
            serverState.createAccount(account);

            CreateAccountResponse response = CreateAccountResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (CannotCreateBrokerException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (UserAlreadyExistsException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteAccount(DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver) {
        if (!this.serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        String userId = request.getUserId();

        try {
            this.serverState.deleteAccount(userId);

            DeleteAccountResponse response = DeleteAccountResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (CannotDeleteBrokerException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (NoSuchUserException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (BalanceNotZeroException e) {
            responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        String account = request.getUserId();
        int result = -1;
        try {
            result = this.serverState.balance(account);
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
    public void transferTo(TransferToRequest request, StreamObserver<TransferToResponse> responseObserver) {
        if (!this.serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        String fromAccount = request.getAccountFrom();
        String destAccount = request.getAccountTo();
        int amount = request.getAmount();

        try {
            serverState.transferTo(fromAccount,destAccount,amount);

            TransferToResponse response = TransferToResponse.newBuilder().build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (NoSuchUserException | InsufficientFundsException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

