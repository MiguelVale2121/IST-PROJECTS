package pt.tecnico.distledger.userclient;

import io.grpc.StatusRuntimeException;
import pt.tecnico.distledger.userclient.grpc.UserService;

import java.util.Scanner;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String CREATE_ACCOUNT = "createAccount";
    private static final String DELETE_ACCOUNT = "deleteAccount";
    private static final String TRANSFER_TO = "transferTo";
    private static final String BALANCE = "balance";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private final UserService userService;

    public CommandParser(UserService userService) {
        this.userService = userService;
    }

    void parseInput() {

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd = line.split(SPACE)[0];

            try{
                switch (cmd) {
                    case CREATE_ACCOUNT:
                        this.createAccount(line);
                        break;

                    case DELETE_ACCOUNT:
                        this.deleteAccount(line);
                        break;

                    case TRANSFER_TO:
                        this.transferTo(line);
                        break;

                    case BALANCE:
                        this.balance(line);
                        break;

                    case HELP:
                        this.printUsage();
                        break;

                    case EXIT:
                        exit = true;
                        break;

                    default:
                        break;
                }
            }
            catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        userService.shutDownUserService();
    }

    private void createAccount(String line){
        UserClientMain.debug("Call createAccount");
        String[] split = line.split(SPACE);

        if (split.length != 3){
            this.printUsage();
            return;
        }

        String server = split[1];
        String username = split[2];
        UserClientMain.debug("Server: "+ server);
        UserClientMain.debug("Username: "+ username);

        try {
            userService.createAccount(username);
            System.out.println("OK\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription() + '\n');
        }
    }

    private void deleteAccount(String line){
        UserClientMain.debug("Call deleteAccount");
        String[] split = line.split(SPACE);

        if (split.length != 3){
            this.printUsage();
            return;
        }
        String server = split[1];
        String username = split[2];
        UserClientMain.debug("Server: "+ server);
        UserClientMain.debug("Username: "+ username);

        try {
            userService.deleteAccount(username);;
            System.out.println("OK\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription() + '\n');
        }
    }


    private void balance(String line){
        UserClientMain.debug("Call balance");
        String[] split = line.split(SPACE);

        if (split.length != 3){
            this.printUsage();
            return;
        }
        String server = split[1];
        String username = split[2];
        UserClientMain.debug("Server: "+ server);
        UserClientMain.debug("Username: "+ username);

        try {
            System.out.println("OK" + "\n" + userService.balance(username) + "\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription() + '\n');
        }
    }

    private void transferTo(String line){
        UserClientMain.debug("Call transferTo");
        String[] split = line.split(SPACE);

        if (split.length != 5){
            this.printUsage();
            return;
        }
        String server = split[1];
        String from = split[2];
        String dest = split[3];
        int amount = Integer.parseInt(split[4]);
        UserClientMain.debug("Server: "+ server);
        UserClientMain.debug("Source username: " + from);
        UserClientMain.debug("Destination username: " + dest);
        UserClientMain.debug("Amout: " + amount);

        try {
            userService.transferTo(from, dest, amount);
            System.out.println("OK\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription() + '\n');
        }
    }

    private void printUsage() {
        System.out.println("Usage:\n" +
                "- createAccount <server> <username>\n" +
                "- deleteAccount <server> <username>\n" +
                "- balance <server> <username>\n" +
                "- transferTo <server> <username_from> <username_to> <amount>\n" +
                "- exit\n");
    }
}