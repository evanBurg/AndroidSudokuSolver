package com.eburg.sudokusolver.ui;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.eburg.sudokusolver.models.ImageResults;
import com.eburg.sudokusolver.solving.StochasticOptimizationSolver;
import com.eburg.sudokusolver.R;
import com.eburg.sudokusolver.models.Solution;
import com.eburg.sudokusolver.database.DBAdapter;
import com.eburg.sudokusolver.solving.backtracking.BacktrackingSolver;
import com.eburg.sudokusolver.solving.backtracking.Grid;
import com.eburg.sudokusolver.solving.backtracking.GridAdapter;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static android.graphics.Color.argb;

public class SolveActivity extends AppCompatActivity implements DBAdapter.Listener, NavigationView.OnNavigationItemSelectedListener {
    public static final int GET_FROM_GALLERY = 3;
    private Bitmap LOADED_IMAGE = null;
    private Uri IMAGE_URI = null;
    private Button solveButton;
    private CardView cardView;
    private boolean isCollapsed = true;
    private ImageResults lastResults = null;
    private ImageView showImage;
    private TextView showImageText;
    private TextView chevron;
    private LinearLayout inputBoardContainer;
    private ArrayList<ArrayList<EditText>> inputBoard;
    private LinearLayout parseContainer;
    private SpinKitView loader;
    BottomNavigationView bottomNavigationView;
    private TextView loadingText;
    private Animation rotateDown;
    private Animation rotateUp;
    private static final int BOARD_START = 0;
    private static final int BOARD_END = 9;
    private static final int originalDimension = 0;
    private static final int newDimension = LinearLayout.LayoutParams.WRAP_CONTENT;
    private DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePicker.Companion.with(this).crop().start(GET_FROM_GALLERY);
        db = new DBAdapter(this);

