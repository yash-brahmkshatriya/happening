package geohash;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bounds {

    private LatLonCoordinate northWest;

    private LatLonCoordinate southEast;
}
