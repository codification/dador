package greeter;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.io.IOException;
import java.util.concurrent.Executors;

public class GreeterServer {

    private final Server server;

    private GreeterServer(Server server) {
        this.server = server;
    }

    private void shutDown() {
        server.shutdownNow();
    }

    public void waitIndefinitely() {
        try {
            server.awaitTermination();
        } catch (InterruptedException ignored) {
        } finally {
            shutDown();
        }
    }

    public static GreeterServer start(int port) throws IOException {
        Server server = NettyServerBuilder
                .forPort(port)
                .addService(new GRPCGreeter())
                .executor(Executors.newSingleThreadExecutor())
                .build()
                .start();
        return new GreeterServer(server);
    }
}
