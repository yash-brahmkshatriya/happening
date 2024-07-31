package ln.dev.repository;

import java.util.List;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.EventStreamFilters;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomEventRepository {

    List<EventPojo> findByEventFilters(EventStreamFilters eventFilters);
}
