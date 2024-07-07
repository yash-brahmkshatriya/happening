package ln.dev.subscription.service;


import io.grpc.stub.StreamObserver;
import ln.dev.subscription.model.Subscription;

public interface SubscriptionService<P, G, F> {

    Subscription<P, G, F> subscribe(F filters, StreamObserver<G> responseObserver);

    void unsubscribe(String subscriptionId);


}
