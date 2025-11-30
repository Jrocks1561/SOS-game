package sos.game;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sos.Game.*;

// Tests for Easy, Medium, and Hard computer players
public class ComputerPlayerTest {

    // helper place S
    private void setS(Board board, int row, int col) {
        board.place(Cell.S, row, col);
    }

    // helper place O
    private void setO(Board board, int row, int col) {
        board.place(Cell.O, row, col);
    }

    // compare expected vs actual move with prints
    private void assertMoveEquals(int expectedRow, int expectedCol, int[] move) {
        assertNotNull(move, "move should not be null");
        int actualRow = move[0];
        int actualCol = move[1];

        System.out.println("  expected move = (" + expectedRow + ", " + expectedCol + ")");
        System.out.println("  actual   move = (" + actualRow + ", " + actualCol + ")");

        assertEquals(expectedRow, actualRow, "row mismatch");
        assertEquals(expectedCol, actualCol, "col mismatch");
    }

    // ================== EASY TESTS ==================

    @Test
    public void easyPlayerPicksEmptyInBounds() {

        System.out.println("---------------------------------------------------");
        System.out.println(" Running ComputerPlayerTest.easyPlayerPicksEmptyInBounds");
        System.out.println("---------------------------------------------------");

        try {
            int size = 7;
            Board board = new Board(size);
            System.out.println("Created board of size " + size + "x" + size);

            // fill a few cells so the AI has to skip them
            setS(board, 0, 0);
            setO(board, 1, 1);
            setS(board, 2, 2);
            System.out.println("Placed some S/O cells at (0,0), (1,1), (2,2)");

            EasyComputerPlayer cpu = new EasyComputerPlayer();
            System.out.println("EasyComputerPlayer created for S-player");

            int[] move = cpu.chooseMove(board, size, 0);  // player 0 = S
            assertNotNull(move, "Easy CPU should find a move");

            int row = move[0];
            int col = move[1];

            System.out.println("CPU move received: (" + row + ", " + col + ")");

            assertTrue(row >= 0 && row < size, "row out of bounds");
            assertTrue(col >= 0 && col < size, "col out of bounds");
            assertTrue(board.get(row, col) != Cell.S && board.get(row, col) != Cell.O,
                    "easy chose an already filled cell");

            System.out.println(">>> easyPlayerPicksEmptyInBounds: PASSED\n");

        } catch (AssertionError e) {
            System.out.println(">>> easyPlayerPicksEmptyInBounds: FAILED - " + e.getMessage() + "\n");
            throw e;
        }
    }

    @Test
    public void easyPlayerChoosesNeighborOfOppositeLetter() {

        System.out.println("---------------------------------------------------");
        System.out.println(" Running ComputerPlayerTest.easyPlayerChoosesNeighborOfOppositeLetter");
        System.out.println("---------------------------------------------------");

        try {
            Board board = new Board(7);
            System.out.println("Created board of size 7x7");

            // place an O at (1,1)
            setO(board, 1, 1);
            System.out.println("Placed O at (1,1)");

            System.out.println("Expected valid neighbors for S: (0,1), (2,1), (1,0), (1,2)");

            EasyComputerPlayer cpu = new EasyComputerPlayer();
            System.out.println("EasyComputerPlayer created for S-player");

            int[] move = cpu.chooseMove(board, 7, 0);
            assertNotNull(move, "Easy CPU should find a move when there is an adjacent empty cell");

            int r = move[0];
            int c = move[1];
            System.out.println("CPU move received: (" + r + ", " + c + ")");

            boolean isNeighbor =
                    (r == 0 && c == 1) ||
                    (r == 2 && c == 1) ||
                    (r == 1 && c == 0) ||
                    (r == 1 && c == 2);

            System.out.println("Checking if move is a valid orthogonal neighbor of (1,1)...");

            assertTrue(isNeighbor,
                    "Easy CPU should choose an orthogonal neighbor of the O cell at (1,1)");

            System.out.println(">>> easyPlayerChoosesNeighborOfOppositeLetter: PASSED\n");

        } catch (AssertionError e) {
            System.out.println(">>> easyPlayerChoosesNeighborOfOppositeLetter: FAILED - " + e.getMessage() + "\n");
            throw e;
        }
    }

    // ================== MEDIUM TESTS ==================

