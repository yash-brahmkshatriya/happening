package ln.dev.geohash;

import ln.dev.protos.coordinate.Coordinate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.*;

class LatLonCoordinateTest {

    @Test
    void distanceBetween() {
        LatLonCoordinate c1 = LatLonCoordinate.builder()
                .latitude(18.5314613)
                .longitude(73.9452993)
                .build();
        LatLonCoordinate c2 = LatLonCoordinate.builder()
                .latitude(18.5130296)
                .longitude(73.9261986)
                .build();
        double distanceInKms = c1.distanceBetween(c2) * 6378.137;
        DecimalFormat decimalFormat = new DecimalFormat("####0.00");
        assertEquals(decimalFormat.format(2.87), decimalFormat.format(distanceInKms));
    }

    @Test
    void protoBasedConstructor() {
        Coordinate coordinate = Coordinate.newBuilder()
                .setLatitude(1.234)
                .setLongitude(4.567)
                .build();
        LatLonCoordinate latLonCoordinate = new LatLonCoordinate(coordinate);
        assertEquals(coordinate.getLatitude(), latLonCoordinate.getLatitude());
        assertEquals(coordinate.getLongitude(), latLonCoordinate.getLongitude());
    }

    @Test
    void toStringTest() {
        LatLonCoordinate c1 = LatLonCoordinate.builder()
                .latitude(18.5314613)
                .longitude(73.9452993)
                .build();
        assertEquals("[Lat=18.5314613, Lon=73.9452993]", c1.toString());
    }
}