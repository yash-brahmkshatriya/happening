package ln.dev.geohash;

import ln.dev.protos.coordinate.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatLonCoordinate {
    public static double LON_MIN = -180L;
    public static double LON_MAX = 180L;
    public static double LAT_MIN = -90L;
    public static double LAT_MAX = 90L;

    private double latitude;

    private double longitude;

    public LatLonCoordinate(Coordinate coordinate) {
        this.latitude = coordinate.getLatitude();
        this.longitude = coordinate.getLongitude();
    }

    @Override
    public String toString() {
        return "[Lat=" + this.latitude + ", Lon=" + this.longitude + "]";
    }

}
