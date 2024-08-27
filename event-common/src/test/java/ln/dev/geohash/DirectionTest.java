package ln.dev.geohash;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DirectionTest {

    @Test
    void getComplementDirection() {
        assertEquals(Direction.NORTH, Direction.getComplementDirection(Direction.EAST));
        assertEquals(Direction.EAST, Direction.getComplementDirection(Direction.NORTH));
        assertEquals(Direction.SOUTH, Direction.getComplementDirection(Direction.WEST));
        assertEquals(Direction.WEST, Direction.getComplementDirection(Direction.SOUTH));
        assertEquals(Direction.NA, Direction.getComplementDirection(Direction.NA));
    }
}
