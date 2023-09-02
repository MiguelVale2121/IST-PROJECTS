package pt.tecnico.distledger.server;


import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;

import static io.grpc.Status.UNAVAILABLE;

public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

    private final ServerState serverState;
    public CrossServerServiceImpl(ServerState serverState) {
        this.serverState = serverState;
    }

    @Override
    public synchronized void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver) {
        if (!serverState.isActive()) {
            responseObserver.onError(UNAVAILABLE.withDescription("Server is deactivate.").asRuntimeException());
            return;
        }

        try {
            serverState.implementState(request.getState().getLedger(0));
            PropagateStateResponse response = PropagateStateResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
