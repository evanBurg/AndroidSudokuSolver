package com.eburg.sudokusolver.PuzzleSolving;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;


public class ErrorFinder {
    private static ArrayList<Coordinate> getErrorCoordsForRow(@NotNull ArrayList<ArrayList<Integer>> board, int yVal) {
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

    private static ArrayList<Coordinate> getErrorCoordsForColumn(@NotNull ArrayList<ArrayList<Integer>> board, int xVal) {
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

    private static ArrayList<Coordinate> getErrorCoordsForBlockByOffset(@NotNull ArrayList<ArrayList<Integer>> board, int baseX, int baseY) {
        ArrayList<Coordinate> errorCoords = new ArrayList<>();
        ArrayList<Integer> virtualBlock = new ArrayList<>();
        for (int yOffset = baseY; yOffset < baseY+3; yOffset++) { //flatten block into a 1d vector
            for (int xOffset = baseX; xOffset < baseX + 3; xOffset++)
                virtualBlock.add(board.get(yOffset).get(xOffset));
        }

        for (int i = 1; i <= 9; i++) { //check each number 1-9
            int occurrence = Collections.frequency(virtualBlock, i);
            for (int p = 0; p < 9; p++) //examine each position in the flattened block
                if (occurrence > 1 && virtualBlock.get(p) == i) //if there is more than one of this number, add it's coordinate to the output
                    errorCoords.add(new Coordinate(baseX + (p % 3), baseY + (p / 3)));
        }
        return errorCoords;
    }

    private static ArrayList<Coordinate> getErrorCoordsForBlock(@NotNull ArrayList<ArrayList<Integer>> board, int blockNum) {
        int yBase = 3 * (blockNum / 3);
        int xBase = 3 * (blockNum % 3);

        return getErrorCoordsForBlockByOffset(board, xBase, yBase);
    }

    public static ArrayList<Coordinate> getAllErrorCoords(@NotNull ArrayList<ArrayList<Integer>> board, @NotNull ArrayList<Coordinate> canonicalPositions) {
        LinkedHashSet<Coordinate> uniqueCoords = new LinkedHashSet<Coordinate>();

        for (int q = 0; q < 9; q++) {
            uniqueCoords.addAll(getErrorCoordsForRow(board, q));
            uniqueCoords.addAll(getErrorCoordsForColumn(board, q));
            uniqueCoords.addAll(getErrorCoordsForBlock(board, q));
        }

        ArrayList<Coordinate> errorCoords = new ArrayList<>(uniqueCoords);
        errorCoords.removeAll(canonicalPositions);
        return errorCoords;
    }
}
