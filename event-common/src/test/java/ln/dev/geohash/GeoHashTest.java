package ln.dev.geohash;

import static org.junit.jupiter.api.Assertions.*;

import ln.dev.geohash.Direction;
import ln.dev.geohash.GeoHash;
import ln.dev.geohash.LatLonCoordinate;
import ln.dev.geohash.Neighbors;
import org.junit.jupiter.api.Test;

class GeoHashTest {

    @Test
    void encode() {
        LatLonCoordinate coordinate =
                LatLonCoordinate.builder().longitude(-4.334).latitude(48.6667).build();

        String encodedGeoHash = GeoHash.encode(coordinate, 6);
        assertEquals("gbsuv7", encodedGeoHash);
    }

    @Test
    void defaultEncode() {
        LatLonCoordinate coordinate =
                LatLonCoordinate.builder().latitude(48.6667).longitude(-4.334).build();

        String encodedGeoHash = GeoHash.encode(coordinate);
        assertEquals("gbsuv7", encodedGeoHash);
    }

    @Test
    void invalidPrecisionEncode() {
        LatLonCoordinate coordinate =
                LatLonCoordinate.builder().longitude(-4.334).latitude(48.6667).build();

        assertThrows(IllegalArgumentException.class, () -> GeoHash.encode(coordinate, -1));
    }

    @Test
    void decode() {
        LatLonCoordinate coordinate =
                LatLonCoordinate.builder().longitude(-4.334).latitude(48.6667).build();

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
    void invalidGeoHashAdjacent() {
        String geoHash = "a";
        assertThrows(IllegalArgumentException.class, () -> GeoHash.adjacent(geoHash, Direction.EAST));
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

    @Test
    void precisionRequired() {
        assertEquals(0, GeoHash.precisionRequired(10000));
        assertEquals(1, GeoHash.precisionRequired(4000));
        assertEquals(2, GeoHash.precisionRequired(600));
        assertEquals(3, GeoHash.precisionRequired(150));
        assertEquals(4, GeoHash.precisionRequired(18));
        assertEquals(5, GeoHash.precisionRequired(4));
        assertEquals(6, GeoHash.precisionRequired(0.5));
        assertEquals(7, GeoHash.precisionRequired(0.1));
        assertEquals(8, GeoHash.precisionRequired(0.018));
        assertEquals(9, GeoHash.precisionRequired(0.00470));
    }
}
