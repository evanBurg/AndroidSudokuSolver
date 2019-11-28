package com.eburg.sudokusolver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
    }

    public void startSolve(View v){
        startActivity(new Intent(this, SolveActivity.class));
    }

    public void viewHistory(View v) {
        startActivity(new Intent(this, ListActivity.class));
    }

}
