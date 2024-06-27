package ln.dev.repository;

import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.EventStreamFilters;

import java.util.List;

public interface CustomEventRepository {

    List<EventPojo> findByEventFilters(EventStreamFilters eventFilters);

}
