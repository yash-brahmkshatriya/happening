package geohash;

import common.Pair;

public class GeoHashBlock extends Pair<Integer, Integer> {

    private static final int NS_BLOCKS = 4;
    private static final int EW_BLOCKS = 8;

    public GeoHashBlock() {
        super();
    }

    public GeoHashBlock(Integer row, Integer col) {
        super(row, col);
    }



    public GeoHashBlock move(int blocks, Direction direction) {
        switch (direction) {
            case EAST -> this.setSecond(
                    (this.getSecond() + (blocks % EW_BLOCKS)) % EW_BLOCKS
            );
            case WEST -> this.setSecond(
                    (this.getSecond() - (blocks % EW_BLOCKS) + EW_BLOCKS) % EW_BLOCKS
            );
            case NORTH -> this.setFirst(
                    (this.getFirst() - (blocks % NS_BLOCKS) + NS_BLOCKS) % EW_BLOCKS
            );
            case SOUTH -> this.setFirst(
                    (this.getFirst() + (blocks % EW_BLOCKS)) % EW_BLOCKS
            );
        }
        return this;
    }

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

    public GeoHashBlock createCopy() {
        return new GeoHashBlock(this.getFirst(), this.getSecond());
    }
}
