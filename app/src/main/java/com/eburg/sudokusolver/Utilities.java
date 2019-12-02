package com.eburg.sudokusolver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Utilities {
    public static String flattenArray(ArrayList<ArrayList<Integer>> array){
        return Arrays.deepToString(array.toArray());
    }

    public static ArrayList<ArrayList<Integer>> inflateArray(String array){
        String[] strings = array.replace("[", "").replace("]", ">").split(", ");
        ArrayList<Integer> stringList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> results = new ArrayList<>();
        for(String str : strings) {
            if(str.endsWith(">")) {
                str = str.substring(0, str.length() - 1);
                if(str.endsWith(">")) {
                    str = str.substring(0, str.length() - 1);
                }
                stringList.add(Integer.parseInt(str));
                results.add(stringList);
                stringList = new ArrayList<>();
            } else {
                stringList.add(Integer.parseInt(str));
            }
        }

        return results;
    }
}
