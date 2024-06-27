package ln.dev.repository;

import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.EventStreamFilters;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomEventRepositoryImpl implements CustomEventRepository {


    @Override
    public List<EventPojo> findByEventFilters(EventStreamFilters eventFilters) {
        return null;
    }
}
