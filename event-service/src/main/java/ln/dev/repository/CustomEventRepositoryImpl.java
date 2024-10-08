package ln.dev.repository;

import java.util.List;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.repository.helper.EventCriteriaBuilder;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CustomEventRepositoryImpl implements CustomEventRepository {

    private final MongoTemplate mongoTemplate;

    private final EventCriteriaBuilder eventCriteriaBuilder;

    public CustomEventRepositoryImpl(MongoTemplate mongoTemplate, EventCriteriaBuilder eventCriteriaBuilder) {
        this.mongoTemplate = mongoTemplate;
        this.eventCriteriaBuilder = eventCriteriaBuilder;
    }

    /**
     * Finds events by given filters
     * @param eventFilters Event filters
     * @return List of events
     */
    @Override
    public List<EventPojo> findByEventFilters(EventStreamFilters eventFilters) {
        NearQuery nearQuery = eventCriteriaBuilder
                .buildNearQuery(eventFilters.getProximityFilter())
                .query(new Query(eventCriteriaBuilder.buildFilterCriterias(eventFilters)));

        return mongoTemplate.geoNear(nearQuery, EventPojo.class).getContent().stream()
                .map(GeoResult::getContent)
                .toList();
    }
}
