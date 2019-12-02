package com.eburg.sudokusolver.PuzzleSolving;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;


public class SudokuBoard {

    public ArrayList<ArrayList<Integer>> board;
    public ArrayList<Coordinate> canonicalPositions;

    public SudokuBoard() {
        board = new ArrayList<ArrayList<Integer>>();
        canonicalPositions = new ArrayList<>();
        for (int x = 0; x < 9; x++) {
            board.add(new ArrayList<Integer>(Arrays.asList(0,0,0,0,0,0,0,0,0)));
        }
        Coordinate c = new Coordinate(2,3);
        c.x = 9;
        setCellValue(c, 9);
    }

    public void addInitialValues(ArrayList<Pair<Coordinate, Integer>> correct) {
        for (Pair<Coordinate, Integer> pair : correct) {
            canonicalPositions.add(pair.first);
            setCellValue(pair.first, pair.second);
        }
    }

    public void setCellValue(Coordinate c, Integer value) { board.get(c.y).set(c.x, value); }
    public void getCellValue(Coordinate c) { board.get(c.y).get(c.x); }

    public ArrayList<Coordinate> getErrorCoordsForRow(int yVal) {
        ArrayList<Coordinate> errorCoords = new ArrayList<>();
        ArrayList<Integer> row = board.get(yVal);
        
        for (int i = 1; i <= 9; i++) {
            int occurrence = Collections.frequency(row, i);
            if (occurrence > 1) { //if there is more than one occurrence in the same row, it's an error
                for (int x = 0; x < 9; x++)
                    if (row.get(x) == i)
                        errorCoords.add(new Coordinate(x, yVal));
            }
        }
        return errorCoords;
    }

    public ArrayList<Coordinate> getErrorCoordsForColumn(int xVal) {
        ArrayList<Coordinate> errorCoords = new ArrayList<>();
        ArrayList<Integer> virtualCol = new ArrayList<>();
        for (int y = 0; y < 9; y++) //flatten the column into a 1d vector
            virtualCol.add(board.get(y).get(xVal));

        for (int i = 1; i <= 9; i++) {
            int occurrence = Collections.frequency(virtualCol, i);
            if (occurrence > 1) {
                for (int y = 0; y < 9; y++)
                    if (virtualCol.get(y) == i)
                        errorCoords.add(new Coordinate(xVal, y));
            }
        }

        return errorCoords;
    }

    private ArrayList<Coordinate> getErrorCoordsForBlockByOffset(int baseX, int baseY) {
        ArrayList<Coordinate> errorCoords = new ArrayList<>();
        ArrayList<Integer> virtualBlock = new ArrayList<>();
        for (int yOffset = baseY; yOffset < baseY+3; yOffset++) { //flatten block into a 1d vector
            for (int xOffset = baseX; xOffset < baseX + 3; xOffset++)
                virtualBlock.add(board.get(yOffset).get(xOffset));
        }

        for (int i = 1; i <= 9; i++) {
            int occurrence = Collections.frequency(virtualBlock, i);
            for (int p = 0; p < 9; p++)
                if (virtualBlock.get(p) == i)
                    errorCoords.add(new Coordinate(baseX + (p % 3), baseY + (p / 3)));
        }
        return errorCoords;
    }
    public ArrayList<Coordinate> getErrorCoordsForBlock(int blockNum) {
        int yBase = 3 * (blockNum / 3);
        int xBase = 3 * (blockNum % 3);

        return getErrorCoordsForBlockByOffset(xBase, yBase);
    }

    public ArrayList<Coordinate> getAllErrorCoords() {

        LinkedHashSet<Coordinate> uniqueCoords = new LinkedHashSet<Coordinate>();

        for (int q = 0; q < 9; q++) {
            uniqueCoords.addAll(getErrorCoordsForRow(q));
            uniqueCoords.addAll(getErrorCoordsForColumn(q));
            uniqueCoords.addAll(getErrorCoordsForBlock(q));
        }

        ArrayList<Coordinate> errorCoords = new ArrayList<>(uniqueCoords);
        errorCoords.removeAll(canonicalPositions);
        return errorCoords;
    }
}
