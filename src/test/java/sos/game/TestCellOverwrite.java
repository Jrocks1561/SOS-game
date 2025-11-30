package sos.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import sos.Game.*;

public class TestCellOverwrite {

    @Test
    public void testCannotOverwriteCell() {
        try {
            GameManager game = new GameManager(4, "Simple");
            assertTrue(game.placeMove(0, 0), "First move should be allowed");
            boolean result = game.placeMove(0, 0);
            assertFalse(result, "Should not be able to overwrite an occupied cell");

            System.out.println("TestCellOverwrite: PASSED");
        } catch (AssertionError e) {
            System.out.println("TestCellOverwrite: FAILED - " + e.getMessage());
            throw e;
        }
    }
}
