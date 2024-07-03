package ln.dev.repository.helper;

import ln.dev.constants.MongoFieldNames;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.protos.event.ProximityFilter;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class EventCriteriaBuilder {

    // TODO: Add other criterias
    public Criteria eventFilterCriteriaBuilder(EventStreamFilters eventStreamFilters) {
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

    public NearQuery buildNearQuery(ProximityFilter proximityFilter) {
        Point locationPoint = new Point(
                proximityFilter.getLocation().getLongitude(),
                proximityFilter.getLocation().getLatitude()
        );
        return NearQuery.near(locationPoint).maxDistance(
                proximityFilter.getLocationRadius(),
                Metrics.KILOMETERS
        ).spherical(true);
    }
}
