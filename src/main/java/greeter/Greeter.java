package greeter;

import dador.HelloReply;
import dador.HelloRequest;

import static java.lang.String.format;

public interface Greeter {
    static Greeter directGreeter() {
        return new Greeter() {};
    }

    default HelloReply replyTo(HelloRequest request) {
        return HelloReply.newBuilder().setMessage(greet(request.getName())).build();
    }

    default String greet(String name) {
        return format("Hello %s!", name);
    }
}

