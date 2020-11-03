package edu.neu.mad_sea.tictactoejava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.neu.mad_sea.tictactoejava.bean.Game;
import edu.neu.mad_sea.tictactoejava.bean.Player;
import edu.neu.mad_sea.tictactoejava.model.TGameModel;
import edu.neu.mad_sea.tictactoejava.util.Constants;

public class StatusFragment extends Fragment {

    private static final String TAG = "StatusFragment";
    private TextView playerOneScore, playerTwoScore, playerStatus;
    private Button resetGame, logOut;
    private OnResetListener resetListener;
    private OnLogOutListener logOutListener;
    private FirebaseAuth auth;
    private String opponent;



    public interface OnResetListener {
        public void onReset();
    }

    public interface OnLogOutListener {
        public void onLogOut();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.status_fragment, container, false);

        initializeUI(view);
        initializeListeners();
        setUIValues(view);

        return view;
    }

    public void reloadUIwithOpponent(String user) {
        this.opponent = user;
        setUIValues(getView());
    }

    private void setUIValues(View view) {
        TextView playerOne = (TextView) view.findViewById(R.id.playerOne);
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Setting user one");

            String un = curateEmail(user.getEmail());
            playerOne.setText(un);
        }

        TextView playerTwo = (TextView) view.findViewById(R.id.playerTwo);
        if (opponent != null) {
            Log.d(TAG, "Setting user two");

            String on = curateEmail(opponent);
            playerTwo.setText(on);
        }

    }

    private String curateEmail(String email) {
        return email.split("@")[0];
    }

    private void initializeListeners() {
        // Reset game
        resetGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: LogOut:StatusFragment");

                auth.signOut();
                logOutListener.onLogOut();
            }
        });
    }

    private void initializeUI(View view) {

        // set and find IDs for TextViews and reset button
        playerOneScore = (TextView) view.findViewById(R.id.playerOneScore);
        playerTwoScore = (TextView) view.findViewById(R.id.playerTwoScore);
        playerStatus = (TextView) view.findViewById(R.id.playerStatus);
        // Btns
        resetGame = (Button) view.findViewById(R.id.resetGameId);
        logOut = (Button) view.findViewById(R.id.logOut);
    }


    // Starts a new round with a clean game board.
    public void resetGame() {

        MainActivityController.setGame(MainActivityController.getModel().resetBoard());
        playerStatus.setText(Constants.NEW_GAME_MESSAGE);
        updateText(Constants.RESET_BOARD);
    }

    public void updateText(String status){

        String playerOneScoreStr="0",playerTwoScoreStr="0";
        if(status.equals(Constants.RESET_BOARD)){
            playerOneScoreStr="0";playerTwoScoreStr="0";
        }else if(status.equals(Constants.GAME_OVER)){
            playerOneScoreStr = MainActivityController.getGame().getPlayerOne().getScore()+"";
            playerTwoScoreStr = MainActivityController.getGame().getPlayerTwo().getScore()+"";
        }
        if(MainActivityController.getGame().getPlayerOne().getScore() > MainActivityController.getGame().getPlayerTwo().getScore()){
            playerStatus.setText(R.string.playerOneIsWinningMessage);
        }else if(MainActivityController.getGame().getPlayerOne().getScore() < MainActivityController.getGame().getPlayerTwo().getScore()){
            playerStatus.setText(R.string.playerTwoIsWinningMessage);

        }else{
            playerStatus.setText("");
        }

        Log.d(TAG, "updateText: "+MainActivityController.getGame().getPlayerOne().getScore());
        playerOneScore.setText(playerOneScoreStr);
        playerTwoScore.setText(playerTwoScoreStr);
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        try {
            resetListener = (OnResetListener) context;
            logOutListener = (OnLogOutListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnClickCellListener");
        }
    }




}
