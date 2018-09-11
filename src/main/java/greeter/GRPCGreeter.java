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
        return new TransformingObserver<>(greeter::replyTo, responseObserver);
    }

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        responseObserver.onNext(greeter.replyTo(request));
        responseObserver.onCompleted();
    }

}
