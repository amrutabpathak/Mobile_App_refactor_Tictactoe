package edu.neu.mad_sea.tictactoejava.bean;

import edu.neu.mad_sea.tictactoejava.util.FinalState;
import edu.neu.mad_sea.tictactoejava.util.GameStatusEnum;

public class Game {
    private GameStatusEnum status;
    private int count;

    public FinalState getFinalState() {
        return finalState;
    }

    public void setFinalState(FinalState finalState) {
        this.finalState = finalState;
    }

    private FinalState finalState;
    private String cellId;
    private String playerStatus;
    private boolean isFirstPlayer;

    public boolean isFirstPlayer() {
        return isFirstPlayer;
    }

    public void setFirstPlayer(boolean firstPlayer) {
        isFirstPlayer = firstPlayer;
    }

    public String getPlayerStatus() {
        return playerStatus;
    }

    public Game(GameStatusEnum status, int count, FinalState message, String cellId, String playerStatus, Player playerOne, Player playerTwo, boolean isFirstPlayer) {
        this.status = status;
        this.count = count;
        this.finalState = message;
        this.cellId = cellId;
        this.playerStatus = playerStatus;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.isFirstPlayer = isFirstPlayer;
    }

    public void setPlayerStatus(String playerStatus) {
        this.playerStatus = playerStatus;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    private Player playerOne;
    private Player playerTwo;



    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public Game(){

    }
    public Game(GameStatusEnum status, int count, FinalState message, int[] gameState) {
        this.status = status;
        this.count = count;
        this.finalState = message;
    }


    public GameStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GameStatusEnum status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
