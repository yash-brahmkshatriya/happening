package ln.dev.proximity;

import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.Event;
import ln.dev.proximity.geohash.GeoHashTree;
import ln.dev.subscription.model.EventSubscription;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeoHashProximity implements Proximity<Event, String> {

    protected GeoHashTree<String> subscriberIdTree;

    @Override
    public List<String> findAllInProximity(Event publishedEvent, double delta) {

        return null;
    }
}
