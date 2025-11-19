package sos.game;

public class SimpleGame extends Game {
    private boolean over = false;
    private Integer winnerIdx = null;

    public SimpleGame(int size, Player p1, Player p2) {
        super(size, p1, p2);
    }

    @Override
    public int move(Cell letter, int row, int col) {
        // ignore after end of game
        if (over) return 0;

        // how many SOS lines existed before this move
        int before = board.getLines().size();

        // place the letter (may create SOS lines)
        int made = board.place(letter, row, col);

        // how many lines exist after this move
        int after = board.getLines().size();

        if (made > 0) {
            over = true;
            // who formed SOS
            winnerIdx = current;

            // tag any newly created lines with the current player
            for (int i = before; i < after; i++) {
                Board.Line ln = board.getLines().get(i);
                scoredLines.add(new ScoredLine(ln, players[current]));
            }
        } else {
            // no SOS just swap turn
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
    public String modeName() { 
        return "Simple"; 
    }
}
