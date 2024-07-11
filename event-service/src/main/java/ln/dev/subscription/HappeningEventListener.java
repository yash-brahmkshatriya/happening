package ln.dev.subscription;

import com.google.common.eventbus.Subscribe;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.Event;
import ln.dev.subscription.service.EventSubscriptionService;
import ln.dev.util.EventConvertor;
import org.springframework.stereotype.Component;

@Component
public class HappeningEventListener {

    private final EventSubscriptionService eventSubscriptionService;

    public HappeningEventListener(EventSubscriptionService eventSubscriptionService) {
        this.eventSubscriptionService = eventSubscriptionService;
    }

    // TODO: Find subscribers in proximity with event
    @Subscribe
    public void handleHappeningEvent(EventPojo eventPojo) {
        Event grpcEvent = EventConvertor.convert(eventPojo);
        eventSubscriptionService.publish(grpcEvent, eventSubscriptionService.getSubscribers());
    }

}
