package edu.neu.mad_sea.tictactoejava;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.neu.mad_sea.tictactoejava.bean.Game;
import edu.neu.mad_sea.tictactoejava.bean.Player;
import edu.neu.mad_sea.tictactoejava.model.TGameModel;
import edu.neu.mad_sea.tictactoejava.util.Constants;
import edu.neu.mad_sea.tictactoejava.util.FinalState;
import edu.neu.mad_sea.tictactoejava.util.GameStatusEnum;

/**
 * This Android in Java application is meant to represent
 * the game tic-tac-toe. Game play details can be found at
 * https://en.wikipedia.org/wiki/Tic-tac-toe
 */

public class MainActivityController extends FragmentActivity implements  StatusFragment.OnResetListener, StatusFragment.OnLogOutListener, BoardFragment.GameFinishedListener {//,View.OnClickListener{

    /**
     * UI Components:
     *    - playerOneScore: number of games won by player one
     *    - playerTwoScore: number of games won by player two
     *    - playerStatus: displays the current overall game leader
     *    - buttons: nine game piece buttons
     *    - resetGame: button used to reset the entire game, player scores, and playerStatus
     */
    private static final String TAG = "MainActivityController";
    private TextView playerOneScore, playerTwoScore, playerStatus;
    private FirebaseAuth auth;

    public static  TGameModel getModel() {
        return model;
    }

    public  static void setModel(TGameModel model) {
        model = model;
    }

    public static Game getGame() {
        return MainActivityController.game;
    }

    public static void setGame(Game game) {
        MainActivityController.game = game;
    }

    private static TGameModel model;

    private Button[] buttons = new Button[Constants.NUMBER_OF_CELLS];
    private Button resetGame;

    private static Game game;

    private BoardFragment boardFragment;
    private StatusFragment statusFragment;
    public String opponent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: in create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        auth = FirebaseAuth.getInstance();

        checkUserStatus();
        initializeGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeGame();
    }

    private void initializeGame() {

        FirebaseUser user = auth.getCurrentUser();
        opponent = null;
        if (getIntent().getExtras() != null) {
            opponent = getIntent().getExtras().getString("opponent");
        }

        if ( user == null) {
            checkUserStatus();
        } else if (opponent == null) {
            gotoChooseOpponent();
        } else {
            Log.d(TAG, "Initialize the game with "+opponent);
            MainActivityController.game = new Game(GameStatusEnum.START, 0, FinalState.NONE, "", "", new Player(user.getEmail(), 0), new Player(opponent, 0), true);
            model = new TGameModel(MainActivityController.game);

            StatusFragment statusFragment = (StatusFragment)getSupportFragmentManager().findFragmentById(R.id.status_fragment);
            statusFragment.reloadUIwithOpponent(opponent);
        }
     
    }

    private void gotoChooseOpponent() {
        Intent listUserIntent = new Intent(this, ListUsersActivity.class);
        startActivity(listUserIntent);
    }

    private void checkUserStatus() {
        Log.d(TAG, "onCreate: Check user exists: in MainActivityController");
        if (auth.getCurrentUser() == null) {
            Log.d(TAG, "onCreate: Going to Profile Activity: in MainActivityController");
            Intent signInIntent = new Intent(this, ProfileActivity.class);
            startActivity(signInIntent);
        }
    }

    private void initializeUI() {
        FragmentManager manager = getSupportFragmentManager();
        boardFragment = (BoardFragment) manager.findFragmentById(R.id.board_fragment);
        statusFragment = (StatusFragment) manager.findFragmentById(R.id.status_fragment);
    }

    @Override
    public void onReset() {
        boardFragment.resetGame();
    }

    @Override
    public void onGameOver() {
        statusFragment.updateText(Constants.GAME_OVER);
    }

    @Override
    public void onLogOut() {
        Intent signInIntent = new Intent(this, ProfileActivity.class);
        startActivity(signInIntent);
    }

}