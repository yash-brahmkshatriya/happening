package ln.dev.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Collections;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    EventService eventService;

    @Test
    void createEvent() {
        EventPojo eventPojo = EventPojo.builder().name("name").id("RANDOM").build();

        EventPojo actual = EventPojo.builder().name("name").id("RANDOM").build();

        when(eventRepository.save(eventPojo)).thenReturn(actual);
        assertEquals(actual, eventService.createEvent(eventPojo));
    }

    @Test
    void findByFilters() {
        EventStreamFilters eventStreamFilters = EventStreamFilters.newBuilder().build();
        when(eventRepository.findByEventFilters(eventStreamFilters)).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), eventService.findByFilters(eventStreamFilters));
    }
}
