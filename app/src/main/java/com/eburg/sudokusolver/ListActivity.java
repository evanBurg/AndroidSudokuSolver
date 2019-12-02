package com.eburg.sudokusolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements DBAdapter.Listener {
    private DBAdapter db;
    Context context;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
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

}
