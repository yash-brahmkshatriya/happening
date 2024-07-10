package ln.dev.subscription;

import com.google.common.eventbus.EventBus;
import ln.dev.pojo.EventPojo;

public class HappeningEventPublisher {

    private final EventBus happeningEventBus;

    public HappeningEventPublisher(HappeningEventListener happeningEventListener) {
        this.happeningEventBus = new EventBus();
        this.happeningEventBus.register(happeningEventListener);
    }

    public void publishEvent(EventPojo eventPojo) {
        this.happeningEventBus.post(eventPojo);
    }
}
