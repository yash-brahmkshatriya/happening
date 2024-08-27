package ln.dev.subscription;

import com.google.common.eventbus.Subscribe;
import ln.dev.pojo.EventPojo;
import ln.dev.subscription.service.EventSubscriptionService;
import ln.dev.util.EventConvertor;
import org.springframework.stereotype.Component;

@Component
public class HappeningEventListener {

    private final EventSubscriptionService eventSubscriptionService;

    public HappeningEventListener(EventSubscriptionService eventSubscriptionService) {
        this.eventSubscriptionService = eventSubscriptionService;
    }

    @Subscribe
    public void handleHappeningEvent(EventPojo eventPojo) {
        eventSubscriptionService.publish(EventConvertor.convert(eventPojo));
    }
}
