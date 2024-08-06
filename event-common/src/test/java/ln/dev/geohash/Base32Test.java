package ln.dev.geohash;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class Base32Test {

    @Test
    void encodeWithDecimalHash() {
        long decimalHash = 707;
        assertEquals("q3", Base32.encode(decimalHash));
    }

    @Test
    void encodeWithBinaryHash() {
        String binaryHash = "1011000011";
        assertEquals("q3", Base32.encode(binaryHash));
    }

    @Test
    void decode() {
        String hash = "q3";
        assertEquals(707, Base32.decode(hash));
    }

    @Test
    void getCharNumber() {
        assertEquals(1, Base32.getCharNumber('1'));
    }

    @Test
    void getCharNumberWithInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Base32.getCharNumber('a'));
    }

    @Test
    void validBase32String() {
        assertTrue(Base32.isValidBase32String("0b"));
    }

    @Test
    void invalidBase32String() {
        assertFalse(Base32.isValidBase32String("a0bi"));
    }

    @Test
    void getBase32Characters() {
        assertEquals(
                "0123456789bcdefghjkmnpqrstuvwxyz",
                Base32.getBase32Characters().stream().map(String::valueOf).collect(Collectors.joining()));
    }
}
