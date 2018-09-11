package greeter;

import io.grpc.stub.StreamObserver;

import java.util.function.Function;

public class TransformingObserver<T,V> implements StreamObserver<T> {

    private final Function<T, V> transform;
    private final StreamObserver<V> delegate;

    public TransformingObserver(Function<T,V> transform, StreamObserver<V> delegate) {
        this.transform = transform;
        this.delegate = delegate;
    }

    @Override
    public void onNext(T value) {
        delegate.onNext(transform.apply(value));
    }

    @Override
    public void onError(Throwable t) {
        delegate.onError(t);
    }

    @Override
    public void onCompleted() {
        delegate.onCompleted();
    }
}
