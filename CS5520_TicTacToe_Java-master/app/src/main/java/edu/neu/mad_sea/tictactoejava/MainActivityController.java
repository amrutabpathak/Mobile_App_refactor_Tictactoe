package edu.neu.mad_sea.tictactoejava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

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

public class MainActivityController extends FragmentActivity implements  StatusFragment.OnResetListener, BoardFragment.GameFinishedListener{//,View.OnClickListener{

    /**
     * UI Components:
     *    - playerOneScore: number of games won by player one
     *    - playerTwoScore: number of games won by player two
     *    - playerStatus: displays the current overall game leader
     *    - buttons: nine game piece buttons
     *    - resetGame: button used to reset the entire game, player scores, and playerStatus
     */

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static FirebaseDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(FirebaseDatabase database) {
        MainActivityController.database = database;
    }

    public static DatabaseReference getMyRef() {
        return myRef;
    }

    public static void setMyRef(DatabaseReference myRef) {
        MainActivityController.myRef = myRef;
    }

    private static DatabaseReference myRef = database.getReference();

    private static String playerSession = "";
    private static String userName = "";

    public static String getPlayerSession() {
        return playerSession;
    }

    public static void setPlayerSession(String playerSession) {
        MainActivityController.playerSession = playerSession;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        MainActivityController.userName = userName;
    }

    public static String getOtherPlayer() {
        return otherPlayer;
    }

    public static void setOtherPlayer(String otherPlayer) {
        MainActivityController.otherPlayer = otherPlayer;
    }

    public static String getLoginUID() {
        return loginUID;
    }

    public static void setLoginUID(String loginUID) {
        MainActivityController.loginUID = loginUID;
    }

    public static String getRequestType() {
        return requestType;
    }

    public static void setRequestType(String requestType) {
        MainActivityController.requestType = requestType;
    }

    private static String otherPlayer = "";
    private static String loginUID = "";
    private static String requestType = "";

    private static final String TAG = "MainActivityController";
    private TextView playerOneScore, playerTwoScore, playerStatus;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: in create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = getIntent().getExtras().get("user_name").toString();
        loginUID = getIntent().getExtras().get("login_uid").toString();
        otherPlayer = getIntent().getExtras().get("other_player").toString();
        requestType = getIntent().getExtras().get("request_type").toString();
        playerSession = getIntent().getExtras().get("player_session").toString();


        FragmentManager manager = getSupportFragmentManager();
         boardFragment = (BoardFragment) manager.findFragmentById(R.id.board_fragment);

         statusFragment = (StatusFragment) manager.findFragmentById(R.id.status_fragment);


        // set and find IDs for TextViews and reset button
        /*playerOneScore = (TextView) findViewById(R.id.playerOneScore);
        playerTwoScore = (TextView) findViewById(R.id.playerTwoScore);
        playerStatus = (TextView) findViewById(R.id.playerStatus);
        //resetGame = (Button) findViewById(R.id.resetGameId);
        Log.d(TAG, "onCreate: Buttons created");*/
        MainActivityController.game = new Game(GameStatusEnum.START,0, FinalState.NONE,"", "", new Player(Constants.PLAYER_ONE_ID,0), new Player(Constants.PLAYER_TWO_ID,0), true);
        model = new TGameModel(MainActivityController.game);

