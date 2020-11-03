package edu.neu.mad_sea.tictactoejava;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.mad_sea.tictactoejava.bean.Game;
import edu.neu.mad_sea.tictactoejava.model.TGameModel;
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
    public void onClick(View v) {
        Log.d(TAG, "onClick: here!!!");

        String buttonID = v.getResources().getResourceEntryName((v.getId()));
        MainActivityController.getGame().setCellId(buttonID);
        Log.d(TAG, "onClick: buttonID is "+buttonID);
        int resourceID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
        ImageButton btn = (ImageButton) v.findViewById(resourceID);
        btn.setEnabled(false);

        Log.d(TAG, "onClick: clicked"+buttonID);
        MainActivityController.getGame().setStatus(GameStatusEnum.IN_PROGRESS);
        setButtonFeatures(v);

        setUpListenerForDb();
        setGameStatusInDB(buttonID);

        checkGameStatus();
        freeze();
    }

    private void freeze() {
        for (int i=0; i < buttons.length; i++) {
            String buttonID = "btn_" + i;
            Log.d(TAG, "onCreate: button"+buttonID);
            int resourceID = getResources().getIdentifier(buttonID, "id",getActivity().getPackageName());
            buttons[i] = (ImageButton) view.findViewById(resourceID);
            buttons[i].setEnabled(false);
        }
    }

    private void setUpListenerForDb() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("games");

        dbRef.child(GAMEID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String opponent = ((MainActivityController) getActivity()).opponent.getId();
                String buttonCLicked = (String) dataSnapshot.child(GAMEID).child(opponent).getValue();
                setButtonFeatures(getView());
                unfreeze();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String opponent = ((MainActivityController) getActivity()).opponent.getId();
                String buttonCLicked = (String) dataSnapshot.child(GAMEID).child(opponent).getValue();
                setButtonFeatures(getView());
                unfreeze();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("games");
        dbRef.child(GAMEID).child(user).setValue(buttonId);
    }

    private void checkGameStatus() {
        MainActivityController.setGame(MainActivityController.getModel().statusCheck(MainActivityController.getGame()));

        if(MainActivityController.getGame().getStatus().equals(GameStatusEnum.FINISHED)){
            String message = "";
            if(MainActivityController.getGame().getFinalState().equals(FinalState.ONE_WINS)){
                message =  getString(R.string.playerOneMessage);
            }else if(MainActivityController.getGame().getFinalState().equals(FinalState.ONE_WINS)){
                message =  getString(R.string.playerTwoMessage);
            }else if(MainActivityController.getGame().getFinalState().equals(FinalState.DRAW)){
                message =  getString(R.string.drawMessage);
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

    private void setButtonFeatures( View v) {

        // Player markings depending on turn.
        if (MainActivityController.getGame().isFirstPlayer()) {
            ((ImageButton) v).setImageResource(R.drawable.xxxx);
        } else {
            ((ImageButton) v).setImageResource(R.drawable.oooo);
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

}
