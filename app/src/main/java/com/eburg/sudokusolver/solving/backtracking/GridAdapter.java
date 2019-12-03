package com.eburg.sudokusolver.solving.backtracking;

import java.util.ArrayList;

public class GridAdapter {
    public static Grid toGrid(ArrayList<ArrayList<Integer>> arrayListsIntegers) {
        int[][] intGrid = new int[9][9];

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++)
                intGrid[y][x] = arrayListsIntegers.get(y).get(x).intValue();
        }

        return Grid.of(intGrid);
    }

    public static ArrayList<ArrayList<Integer>> fromGrid(Grid grid) {
        ArrayList<ArrayList<Integer>> results = new ArrayList<ArrayList<Integer>>();

        for (int y = 0; y < 9; y++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int x = 0; x < 9; x++)
                row.add(new Integer(grid.getCell(y, x).getValue()));
            results.add(row);
        }

        return results;
    }
}
