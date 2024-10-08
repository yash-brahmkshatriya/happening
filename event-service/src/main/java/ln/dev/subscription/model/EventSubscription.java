package ln.dev.subscription.model;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import ln.dev.geohash.LatLonCoordinate;
import ln.dev.protos.coordinate.Coordinate;
import ln.dev.protos.event.*;
import ln.dev.util.EventConvertor;
import lombok.*;
import org.springframework.data.geo.Metrics;
import org.springframework.util.Assert;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventSubscription extends Subscription<Event, EventStreamFilters> {

    @Setter(AccessLevel.NONE)
    private LatLonCoordinate latLonCoordinate;

    public EventSubscription(String subscriptionId, Date timestamp, EventStreamFilters subscriptionRequestFilters) {
        super(subscriptionId, timestamp, Optional.empty(), Optional.of(subscriptionRequestFilters));
        this.updateRequestData(subscriptionRequestFilters);
    }

    /**
     * Updates the request data
     */
    public void updateRequestData(EventStreamFilters eventStreamFilters) {
        this.latLonCoordinate =
                new LatLonCoordinate(eventStreamFilters.getProximityFilter().getLocation());
        super.updateRequestData(eventStreamFilters);
    }

    /**
     * Apply proximity filtering to published event
     * @param proximityFilter
     * @param publishedEventCoordinate
     * @return True if subscriber is in proximity of published event
     */
    public boolean applyProximityFilter(ProximityFilter proximityFilter, Coordinate publishedEventCoordinate) {
        LatLonCoordinate p1 = new LatLonCoordinate(proximityFilter.getLocation());
        LatLonCoordinate p2 = new LatLonCoordinate(publishedEventCoordinate);
        double distanceInRadians = p1.distanceBetween(p2);
        double distanceInKms = distanceInRadians * Metrics.KILOMETERS.getMultiplier();
        return distanceInKms <= proximityFilter.getLocationRadius();
    }

    /**
     * Apply Name filtering
     * @param subscribedFilterName
     * @param publishedEventName
     * @return True if subscriber's name filter matches published event's name
     */
    public boolean applyNameFilter(String subscribedFilterName, String publishedEventName) {
        Pattern pattern = Pattern.compile(subscribedFilterName, Pattern.CASE_INSENSITIVE);
        return subscribedFilterName.isEmpty() || publishedEventName.matches(pattern.pattern());
    }

    /**
     * Apply Type filtering
     * @param subscribedTypes
     * @param publishedType
     * @return True if subscriber's type filters matches published event's type
     */
    public boolean applyTypeFilter(List<EventType> subscribedTypes, EventType publishedType) {
        return subscribedTypes.isEmpty() || subscribedTypes.contains(publishedType);
    }

    /**
     * @param subscribedTimestampFilter
     * @param publishedEvent
     * @return True if subscriber's timestamp filter passes published event's timestamps
     * @throws ParseException if invalid date
     */
    public boolean applyDateFilter(TimestampFilter subscribedTimestampFilter, Event publishedEvent)
            throws ParseException {
        if (subscribedTimestampFilter.getTimestampFilterKey().equals(TimestampFilterKey.UNSPECIFIED_KEY)
                || subscribedTimestampFilter
                        .getTimestampFilterOperator()
                        .equals(TimestampFilterOperator.UNSPECIFIED_OPERATOR)
                || subscribedTimestampFilter.getTimestamp().isEmpty()
                || subscribedTimestampFilter.getTimestamp2().isEmpty()) {
            return true;
        }
        Date filterBasedOnDate;
        if (subscribedTimestampFilter.getTimestampFilterKey().equals(TimestampFilterKey.START)) {
            filterBasedOnDate = EventConvertor.parseISODate(publishedEvent.getStartTimestamp());
        } else filterBasedOnDate = EventConvertor.parseISODate(publishedEvent.getEndTimestamp());
        Assert.notNull(filterBasedOnDate, "Incoming Event timestamp is null");
        switch (subscribedTimestampFilter.getTimestampFilterOperator()) {
            case AFTER -> {
                return filterBasedOnDate.after(EventConvertor.parseISODate(subscribedTimestampFilter.getTimestamp()));
            }
            case BEFORE -> {
                return filterBasedOnDate.before(EventConvertor.parseISODate(subscribedTimestampFilter.getTimestamp()));
            }
            case BETWEEN -> {
                return filterBasedOnDate.after(EventConvertor.parseISODate(subscribedTimestampFilter.getTimestamp()))
                        && filterBasedOnDate.before(
                                EventConvertor.parseISODate(subscribedTimestampFilter.getTimestamp2()));
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Returns if the incoming event passes the request filters.
     * The approximate proximity filtering will already be applied beforehand
     * */
    public boolean applyFilter(Event event) {
        if (this.getRequestData().isEmpty()) return true;
        EventStreamFilters filters = this.getRequestData().get();

        try {
            boolean passesFilter = applyProximityFilter(filters.getProximityFilter(), event.getLocation());
            passesFilter = passesFilter & applyNameFilter(filters.getName(), event.getName());
            passesFilter = passesFilter & applyTypeFilter(filters.getTypeList(), event.getType());
            passesFilter = passesFilter & applyDateFilter(filters.getTimestampFilter(), event);

            return passesFilter;
        } catch (ParseException e) {
            System.out.println("Rejecting..." + this.getSubscriptionId());
            return false;
        }
    }
}
