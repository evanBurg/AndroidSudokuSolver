package com.eburg.sudokusolver;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.takusemba.spotlight.OnTargetListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.Target;
import com.takusemba.spotlight.effet.RippleEffect;
import com.takusemba.spotlight.shape.Circle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static android.graphics.Color.argb;

public class SolveActivity extends AppCompatActivity {
    public static final int GET_FROM_GALLERY = 3;
    private Bitmap LOADED_IMAGE = null;
    private TextView output;
    private CardView cardView;
    private RelativeLayout solveContainer;
    private boolean isCollapsed = true;
    private ImageResults lastResults = null;
    private ImageView showImage;
    private TextView showImageText;
    private TextView chevron;
    private LinearLayout inputBoardContainer;
    private ArrayList<ArrayList<EditText>> inputBoard;
    private LinearLayout parseContainer;
    private SpinKitView loader;
    private Animation rotateDown;
    private Animation rotateUp;
    private static final int BOARD_START = 0;
    private static final int BOARD_END = 9;
    private static final int originalDimension = 0;
    private static final int newDimension = LinearLayout.LayoutParams.WRAP_CONTENT;
    private Target spotlightTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePicker.Companion.with(this).crop().start(GET_FROM_GALLERY);

        setContentView(R.layout.solve_activity);
        showImage = findViewById(R.id.providedImage);
        chevron = findViewById(R.id.chevron);
        solveContainer = findViewById(R.id.solveContainer);
        showImageText = findViewById(R.id.showImageText);
        inputBoardContainer = findViewById(R.id.inputBoardContainer);
        parseContainer = findViewById(R.id.parseContainer);
        parseContainer.setVisibility(View.INVISIBLE);
        loader = findViewById(R.id.loader);
        inputBoard = new ArrayList<>();
        cardView = findViewById(R.id.imageContainer);
        rotateDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_chevron_down);
        rotateUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_chevron_up);
        ViewGroup.LayoutParams params = showImage.getLayoutParams();
        params.height = originalDimension;
        showImage.setLayoutParams(params);

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

    private int getBorder(int x, int y){

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

        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
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
                    showImage.setAdjustViewBounds(true);
                })
                .addOnFailureListener(exception -> {
                    output.setText("Could Not Load Image");
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
}
