package ln.dev.geohash;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Bounds {

    private LatLonCoordinate northWest;

    private LatLonCoordinate southEast;
}
