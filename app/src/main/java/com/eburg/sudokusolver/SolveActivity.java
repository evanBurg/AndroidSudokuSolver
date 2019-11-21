package com.eburg.sudokusolver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SolveActivity extends AppCompatActivity {
    public static final int GET_FROM_GALLERY = 3;
    private Bitmap LOADED_IMAGE = null;
    private TextView output;
    private ImageResults lastResults = null;
    private ImageView showImage;
    private Button loadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = findViewById(R.id.outputBoard);
        showImage = findViewById(R.id.providedImage);
        ImagePicker.Companion.with(this).crop().start(GET_FROM_GALLERY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
                    String boardString = "╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗\n";
                    for(int i = 0; i < board.size(); i++){
                        ArrayList<Integer> row = board.get(i);
                        for(int j = 0; j < row.size(); j++){
                            if(j == 0){
                                boardString += "║    ";
                            }

                            Integer num = row.get(j);
                            boardString += num + "    ";

                            if(j % 2 == 0 && j != 0){
                                boardString += "║    ";
                            }else{
                                boardString += "│    ";
                            }

                            if(j == 8){
                                boardString += "║";
                            }
                        }
                        if(i == 8){
                            boardString += "╚═══╧═══╧═══╩═══╧═══╧═══╩═══╧═══╧═══╝";
                        }else{
                            boardString += "───┼───┼───╫───┼───┼───╫───┼───┼───╢\n";
                        }
                    }

                    output.setText(boardString);
                    showImage.setImageBitmap(LOADED_IMAGE);
                    showImage.setVisibility(View.VISIBLE);
                    loadImage.setVisibility(View.INVISIBLE);

                    //Calc
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
