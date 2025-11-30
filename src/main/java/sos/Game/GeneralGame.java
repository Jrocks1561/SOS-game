package sos.Game;

public class GeneralGame extends Game {

    // score0 = P1, score1 = P2
    private final int[] score = new int[2];
    private boolean over = false;
    // 0, 1, or null for draw
    private Integer winnerIdx = null;      

    public GeneralGame(int size, Player p1, Player p2) {
        super(size, p1, p2);
    }

    @Override
    public int move(Cell letter, int row, int col) {
        // ignore moves after game is over
        if (isOver()) return 0;

        // remember how many lines existed before this move
        int before = board.getLines().size();

        // place the letter on the board 
        int made = board.place(letter, row, col);

        // how many lines after this move
        int after = board.getLines().size();

        if (made > 0) {
            // add points for the current player
            score[current] += made;

            // record the scored lines with the player who made them
            for (int i = before; i < after; i++) {
                Board.Line ln = board.getLines().get(i);
                scoredLines.add(new ScoredLine(ln, players[current]));
            }

            // General SOS usually gives another turn when you score so we DO NOT swapTurn() here
        } else {
            // no SOS formed normal turn 
            swapTurn();
        }

        // check who was winner when board is full
        if (board.isFull()) {
            over = true;
            if (score[0] > score[1]) {
                winnerIdx = 0;
            } else if (score[1] > score[0]) {
                winnerIdx = 1;
            } else {
                winnerIdx = null; // draw
            }
        }

        return made;
    }

    @Override
    public boolean isOver() {
        // either we explicitly flagged over or board is full
        return over || board.isFull();
    }

    @Override
    public String statusText() {

        // If the game is over, show final score first, then winner/draw
        if (isOver()) {

            // Format final score string
            String finalScore = String.format("");

            if (winnerIdx == null) {
                // Draw
                return finalScore + "Draw";
            } else {
                // Winner
                return finalScore + "Winner: " + players[winnerIdx].name();
            }
        }

        //show the live score and whose turn it is
        return String.format("P1: %d  P2: %d  • Turn: %s",
                score[0], score[1], currentPlayer().name());
    }

    public int score(int i) {
        return score[i];
    }

    @Override
    public String modeName() {
        return "General";
    }
}
