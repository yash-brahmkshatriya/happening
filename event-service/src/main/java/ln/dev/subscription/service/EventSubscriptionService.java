package ln.dev.subscription.service;

import io.grpc.stub.StreamObserver;
import java.util.*;
import ln.dev.geohash.LatLonCoordinate;
import ln.dev.grpc.ClientSubscription;
import ln.dev.protos.event.Event;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.proximity.GeoHashProximity;
import ln.dev.subscription.model.EventSubscription;
import ln.dev.util.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Component;

@Component
public class EventSubscriptionService implements SubscriptionService<EventSubscription, Event, EventStreamFilters> {

    private final GeoHashProximity proximity;
    private final Map<String, EventSubscription> subscribers;

    // Notify in given radians about new published event
    private final double notifyIn;

    public EventSubscriptionService(
            GeoHashProximity geoHashProximity, @Value("${event.subscription.spatial.publish-radius}") double notifyIn) {
        this.proximity = geoHashProximity;
        this.subscribers = new HashMap<>();
        this.notifyIn = notifyIn / Metrics.KILOMETERS.getMultiplier();
    }

    /**
     * Register the subscription with given filters and observer
     * @param filters
     * @param responseObserver
     * @return EventSubscription
     */
    @Override
    public EventSubscription subscribe(
            EventStreamFilters filters, StreamObserver<ClientSubscription> responseObserver) {
        String subscriberId = IdGenerator.generate();

        EventSubscription subscriber = new EventSubscription(subscriberId, new Date(), filters);
        LatLonCoordinate subscriberLocation =
                new LatLonCoordinate(filters.getProximityFilter().getLocation());
        proximity.watch(subscriber.getSubscriptionId(), subscriberLocation);
        this.subscribers.put(subscriber.getSubscriptionId(), subscriber);
        return subscriber;
    }

    /**
     * Listen to subscription already registered
     * @param subscriptionId
     * @param responseObserver
     */
    @Override
    public void listenSubscription(String subscriptionId, StreamObserver<Event> responseObserver) {
        if (this.subscribers.containsKey(subscriptionId)) {
            EventSubscription subscription = this.subscribers.get(subscriptionId);
            subscription.setResponseObserver(Optional.of(responseObserver));
            this.subscribers.put(subscriptionId, subscription);
        }
        // TODO: write else path
    }

    /**
     * Publish given event to all subscribers in proximity
     * @param event
     */
    public void publish(Event event) {
        publish(event, proximity.findAllInProximity(event, this.notifyIn, Metrics.KILOMETERS));
    }

    /**
     * Publish given event to all given subscriber Ids
     * @param event
     * @param subscriberIds
     */
    @Override
    public void publish(Event event, List<String> subscriberIds) {
        subscriberIds.stream()
                .map(subscriptionId -> subscribers.getOrDefault(subscriptionId, null))
                .filter(Objects::nonNull)
                .filter(subscriber -> subscriber.applyFilter(event))
                .map(EventSubscription::getResponseObserver)
                .forEach(
                        optionalStreamObserver -> optionalStreamObserver.ifPresent(observer -> observer.onNext(event)));
    }

    /**
     * Unsubscribes with given subscription id
     * @param subscriptionId
     */
    @Override
    public void unsubscribe(String subscriptionId) {
        if (this.subscribers.containsKey(subscriptionId)) {
            var optionalResponseObserver = this.subscribers.get(subscriptionId).getResponseObserver();
            optionalResponseObserver.ifPresent(StreamObserver::onCompleted);
            proximity.unwatch(
                    subscriptionId, this.subscribers.get(subscriptionId).getLatLonCoordinate());
        }
        this.subscribers.remove(subscriptionId);
    }
}
