package com.eburg.sudokusolver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int GET_FROM_GALLERY = 3;
    private Bitmap LOADED_IMAGE = null;
    private TextView output;
    private ImageResults lastResults = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        output = findViewById(R.id.text);
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

    public void loadImage(View v) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
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
                    ArrayList<ArrayList<Integer>> test = lastResults.get2DArray();
                    output.setText(resultText);

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
