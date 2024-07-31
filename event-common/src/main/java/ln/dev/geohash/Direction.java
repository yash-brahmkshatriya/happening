package ln.dev.geohash;

public enum Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST;

    public static Direction getComplementDirection(Direction direction) {
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
            default -> {
                return null;
            }
        }
    }
}
