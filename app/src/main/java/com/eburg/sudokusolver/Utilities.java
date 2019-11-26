package com.eburg.sudokusolver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Utilities {
    // convert from bitmap to byte array
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap bytesToBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

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
