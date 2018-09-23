import greeter.GreeterServer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;

import static java.text.MessageFormat.format;

public class Main {
    public static void main(String[] args) {
        try {
            int port = port(args);
            System.out.println(format("Starting server at port {0,number,#}", port));
            GreeterServer.start(port)
                    .waitIndefinitely();
        } catch (IOException e) {
            System.out.println(format("Failed to start server because: {0}", e.getMessage()));
        } catch (NumberFormatException e) {
            System.out.println(format("Failed to parse desired port: {0}", e.getMessage()));
        }
    }

    private static int port(String[] args) throws NumberFormatException {
        return Arrays.stream(args)
                .limit(1)
                .map(Integer::parseInt)
                .findFirst()
                .orElse(1234);
    }

}
