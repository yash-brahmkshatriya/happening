package ln.dev.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IdGeneratorTest {

    @Test
    void generate() {
        assertFalse(IdGenerator.generate().isEmpty());
    }
}
