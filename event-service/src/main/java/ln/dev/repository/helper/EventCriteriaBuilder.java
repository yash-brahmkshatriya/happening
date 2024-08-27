package ln.dev.repository.helper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import ln.dev.constants.MongoFieldNames;
import ln.dev.protos.event.*;
import ln.dev.util.EventConvertor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Component;

@Component
public class EventCriteriaBuilder {

    /**
     * Builds criteria for timestamp
     * @param timestampFilter Given filter
     * @return Optional Criteria
     * @throws ParseException if invalid date
     */
    protected Optional<Criteria> timestampFilterCriteria(TimestampFilter timestampFilter) throws ParseException {
        String timestampFilterKey = timestampFilter.getTimestampFilterKey().equals(TimestampFilterKey.START)
                ? MongoFieldNames.Event.START_TIMESTAMP
                : MongoFieldNames.Event.END_TIMESTAMP;
        Criteria timestampCriteria = Criteria.where(timestampFilterKey);

        switch (timestampFilter.getTimestampFilterOperator()) {
            case AFTER -> timestampCriteria.gt(EventConvertor.parseISODate(timestampFilter.getTimestamp()));

            case BEFORE -> timestampCriteria.lt(EventConvertor.parseISODate(timestampFilter.getTimestamp()));

            case BETWEEN -> timestampCriteria
                    .gt(EventConvertor.parseISODate(timestampFilter.getTimestamp()))
                    .lt(EventConvertor.parseISODate(timestampFilter.getTimestamp2()));
            case UNSPECIFIED_OPERATOR, UNRECOGNIZED -> {
                return Optional.empty();
            }
        }
        return Optional.of(timestampCriteria);
    }

    /**
     * Builds criteria for Event type
     * @param eventTypeList event type list for filtering
     * @return Optional criteria
     */
    protected Optional<Criteria> eventTypeCriteriaBuilder(List<EventType> eventTypeList) {
        if (eventTypeList.isEmpty()) return Optional.empty();
        return Optional.of(Criteria.where(MongoFieldNames.Event.TYPE).in(eventTypeList));
    }

    /**
     * Builds criteria for event name
     * @param eventName event name for filtering
     * @return Optional criteria
     */
    protected Optional<Criteria> eventNameCriteriaBuilder(@NotNull String eventName) {
        if (eventName.isEmpty()) return Optional.empty();
        return Optional.of(
                Criteria.where(MongoFieldNames.Event.NAME).regex(Pattern.compile(eventName, Pattern.CASE_INSENSITIVE)));
    }

    /**
     * Builds complete filter criteria
     * @param eventStreamFilters Event filters
     * @return Criteria
     */
    public Criteria buildFilterCriterias(EventStreamFilters eventStreamFilters) {
        try {
            Optional<Criteria> eventTypeCriteria = eventTypeCriteriaBuilder(eventStreamFilters.getTypeList());
            Optional<Criteria> eventNameCriteria = eventNameCriteriaBuilder(eventStreamFilters.getName());
            Optional<Criteria> timestampCriteria = timestampFilterCriteria(eventStreamFilters.getTimestampFilter());

            List<Criteria> criterias = new ArrayList<>();

            eventTypeCriteria.ifPresent(criterias::add);
            eventNameCriteria.ifPresent(criterias::add);
            timestampCriteria.ifPresent(criterias::add);

            return new Criteria().andOperator(criterias);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds NearQuery with given proximity filter
     * @param proximityFilter Proximity Filters
     * @return Built NearQuery
     */
    public NearQuery buildNearQuery(ProximityFilter proximityFilter) {
        Point locationPoint = new Point(
                proximityFilter.getLocation().getLongitude(),
                proximityFilter.getLocation().getLatitude());
        return NearQuery.near(locationPoint)
                .maxDistance(proximityFilter.getLocationRadius(), Metrics.KILOMETERS)
                .spherical(true);
    }
}
