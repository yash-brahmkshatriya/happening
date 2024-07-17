package geohash;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LatLonCoordinate {
    public static double LON_MIN = -180L;
    public static double LON_MAX = 180L;
    public static double LAT_MIN = -90L;
    public static double LAT_MAX = 90L;

    private double latitude;

    private double longitude;

    @Override
    public String toString() {
        return "[Lat=" + this.latitude + ", Lon=" + this.longitude + "]";
    }

}
