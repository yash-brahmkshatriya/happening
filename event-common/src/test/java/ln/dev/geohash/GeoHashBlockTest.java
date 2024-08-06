package ln.dev.geohash;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GeoHashBlockTest {

    @Test
    void move() {
        GeoHashBlock block = new GeoHashBlock(0, 3);
        assertEquals(new GeoHashBlock(0, 4), block.createCopy().move(1, Direction.EAST));
        assertEquals(new GeoHashBlock(0, 2), block.createCopy().move(1, Direction.WEST));
        assertEquals(new GeoHashBlock(3, 3), block.createCopy().move(1, Direction.NORTH));
        assertEquals(new GeoHashBlock(1, 3), block.createCopy().move(1, Direction.SOUTH));
    }

    @Test
    void crossesBoundaryOnWest() {
        GeoHashBlock block = new GeoHashBlock(1, 0);
        assertTrue(block.doesItCrossBoundaryWileMoving(Direction.WEST));
    }

    @Test
    void crossesBoundaryOnEast() {
        GeoHashBlock block = new GeoHashBlock(1, 7);
        assertTrue(block.doesItCrossBoundaryWileMoving(Direction.EAST));
    }

    @Test
    void crossesBoundaryOnNorth() {
        GeoHashBlock block = new GeoHashBlock(0, 1);
        assertTrue(block.doesItCrossBoundaryWileMoving(Direction.NORTH));
    }

    @Test
    void crossesBoundaryOnSouth() {
        GeoHashBlock block = new GeoHashBlock(3, 1);
        assertTrue(block.doesItCrossBoundaryWileMoving(Direction.SOUTH));
    }

    @Test
    void invalidDirection() {
        GeoHashBlock block = new GeoHashBlock(1, 0);
        assertFalse(block.doesItCrossBoundaryWileMoving(Direction.NA));
    }

    @Test
    void createCopy() {
        GeoHashBlock block = new GeoHashBlock(1, 1);
        GeoHashBlock copyBlock = block.createCopy();
        assertEquals(block.getFirst(), copyBlock.getFirst());
        assertEquals(block.getSecond(), copyBlock.getSecond());
    }
}
