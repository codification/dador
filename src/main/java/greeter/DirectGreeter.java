package greeter;

import dador.GreeterGrpc;
import dador.HelloReply;
import dador.HelloRequest;
import io.grpc.stub.StreamObserver;

import static java.lang.String.format;

public class DirectGreeter extends GreeterGrpc.GreeterImplBase {

    @Override
    public StreamObserver<HelloRequest> sayHelloBidi(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest value) {
                responseObserver.onNext(greetMsg(value.getName()));
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
        responseObserver.onNext(greetMsg(request.getName()));
        responseObserver.onCompleted();
    }

    private HelloReply greetMsg(String name) {
        return HelloReply.newBuilder().setMessage(greet(name)).build();
    }

    private String greet(String name) {
        return format("Hello %s!", name);
    }
}
