package sos.game;

public class GeneralGame extends Game {
    private final int[] score = new int[2];

    public GeneralGame(int size, Player p1, Player p2) {
        super(size, p1, p2);
    }

    @Override
    public int move(Cell letter, int row, int col) {
        int made = board.place(letter, row, col);
        if (made > 0) {
            score[current] += made;
        }
        //swap turns after scoring
        swapTurn();
        return made;
    }

    @Override
    public boolean isOver() {
        return board.isFull();
    }

    @Override
    public String statusText() {
        return String.format("P1: %d  P2: %d  • Turn: %s",
                score[0], score[1], currentPlayer().name());
    }

    public int score(int i) { return score[i]; }
    @Override
    public String modeName() {
        return "General";
}
}