    // S should complete horizontal S-O-_ as SOS
    @Test
    public void mediumSCompletesSOSHorizontal() {

        System.out.println("---------------------------------------------------");
        System.out.println(" Running ComputerPlayerTest.mediumSCompletesSOSHorizontal");
        System.out.println("---------------------------------------------------");

        try {
            int size = 7;
            Board board = new Board(size);
            System.out.println("Created board of size " + size + "x" + size);

            MediumComputerPlayer cpu = new MediumComputerPlayer();
            System.out.println("MediumComputerPlayer created for S-player");

            // row 3: _ S O _
            setS(board, 3, 2);
            setO(board, 3, 3);
            System.out.println("Placed pattern S at (3,2) and O at (3,3); expecting S at (3,4)");

            int[] move = cpu.chooseMove(board, size, 0); // player 0 = S

            assertMoveEquals(3, 4, move);

            System.out.println(">>> mediumSCompletesSOSHorizontal: PASSED\n");

        } catch (AssertionError e) {
            System.out.println(">>> mediumSCompletesSOSHorizontal: FAILED - " + e.getMessage() + "\n");
            throw e;
        }
    }

    // O should block S-O-_ horizontally
    @Test
    public void mediumOBlocksSO_Horizontal() {

        System.out.println("---------------------------------------------------");
        System.out.println(" Running ComputerPlayerTest.mediumOBlocksSO_Horizontal");
        System.out.println("---------------------------------------------------");

        try {
            int size = 7;
            Board board = new Board(size);
            System.out.println("Created board of size " + size + "x" + size);

            MediumComputerPlayer cpu = new MediumComputerPlayer();
            System.out.println("MediumComputerPlayer created for O-player");

            // row 2: S O _
            setS(board, 2, 1);
            setO(board, 2, 2);
            System.out.println("Placed pattern S at (2,1) and O at (2,2); expecting block O at (2,3)");

            int[] move = cpu.chooseMove(board, size, 1); // player 1 = O

            assertMoveEquals(2, 3, move);

            System.out.println(">>> mediumOBlocksSO_Horizontal: PASSED\n");

        } catch (AssertionError e) {
            System.out.println(">>> mediumOBlocksSO_Horizontal: FAILED - " + e.getMessage() + "\n");
            throw e;
        }
    }

    // ================== HARD TESTS ==================

    // Hard O should score by taking middle of S _ S
    @Test
    public void hardOTakesMiddleToScore() {

        System.out.println("---------------------------------------------------");
        System.out.println(" Running ComputerPlayerTest.hardOTakesMiddleToScore");
        System.out.println("---------------------------------------------------");

        try {
            int size = 7;
            Board board = new Board(size);
            System.out.println("Created board of size " + size + "x" + size);

            HardComputerPlayer cpu = new HardComputerPlayer();
            System.out.println("HardComputerPlayer created for O-player");

            // row 1: S _ S
            setS(board, 1, 1);
            setS(board, 1, 3);
            System.out.println("Placed S at (1,1) and (1,3); expecting O at (1,2)");

            int[] move = cpu.chooseMove(board, size, 1); // player 1 = O

            assertMoveEquals(1, 2, move);

            System.out.println(">>> hardOTakesMiddleToScore: PASSED\n");

        } catch (AssertionError e) {
            System.out.println(">>> hardOTakesMiddleToScore: FAILED - " + e.getMessage() + "\n");
            throw e;
        }
    }

    // Hard O should still block S-O-_
    @Test
    public void hardOBlocksSO_Horizontal() {

        System.out.println("---------------------------------------------------");
        System.out.println(" Running ComputerPlayerTest.hardOBlocksSO_Horizontal");
        System.out.println("---------------------------------------------------");

        try {
            int size = 7;
            Board board = new Board(size);
            System.out.println("Created board of size " + size + "x" + size);

            HardComputerPlayer cpu = new HardComputerPlayer();
            System.out.println("HardComputerPlayer created for O-player");

            // row 4: S O _
            setS(board, 4, 2);
            setO(board, 4, 3);
            System.out.println("Placed S at (4,2) and O at (4,3); expecting block O at (4,4)");

            int[] move = cpu.chooseMove(board, size, 1); // player 1 = O

            assertMoveEquals(4, 4, move);

            System.out.println(">>> hardOBlocksSO_Horizontal: PASSED\n");

        } catch (AssertionError e) {
            System.out.println(">>> hardOBlocksSO_Horizontal: FAILED - " + e.getMessage() + "\n");
            throw e;
        }
    }
}
