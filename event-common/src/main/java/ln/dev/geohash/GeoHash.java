package ln.dev.geohash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * GeoHash is a geospatial indexing to get proximity information
 * It divides earth surface into 32 parts recursively to achieve required precision.
 */
public final class GeoHash {
    public static final int MAX_HASH_PRECISION = 9;

    public static final int DEFAULT_HASH_PRECISION = 6;

    private static final HashMap<GeoHashBlock, Character> indicesToCharMap = new HashMap<>();

    private static final HashMap<Character, GeoHashBlock> charToIndicesMap = new HashMap<>();

    /* Bas32 character to indices map and vice versa */
    static {
        String[] baseBlocks = {"bcfguvyz", "89destwx", "2367kmqr", "0145hjnp"};
        for (int i = 0; i < baseBlocks.length; i++) {
            for (int j = 0; j < baseBlocks[i].length(); j++) {
                GeoHashBlock block = new GeoHashBlock(i, j);
                char ch = baseBlocks[i].charAt(j);
                indicesToCharMap.put(block, ch);
                charToIndicesMap.put(ch, block);
            }
        }
    }

    /**
     * Checks if given character is valid geohash character
     * @param ch character to check
     * @throws IllegalArgumentException if character is invalid geohash char
     */
    private static void checkIfGeoHashCharOrThrow(char ch) {
        if (!Base32.isBase32Char(ch)) throw new IllegalArgumentException("Not a valid geohash character");
    }

    /**
     * Encodes the coordinate to geohash with base 10 (decimal) with 5 bits giving one level
     * @param coordinate coordinate to convert to geo hash
     * @param precision level of precision needed (1 upto 12)
     * @return decimal representation of Geohash
     * @throws IllegalArgumentException if precision is less than 0 or greater than 12
     */
    public static long encodeToDecimalHash(LatLonCoordinate coordinate, int precision) {
        if (precision > MAX_HASH_PRECISION || precision < 0)
            throw new IllegalArgumentException(
                    "Precision = " + precision + " is not in range [0," + MAX_HASH_PRECISION + "]");

        long decimalGeoHash = 0;

        boolean divideByLon = true;

        double lonMax = LatLonCoordinate.LON_MAX;
        double lonMin = LatLonCoordinate.LON_MIN;
        double latMax = LatLonCoordinate.LAT_MAX;
        double latMin = LatLonCoordinate.LAT_MIN;

        for (int achievedPrecision = 0; achievedPrecision < precision; achievedPrecision++) {
            for (int i = 0; i < 5; i++) {
                decimalGeoHash <<= 1;
                if (divideByLon) {
                    double mid = (lonMax + lonMin) / 2;
                    if (coordinate.getLongitude() > mid) {
                        decimalGeoHash |= 1;
                        lonMin = mid;
                    } else lonMax = mid;
                } else {
                    double mid = (latMax + latMin) / 2;
                    if (coordinate.getLatitude() > mid) {
                        decimalGeoHash |= 1;
                        latMin = mid;
                    } else latMax = mid;
                }
                divideByLon = !divideByLon;
            }
        }
        return decimalGeoHash;
    }

    /**
     * Encodes the coordinate to geohash with base 32
     * @param coordinate coordinate to convert to geo hash
     * @param precision level of precision needed (1 upto 12)
     * @return Base32 representation of Geohash
     * @throws IllegalArgumentException if precision is less than 0 or greater than 12
     */
    public static String encode(LatLonCoordinate coordinate, int precision) {
        return Base32.encode(encodeToDecimalHash(coordinate, precision));
    }

    /**
     * Encodes the coordinate to geohash with base 32
     * @param coordinate coordinate to convert to geo hash
     * @return Base32 representation of Geohash with default precision
     */
    public static String encode(LatLonCoordinate coordinate) {
        return encode(coordinate, DEFAULT_HASH_PRECISION);
    }

    /**
     * Returns the latitude and longitude of given geohash based on level of precision
     * @param geoHash the hash to decode
     * @return coordinates in Latitude-longitude system
     */
    public static LatLonCoordinate decode(String geoHash) {
        Bounds bounds = getBounds(geoHash);

        double latMin = bounds.getNorthWest().getLatitude();
        double latMax = bounds.getSouthEast().getLatitude();
        double lonMin = bounds.getNorthWest().getLongitude();
        double lonMax = bounds.getSouthEast().getLongitude();

        double latCentre = (latMin + latMax) / 2;
        double lonCentre = (lonMin + lonMax) / 2;

        // to remove unrequired extra precision
        int decimalPrecisionOfLat = (int) Math.floor(2 - Math.log10(latMax - latMin));
        int decimalPrecisionOfLon = (int) Math.floor(2 - Math.log10(lonMax - lonMin));

        return LatLonCoordinate.builder()
                .latitude(BigDecimal.valueOf(latCentre)
                        .setScale(decimalPrecisionOfLat, RoundingMode.HALF_UP)
                        .doubleValue())
                .longitude(BigDecimal.valueOf(lonCentre)
                        .setScale(decimalPrecisionOfLon, RoundingMode.HALF_UP)
                        .doubleValue())
                .build();
    }

