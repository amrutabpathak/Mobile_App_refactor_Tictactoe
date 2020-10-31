package edu.neu.mad_sea.tictactoejava.util;

public class Constants {
    public static final int[][] WINNING_POSITIONS = {
            {0,1,2}, {3,4,5}, {6,7,8}, // rows
            {0,3,6}, {1,4,7}, {2,5,8}, // columns
            {0,4,8}, {2,4,6}           // diagonal
    };

    public static final int PLAYER_ONE_INDICATOR = 0;
    public static final int PLAYER_TWO_INDICATOR = 1;
    public static final int BLANK_CELL_INDICATOR = 2;


    public static final String PLAYER_ONE_ID = "PLAYER1";
    public static final String PLAYER_TWO_ID = "PLAYER2";

    public static final String PLAYER_ONE_MESSAGE = "Player 1 Wins!";
    public static final String PLAYER_TWO_MESSAGE= "Player 2 Wins!";
    public static final String DRAW_MESSAGE= "Its a draw!";
    public static final String NEW_GAME_MESSAGE= "Lets start the game";


    public static final int NUMBER_OF_CELLS= 9;

    public static final String FINISHED = "Finished";

    public static final String PLAYER_ONE_IS_WINNING = "Player 1 is winning";
    public static final String PLAYER_TWO_IS_WINNING = "Player 2 is winning";

    public static final String X = "X";
    public static final String O = "O";

    public static final String RESET_BOARD= "R";
    public static final String GAME_OVER = "O";


}
