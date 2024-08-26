package ln.dev.config;

import static org.junit.jupiter.api.Assertions.*;

import ln.dev.pojo.BasePojo;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;

class MongoListenerTest {

    @Test
    void onBeforeConvert() {
        MongoMappingEvent<BasePojo> event = new BeforeConvertEvent<>(new BasePojo(), "test-collection");

        MongoListener listener = new MongoListener();
        listener.onApplicationEvent(event);

        BasePojo basePojo = event.getSource();
        assertNotNull(basePojo.getCreatedAt());
    }
}
