package edu.neu.mad_sea.tictactoejava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.neu.mad_sea.tictactoejava.bean.Game;
import edu.neu.mad_sea.tictactoejava.util.Constants;
import edu.neu.mad_sea.tictactoejava.util.FinalState;
import edu.neu.mad_sea.tictactoejava.util.GameStatusEnum;

public class BoardFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "BoardFragment";

    public static String GAMEID = null;
    private ImageButton[] buttons = new ImageButton[Constants.NUMBER_OF_CELLS];
    private Button resetGame;
    private GameFinishedListener gameFinishedListenerlistener;
    View view;

    public interface GameFinishedListener {
        public void onGameOver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.board_fragment, container, false);

        // sets and finds all of the IDs for the game piece buttons.
        for (int i=0; i < buttons.length; i++) {
            String buttonID = "btn_" + i;
            Log.d(TAG, "onCreate: button"+buttonID);
            int resourceID = getResources().getIdentifier(buttonID, "id",getActivity().getPackageName());
            buttons[i] = (ImageButton) view.findViewById(resourceID);
            buttons[i].setOnClickListener((View.OnClickListener) this);
        }
        // Inflate the layout for this fragment
        return view;
    }
    @Override
    public void onResume() {

        super.onResume();
        if(GAMEID != null){
            setUpListenerForSelf();
        }
    }



    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: here!!!");

        String buttonID = v.getResources().getResourceEntryName((v.getId()));
        MainActivityController.getGame().setCellId(buttonID);
        Log.d(TAG, "onClick: buttonID is "+buttonID);
        int resourceID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
        ImageButton btn = (ImageButton) v.findViewById(resourceID);
        btn.setEnabled(false);


        MainActivityController.getGame().setStatus(GameStatusEnum.IN_PROGRESS);
        setButtonFeatures(btn, true);
        setGameStatusInDB(buttonID);
        checkGameStatus(true);
        freeze();
    }

    private void freeze() {
        for (int i=0; i < buttons.length; i++) {
            String buttonID = "btn_" + i;
            Log.d(TAG, "onFreeze: button"+buttonID);
            int resourceID = getResources().getIdentifier(buttonID, "id",getActivity().getPackageName());
            buttons[i] = (ImageButton) view.findViewById(resourceID);
            buttons[i].setEnabled(false);
        }
    }

    public void setUpListenerForSelf() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("games");
    final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        dbRef.child(GAMEID).child(hash(user)).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (user != null && !user.isEmpty()) {
                    unfreeze();
                    Log.w(TAG, user);
                    Log.d(TAG, "hash user: "+hash(user));
                    String buttonCLicked = (String) dataSnapshot.getValue();
                    if (!buttonCLicked.isEmpty()) {
                        Log.w(TAG, "Opp->"+buttonCLicked);
                        Log.w(TAG, "Opp->"+getActivity().getPackageName());
                        int resourceID = getResources().getIdentifier(buttonCLicked, "id", getActivity().getPackageName());
                        ImageButton btn = (ImageButton) getView().findViewById(resourceID);
                        btn.setEnabled(false);
                        setButtonFeatures(btn, false);
                    }
                } else {
                    goProfileActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goProfileActivity() {
        Intent profileActivity = new Intent(getActivity(), ProfileActivity.class);
        startActivity(profileActivity);
    }

    private void unfreeze() {
        for (int i=0; i < buttons.length; i++) {
            String buttonID = "btn_" + i;
            Log.d(TAG, "onCreate: button"+buttonID);
            int resourceID = getResources().getIdentifier(buttonID, "id",getActivity().getPackageName());
            buttons[i] = (ImageButton) view.findViewById(resourceID);
            buttons[i].setEnabled(true);
        }
    }

    private void setGameStatusInDB(String buttonId) {
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.w(TAG, "setGameStatusInDB User->"+user);
        Log.w(TAG, String.valueOf(hash(user)));

        final String opponent = ((MainActivityController) getActivity()).opponent;
        Log.w(TAG, "setGameStatusInDB opponent->"+opponent);
        Log.w(TAG, String.valueOf(hash(opponent)));

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("games");
        dbRef.child(GAMEID).child(hash(opponent)).setValue(buttonId);
    }

    private void checkGameStatus(boolean turn) {
        MainActivityController.setGame(MainActivityController.getModel().statusCheck(MainActivityController.getGame()));

        if(MainActivityController.getGame().getStatus().equals(GameStatusEnum.FINISHED)){
            String message = "";
            if(MainActivityController.getGame().getFinalState().equals(FinalState.ONE_WINS)){
                message =  getString(R.string.playerOneMessage);
            }else if(MainActivityController.getGame().getFinalState().equals(FinalState.TWO_WINS)){
                message =  getString(R.string.playerTwoMessage);
            }else if(MainActivityController.getGame().getFinalState().equals(FinalState.DRAW)){
                message =  getString(R.string.drawMessage);
            }

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("games");
            if (turn) {
                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                dbRef.child(GAMEID).child("result").setValue(user);
            } else {
                String opp = ((MainActivityController)getActivity()).opponent;
                dbRef.child(GAMEID).child("result").setValue(opp);
            }

            Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
            playAgain();

            gameFinishedListenerlistener.onGameOver();
        }
    }

    public void playAgain() {
        MainActivityController.setGame(MainActivityController.getModel().clearBoard(MainActivityController.getGame()));

        for (int i = 0; i < buttons.length; i++) {
            int resourceID = getResources().getIdentifier("btn_"+i, "id", getActivity().getPackageName());
            buttons[i] = (ImageButton) view.findViewById(resourceID);
            buttons[i].setEnabled(true);
            buttons[i].setImageResource(android.R.color.transparent);

        }
    }

    private void setButtonFeatures(ImageButton iB, boolean turn) {
         MainActivityController.getGame().setFirstPlayer(turn);
        // Player markings depending on turn.
        if (turn) {
            iB.setImageResource(R.drawable.xxxx);
        } else {
            iB.setImageResource(R.drawable.oooo);
        }
    }

    public void resetGame() {

        for (int i = 0; i < buttons.length; i++) {
            int resourceID = getResources().getIdentifier("btn_"+i, "id", getActivity().getPackageName());
            buttons[i] = (ImageButton) view.findViewById(resourceID);
            buttons[i].setEnabled(true);
            buttons[i].setImageResource(android.R.color.transparent);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            gameFinishedListenerlistener = (GameFinishedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement GameFinishedListener");
        }
    }

    private  String hash(String stringToHash) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(stringToHash.getBytes());
        return (new String(messageDigest.digest())).replaceAll("[^a-zA-Z0-9]", "");

    }

}
