package edu.neu.mad_sea.tictactoejava;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import edu.neu.mad_sea.tictactoejava.bean.Player;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "";
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private EditText signEmail,signPwd;
    private Button signBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get auth DB instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        initializeUI();
        initializeListeners();
    }


    private void initializeListeners() {

        // On SignIn
        signBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String email = signEmail.getText().toString();
                String pwd = signPwd.getText().toString();
                signInUser(email, pwd);
            }
        });
    }


    private void signInUser(final String email,final String pwd) {

        // Try to sign user
        auth.signInWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    checkUserandRedirect(task, email, pwd);
                }
            });
    }


    private void goToMainActivity() {

        // Go to Game main page
        Intent signInIntent = new Intent(this, MainActivityController.class);
        startActivity(signInIntent);
    }


    private void createUser(final String email,final String pwd) {

        // Try create user
        auth.createUserWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    checkCreateUserandRedirect(task);
                }
            });
    }


    private void initializeUI() {

        // User / pwd
        signEmail = (EditText) findViewById(R.id.username);
        signPwd = (EditText) findViewById(R.id.password);

        // Sign Btn
        signBtn = (Button) findViewById(R.id.signBtn);
    }


    private void checkUserandRedirect(Task task, String email, String pwd) {

        // Check if the user was logged in or not
        if (task.isSuccessful()) {

            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "signedIn-{email}:checkUserandRedirect");

            goToMainActivity();
            finish();
        } else {

            // If sign in fails, display a message to the user.
            // get exception
            Exception e = task.getException();
            Log.e(TAG, "signIn:Failure:checkUserandRedirect", e);

            processErrorAndCreateUserIfReqd(e, email, pwd);
        }
    }

    private void checkCreateUserandRedirect(Task task) {

        // check if user was created
        if (task.isSuccessful()) {

            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "createUserWithEmail:success");
            FirebaseUser user = auth.getCurrentUser();

            setUserInScoreDB(user.getEmail());
            goToMainActivity();

            finish();
        } else {

            // If create fails, display a message to the user.
            Log.w(TAG, "signIn/create:failure", task.getException());
            Toast.makeText(ProfileActivity.this, task.getException().getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void processErrorAndCreateUserIfReqd(Exception e, String email, String pwd) {

        // Check error from user Sign In
        if( e instanceof FirebaseAuthInvalidUserException) {

            // user does not exist, create
            Log.d(TAG, "userDoesNotExist:checkUserandRedirect");
            createUser(email, pwd);
        } else {

            // User sign failed, display message
            Log.d(TAG, "signIn:Failure:checkUserandRedirect");
            Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserInScoreDB(String email) {

        // Prepare data
        Player p1 = new Player(email, 0);

        // Set value
        DatabaseReference db = database.getReference("scores");
        db.child(hash(email)).setValue(p1);
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