package com.eburg.sudokusolver.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.eburg.sudokusolver.R;
import com.eburg.sudokusolver.models.Solution;
import com.eburg.sudokusolver.models.SolutionAdapter;
import com.eburg.sudokusolver.database.DBAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements DBAdapter.Listener, NavigationView.OnNavigationItemSelectedListener {
    private DBAdapter db;
    Context context;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    BottomNavigationView bottomNavigationView;
    ArrayList<Solution> solutions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        solutions = new ArrayList<>();
        db = new DBAdapter(this);
        db.open(this);
        solutions = db.getAllSolutions();
        context = getApplicationContext();

        linearLayout = findViewById(R.id.LinearLayout);
        recyclerView = findViewById(R.id.RecyclerView);
        recyclerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new SolutionAdapter(context, solutions, db, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_history);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    public void onClear(View view) {
        db.deleteAllSolutions();
        onRefresh();
    }

    public void onBack(View view) {
        finish();
    }

    public void onRefresh(){
        solutions = db.getAllSolutions();
        recyclerViewAdapter = new SolutionAdapter(context, solutions, db, this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void update() {
        onRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        try {
            switch (menuItem.getItemId()) {
                case R.id.action_history:
                    break;
                case R.id.action_main:
                    finish();
                    break;
                case R.id.action_solve:
                    finish();
                    startActivity(new Intent(this, SolveActivity.class));
                    break;
                default:
                    return false;
            }

            return true;

        } catch (Exception e) {
            String what = e.getMessage();
            return false;
        }
    }

}
