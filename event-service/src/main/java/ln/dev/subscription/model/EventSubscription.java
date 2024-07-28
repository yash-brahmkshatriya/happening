package ln.dev.subscription.model;

import ln.dev.geohash.LatLonCoordinate;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.Event;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.protos.event.TimestampFilter;
import ln.dev.protos.event.TimestampFilterKey;
import ln.dev.util.EventConvertor;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventSubscription extends Subscription<Event, EventStreamFilters> {

    @Setter(AccessLevel.NONE)
    private LatLonCoordinate latLonCoordinate;

    public EventSubscription(String subscriptionId, Date timestamp, EventStreamFilters subscriptionRequestFilters) {
        super(subscriptionId, timestamp, Optional.empty(), Optional.of(subscriptionRequestFilters));
        this.updateRequestDate(subscriptionRequestFilters);
    }

    public void updateRequestDate(EventStreamFilters eventStreamFilters) {
        this.latLonCoordinate = LatLonCoordinate.builder()
                .latitude(eventStreamFilters.getProximityFilter().getLocation().getLatitude())
                .longitude(eventStreamFilters.getProximityFilter().getLocation().getLongitude())
                .build();
        super.updateRequestData(eventStreamFilters);
    }

    /**
     * Returns if the incoming event passes the request filters.
     * The proximity filtering will already be applied beforehand
    * */
    public boolean applyFilter(Event event)  {
        if(this.getRequestData().isEmpty()) return true;
        EventStreamFilters filters = this.getRequestData().get();


        if(filters.getName().isEmpty()) {
            Pattern pattern = Pattern.compile(filters.getName(), Pattern.CASE_INSENSITIVE);
            boolean matches = event.getName().matches(pattern.pattern());
            if(!matches) return false;
        }
        if(!filters.getTypeList().isEmpty()) {
            if(!filters.getTypeList().contains(event.getType())) return false;
        }

        try {
            Date filterBasedOnDate;
            TimestampFilter timestampFilter = this.getRequestData().get().getTimestampFilter();
            if(timestampFilter.getTimestampFilterKey().equals(TimestampFilterKey.START)) {
                filterBasedOnDate = EventConvertor.parseISODate(event.getStartTimestamp());
            } else filterBasedOnDate = EventConvertor.parseISODate(event.getEndTimestamp());
            Assert.notNull(filterBasedOnDate, "Incoming Event timestamp is null");
            switch (timestampFilter.getTimestampFilterOperator()) {
                case AFTER -> {
                    return filterBasedOnDate.after(EventConvertor.parseISODate(timestampFilter.getTimestamp()));
                }
                case BEFORE -> {
                    return filterBasedOnDate.before(EventConvertor.parseISODate(timestampFilter.getTimestamp()));
                }
                case BETWEEN -> {
                    return filterBasedOnDate.after(EventConvertor.parseISODate(timestampFilter.getTimestamp())) &&
                            filterBasedOnDate.before(EventConvertor.parseISODate(timestampFilter.getTimestamp2()));
                }
            }
        } catch (ParseException e) {
            System.out.println("Rejecting");
            return false;
        }

        return true;
    }
}
