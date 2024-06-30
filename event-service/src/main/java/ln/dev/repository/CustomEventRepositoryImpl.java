package ln.dev.repository;


import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.EventStreamFilters;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

@Repository
public class CustomEventRepositoryImpl implements CustomEventRepository {

    private final MongoTemplate mongoTemplate;

    public CustomEventRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<EventPojo> findByEventFilters(EventStreamFilters eventFilters) {

        Criteria queryCriteria = new Criteria();
        queryCriteria.andOperator(
                Criteria.where("type").in(eventFilters.getTypeList()),
                Criteria.where("name").regex(Pattern.compile(eventFilters.getName(), Pattern.CASE_INSENSITIVE))
        );

        Point locationPoint = new Point(
                eventFilters.getLocation().getLongitude(),
                eventFilters.getLocation().getLatitude()
        );
        NearQuery nearQuery = NearQuery.near(locationPoint).maxDistance(
                eventFilters.getLocationRadius(),
                Metrics.KILOMETERS
        ).spherical(true).query(new Query(Criteria.where("type").in(eventFilters.getTypeList())));

        return mongoTemplate.geoNear(nearQuery, EventPojo.class).getContent()
                .stream().map(GeoResult::getContent).toList();

    }
}
