package pt.tecnico.distledger.adminclient;

import io.grpc.StatusRuntimeException;
import pt.tecnico.distledger.adminclient.grpc.AdminService;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;

import java.util.Scanner;

import static pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String ACTIVATE = "activate";
    private static final String DEACTIVATE = "deactivate";
    private static final String GET_LEDGER_STATE = "getLedgerState";
    private static final String GOSSIP = "gossip";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private final AdminService adminService;
    public CommandParser(AdminService adminService) {
        this.adminService = adminService;
    }
    void parseInput() {

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd = line.split(SPACE)[0];

            switch (cmd) {
                case ACTIVATE:
                    this.activate(line);
                    break;

                case DEACTIVATE:
                    this.deactivate(line);
                    break;

                case GET_LEDGER_STATE:
                    this.dump(line);
                    break;

                case GOSSIP:
                    this.gossip(line);
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

        adminService.shutDownAdminService();
    }

    private void activate(String line){
        AdminClientMain.debug("Call activate");
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        AdminClientMain.debug("Server: "+ server);

        try {
            this.adminService.activate();
            System.out.println("OK\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription());
        }
    }

    private void deactivate(String line){
        AdminClientMain.debug("Call deactivate");
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        AdminClientMain.debug("Server: "+ server);

        try {
            this.adminService.deactivate();
            System.out.println("OK\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription());
        }
    }

    private void dump(String line){
        AdminClientMain.debug("Call dump");
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }

        String server = split[1];
        AdminClientMain.debug("Server: "+ server);

        try {
            LedgerState ledgerState = adminService.getLedgerState();
            System.out.println("OK");
            System.out.println("ledgerState {");
            for (Operation op : ledgerState.getLedgerList()) {
                String leadgerString =
                        "  ledger {\n" +
                                "    type: \"" + op.getType() + "\"\n" +
                                "    userId: \"" + op.getUserId() + "\"\n";
                if(op.getType() == OP_TRANSFER_TO)
                    leadgerString +=
                            "    destUserId: \"" + op.getDestUserId() + "\"\n" +
                                    "    amount: \"" + op.getAmount() + "\"\n";
                leadgerString += "  }\n";
                System.out.print(leadgerString);
            }
            System.out.println("}\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " + e.getStatus().getDescription());
        }
    }

    @SuppressWarnings("unused")
    private void gossip(String line){
        /* TODO Phase-3 */
        System.out.println("TODO: implement gossip command (only for Phase-3)");
    }
    private void printUsage() {
        System.out.println("Usage:\n" +
                "- activate <server>\n" +
                "- deactivate <server>\n" +
                "- getLedgerState <server>\n" +
                "- gossip <server>\n" +
                "- exit\n");
    }
}
