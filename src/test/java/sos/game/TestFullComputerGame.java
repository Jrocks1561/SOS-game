package sos.game;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TestFullComputerGame {

    @Test
    public void testFullComputerGameFillsBoardAndStops() {
        GameManager gm = new GameManager(3, "Simple", true, true, difficulty.Easy);

        // Let the computer make moves until the board is full (or fail safely)
        int safety = 100;
        while (!gm.getBoard().isFull() && safety-- > 0) {
            int[] move = gm.chooseComputerMove();
            if (move == null) break; // broken, stop loop
            gm.placeMove(move[0], move[1]);
        }

        assertTrue(gm.getBoard().isFull(), "Board should be full after repeated computer moves (or safety) ");
        assertNull(gm.chooseComputerMove(), "No move should be returned when the board is full");
    }
}
