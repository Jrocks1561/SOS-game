package sos.game;

import java.awt.Color;

public class GameManager {
    private final Game game;

    // board size 
    private final int size;

    // who is computer + difficulty
    private final boolean p1IsComputer;
    private final boolean p2IsComputer;
    private final difficulty selectedDifficulty;
    private final String mode;

    // computer logic 
    private final EasyComputerPlayer computerPlayer;

    // how many SOS lines were made by the last move
    private int lastMade = 0;

    // main constructor used by GameScreen
    public GameManager(int size,
                       String mode,
                       boolean p1IsComputer,
                       boolean p2IsComputer,
                       difficulty diff) {

        this.size = size;
        this.p1IsComputer = p1IsComputer;
        this.p2IsComputer = p2IsComputer;
        this.selectedDifficulty = diff;
        this.mode = mode;

        Player p1 = Player.of(p1IsComputer ? "Computer (P1)" : "Player 1", Color.BLUE);
        Player p2 = Player.of(p2IsComputer ? "Computer (P2)" : "Player 2", Color.YELLOW);

        if ("General".equalsIgnoreCase(mode)) {
            this.game = new GeneralGame(size, p1, p2);
        } else {
            this.game = new SimpleGame(size, p1, p2);
        }

        // pick a ComputerPlayer implementation based on difficulty
        switch (selectedDifficulty) {
            case Easy -> this.computerPlayer = new EasyComputerPlayer();
            case Medium -> this.computerPlayer = new EasyComputerPlayer();
            case Hard -> this.computerPlayer = new EasyComputerPlayer();
            default -> this.computerPlayer = new EasyComputerPlayer();
        }
    }

    // human vs human constructor
    public GameManager(int size, String mode) {
        this(size, mode, false, false, difficulty.Easy);
    }

    // the move logic 
    public boolean placeMove(int r, int c) {
        if (game.isOver()) return false;

        try {
            // index 0 = Player 1 (S), index 1 = Player 2 (O)
            Cell letter = (game.currentIndex() == 0) ? Cell.S : Cell.O;
            lastMade = game.move(letter, r, c);
            return true;
        } catch (IllegalStateException ex) {
            lastMade = 0;
            return false;
        }
    }

    // ask the computer player to choose a move for the current turn
    public int[] chooseComputerMove() {
        if (game.isOver()) return null;
        if (computerPlayer == null) return null;
        return computerPlayer.chooseMove(game.board(), size, game.currentIndex());
    }

    public String getCell(int r, int c) {
        Cell cell = game.board().get(r, c);
        if (cell == Cell.S) return "S";
        if (cell == Cell.O) return "O";
        return "";
    }

    public boolean isPlayerOneTurn() {
        return game.currentIndex() == 0;
    }

    public Game getGame() {
        return game;
    }

    // Score/Status helpers used by GameScreen 

    public boolean isOver() {
        return game.isOver();
    }

    public String status() {
        return game.statusText();
    }

    public int scoreP1() {
        if (game instanceof GeneralGame) {
            return ((GeneralGame) game).score(0);
        }
        return 0;
    }

    public int scoreP2() {
        if (game instanceof GeneralGame) {
            return ((GeneralGame) game).score(1);
        }
        return 0;
    }

    // how many lines made by last move
    public int lastMade() {
        return lastMade;
    }

    // let the UI access the board (for SOS line drawing, if needed)
    public Board getBoard() {
        return game.board();
    }

    // let the UI access colored SOS lines
    public java.util.List<Game.ScoredLine> getScoredLines() {
        return game.getScoredLines();
    }

    // getters for algorithms / UI
    public boolean isP1Computer() { return p1IsComputer; }
    public boolean isP2Computer() { return p2IsComputer; }
    public difficulty getDifficulty() { return selectedDifficulty; }

    public int getSize() { return size; }
    public String getMode() { return mode; }
}
