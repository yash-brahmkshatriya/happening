package ln.dev.util;

import ln.dev.pojo.EventPojo;
import ln.dev.pojo.Location;
import ln.dev.protos.coordinate.Coordinate;
import ln.dev.protos.event.Event;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * Used to interconvert between classes used by grpc and POJOs stored in DB
 * */
@Component
public class EventConvertor {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public Location convert(@NotNull Coordinate coordinates) {
        return Location.builder()
                .type("Point")
                .coordinates(
                        List.of(coordinates.getLongitude(), coordinates.getLatitude())
                )
                .build();
    }

    public Coordinate convert(@NotNull Location location) {
        if(location.getCoordinates() == null || location.getCoordinates().size() < 2) {
            return Coordinate.getDefaultInstance();
        }
        return Coordinate.newBuilder()
                .setLongitude(location.getCoordinates().get(0))
                .setLatitude(location.getCoordinates().get(1))
                .build();
    }

    public Event convert(@NotNull EventPojo eventPojo) {
        return Event.newBuilder()
                .setId(eventPojo.getId())
                .setName(eventPojo.getName())
                .setType(eventPojo.getType())
                .setDescription(eventPojo.getDescription())
                .setStartTimestamp(simpleDateFormat.format(eventPojo.getStartTimestamp()))
                .setEndTimestamp(simpleDateFormat.format(eventPojo.getEndTimestamp()))
                .setLocation(convert(eventPojo.getLocation()))
                .build();
    }

    public EventPojo convert(@NotNull Event event) throws ParseException {
        return EventPojo.builder()
                .id(event.getId())
                .name(event.getName())
                .type(event.getType())
                .description(event.getDescription())
                .startTimestamp(simpleDateFormat.parse(event.getStartTimestamp()))
                .endTimestamp(simpleDateFormat.parse(event.getEndTimestamp()))
                .location(convert(event.getLocation()))
                .build();
    }

}