    /**
     * Method to find precision required to cover region with given radius in KMs based on division by geo hashes
     * @param radius the radius of the region to query
     * @return the amount of hash precision required
     */
    public static int precisionRequired(double radius) {
        if (radius >= 5000) return 0;
        if (radius >= 625) return 1;
        if (radius >= 156) return 2;
        if (radius >= 19.5) return 3;
        if (radius >= 4.89) return 4;
        if (radius >= 0.61) return 5;
        if (radius >= 0.153) return 6;
        if (radius >= 0.0191) return 7;
        if (radius >= 0.00477) return 8;
        return 9;
    }

    /**
     * Finds the bounds of the rectangle covered by given geohash
     * @param geoHash hash to find bounds of
     * @return NorthWest and SouthEast coordinates of the rectangle
     */
    public static Bounds getBounds(String geoHash) {
        if (geoHash.isEmpty()) throw new IllegalArgumentException("Invalid Geohash");
        String hash = geoHash.toLowerCase();
        boolean divideByLon = true;

        double lonMax = LatLonCoordinate.LON_MAX;
        double lonMin = LatLonCoordinate.LON_MIN;
        double latMax = LatLonCoordinate.LAT_MAX;
        double latMin = LatLonCoordinate.LAT_MIN;

        for (char base32Ch : hash.toCharArray()) {
            int base32Index = Base32.getCharNumber(base32Ch);
            for (int i = 4; i >= 0; i--) {
                boolean isSet = ((base32Index >> i) & 1) > 0;
                if (divideByLon) {
                    double mid = (lonMin + lonMax) / 2;
                    if (isSet) lonMin = mid;
                    else lonMax = mid;
                } else {
                    double mid = (latMin + latMax) / 2;
                    if (isSet) latMin = mid;
                    else latMax = mid;
                }
                divideByLon = !divideByLon;
            }
        }
        return Bounds.builder()
                .northWest(LatLonCoordinate.builder()
                        .latitude(latMin)
                        .longitude(lonMin)
                        .build())
                .southEast(LatLonCoordinate.builder()
                        .latitude(latMax)
                        .longitude(lonMax)
                        .build())
                .build();
    }

    /**
     * Finds adjacent block of character
     * @param ch character of which adjacent is supposed to be found
     * @param direction direction in which adjacent is supposed to be found
     * @return adjacent character
     */
    private static char findAdjacentBase32Char(char ch, Direction direction) {
        checkIfGeoHashCharOrThrow(ch);
        if (!charToIndicesMap.containsKey(ch)) throw new RuntimeException("Character indices not found for ch=" + ch);
        return indicesToCharMap.get(charToIndicesMap.get(ch).createCopy().move(1, direction));
    }

    /**
     * Checks if adjacent block in the given direction crosses parent level's boundary
     * @param ch character to check
     * @param direction direction to check
     * @return true if crosses boundary
     */
    private static boolean doesItCrossBoundary(char ch, Direction direction) {
        checkIfGeoHashCharOrThrow(ch);
        return charToIndicesMap.get(ch).doesItCrossBoundaryWileMoving(direction);
    }

    /**
     * Finds adjacent block of geohash in the given direction
     * @param geoHash hash of which adjacent is to be found
     * @param direction direction in which adjacent is to be found
     * @return adjacent geohash in the given direction
     */
    public static String adjacent(String geoHash, Direction direction) {
        char[] hashLevels = geoHash.toCharArray();
        boolean shouldUpdateParentLevelBlock = true;
        for (int i = hashLevels.length - 1; i >= 0; i--) {
            if (!shouldUpdateParentLevelBlock) break;

            boolean isHorizontalLayout = i % 2 == 0;
            Direction realizedDirection = isHorizontalLayout ? direction : Direction.getComplementDirection(direction);

            shouldUpdateParentLevelBlock = doesItCrossBoundary(hashLevels[i], realizedDirection);

            char adjacentBlock = findAdjacentBase32Char(hashLevels[i], realizedDirection);
            hashLevels[i] = adjacentBlock;
        }
        return String.valueOf(hashLevels);
    }

    /**
     * Finds neighbors of given geohash in all 8 directions: N, NW, W, SW, S, SE, E, NE
     * @param geoHash hash of which neighbors are to be found
     * @return Geohashes of all 8 neighbors
     */
    public static Neighbors findNeighbors(String geoHash) {
        return Neighbors.builder()
                .north(adjacent(geoHash, Direction.NORTH))
                .northWest(adjacent(adjacent(geoHash, Direction.WEST), Direction.NORTH))
                .west(adjacent(geoHash, Direction.WEST))
                .southWest(adjacent(adjacent(geoHash, Direction.WEST), Direction.SOUTH))
                .south(adjacent(geoHash, Direction.SOUTH))
                .southEast(adjacent(adjacent(geoHash, Direction.EAST), Direction.SOUTH))
                .east(adjacent(geoHash, Direction.EAST))
                .northEast(adjacent(adjacent(geoHash, Direction.EAST), Direction.NORTH))
                .build();
    }
}
