package ln.dev.config;

import ln.dev.pojo.EventPojo;
import ln.dev.subscription.HappeningEventPublisher;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

@Component
public class EventEntityMongoListener extends AbstractMongoEventListener<EventPojo> {
    private final HappeningEventPublisher happeningEventPublisher;

    public EventEntityMongoListener(HappeningEventPublisher happeningEventPublisher) {
        this.happeningEventPublisher = happeningEventPublisher;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<EventPojo> event) {
        happeningEventPublisher.publishEvent(event.getSource());
    }
}
