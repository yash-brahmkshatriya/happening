package ln.dev.geohash;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Base32 {

    private static final char[] base32Characters = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p',
        'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final Long BASE = 32L;

    private static final HashMap<Character, Integer> charToNumberMap = new HashMap<>();

    static {
        int i = 0;
        for (char c : base32Characters) charToNumberMap.put(c, i++);
    }

    public static List<Character> getBase32Characters() {
        return IntStream.range(0, base32Characters.length)
                .mapToObj(i -> base32Characters[i])
                .collect(Collectors.toList());
    }

    public static boolean isBase32Char(char ch) {
        return charToNumberMap.containsKey(ch);
    }

    public static boolean isValidBase32String(String s) {
        return IntStream.range(0, s.length())
                .mapToObj(s::charAt)
                .map(Base32::isBase32Char)
                .reduce(true, (acc, curr) -> acc && curr);
    }

    public static String encode(Long decimalGeoHash) {
        StringBuilder base32GeoHash = new StringBuilder();
        while (decimalGeoHash > 0) {
            int index = (int) (decimalGeoHash % BASE);
            base32GeoHash.append(base32Characters[index]);
            decimalGeoHash = decimalGeoHash / BASE;
        }
        base32GeoHash.reverse();
        return base32GeoHash.toString();
    }

    public static String encode(String binaryGeoHash) {
        return encode(Long.valueOf(binaryGeoHash, 2));
    }

    public static Long decode(String geoHash) {
        long decimalGeoHash = 0;
        for (char ch : geoHash.toCharArray()) {
            int decimalRepOfChar = getCharNumber(ch);
            decimalGeoHash *= BASE;
            decimalGeoHash += decimalRepOfChar;
        }
        return decimalGeoHash;
    }

    public static int getCharNumber(char ch) {
        if (isBase32Char(ch)) return charToNumberMap.get(ch);
        else throw new IllegalArgumentException("Not a base32 character: " + ch);
    }
}
