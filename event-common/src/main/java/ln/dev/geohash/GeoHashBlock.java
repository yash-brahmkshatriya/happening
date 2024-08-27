package ln.dev.geohash;

import ln.dev.common.Pair;

public class GeoHashBlock extends Pair<Integer, Integer> {

    private static final int NS_BLOCKS = 4;
    private static final int EW_BLOCKS = 8;

    public GeoHashBlock() {
        super();
    }

    public GeoHashBlock(Integer row, Integer col) {
        super(row, col);
    }

    /**
     * Moves the current block
     * @param blocks number of positions to move
     * @param direction direction to move
     * @return mutated block with moved position
     */
    public GeoHashBlock move(int blocks, Direction direction) {
        switch (direction) {
            case EAST -> this.setSecond((this.getSecond() + (blocks % EW_BLOCKS)) % EW_BLOCKS);
            case WEST -> this.setSecond((this.getSecond() - (blocks % EW_BLOCKS) + EW_BLOCKS) % EW_BLOCKS);
            case NORTH -> this.setFirst((this.getFirst() - (blocks % NS_BLOCKS) + NS_BLOCKS) % NS_BLOCKS);
            case SOUTH -> this.setFirst((this.getFirst() + (blocks % NS_BLOCKS)) % NS_BLOCKS);
        }
        return this;
    }

    /**
     * Checks if Boundary is crossed of current level while moving
     * @param direction Direction to move
     * @return True if it crosses
     */
    public boolean doesItCrossBoundaryWileMoving(Direction direction) {
        switch (direction) {
            case WEST -> {
                return this.getSecond() == 0;
            }
            case EAST -> {
                return this.getSecond() == (EW_BLOCKS - 1);
            }
            case NORTH -> {
                return this.getFirst() == 0;
            }
            case SOUTH -> {
                return this.getFirst() == (NS_BLOCKS - 1);
            }
        }
        return false;
    }

    /**
     * Creates copy of given block
     * @return Copy of Block
     */
    public GeoHashBlock createCopy() {
        return new GeoHashBlock(this.getFirst(), this.getSecond());
    }
}
