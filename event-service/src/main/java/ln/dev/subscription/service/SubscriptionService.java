package ln.dev.subscription.service;

import io.grpc.stub.StreamObserver;
import java.util.List;
import ln.dev.grpc.ClientSubscription;

/**
 *
 * */
public interface SubscriptionService<C, G, T> {

    C subscribe(T requestData, StreamObserver<ClientSubscription> responseObserver);

    void listenSubscription(String subscriptionId, StreamObserver<G> responseObserver);

    void publish(G event, List<String> subscriberIds);

    void unsubscribe(String subscriptionId);
}