        myRef.child(String.valueOf(R.string.Playing)).child(playerSession).child("turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    String value = (String) dataSnapshot.getValue();
                    if(value.equals(userName)) {
                        game.setFirstPlayer(true);


                    }else if(value.equals(otherPlayer)){
                        game.setFirstPlayer(false);
                    }

                    boardFragment.dataChangedForTurn();
                    statusFragment.dataChangedForTurn();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.child("playing").child(playerSession).child("game").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{


                           /* Player1.clear();
                            Player2.clear();
                            activePlayer = 2;*/
                            Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            if(map != null){
                                String value = "";
                                String firstPlayer = userName;
                                for(String key:map.keySet()){
                                    value = (String) map.get(key);
                                    if(value.equals(userName)){
                                        game.setFirstPlayer(true);
                                    }else{
                                        game.setFirstPlayer(false);
                                    }
                                    firstPlayer = value;
                                    boardFragment.dataChangedForGame();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }

      /*  boardFragment.setGame(game, model);
        statusFragment.setGame(game,model);*/

        // sets and finds all of the IDs for the game piece buttons.
       /* for (int i=0; i < buttons.length; i++) {
            String buttonID = "btn_" + i;
            Log.d(TAG, "onCreate: button"+buttonID);
            int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = (Button) findViewById(resourceID);
            buttons[i].setOnClickListener((View.OnClickListener) this);
        } */




    //@Override
    /*public void onClick1(View v) {
        // Checks to see if a button has been previously been pressed.
        // If button has been pressed, players cannot reselect a button, but if not, the button
        // is still selectable in game play.
        Log.d(TAG, "onClick: in here"+((Button) v).getText().toString());
        if(model.isAlreadySelected(((Button) v).getText().toString())){
            return;
        }

        // Below finds the button ID that was clicked and updates the game state.
        String buttonID = v.getResources().getResourceEntryName((v.getId()));
        game.setCellId(buttonID);
        Log.d(TAG, "onClick: buttonID is "+buttonID);
        int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
        Button btn = (Button) findViewById(resourceID);
         btn.setEnabled(false);
        Log.d(TAG, "onClick: clicked"+buttonID);

        game.setStatus(GameStatusEnum.IN_PROGRESS);
        setButtonFeatures(game, v);
        game = model.statusCheck(game);
        if(game.getStatus().equals(GameStatusEnum.FINISHED)){
            String message = "";
            if(game.getFinalState().equals(FinalState.ONE_WINS)){
               message =  getString(R.string.playerOneMessage);
            }else if(game.getFinalState().equals(FinalState.ONE_WINS)){
                message =  getString(R.string.playerTwoMessage);
            }else if(game.getFinalState().equals(FinalState.DRAW)){
                message =  getString(R.string.drawMessage);
            }
            Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
            playAgain();
        }
        //updateText();
        // Reset game
        resetGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();

            }
        });
    }*/

    /*private void updateText(){
        playerStatus.setText(game.getPlayerStatus());
        Log.d(TAG, "updateText: "+game.getPlayerOne().getScore());
        playerOneScore.setText(Integer.toString(game.getPlayerOne().getScore()));
        playerTwoScore.setText(Integer.toString(game.getPlayerTwo().getScore()));
    }*/

/*
    private void setButtonFeatures(Game  game, View v){
        // Player markings depending on turn.
        if(game.isFirstPlayer()) {
            ((Button)v).setText(R.string.X);
            ((Button)v).setTextColor(getApplication().getResources().getColor(R.color.playerOneColor));
        }

        else{
            ((Button)v).setText(R.string.O);
            ((Button)v).setTextColor(getApplication().getResources().getColor(R.color.playerTwoColor));        }
    }*/




  /*  // Starts a new round with a clean game board.
    public void playAgain() {
        game = model.clearBoard(game);

        for (int i = 0; i < buttons.length; i++) {
            int resourceID = getResources().getIdentifier("btn_"+i, "id", getPackageName());
            buttons[i] = (Button) findViewById(resourceID);
            buttons[i].setEnabled(true);
            buttons[i].setText("");

        }
        updateText();
    }*/

    // Starts a new round with a clean game board.
    /*public void resetGame() {
        game = model.resetBoard();

        for (int i = 0; i < buttons.length; i++) {
            int resourceID = getResources().getIdentifier("btn_"+i, "id", getPackageName());
            buttons[i] = (Button) findViewById(resourceID);
            buttons[i].setEnabled(true);
            buttons[i].setText("");

        }
        playerStatus.setText(Constants.NEW_GAME_MESSAGE);
        updateText();
    }*/


    @Override
    public void onReset() {
        boardFragment.resetGame();
    }

    @Override
    public void onGameOver() {
        statusFragment.updateText(Constants.GAME_OVER);
    }
}