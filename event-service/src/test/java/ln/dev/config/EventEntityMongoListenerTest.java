package ln.dev.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import ln.dev.pojo.EventPojo;
import ln.dev.subscription.HappeningEventPublisher;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;

@ExtendWith(MockitoExtension.class)
class EventEntityMongoListenerTest {

    @Mock
    HappeningEventPublisher happeningEventPublisher;

    @Captor
    ArgumentCaptor<EventPojo> argumentCaptor;

    @Test
    void onAfterSave() {
        EventPojo eventPojo = EventPojo.builder().id("TEST_ID").build();
        Mockito.doNothing().when(happeningEventPublisher).publishEvent(any(EventPojo.class));

        MongoMappingEvent<EventPojo> event = new AfterSaveEvent<>(eventPojo, new Document(), "test-collection");

        EventEntityMongoListener listener = new EventEntityMongoListener(happeningEventPublisher);
        listener.onApplicationEvent(event);

        Mockito.verify(happeningEventPublisher).publishEvent(argumentCaptor.capture());

        assertEquals(event.getSource().getId(), argumentCaptor.getValue().getId());
    }
}
