package it;

import dador.GreeterGrpc;
import dador.HelloReply;
import dador.HelloRequest;
import greeter.GRPCGreeter;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GrpcIT {

    @Rule
    public GrpcCleanupRule cleanupRule = new GrpcCleanupRule();
    private GreeterGrpc.GreeterStub greeterStub;

    @Before
    public void asdf() throws IOException {

        cleanupRule.register(
                NettyServerBuilder
                        .forPort(1234)
                        .directExecutor()
                        .addService(new GRPCGreeter())
                        .build()
                        .start()
        );

        greeterStub = GreeterGrpc.newStub(cleanupRule.register(
                NettyChannelBuilder
                        .forAddress("localhost", 1234)
                        .usePlaintext()
                        .build()
        ))
        .withDeadlineAfter(2, TimeUnit.SECONDS);
    }

    @Test
    public void itWorks() throws Exception {
        StreamRecorder<HelloReply> responseObserver = StreamRecorder.create();
        greeterStub.sayHello(HelloRequest.newBuilder().setName("hullo").build(), responseObserver);
        assertTrue(responseObserver.awaitCompletion(5, TimeUnit.SECONDS));
        HelloReply reply = responseObserver.firstValue().get();
        assertNotNull(reply);
        assertThat(reply.getMessage(), containsString("hullo"));
    }
}
