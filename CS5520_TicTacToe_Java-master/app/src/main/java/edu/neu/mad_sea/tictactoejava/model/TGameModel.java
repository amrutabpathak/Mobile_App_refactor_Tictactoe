package edu.neu.mad_sea.tictactoejava.model;

import android.util.Log;

import java.util.Arrays;

import edu.neu.mad_sea.tictactoejava.MainActivityController;
import edu.neu.mad_sea.tictactoejava.bean.Game;
import edu.neu.mad_sea.tictactoejava.bean.Player;
import edu.neu.mad_sea.tictactoejava.util.Constants;
import edu.neu.mad_sea.tictactoejava.util.FinalState;
import edu.neu.mad_sea.tictactoejava.util.GameStatusEnum;

public class TGameModel {

    private static final String TAG = "TGameModel";

    /**
     * Game State: the state will be controlled by an array of integers
     *    - 0: player one has selected this game piece (or button)
     *    - 1: player two has selected this game piece (or button)
     *    - 2: neither player has chosen this game piece and is available for use
     */
    private boolean activePlayer;
    private Game game;
    private int[] gameState;
    private int roundCount;
    private String statusMessage;


    public TGameModel(){
        // Counts
        roundCount = 0; // will never be greater than nine. Nine is the most number of turns in a game.
        // Turns
        activePlayer = true;
        initialiseGameState();
    }

    public TGameModel(Game game){
        this.game = game;
        // Counts
        roundCount = 0; // will never be greater than nine. Nine is the most number of turns in a game.
        // Turns
        activePlayer = true;
        initialiseGameState();
    }

    private void initialiseGameState(){
        gameState = new int[Constants.NUMBER_OF_CELLS];
        for(int i = 0; i < gameState.length; i++){
            gameState[i] = Constants.BLANK_CELL_INDICATOR;
        }
    }

    public boolean isAlreadySelected(String s){
        Log.d(TAG, "isAlreadySelected: "+s);
        if(s != null && !s.trim().equals("")){
            Log.d(TAG, "isAlreadySelected: "+false);
            return true;
        }
        Log.d(TAG, "isAlreadySelected: "+true);
        return false;
    }

    public void setGameStates(){


        String cellId = game.getCellId();
        if(cellId == null){
            return;
        }
        int gameStatePointer = Integer.parseInt(cellId.substring(cellId.length()-1, cellId.length()));
        int playerIndicator = 2;
        // Player markings depending on turn.
        if(game.isFirstPlayer()) {
            playerIndicator = Constants.PLAYER_ONE_INDICATOR;
            gameState[gameStatePointer] = Constants.PLAYER_ONE_INDICATOR;
        }
        else{
            playerIndicator= Constants.PLAYER_TWO_INDICATOR;
            gameState[gameStatePointer] = Constants.PLAYER_TWO_INDICATOR;
        }

        MainActivityController.getMyRef().child("playing").child(MainActivityController.getPlayerSession()).child("game").child("block:"+gameStatePointer).setValue(MainActivityController.getUserName());
        MainActivityController.getMyRef().child("playing").child(MainActivityController.getPlayerSession()).child("turn").setValue(MainActivityController.getOtherPlayer());

        Log.d(TAG, "setGameStates: "+ Arrays.toString(gameState));
        Log.d(TAG, "setGameStates: count "+game.getCount());

    }

    public boolean checkWinner() {
        boolean winnerResult = false;

        for(int[] winningPosition: Constants.WINNING_POSITIONS) {
            if(gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]] &&
                    gameState[winningPosition[0]] != 2) {
                winnerResult = true;
            }
        }
        Log.d(TAG, "checkWinner: "+winnerResult);
        return winnerResult;
    }

    public Game statusCheck(Game game) {
        this.game = game;
        Log.d(TAG, "statusCheck: ");
        setGameStates();
        String message = "";
        if(checkWinner()) {
            game.setStatus(GameStatusEnum.FINISHED);
            if(game.isFirstPlayer()){
                game.getPlayerOne().setScore(game.getPlayerOne().getScore()+1);
                game.setFinalState(FinalState.ONE_WINS);
            }else{
                game.getPlayerTwo().setScore(game.getPlayerTwo().getScore()+1);
                game.setFinalState(FinalState.TWO_WINS);
            }
        } else if (game.getCount() == Constants.NUMBER_OF_CELLS-1) {
            // If nine rounds have been reached, no winner is declared.
            game.setFinalState(FinalState.DRAW);
            game.setStatus(GameStatusEnum.FINISHED);
            //resetBoard();
        } else{
            // If there is no winner, switch  player.
            game.setFirstPlayer(!game.isFirstPlayer());
            game.setCount(game.getCount()+1);
        }
        statusMessage = "";
        if(game.getPlayerOne().getScore() != game.getPlayerTwo().getScore()){
            if(game.getPlayerOne().getScore() > game.getPlayerTwo().getScore()){
                statusMessage = Constants.PLAYER_ONE_IS_WINNING;
            }else{
                statusMessage = Constants.PLAYER_TWO_IS_WINNING;
            }

        }
        game.setPlayerStatus(statusMessage);
        return game;
    }

    public Game resetBoard(){
        Game game = new Game();
        game.setCount(0);
        initialiseGameState();
        game.setFinalState(FinalState.NONE);
        game.setStatus(GameStatusEnum.START);
        game.setFirstPlayer(true);
        game.setCellId("");
        game.setPlayerOne(new Player(Constants.PLAYER_ONE_ID,0));
        game.setPlayerTwo(new Player(Constants.PLAYER_TWO_ID,0));
        game.setPlayerStatus(Constants.NEW_GAME_MESSAGE);
        return game;
    }

    public Game clearBoard(Game game){
        game.setCount(0);
        initialiseGameState();
        game.setFinalState(FinalState.NONE);
        game.setStatus(GameStatusEnum.START);
        game.setFirstPlayer(true);
        game.setCellId("");
        //game.setPlayerOne(new Player(Constants.PLAYER_ONE_ID,0));
        //game.setPlayerTwo(new Player(Constants.PLAYER_TWO_ID,0));
        //game.setPlayerStatus("");
        return game;
    }

}
