package sos.game;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TestEasyComputerBehavior {

    @Test
    public void testEasyComputerChoosesNeighborOfOppositeLetter() {
        Board board = new Board(3);

        // place an O at the center
        board.place(Cell.O, 1, 1);

        // Easy strategy for S-player should look for empty neighbor of O
        EasyComputerPlayer cpu = new EasyComputerPlayer();
        int[] move = cpu.chooseMove(board, 3, 0);

        assertNotNull(move, "CPU should find a move when there is an adjacent empty cell");

        int r = move[0];
        int c = move[1];

        // Must be one of the orthogonal neighbors of (1,1)
        boolean isNeighbor = (r == 0 && c == 1) || (r == 2 && c == 1) || (r == 1 && c == 0) || (r == 1 && c == 2);
        assertTrue(isNeighbor, "CPU should choose an orthogonal neighbor of the O cell");
    }
}
