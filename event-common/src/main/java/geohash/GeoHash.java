package geohash;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GeoHash {
    public static final int MAX_HASH_PRECISION = 12;

    public static final int DEFAULT_HASH_PRECISION = 6;

    public static long encodeToDecimalHash(LatLonCoordinate coordinate, int precision) {
        if(precision > MAX_HASH_PRECISION)
            throw new IllegalArgumentException("Precision = " + precision + " is greater than max allowed precision of geohash = " + MAX_HASH_PRECISION);

        long decimalGeoHash = 0;

        boolean divideByLon = true;

        double lonMax = LatLonCoordinate.LON_MAX;
        double lonMin = LatLonCoordinate.LON_MIN;
        double latMax = LatLonCoordinate.LAT_MAX;
        double latMin = LatLonCoordinate.LAT_MIN;

        for (int achievedPrecision = 0; achievedPrecision < precision; achievedPrecision++) {
            for (int i = 0; i < 5; i++) {
                decimalGeoHash <<= 1;
                if(divideByLon) {
                    double mid = (lonMax + lonMin) / 2;
                    if(coordinate.getLongitude() > mid) {
                        decimalGeoHash |= 1;
                        lonMin = mid;
                    } else lonMax = mid;
                } else {
                    double mid = (latMax + latMin) / 2;
                    if(coordinate.getLatitude() > mid) {
                        decimalGeoHash |= 1;
                        latMin = mid;
                    } else latMax = mid;
                }
                divideByLon = !divideByLon;
            }
        }
        return decimalGeoHash;
    }

    public static String encode(LatLonCoordinate coordinate, int precision) {
        return Base32.encode(encodeToDecimalHash(coordinate, precision));
    }

    public static String encode(LatLonCoordinate coordinate) {
        return encode(coordinate, DEFAULT_HASH_PRECISION);
    }

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
                .latitude(BigDecimal.valueOf(latCentre).setScale(decimalPrecisionOfLat, RoundingMode.HALF_UP).doubleValue())
                .longitude(BigDecimal.valueOf(lonCentre).setScale(decimalPrecisionOfLon, RoundingMode.HALF_UP).doubleValue())
                .build();


    }

    public static Bounds getBounds(String geoHash) {
        if(geoHash.isEmpty()) throw new IllegalArgumentException("Invalid Geohash");
        String hash = geoHash.toLowerCase();
        boolean divideByLon = true;

        double lonMax = LatLonCoordinate.LON_MAX;
        double lonMin = LatLonCoordinate.LON_MIN;
        double latMax = LatLonCoordinate.LAT_MAX;
        double latMin = LatLonCoordinate.LAT_MIN;

        for(char base32Ch: hash.toCharArray()) {
            int base32Index = Base32.getCharNumber(base32Ch);
            for(int i = 4; i>=0; i--) {
                boolean isSet = ((base32Index >> i) & 1) > 0;
                if(divideByLon) {
                    double mid = (lonMin + lonMax) / 2;
                    if(isSet) lonMin = mid;
                    else lonMax = mid;
                } else {
                    double mid = (latMin + latMax) / 2;
                    if(isSet) latMin = mid;
                    else latMax = mid;
                }
                divideByLon = !divideByLon;
            }
        }
        return Bounds.builder()
                .northWest(
                        LatLonCoordinate.builder()
                                .latitude(latMin)
                                .longitude(lonMin)
                                .build()
                )
                .southEast(
                        LatLonCoordinate.builder()
                                .latitude(latMax)
                                .longitude(lonMax)
                                .build()
                )
                .build();

    }
}
