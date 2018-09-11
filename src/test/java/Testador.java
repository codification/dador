import dador.GreeterGrpc;
import dador.HelloReply;
import dador.HelloRequest;
import greeter.DirectGreeter;
import io.grpc.*;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.stub.StreamObserver;
import io.grpc.stub.StreamObservers;
import io.grpc.testing.GrpcCleanupRule;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class Testador {

    @Rule
    public GrpcCleanupRule cleanupRule = new GrpcCleanupRule();
    private String serverName;
    private final AtomicInteger calls = new AtomicInteger(0);
    private ManagedChannel channel;
    private GreeterGrpc.GreeterBlockingStub blockingStub;
    private GreeterGrpc.GreeterStub stub;

    @Before
    public void setUp() throws IOException {

        serverName = InProcessServerBuilder.generateName();
        Server server = cleanupRule
                .register(InProcessServerBuilder
                        .forName(serverName)
                        .directExecutor()
                        .addService(new DirectGreeter())
                        .intercept(new ServerInterceptor() {
                            @Override
                            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                                         Metadata headers,
                                                                                         ServerCallHandler<ReqT, RespT> next) {
                                calls.incrementAndGet();
                                return next.startCall(call, headers);
                            }
                        })
                        .build()
                        .start()
                );
        channel = cleanupRule.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
        blockingStub = GreeterGrpc.newBlockingStub(channel)
                .withDeadlineAfter(1L, TimeUnit.SECONDS);
        stub = GreeterGrpc.newStub(channel);
    }

    @Test
    public void hiHo() {

        String name = "Bosse";

        HelloReply reply = blockingStub.sayHello(HelloRequest.newBuilder().setName(name).build());
        assertThat(reply.getMessage(),
                both(containsString("Hello"))
                        .and(containsString(name)));

        assertThat(calls.get(), is(greaterThan(0)));
    }

    @Test
    public void hoHoStream() throws Exception {
        StreamRecorder<HelloReply> responseObserver = StreamRecorder.create();
        StreamObserver<HelloRequest> requestObserver = stub.sayHelloBidi(responseObserver);
        requestObserver.onNext(HelloRequest.newBuilder().setName("name1").build());
        requestObserver.onNext(HelloRequest.newBuilder().setName("name2").build());
        requestObserver.onCompleted();

        assertTrue(responseObserver.awaitCompletion(1, TimeUnit.SECONDS));
        List<String> greetings = responseObserver.getValues().stream()
                .map(HelloReply::getMessage).collect(Collectors.toList());
        assertThat(greetings, Matchers.hasItems("Hello name1!", "Hello name2!"));
    }

    @Test
    public void hoHoStreamOneAtATime() throws Exception {
        LinkedBlockingQueue<HelloReply> helloReplies = new LinkedBlockingQueue<>(2);
        AtomicBoolean wasCompleted = new AtomicBoolean(false);
        StreamObserver<HelloReply> responseObserver = new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply value) {
                helloReplies.offer(value);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                wasCompleted.set(true);
            }
        };
        StreamObserver<HelloRequest> requestObserver = stub.sayHelloBidi(responseObserver);

        requestObserver.onNext(HelloRequest.newBuilder().setName("name1").build());
        HelloReply firstReply = helloReplies.poll(100, TimeUnit.MILLISECONDS);
        assertNotNull(firstReply);
        assertThat(firstReply.getMessage(), containsString("name1"));

        requestObserver.onNext(HelloRequest.newBuilder().setName("name2").build());
        HelloReply secondReply = helloReplies.poll(100, TimeUnit.MILLISECONDS);
        assertNotNull(secondReply);

        requestObserver.onCompleted();
        assertTrue(wasCompleted.get());

    }
}
