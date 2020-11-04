package edu.neu.mad_sea.tictactoejava;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.neu.mad_sea.tictactoejava.bean.ACTIONS;
import edu.neu.mad_sea.tictactoejava.bean.Player;
import edu.neu.mad_sea.tictactoejava.bean.Request;

public class ListUsersActivity extends AppCompatActivity {

    private Set<Player> players;
    private static String TAG = "ListUsersActivity";
    private Map<Integer, Player> texttoPlayer;
    private Map<Integer, Player> scoreToPlayer;
    private Button logOutButton;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeDB();
        initializeStorage();
        receiveRequest();

        setContentView(R.layout.activity_list_users);
        initializeUI();

        loadTop5Users();
        setView();
    }

    private void initializeDB() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private void loadTop5Users() {

        // Get top 5 users by score
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("scores");
        Query top10query = ref.orderByChild("score").limitToLast(5);

        top10query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.w(TAG, dataSnapshot.getValue().toString());
                Long score = (Long) dataSnapshot.child("score").getValue();
                String id = (String) dataSnapshot.child("id").getValue();
                Player player = new Player(id, score.intValue());
                players.add(player);
                setView();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Long score = (Long) dataSnapshot.child("score").getValue();
                String id = (String) dataSnapshot.child("id").getValue();
                Player player = new Player(id, score.intValue());
                for (Player p1: players) {
                    if (p1.getId().equals(player.getId()))
                    {
                        players.remove(p1);
                        break;
                    }
                }
                players.add(player);
                setView();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Player player = ds.getValue(Player.class);
                    players.remove(player.getId());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setView() {
        Integer i = 1;

        for ( Player player: players) {
            if (i >= 5) {
                break;
            }
            initializeOpponentId(i, player);
            initializeOpponentScore(i, player);
            i++;
        }

        for (; i < 5; i++) {
            initializeEmptyOpponent(i);
        }
    }


    private void initializeOpponentId(Integer i, Player player) {
        TextView user;

        user = (TextView) findViewById(getUserId(i));
        user.setText(player.getId());

        texttoPlayer.put(getUserId(i), player);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClick(v);
            }
        });
    }

    private void initializeOpponentScore(Integer i, Player player) {
        TextView score;
        player.getScore();
        Log.d(TAG,"hiiii"+ i +"      "+new Integer(getScoreId(i)).toString());
        score = (TextView) findViewById(getScoreId(i));
        score.setText(String.valueOf(player.getScore()));

        scoreToPlayer.put(getScoreId(i), player);

        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScoreClick(v);
            }
        });
    }


    private void initializeEmptyOpponent(Integer i) {
        TextView user, score;

        Integer id = getUserId(i);

        user = (TextView) findViewById(getUserId(i));
        score = (TextView) findViewById(getScoreId(i));

        user.setText("No User");
        score.setText("0");

        user.setClickable(false);
        score.setClickable(false);
    }

    private void initializeStorage() {
        players = new HashSet<>();
        texttoPlayer = new HashMap<>();
        scoreToPlayer = new HashMap<>();
    }

    public void onUserClick(View v) {
        Player player = texttoPlayer.get(v.getId());

        if (player != null) {
            sendNotificationToUser(player.getId());
        }
    }

    public void onScoreClick(View v) {
        Player player = scoreToPlayer.get(v.getId());

        if (player != null) {
            sendNotificationToUser(player.getId());
        }
    }

    private void createGameInDB(String gameId, String user, String opponent) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("games");
        ref.child(gameId).setValue("");
        ref.child(gameId).child(hash(user)).setValue("");
        ref.child(gameId).child(hash(opponent)).setValue("");
        ref.child(gameId).child("result").setValue("");
    }

    private int getUserId(Integer i) {
        switch (i) {
            case 1:
                return R.id.user1;
            case 2:
                return R.id.user2;
            case 3:
                return R.id.user3;
            case 4:
                return R.id.user4;
            default:
                return 0;
        }
    }

    private int getScoreId(Integer i) {
        switch (i) {
            case 1:
                return R.id.score1;
            case 2:
                return R.id.score2;
            case 3:
                return R.id.score3;
            case 4:
                return R.id.score4;
            default:
                return 0;
        }
    }

    private void sendNotificationToUser(String id) {
        String email = auth.getCurrentUser().getEmail();
        // Set value
        Request request = new Request();
        request.action = ACTIONS.SEND;
        request.sender = email;
        request.receiver = "";
        request.gameId = UUID.randomUUID().toString();

        Integer userReqQ = id.hashCode();
        DatabaseReference dbRef = database.getReference("requests");
        Log.d(TAG, request.receiver.toString());
        dbRef.child(userReqQ.toString()).setValue(request);

        Toast.makeText(this, "Sent an invite to user "+curateEmail(email)+". Game will start if the user accepts the invite. ",
                Toast.LENGTH_SHORT).show();
    }

    private void receiveRequest() {
        Integer userReqQ = auth.getCurrentUser().getEmail().hashCode();

        DatabaseReference dbRef = database.getReference("requests");
        dbRef.child(userReqQ.toString()).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    String sender = (String) dataSnapshot.child("sender").getValue();
                    ACTIONS action = ACTIONS.valueOf((String) dataSnapshot.child("action").getValue());
                    String gameId = (String) dataSnapshot.child("gameId").getValue();
                    String receiver = (String) dataSnapshot.child("receiver").getValue();

                    if (action == null || action.equals(ACTIONS.SEND)) {
                        AskNotification(sender, gameId);
                    } else if (action.equals(ACTIONS.ACCEPTED)) {
                        removeRequestFromDB(sender);
                        Player player = new Player(receiver, 0);
                        goToMainActivity(player, gameId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void removeRequestFromDB(String sender) {
        DatabaseReference dbRef = database.getReference("requests");
        dbRef.child(((Integer) sender.hashCode()).toString()).removeValue();
    }

    private void AskNotification(final String sender, final String gameId) {
        final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        new AlertDialog.Builder(ListUsersActivity.this)
                .setTitle("Invite")
                .setMessage("Player "+sender+" wants to start a game?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendAcceptanceToSender(sender, gameId);
                        createGameInDB(gameId, user, sender);

                        Player player = new Player(sender, 0);
                        goToMainActivity(player, gameId);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference dbRef = database.getReference("requests");
                        dbRef.child(((Integer) auth.getCurrentUser().getEmail().hashCode()).toString()).removeValue();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void sendAcceptanceToSender(String sender, String gameId) {

        Request acceptanceRequest = new Request();
        acceptanceRequest.sender = sender;
        acceptanceRequest.receiver = auth.getCurrentUser().getEmail();
        acceptanceRequest.action = ACTIONS.ACCEPTED;
        acceptanceRequest.gameId = gameId;

        DatabaseReference dbRef = database.getReference("requests");
        dbRef.child(((Integer) acceptanceRequest.receiver.hashCode()).toString()).removeValue();

        Integer userReqQ = sender.hashCode();
        dbRef.child(userReqQ.toString()).setValue(acceptanceRequest);
    }

    private void goToMainActivity(Player player, String gameId) {
        if (player != null && gameId != null && !gameId.isEmpty()) {
            BoardFragment.GAMEID = gameId;
            Intent mainActivitIntent = new Intent(this, MainActivityController.class);
            mainActivitIntent.putExtra("opponent", player.getId());
            startActivity(mainActivitIntent);
        }
    }

    private void logOut() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private void initializeUI() {
        final Button logOutBtn = (Button) findViewById(R.id.logOutList);

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    private String curateEmail(String email) {
        return email.split("@")[0];
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