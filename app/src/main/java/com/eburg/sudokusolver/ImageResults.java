package com.eburg.sudokusolver;

import android.graphics.Rect;

import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;

import java.util.ArrayList;

public class ImageResults {
    public static class Result {
        public String text;
        public Float confidence;
        public Rect boundingBox;

        public Result(String text, Float confidence, Rect boundingBox){
            this.text = text;
            this.confidence = confidence;
            this.boundingBox = boundingBox;
        }
    }

    public ArrayList<Result> characters;
    public String text;

    public ImageResults(
            ArrayList<Result> characters,
            String text
    ) {
        this.characters = characters;
        this.text = text;
    }
}
