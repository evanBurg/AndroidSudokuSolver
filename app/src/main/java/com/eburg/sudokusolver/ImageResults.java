package com.eburg.sudokusolver;

import android.graphics.Rect;

import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ImageResults {
    public static class Result implements Comparable<Result> {
        public String text;
        public Float confidence;
        public Rect boundingBox;

        public Result(String text, Float confidence, Rect boundingBox){
            this.text = text;
            this.confidence = confidence;
            this.boundingBox = boundingBox;
        }

        @Override
        public int compareTo(Result result) {
            int thisSize = this.boundingBox.bottom - this.boundingBox.top;
            int thatSize = result.boundingBox.bottom - result.boundingBox.top;
            float threshold = 0;

            if(thatSize >= thisSize){
                threshold = (float) (thatSize * 0.15);
            }else{
                threshold = (float) (thisSize * 0.15);
            }
            int diff = this.boundingBox.top - result.boundingBox.top;

            if(Math.abs(diff) < threshold){
                return this.boundingBox.left - result.boundingBox.left;
            }

           return diff;
        }
    }

    public ArrayList<Result> characters;
    public String text;
    private static float ROW_THRESHOLD = 10;

    public ImageResults(
            ArrayList<Result> characters,
            String text
    ) {
        this.characters = characters;
        Collections.sort(this.characters);
        this.text = text;
    }

    public ArrayList<ArrayList<Integer>> get2DArray(){
        float top = 10000, bottom = 0, left = 10000, right = 0;
        ArrayList<ArrayList<Integer>> board = new ArrayList<>();
        for(int i = 0; i < 9; i++){
            board.add(new ArrayList<>());
            board.get(i).addAll(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));
        }

        //Get avg width
        float maxRight = 0, maxLeft = 0, maxTop = 0, maxBottom = 0;
        for (Result character : characters) {
            maxLeft += character.boundingBox.left;
            maxRight += character.boundingBox.right;
            maxTop += character.boundingBox.top;
            maxBottom += character.boundingBox.bottom;

            if(character.boundingBox.top < top){
                top = character.boundingBox.top;
            }

            if(character.boundingBox.bottom > bottom){
                bottom = character.boundingBox.top;
            }

            if(character.boundingBox.left < left){
                left = character.boundingBox.left;
            }

            if(character.boundingBox.top > right){
                right = character.boundingBox.right;
            }
        }

        float avgWidth = ((maxRight / characters.size()) - (maxLeft / characters.size()));
        float avgHeight = ((maxBottom / characters.size()) - (maxTop / characters.size()));

        int insertedIntoCurrentRow = 0, currentRow = 0;
        for (Result character : characters) {
            int boxesFromLeft = (int) Math.floor((character.boundingBox.left - left) / avgWidth);
            for(int i = 0; i < boxesFromLeft; i++){
                board.get(currentRow).set(i, 0);
                insertedIntoCurrentRow++;
            }
            board.get(currentRow).set(insertedIntoCurrentRow, Integer.parseInt(character.text));
        }

        return board;
    }
}
