package geohash;

import static org.junit.jupiter.api.Assertions.*;

import ln.dev.geohash.Base32;
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
}
