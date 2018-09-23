import greeter.GreeterServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            GreeterServer.start(port(args))
                    .waitIndefinitely();
        } catch (IOException e) {
            System.out.println("Failed to start server because: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse desired port: " + e.getMessage());
        }
    }

    private static int port(String[] args) throws NumberFormatException {
        if (args.length > 0) {
            return Integer.parseInt(args[0]);
        } else {
            return 1234;
        }
    }

}
