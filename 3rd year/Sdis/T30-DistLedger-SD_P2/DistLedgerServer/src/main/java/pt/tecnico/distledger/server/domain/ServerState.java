package pt.tecnico.distledger.server.domain;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;

import pt.tecnico.distledger.server.exception.*;

import pt.tecnico.distledger.server.exception.BalanceNotZeroException;
import pt.tecnico.distledger.server.exception.InsufficientFundsException;
import pt.tecnico.distledger.server.exception.NoSuchUserException;
import pt.tecnico.distledger.server.exception.UserAlreadyExistsException;
import pt.tecnico.distledger.server.exception.UnspecifiedOperationTypeException;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


public class ServerState {

    enum Mode {
        ACTIVE,
        INACTIVE
    }

    private List<Operation> ledger;

    private HashMap<String, User> users;

    private Mode mode;

    private String qualifier;

    public ServerState() {
        this.users = new HashMap<>();
        this.ledger = new ArrayList<>();
        this.mode = Mode.ACTIVE;

        User broker = new User("broker", 1000);
        this.users.put(broker.getUserID(), broker);
    }

    public void setQualifier(String serverQualifier) {
           qualifier = serverQualifier;
    }

    public synchronized Boolean isPrimary() {
        return qualifier.equals("A");
    }

    public synchronized List<Operation> getLedger() {
            return this.ledger;
    }

    public synchronized void validOpCreateAccount(String account) throws Exception {
        if (account.equals("broker"))
            throw new CannotCreateBrokerException();
        if (users.containsKey(account))
            throw new UserAlreadyExistsException();

    }
    public synchronized void createAccount(String account) throws Exception {
        validOpCreateAccount(account);

        User user = new User(account);
        users.put(account, user);

        Operation createOp = new CreateOp(account);
        ledger.add(createOp);
    }


    public synchronized int balance(String account) throws Exception {
        if (users.containsKey(account)) {
            return users.get(account).getBalance();
        } else {
            throw new NoSuchUserException();
        }
    }

    public synchronized void validOpDeleteAccount(String account)throws Exception {
        if (account.equals("broker"))
            throw new CannotDeleteBrokerException();
        if (!users.containsKey(account))
            throw new NoSuchUserException();
        if (users.get(account).getBalance() != 0)
            throw new BalanceNotZeroException();
    }

    public synchronized void deleteAccount(String account) throws Exception {
        validOpDeleteAccount(account);

        users.remove(account);

        Operation deleteOperation = new DeleteOp(account);
        ledger.add(deleteOperation);
    }

    public synchronized void validOpTransferTo(String fromAccount, String destAccount, int amount) throws Exception {
        if(!users.containsKey(fromAccount) || !users.containsKey(destAccount)){
            throw new NoSuchUserException();
        }
    }

    public synchronized void transferTo(String fromAccount, String destAccount, int amount) throws Exception{
        validOpTransferTo(fromAccount, destAccount, amount);

        User from = users.get(fromAccount);
        User dest = users.get(destAccount);

        if (from == null || from.getBalance() < amount || dest == null) {
            throw new InsufficientFundsException();
        }

        from.setBalance(from.getBalance()-amount);
        dest.setBalance(dest.getBalance()+amount);

        Operation transferOperation = new TransferOp(fromAccount, destAccount, amount);
        ledger.add(transferOperation);

    }

    public synchronized boolean isActive() {
        return mode.equals(Mode.ACTIVE);
    }

    public synchronized void changeModeToInactive() throws Exception {
        if (mode.equals(Mode.INACTIVE))
            throw new ServerAlreadyInactiveException();
        mode = Mode.INACTIVE;
    }

    public synchronized void changeModeToActive() throws Exception {
        if (mode.equals(Mode.ACTIVE))
            throw new ServerAlreadyActiveException();
        mode = Mode.ACTIVE;
    }

    public synchronized void implementState(DistLedgerCommonDefinitions.Operation operation) throws Exception {
        switch (operation.getType()) {
            case OP_CREATE_ACCOUNT:
                createAccount(operation.getUserId());
                break;
            case OP_DELETE_ACCOUNT:
                deleteAccount(operation.getUserId());
                break;
            case OP_TRANSFER_TO:
                transferTo(operation.getUserId(), operation.getDestUserId(), operation.getAmount());
                break;
            default:
                throw new UnspecifiedOperationTypeException();
        }
    }
}
