package ln.dev.service;

import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventPojo createEvent(EventPojo eventPojo) {
        eventPojo.setId(null);
        return eventRepository.save(eventPojo);
    }

    // TODO: Validate Payload eventStreamFilters
    public List<EventPojo> findByFilters(EventStreamFilters eventStreamFilters) {
        return eventRepository.findByEventFilters(eventStreamFilters);
    }

}
