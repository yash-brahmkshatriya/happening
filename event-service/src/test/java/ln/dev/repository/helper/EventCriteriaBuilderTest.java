package ln.dev.repository.helper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import ln.dev.constants.MongoFieldNames;
import ln.dev.protos.coordinate.Coordinate;
import ln.dev.protos.event.*;
import ln.dev.util.EventConvertor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;

@ExtendWith(MockitoExtension.class)
class EventCriteriaBuilderTest {

    @InjectMocks
    EventCriteriaBuilder eventCriteriaBuilder;

    @org.junit.jupiter.api.Test
    void eventNameCriteriaBuilder() {
        String eventName = "TEST_EVENT_NAME";

        Criteria expected =
                Criteria.where(MongoFieldNames.Event.NAME).regex(Pattern.compile(eventName, Pattern.CASE_INSENSITIVE));

        Optional<Criteria> eventNameCriteria = eventCriteriaBuilder.eventNameCriteriaBuilder(eventName);

        assertTrue(eventNameCriteria.isPresent());
        assertEquals(expected, eventNameCriteria.get());

        assertTrue(eventCriteriaBuilder.eventNameCriteriaBuilder("").isEmpty());
    }

    @org.junit.jupiter.api.Test
    void eventTypeCriteriaBuilder() {
        List<EventType> eventTypeList = List.of(EventType.FOOD, EventType.HEALTH);

        Criteria expected = Criteria.where(MongoFieldNames.Event.TYPE).in(eventTypeList);

        Optional<Criteria> actual = eventCriteriaBuilder.eventTypeCriteriaBuilder(eventTypeList);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());

        assertTrue(eventCriteriaBuilder
                .eventTypeCriteriaBuilder(Collections.emptyList())
                .isEmpty());
    }

    @org.junit.jupiter.api.Test
    void eventTimestampCriteriaBuilder_Unrecognized() throws ParseException {
        TimestampFilter timestampFilter = TimestampFilter.newBuilder()
                .setTimestampFilterKey(TimestampFilterKey.START)
                .setTimestampFilterOperator(TimestampFilterOperator.UNSPECIFIED_OPERATOR)
                .build();

        Optional<Criteria> actual = eventCriteriaBuilder.timestampFilterCriteria(timestampFilter);
        assertTrue(actual.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void eventTimestampCriteriaBuilder_Between() throws ParseException {
        TimestampFilter timestampFilter = TimestampFilter.newBuilder()
                .setTimestampFilterKey(TimestampFilterKey.END)
                .setTimestampFilterOperator(TimestampFilterOperator.BETWEEN)
                .setTimestamp("2024-08-01T06:58:42.504Z")
                .setTimestamp2("2024-09-01T06:58:42.504Z")
                .build();

        Criteria expected = Criteria.where(MongoFieldNames.Event.END_TIMESTAMP)
                .gt(EventConvertor.parseISODate(timestampFilter.getTimestamp()))
                .lt(EventConvertor.parseISODate(timestampFilter.getTimestamp2()));
        Optional<Criteria> actual = eventCriteriaBuilder.timestampFilterCriteria(timestampFilter);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @org.junit.jupiter.api.Test
    void eventTimestampCriteriaBuilder_After() throws ParseException {
        TimestampFilter timestampFilter = TimestampFilter.newBuilder()
                .setTimestampFilterKey(TimestampFilterKey.END)
                .setTimestampFilterOperator(TimestampFilterOperator.AFTER)
                .setTimestamp("2024-08-01T06:58:42.504Z")
                .build();

        Criteria expected = Criteria.where(MongoFieldNames.Event.END_TIMESTAMP)
                .gt(EventConvertor.parseISODate(timestampFilter.getTimestamp()));

        Optional<Criteria> actual = eventCriteriaBuilder.timestampFilterCriteria(timestampFilter);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @org.junit.jupiter.api.Test
    void eventTimestampCriteriaBuilder_Before() throws ParseException {
        TimestampFilter timestampFilter = TimestampFilter.newBuilder()
                .setTimestampFilterKey(TimestampFilterKey.END)
                .setTimestampFilterOperator(TimestampFilterOperator.BEFORE)
                .setTimestamp("2024-08-01T06:58:42.504Z")
                .build();

        Criteria expected = Criteria.where(MongoFieldNames.Event.END_TIMESTAMP)
                .lt(EventConvertor.parseISODate(timestampFilter.getTimestamp()));

        Optional<Criteria> actual = eventCriteriaBuilder.timestampFilterCriteria(timestampFilter);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @org.junit.jupiter.api.Test
    void buildFilterCriteria() throws ParseException {
        EventCriteriaBuilder spyCriteriaBuilder = Mockito.spy(eventCriteriaBuilder);

        Optional<Criteria> typeCriteria = Optional.of(Criteria.where("test1").in("abc"));
        Optional<Criteria> nameCriteria = Optional.of(Criteria.where("test2").regex("regex"));
        Optional<Criteria> timestampCriteria =
                Optional.of(Criteria.where("test3").lt("date"));

        Mockito.doReturn(typeCriteria).when(spyCriteriaBuilder).eventTypeCriteriaBuilder(any());
        Mockito.doReturn(nameCriteria).when(spyCriteriaBuilder).eventNameCriteriaBuilder(any());
        Mockito.doReturn(timestampCriteria).when(spyCriteriaBuilder).timestampFilterCriteria(any());

        Criteria actual = spyCriteriaBuilder.buildFilterCriterias(
                EventStreamFilters.newBuilder().build());

        List<Criteria> criterias = new ArrayList<>();

        typeCriteria.ifPresent(criterias::add);
        nameCriteria.ifPresent(criterias::add);
        timestampCriteria.ifPresent(criterias::add);

        Criteria expected = new Criteria().andOperator(criterias);
        assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void buildNearQuery() {
        ProximityFilter proximityFilter = ProximityFilter.newBuilder()
                .setLocation(Coordinate.newBuilder()
                        .setLongitude(12.23)
                        .setLatitude(56.89)
                        .build())
                .setLocationRadius(10)
                .build();

        NearQuery nearQuery = eventCriteriaBuilder.buildNearQuery(proximityFilter);

        Point point = new Point(
                proximityFilter.getLocation().getLongitude(),
                proximityFilter.getLocation().getLatitude());
        NearQuery expectedNearQuery = NearQuery.near(point)
                .maxDistance(proximityFilter.getLocationRadius(), Metrics.KILOMETERS)
                .spherical(true);
        assertEquals(expectedNearQuery.getMaxDistance(), nearQuery.getMaxDistance());
        assertEquals(expectedNearQuery.getMetric(), nearQuery.getMetric());
        assertEquals(expectedNearQuery.getCollation(), nearQuery.getCollation());
    }
}
