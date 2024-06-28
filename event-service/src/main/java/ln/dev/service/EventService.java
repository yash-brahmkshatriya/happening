package ln.dev.service;

import ln.dev.pojo.EventPojo;
import ln.dev.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventPojo createEvent(EventPojo eventPojo) {
        eventPojo.setId(null);
        eventPojo.setCreatedAt(new Date());
        return this.eventRepository.save(eventPojo);
    }


}