        db.open(this);
        setContentView(R.layout.solve_activity);
        showImage = findViewById(R.id.providedImage);
        chevron = findViewById(R.id.chevron);
        solveButton = findViewById(R.id.solvePuzzleButton);
        showImageText = findViewById(R.id.showImageText);
        inputBoardContainer = findViewById(R.id.inputBoardContainer);
        parseContainer = findViewById(R.id.parseContainer);
        parseContainer.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            parseContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
        loader = findViewById(R.id.loader);
        inputBoard = new ArrayList<>();
        cardView = findViewById(R.id.imageContainer);
        loadingText = findViewById(R.id.loadingText);
        rotateDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_chevron_down);
        rotateUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_chevron_up);
        ViewGroup.LayoutParams params = showImage.getLayoutParams();
        params.height = originalDimension;
        showImage.setLayoutParams(params);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_solve);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        for(int i = BOARD_START; i < BOARD_END; i++){
            LinearLayout row = new LinearLayout(this);
            inputBoard.add(new ArrayList<EditText>());
            for(int j = BOARD_START; j < BOARD_END; j++){
                EditText text = new EditText(this);
                text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                text.setInputType(InputType.TYPE_CLASS_NUMBER);
                text.setGravity(Gravity.CENTER_HORIZONTAL);
                int maxLength = 1;
                text.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                text.setBackgroundResource(getBorder(j, i));
                row.addView(text);
                inputBoard.get(i).add(text);
            }
            inputBoardContainer.addView(row);
        }

        loader.setOnClickListener(v -> ImagePicker.Companion.with(this).crop().start(GET_FROM_GALLERY));
    }

    public void onImageClick(View v) {
        if(isCollapsed){
            TransitionManager.beginDelayedTransition(cardView, new TransitionSet()
                    .addTransition(new ChangeBounds()));

            chevron.startAnimation(rotateDown);
            showImageText.setText(R.string.hideImage);
            ViewGroup.LayoutParams params = showImage.getLayoutParams();
            params.height = newDimension;
            showImage.setLayoutParams(params);

            isCollapsed = false;
        } else {
            TransitionManager.beginDelayedTransition(cardView, new TransitionSet()
                    .addTransition(new ChangeBounds()));

            chevron.startAnimation(rotateUp);

            showImageText.setText(R.string.showImage);
            ViewGroup.LayoutParams params = showImage.getLayoutParams();
            params.height = originalDimension;
            showImage.setLayoutParams(params);

            isCollapsed = true;
        }
    }

    public static int getBorder(int x, int y){

        if(((x + 1) % 3 == 0 && x != (BOARD_END - 1)) && (y + 1) % 3 == 0 && y != (BOARD_END - 1)){
            return R.drawable.edit_text_border_both_bold;
        }

        if(x == BOARD_START && (y + 1) % 3 == 0 && y != (BOARD_END - 1)){
            return R.drawable.edit_text_border_first_end_bold;
        }

        if((y + 1) % 3 == 0 && y != (BOARD_END - 1)){
            return R.drawable.edit_text_border_end_bold;
        }

        if(y == (BOARD_END - 1) && (x + 1) % 3 == 0 && x != (BOARD_END - 1)){
            return R.drawable.edit_text_border_bottom_bold;
        }

        if((x + 1) % 3 == 0 && x != (BOARD_END - 1)){
            return R.drawable.edit_text_border_bold;
        }

        if(x == BOARD_START && y == (BOARD_END - 1)){
            return R.drawable.edit_text_border_bottom_start;
        }

        if(y == (BOARD_END - 1)){
            return R.drawable.edit_text_border_bottom;
        }

        if(x == BOARD_START){
            return R.drawable.edit_text_border_start;
        }

        return R.drawable.edit_text_border;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void goBack(View v){
        this.onBackPressed();
    }

    /*
           Usage:
           getImageText(image)
              .addOnSuccessListener(firebaseVisionText -> {
                  firebaseVisionText.getText();
               })
             .addOnFailureListener(exception -> {
                  txtView.setText("task failed");
              });
        */
    private Task<FirebaseVisionText> getImageText(Bitmap image) {
        FirebaseVisionImage vImage = FirebaseVisionImage.fromBitmap(image);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        return detector.processImage(vImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            IMAGE_URI = selectedImage;
            LOADED_IMAGE = null;
            try {
                LOADED_IMAGE = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                getImageText(LOADED_IMAGE).addOnSuccessListener(firebaseVisionText -> {
                    String resultText = firebaseVisionText.getText();
                    ArrayList<ImageResults.Result> characters = new ArrayList<>();
                    ArrayList<ImageResults.Result> symbols = new ArrayList<>();
                    for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                        for (FirebaseVisionText.Line paragraph: block.getLines()) {
                            for (FirebaseVisionText.Element character: paragraph.getElements()) {
                                String characterText = character.getText();
                                Float characterConfidence = character.getConfidence();
                                Rect characterFrame = character.getBoundingBox();
                                characters.add(new ImageResults.Result(characterText, characterConfidence, characterFrame));
                            }
                        }
                    }

                    lastResults = new ImageResults(characters, resultText);
                    ArrayList<ArrayList<Integer>> board = lastResults.get2DArray();
                    for(int i = 0; i < board.size(); i++){
                        ArrayList<Integer> row = board.get(i);
                        for(int j = 0; j < row.size(); j++){
                            Integer num = row.get(j);
                            try{
                               inputBoard.get(i).get(j).setText(num != 0 ? String.valueOf(num) : "");
                            }catch (Exception e){
                                String what = e.getMessage();
                                String className = e.toString();
                            }
                        }
                    }

                    showImage.setImageBitmap(LOADED_IMAGE);
                    showImage.setVisibility(View.VISIBLE);
                    parseContainer.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.INVISIBLE);
                    loadingText.setVisibility(View.INVISIBLE);
                    showImage.setAdjustViewBounds(true);
                    solveButton.setEnabled(true);
                });
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void solvePuzzle(View v) throws ExecutionException, InterruptedException {
        ArrayList<ArrayList<Integer>> unsolved = new ArrayList<>();
        for(int i = 0; i < BOARD_END; i++){
            ArrayList<EditText> row = inputBoard.get(i);
            unsolved.add(new ArrayList<>());
            unsolved.get(i).addAll(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));
            for(int j = 0; j < BOARD_END; j++){
                EditText text = row.get(j);
                try{
                    unsolved.get(i).set(j, Integer.parseInt(String.valueOf(text.getText())));
                }catch (Exception e){
                    String what = e.getMessage();
                    String className = e.toString();
                }
            }
        }

        //Send to solve function
        try {
            Solution solution = new Solution(0, unsolved, unsolved, IMAGE_URI);
            AsyncTaskRunner runner = new AsyncTaskRunner(solution);
            runner.execute();
        }catch (Exception e){

        }
    }

    @Override
    public void update() { }

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
                    finish();
                    startActivity(new Intent(this, ListActivity.class));
                    break;
                case R.id.action_main:
                    finish();
                    break;
                case R.id.action_solve:
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

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        public Solution solution;
        ProgressDialog progressDialog;

        public AsyncTaskRunner(Solution solution){
            this.solution = solution;
        }

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Solving...");
            try {
                //this.solution = StochasticOptimizationSolver.solve(this.solution);

                publishProgress("Solved"); Grid grid = GridAdapter.toGrid(this.solution.getProblem());
                BacktrackingSolver solver = new BacktrackingSolver();
                solver.solve(grid); //may throw IllegalStateException if it is a non-solvable sudoku
                this.solution.setSolution(GridAdapter.fromGrid(grid));
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress("Failed");

                return "failure";
            }
            return "success";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            if(result.equals("failure")){
                new AlertDialog.Builder(SolveActivity.this)
                        .setTitle("Could not Solve Puzzle")
                        .setMessage("Sorry we ran into an issue while trying to solve your puzzle, please make sure it is a valid Sudoku puzzle and try again.")
                        .setIcon(R.drawable.ic_error_outline_black_24dp)
                        .show();
            }else {
                //Show solved cells as light grey
                ArrayList<ArrayList<Integer>> solved = solution.getSolution();
                ArrayList<ArrayList<Integer>> unsolved = solution.getProblem();
                for (int i = 0; i < BOARD_END; i++) {
                    ArrayList<EditText> inputRow = inputBoard.get(i);
                    ArrayList<Integer> numRow = solved.get(i);
                    for (int j = 0; j < BOARD_END; j++) {
                        EditText text = inputRow.get(j);
                        Integer num = numRow.get(j);
                        try {
                            text.setText(String.valueOf(num));
                            if (unsolved.get(i).get(j) == 0) {
                                text.setTextColor(Color.LTGRAY);
                            }
                        } catch (Exception e) {
                            String what = e.getMessage();
                            String className = e.toString();
                        }
                    }
                }

                //Insert solution into database
                db.insertSolution(solution);
                CharSequence text = "Solution Saved!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(SolveActivity.this, text, duration);
                toast.show();
                solveButton.setEnabled(false);
                progressDialog.dismiss();
            }
        }


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SolveActivity.this, R.style.SpinnerColor);
            progressDialog = ProgressDialog.show(SolveActivity.this,
                    "Solving",
                    "Please wait while we solve your puzzle...",
                    true,
                    false,
                    null
            );
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
