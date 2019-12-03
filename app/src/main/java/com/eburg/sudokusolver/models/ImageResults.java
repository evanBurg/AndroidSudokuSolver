package com.eburg.sudokusolver.models;

import android.graphics.Rect;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ImageResults implements Serializable {
    public static class Result implements Comparable<Result>, Serializable {
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
                threshold = (float) (thatSize * ROW_THRESHOLD);
            }else{
                threshold = (float) (thisSize * ROW_THRESHOLD);
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
    private static float ROW_THRESHOLD = 0.15f;

    public ImageResults(
            ArrayList<Result> characters,
            String text
    ) {
        this.characters = characters;
        Collections.sort(this.characters);
        this.text = text;
    }

    public ArrayList<ArrayList<Integer>> get2DArray(){
        float bottom = 0, right = 0;
        ArrayList<ArrayList<Integer>> board = new ArrayList<>();
        for(int i = 0; i < 9; i++){
            board.add(new ArrayList<>());
            board.get(i).addAll(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        for (Result character : characters) {

            try{
                Integer.parseInt(character.text);
            }catch (Exception e){
                continue;
            }

            if(character.boundingBox.bottom > bottom){
                bottom = character.boundingBox.top;
            }

            if(character.boundingBox.top > right){
                right = character.boundingBox.right;
            }
        }
        for (Result character : characters) {
            try{
                Integer.parseInt(character.text);
            }catch (Exception e){
                continue;
            }

            int boxesFromLeft = (int) Math.floor(Math.floor(character.boundingBox.right) / (right / 9));
            int boxesFromTop =  (int) Math.floor(Math.floor(character.boundingBox.top) / (bottom / 9));

            board.get(boxesFromTop >= 9 ? 8 : boxesFromTop).set(boxesFromLeft >= 9 ? 8 : boxesFromLeft, Integer.parseInt(character.text));
        }

        return board;
    }
}
