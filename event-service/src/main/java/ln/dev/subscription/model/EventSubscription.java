package ln.dev.subscription.model;

import io.grpc.stub.StreamObserver;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.event.Event;
import ln.dev.protos.event.EventStreamFilters;
import ln.dev.protos.event.TimestampFilter;
import ln.dev.protos.event.TimestampFilterKey;
import ln.dev.util.EventConvertor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventSubscription extends Subscription<EventPojo, Event, EventStreamFilters> {

    public EventSubscription(String subscriptionId, Date timestamp, StreamObserver<Event> responseObserver, EventStreamFilters filters) {
        super(subscriptionId, timestamp, responseObserver, filters);
    }

    /**
     * Returns if the incoming event is subscribed or not.
     * The proximity filtering will already be applied beforehand
    * */
    @Override
    protected boolean applyFilter(EventPojo eventPojo)  {
        if(this.getFilters() == null) return true;
        EventStreamFilters filters = this.getFilters();


        if(filters.getName().isEmpty()) {
            Pattern pattern = Pattern.compile(filters.getName(), Pattern.CASE_INSENSITIVE);
            boolean matches = eventPojo.getName().matches(pattern.pattern());
            if(!matches) return false;
        }
        if(!filters.getTypeList().isEmpty()) {
            if(!filters.getTypeList().contains(eventPojo.getType())) return false;
        }
        Date filterBasedOnDate;
        TimestampFilter timestampFilter = this.getFilters().getTimestampFilter();
        if(timestampFilter.getTimestampFilterKey().equals(TimestampFilterKey.START)) {
            filterBasedOnDate = eventPojo.getStartTimestamp();
        } else filterBasedOnDate = eventPojo.getEndTimestamp();

        try {
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
