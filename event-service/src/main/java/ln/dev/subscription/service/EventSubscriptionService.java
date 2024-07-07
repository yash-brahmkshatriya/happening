package ln.dev.subscription.service;

import io.grpc.stub.StreamObserver;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.Event;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.subscription.model.EventSubscription;
import ln.dev.util.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventSubscriptionService implements SubscriptionService<EventPojo, Event, EventStreamFilters> {

    private final Map<String, EventSubscription> subscribers;

    public EventSubscriptionService() {
        this.subscribers = new HashMap<>();
    }

    @Override
    public EventSubscription subscribe(EventStreamFilters filters, StreamObserver<Event> responseObserver) {
        String subscriberId = IdGenerator.generate();

        EventSubscription subscriber = new EventSubscription(
                subscriberId,
                new Date(),
                responseObserver,
                filters
        );
        this.subscribers.put(subscriberId, subscriber);
        return subscriber;
    }

    @Override
    public void unsubscribe(String subscriptionId) {
        this.subscribers.remove(subscriptionId);
    }
}
