package sos.game;

public class SimpleGame extends Game {
    private boolean over = false;
    private Integer winnerIdx = null;

    public SimpleGame(int size, Player p1, Player p2) {
        super(size, p1, p2);
    }

    @Override
    public int move(Cell letter, int row, int col) {
        // ignore after end
        if (over) return 0;

        int made = board.place(letter, row, col);

        if (made > 0) {
            over = true;
            // who formed SOS
            winnerIdx = current;
        } else {
            // no SOS, just swap turn
            swapTurn();
        }

        // how many made
        return made;
    }

    @Override
    public boolean isOver() {
        return over || board.isFull();
    }

    @Override
    public String statusText() {
        if (over && winnerIdx != null) return "Winner: " + players[winnerIdx].name();
        if (board.isFull() && winnerIdx == null) return "Draw (no SOS)";
        return "Simple • Turn: " + currentPlayer().name();
    }

    @Override
    public String modeName() { return "Simple"; }
}
