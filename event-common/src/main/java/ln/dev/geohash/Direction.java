package ln.dev.geohash;

import org.jetbrains.annotations.NotNull;

public enum Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST,
    NA;

    /**
     * @param direction Current direction
     * @return Complement of direction when division axis is changed
     */
    public static Direction getComplementDirection(@NotNull Direction direction) {
        switch (direction) {
            case EAST -> {
                return Direction.NORTH;
            }
            case WEST -> {
                return Direction.SOUTH;
            }
            case NORTH -> {
                return Direction.EAST;
            }
            case SOUTH -> {
                return Direction.WEST;
            }
        }
        return Direction.NA;
    }
}
