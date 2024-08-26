package ln.dev.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

    @Test
    void generate() {
        assertFalse(IdGenerator.generate().isEmpty());
    }
}