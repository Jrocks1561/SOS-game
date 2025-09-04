package sos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class MainTest {

    // First test case
    @Test
    void testFindLargest() {
        assertEquals(15, Main.findLargest(15, 5));
        assertEquals(12, Main.findLargest(10, 12));
        assertEquals(-1, Main.findLargest(3, 3));
    }

    // Second test case
    @Test
    void testSimpleMath() {
        assertEquals(8, 4 + 4, "4 + 4 should equal 8");
    }
}
