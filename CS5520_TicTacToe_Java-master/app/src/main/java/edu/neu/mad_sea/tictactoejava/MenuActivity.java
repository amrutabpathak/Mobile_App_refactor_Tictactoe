package edu.neu.mad_sea.tictactoejava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }


    public void startGame_SinglePlayer(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivityController.class);
        startActivity(intent);
    }

    public void EndGame(View view) {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    public void StartGameOnline(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

}