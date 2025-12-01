package sos.Game.gamerecorderstuff;

import java.util.List;

public interface GameRecorder {

    //called to record a move
    void recordMove(int moveNumber, int row, int col, char letter, int playerIndex);

    //called at the start of a new game
    void startGame(int boardSize, String mode);

    //loads moves for a given game from the database
    List<RecordedMove> loadGame(int gameId);

    //called at the end of the game to record the result
    void endGame(String result);
    
    //returns the id of the current game being recorded
    int getCurrentGameId();

}
