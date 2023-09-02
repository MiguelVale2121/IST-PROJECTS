package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;

import pt.tecnico.distledger.server.exception.*;

import pt.tecnico.distledger.server.exception.BalanceNotZeroException;
import pt.tecnico.distledger.server.exception.InsufficientFundsException;
import pt.tecnico.distledger.server.exception.NoSuchUserException;
import pt.tecnico.distledger.server.exception.UserAlreadyExistsException;

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

    public ServerState() {
        this.users = new HashMap<>();
        this.ledger = new ArrayList<>();
        this.mode = Mode.ACTIVE;

        User broker = new User("broker", 1000);
        this.users.put(broker.getUserID(), broker);
    }

    public List<Operation> getLedger() {
        return this.ledger;
    }

    public void createAccount(String account) throws Exception {
        if (account.equals("broker"))
            throw new CannotCreateBrokerException();
        if (users.containsKey(account))
            throw new UserAlreadyExistsException();

        User user = new User(account);
        users.put(account, user);

        Operation createOp = new CreateOp(account);
        ledger.add(createOp);
    }


    public int balance(String account) throws Exception {
        if (users.containsKey(account)) {
            int balance = users.get(account).getBalance();
            return balance;
        } else {
            throw new NoSuchUserException();
        }
    }

    public void deleteAccount(String account) throws Exception {
        if (account.equals("broker"))
            throw new CannotDeleteBrokerException();
        if (!users.containsKey(account))
            throw new NoSuchUserException();
        if (users.get(account).getBalance() != 0)
            throw new BalanceNotZeroException();

        users.remove(account);

        Operation deleteOperation = new DeleteOp(account);
        ledger.add(deleteOperation);
    }

    public void transferTo(String fromAccount, String destAccount, int amount) throws Exception{

        if(!users.containsKey(fromAccount) || !users.containsKey(destAccount)){
            throw new NoSuchUserException();
        }

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

    public boolean isActive() {
        return this.mode == Mode.ACTIVE;
    }

    public void changeModeToInactive() throws Exception {
        if (this.mode == Mode.INACTIVE)
            throw new ServerAlreadyInactiveException();
        this.mode = Mode.INACTIVE;
    }

    public void changeModeToActive() throws Exception {
        if (this.mode == Mode.ACTIVE)
            throw new ServerAlreadyActiveException();
        this.mode = Mode.ACTIVE;
    }

}
