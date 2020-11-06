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

import androidx.fragment.app.Fragment;

import edu.neu.mad_sea.tictactoejava.bean.Game;
import edu.neu.mad_sea.tictactoejava.model.TGameModel;
import edu.neu.mad_sea.tictactoejava.util.Constants;
import edu.neu.mad_sea.tictactoejava.util.FinalState;
import edu.neu.mad_sea.tictactoejava.util.GameStatusEnum;

public class BoardFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "BoardFragment";

    private ImageButton[] buttons = new ImageButton[Constants.NUMBER_OF_CELLS];
    private Button resetGame;
    private GameFinishedListener gameFinishedListenerlistener;

    View view;

    /*public interface OnClickCellListener {
        public void onCellClick(String cellId);
    }*/

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
        setButtonFeatures( v);
        Log.d(TAG, "onClick: "+MainActivityController.getGame());
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


   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnClickCellListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnClickCellListener");
        }
    }*/

    private void setButtonFeatures( View v) {
        // Player markings depending on turn.
        if (MainActivityController.getGame().isFirstPlayer()) {
            ((ImageButton) v).setImageResource(R.drawable.xxxx);
            //((Button)v).setTextColor(getActivity().getApplication().getResources().getColor(R.color.playerOneColor));
        } else {
            ((ImageButton) v).setImageResource(R.drawable.oooo);
            //((Button)v).setTextColor(getActivity().getApplication().getResources().getColor(R.color.playerTwoColor));        }
        }
    }

    public void resetGame() {

        for (int i = 0; i < buttons.length; i++) {
            int resourceID = getResources().getIdentifier("btn_"+i, "id", getActivity().getPackageName());
            buttons[i] = (ImageButton) view.findViewById(resourceID);
            buttons[i].setEnabled(true);
            buttons[i].setImageResource(android.R.color.transparent);
            buttons[i].setBackground(null);

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
