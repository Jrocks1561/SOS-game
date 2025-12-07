package sos.Game;

import java.awt.Color;

import sos.Game.gamerecorderstuff.DatabaseGameRecorder;
import sos.Game.gamerecorderstuff.GameRecorder;

public class GameManager {
    private final Game game;

    // db record stuff
    private GameRecorder recorder;

    // track moves num 
    private int moveNumber = 0;

    // board size 
    private final int size;

    // who is computer + difficulty
    private final boolean p1IsComputer;
    private final boolean p2IsComputer;
    private final difficulty selectedDifficulty;
    private final String mode;

    // computer logic 
    private final ComputerPlayer computerPlayer;

    // how many SOS lines were made by the last move
    private int lastMade = 0;
    // In GameManager.java, inside the class, near other methods:

public void applyReplayMove(int row, int col, char letterChar) {
    if (game.isOver()) return;

    Cell letter = (letterChar == 'S') ? Cell.S : Cell.O;

    // Apply the move directly to the underlying game.
    // This does NOT increment moveNumber or call the recorder.
    try {
        lastMade = game.move(letter, row, col);
    } catch (IllegalStateException ex) {
        // ignore invalid moves during replay
    }
}


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
            case Medium -> this.computerPlayer = new MediumComputerPlayer();
            case Hard -> this.computerPlayer = new HardComputerPlayer();
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
        int currentPlayerIndex = game.currentIndex();
        Cell letter = (currentPlayerIndex == 0) ? Cell.S : Cell.O;

        lastMade = game.move(letter, r, c);

        // ---- DB recording logic ----
        moveNumber++;
        if (recorder != null) {
            char letterChar = (letter == Cell.S) ? 'S' : 'O';
            System.out.println("GM: recording move " + moveNumber +
                               " for player " + currentPlayerIndex +
                               " at (" + r + "," + c + "), letter=" + letterChar);
            recorder.recordMove(moveNumber, r, c, letterChar, currentPlayerIndex);

            if (game.isOver()) {
                System.out.println("GM: game is over, calling recorder.endGame");
                recorder.endGame(game.statusText());
            }
        }
        // ----------------------------

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

    // let the UI access the board 
    public Board getBoard() {
        return game.board();
    }

    // let the UI access colored SOS lines
    public java.util.List<Game.ScoredLine> getScoredLines() {
        return game.getScoredLines();
    }

    public void startRecorder(GameRecorder recorder) {
    this.recorder = recorder;
    moveNumber = 0;

    if (recorder != null) {
        // selectedDifficulty is your enum: difficulty.Easy / Medium / Hard
        String diffName = (selectedDifficulty != null)
                ? selectedDifficulty.name()
                : "Easy";  // fallback if ever null

        // pass all the info needed so we can resume correctly later
        recorder.startGame(
                size,          // board size field in this class
                mode,          // "Simple" or "General"
                p1IsComputer,  // whether P1 is a computer
                p2IsComputer,  // whether P2 is a computer
                diffName       // difficulty as a String
        );
    }
}


    // getters for algorithms / UI
    public boolean isP1Computer() { return p1IsComputer; }
    public boolean isP2Computer() { return p2IsComputer; }
    public difficulty getDifficulty() { return selectedDifficulty; }

    public int getSize() { return size; }
    public String getMode() { return mode; }
}
