package ln.dev.repository;


import ln.dev.constants.MongoFieldNames;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.protos.event.ProximityFilter;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class CustomEventRepositoryImpl implements CustomEventRepository {

    private final MongoTemplate mongoTemplate;

    public CustomEventRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // TODO: Add other criterias
    private Criteria eventFilterCriteriaBuilder(EventStreamFilters eventStreamFilters) {
        List<Criteria> criterias = new ArrayList<>();
        if(!eventStreamFilters.getTypeList().isEmpty()) {
            criterias.add(
                    Criteria.where(MongoFieldNames.Event.TYPE)
                            .in(eventStreamFilters.getTypeList())
            );
        }
        if(!eventStreamFilters.getName().isEmpty()) {
            criterias.add(
                    Criteria.where(MongoFieldNames.Event.NAME)
                            .regex(Pattern.compile(eventStreamFilters.getName(), Pattern.CASE_INSENSITIVE))
            );
        }
        Criteria filterCriteria = new Criteria();
        if(!criterias.isEmpty()) filterCriteria.andOperator(criterias);
        return filterCriteria;
    }

    @Override
    public List<EventPojo> findByEventFilters(EventStreamFilters eventFilters) {
        ProximityFilter proximityFilter = eventFilters.getProximityFilter();

        Point locationPoint = new Point(
                proximityFilter.getLocation().getLongitude(),
                proximityFilter.getLocation().getLatitude()
        );
        NearQuery nearQuery = NearQuery.near(locationPoint).maxDistance(
                proximityFilter.getLocationRadius(),
                Metrics.KILOMETERS
        ).spherical(true).query(new Query(eventFilterCriteriaBuilder(eventFilters)));

        return mongoTemplate.geoNear(nearQuery, EventPojo.class).getContent()
                .stream().map(GeoResult::getContent).toList();

    }
}
