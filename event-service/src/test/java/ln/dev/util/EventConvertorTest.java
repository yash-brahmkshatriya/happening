package ln.dev.util;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import ln.dev.pojo.EventPojo;
import ln.dev.pojo.Location;
import ln.dev.protos.coordinate.Coordinate;
import ln.dev.protos.event.Event;
import ln.dev.protos.event.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventConvertorTest {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    String dateStr = "2024-01-01T00:00:00";
    double lat = 12.34, lon = -10.44;
    String id = "Test_id", name = "ABC", desc = "description";

    EventPojo defaultEventPojo;

    Event defaultEventProto;

    @BeforeEach
    void setDefaults() throws ParseException {
        Date date = simpleDateFormat.parse(dateStr);

        defaultEventPojo = EventPojo.builder()
                .id(id)
                .name(name)
                .type(EventType.BUSINESS)
                .description(desc)
                .startTimestamp(simpleDateFormat.parse(dateStr))
                .endTimestamp(simpleDateFormat.parse(dateStr))
                .location(Location.builder()
                        .type("Point")
                        .coordinates(List.of(lon, lat))
                        .build())
                .build();

        defaultEventProto = Event.newBuilder()
                .setId(id)
                .setName(name)
                .setType(EventType.BUSINESS)
                .setDescription(desc)
                .setStartTimestamp(simpleDateFormat.format(date))
                .setEndTimestamp(simpleDateFormat.format(date))
                .setLocation(Coordinate.newBuilder()
                        .setLatitude(lat)
                        .setLongitude(lon)
                        .build())
                .build();
    }

    @Test
    void parseISODate() throws ParseException {
        Date date = EventConvertor.parseISODate(dateStr);
        assertEquals(simpleDateFormat.parse(dateStr), date);
    }

    @Test
    void formatISODate() throws ParseException {
        Date date = simpleDateFormat.parse(dateStr);
        assertEquals(dateStr, EventConvertor.formatISODate(date));
    }

    @Test
    void locationConvert() {
        Coordinate coordinate =
                Coordinate.newBuilder().setLatitude(lat).setLongitude(lon).build();

        Location location =
                Location.builder().coordinates(List.of(lon, lat)).type("Point").build();

        assertEquals(location, EventConvertor.convert(coordinate));
    }

    @Test
    void coordinateConvert() {
        Location location =
                Location.builder().coordinates(List.of(lon, lat)).type("Point").build();

        Coordinate coordinate =
                Coordinate.newBuilder().setLatitude(lat).setLongitude(lon).build();

        assertEquals(coordinate, EventConvertor.convert(location));
    }

    @Test
    void eventPojoConvert() throws ParseException {
        assertEquals(defaultEventPojo, EventConvertor.convert(defaultEventProto));
    }

    @Test
    void eventConvertor() {
        assertEquals(defaultEventProto, EventConvertor.convert(defaultEventPojo));
    }
}
