package geohash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Neighbors {
    private String north;
    private String northWest;
    private String west;
    private String southWest;
    private String south;
    private String southEast;
    private String east;
    private String northEast;
}
