package pt.tecnico.distledger.userclient;


import pt.tecnico.distledger.userclient.grpc.UserService;

public class UserClientMain {
    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

    /** Helper method to print debug messages. */
    public static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.println(debugMessage);
    }
    public static void main(String[] args) {

        System.out.println(UserClientMain.class.getSimpleName());

        // receive and print arguments
        debug(String.format("Received %d arguments", args.length));
        for (int i = 0; i < args.length; i++) {
            debug(String.format("arg[%d] = %s", i, args[i]));
        }

        // check arguments
        if (args.length != 2) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: mvn exec:java -Dexec.args=<host> <port>");
            return;
        }

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        debug("Target: " + host + ":" + port);

        CommandParser parser = new CommandParser(new UserService(host+":"+port));
        parser.parseInput();

    }
}
