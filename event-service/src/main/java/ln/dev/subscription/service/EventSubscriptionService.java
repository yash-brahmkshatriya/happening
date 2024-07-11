package ln.dev.subscription.service;

import io.grpc.stub.StreamObserver;
import ln.dev.grpc.ClientSubscription;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.Event;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.subscription.model.EventSubscription;
import ln.dev.util.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EventSubscriptionService implements SubscriptionService<EventPojo, Event, EventStreamFilters> {

    private final Map<String, EventSubscription> subscribers;

    public EventSubscriptionService() {
        this.subscribers = new HashMap<>();
    }

    // TODO: only for testing purposes, Remove later
    public List<String> getSubscribers() {
        return this.subscribers.keySet().stream().toList();
    }

    @Override
    public EventSubscription subscribe(EventStreamFilters filters, StreamObserver<ClientSubscription> responseObserver) {
        String subscriberId = IdGenerator.generate();

        EventSubscription subscriber = new EventSubscription(
                subscriberId,
                new Date(),
                filters
        );
        this.subscribers.put(subscriberId, subscriber);
        return subscriber;
    }

    @Override
    public void listenSubscription(String subscriptionId, StreamObserver<Event> responseObserver) {
        if(this.subscribers.containsKey(subscriptionId)) {
            EventSubscription subscription = this.subscribers.get(subscriptionId);
            subscription.setResponseObserver(
                    Optional.of(responseObserver)
            );
            this.subscribers.put(subscriptionId, subscription);
        }
        // TODO: write else path
    }

    @Override
    public void publish(Event event, List<String> subscriberIds) {
        subscriberIds.stream()
                .map(subscriptionId -> subscribers.getOrDefault(subscriptionId, null))
                .filter(Objects::nonNull)
                .map(EventSubscription::getResponseObserver)
                .forEach(optionalStreamObserver ->
                        optionalStreamObserver.ifPresent(
                            observer -> observer.onNext(event)
                        )
                );
    }

    @Override
    public void unsubscribe(String subscriptionId) {
        if(this.subscribers.containsKey(subscriptionId)) {
            var optionalResponseObserver = this.subscribers.get(subscriptionId).getResponseObserver();
            optionalResponseObserver.ifPresent(StreamObserver::onCompleted);
        }
        this.subscribers.remove(subscriptionId);
    }
}
