package geohash;

import ln.dev.geohash.Direction;
import ln.dev.geohash.GeoHash;
import ln.dev.geohash.LatLonCoordinate;
import ln.dev.geohash.Neighbors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoHashTest {

    @Test
    void encode() {
        LatLonCoordinate coordinate = LatLonCoordinate.builder()
                .longitude(-4.334)
                .latitude(48.6667)
                .build();

        String encodedGeoHash = GeoHash.encode(coordinate, 6);
        assertEquals("gbsuv7", encodedGeoHash);
    }

    @Test
    void decode() {
        LatLonCoordinate coordinate = LatLonCoordinate.builder()
                .longitude(-4.334)
                .latitude(48.6667)
                .build();

        LatLonCoordinate decodedCoordinate = GeoHash.decode("gbsuv7");
        assertEquals(coordinate, decodedCoordinate);
    }

    @Test
    void adjacent() {
        String geoHash = "zbz";
        assertEquals("b0b", GeoHash.adjacent(geoHash, Direction.EAST));
        assertEquals("zcp", GeoHash.adjacent(geoHash, Direction.NORTH));
        assertEquals("zby", GeoHash.adjacent(geoHash, Direction.WEST));
        assertEquals("zbx", GeoHash.adjacent(geoHash, Direction.SOUTH));
    }

    @Test
    void neighbors() {
        String geoHash = "zbz";
        Neighbors neighbors = GeoHash.findNeighbors(geoHash);
        assertEquals("b0b", neighbors.getEast());
        assertEquals("b10", neighbors.getNorthEast());
        assertEquals("zcp", neighbors.getNorth());
        assertEquals("zcn", neighbors.getNorthWest());
        assertEquals("zby", neighbors.getWest());
        assertEquals("zbw", neighbors.getSouthWest());
        assertEquals("zbx", neighbors.getSouth());
        assertEquals("b08", neighbors.getSouthEast());
    }
}