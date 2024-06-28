package ln.dev.repository;

import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.EventStreamFilters;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomEventRepository {

    List<EventPojo> findByEventFilters(EventStreamFilters eventFilters);

}
