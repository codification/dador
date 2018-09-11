package greeter;

import dador.GreeterGrpc;
import dador.HelloReply;
import dador.HelloRequest;
import io.grpc.stub.StreamObserver;

import static java.lang.String.format;

public class GRPCGreeter extends GreeterGrpc.GreeterImplBase {

    private final Greeter greeter = Greeter.directGreeter();

    @Override
    public StreamObserver<HelloRequest> sayHelloBidi(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest value) {
                responseObserver.onNext(greeter.replyTo(value));
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        responseObserver.onNext(greeter.replyTo(request));
        responseObserver.onCompleted();
    }

}
