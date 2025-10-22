package sos.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TestBoardInitialization {

    @Test
    public void testBoardInitializesToRequestedSize() {
        int[] sizes = {4, 9, 12};

        for (int size : sizes) {
            GameManager simpleGame = new GameManager(size, "Simple");
            assertEquals(size, simpleGame.getSize(), "Simple mode board size should match " + size);
            assertEquals("Simple", simpleGame.getMode(), "Mode should be Simple");
            checkBoardIsEmpty(simpleGame);

            GameManager generalGame = new GameManager(size, "General");
            assertEquals(size, generalGame.getSize(), "General mode board size should match " + size);
            assertEquals("General", generalGame.getMode(), "Mode should be General");
            checkBoardIsEmpty(generalGame);
        }

        System.out.println("✅ Board initializes correctly for all sizes and modes");
    }

    private void checkBoardIsEmpty(GameManager game) {
        for (int r = 0; r < game.getSize(); r++) {
            for (int c = 0; c < game.getSize(); c++) {
                assertEquals("", game.getCell(r, c), "Each cell should start empty");
            }
        }
    }
}
