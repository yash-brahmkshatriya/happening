package ln.dev.subscription.service;


import io.grpc.stub.StreamObserver;
import ln.dev.grpc.ClientSubscription;
import ln.dev.subscription.model.Subscription;

import java.util.List;

/**
*
* */
public interface SubscriptionService<P, G, F> {

    Subscription<P, G, F> subscribe(F filters, StreamObserver<ClientSubscription> responseObserver);

    void listenSubscription(String subscriptionId, StreamObserver<G> responseObserver);

    void publish(G event, List<String> subscriberIds);

    void unsubscribe(String subscriptionId);


}
