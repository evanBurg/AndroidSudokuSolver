package com.eburg.sudokusolver.PuzzleSolving;

import com.eburg.sudokusolver.Solution;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class StochasticOptimizationSolver {

    private static ArrayList<Coordinate> boardToCoordinateList(ArrayList<ArrayList<Integer>> board) {
        ArrayList<Coordinate> output = new ArrayList<>();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (!board.get(y).get(x).equals(0)) {
                    output.add(new Coordinate(x,y));
                }
            }
        }
        return output;
    }

    public static Solution solve(Solution solutionModel) {
        ArrayList<Coordinate> canonicalPositions = boardToCoordinateList(solutionModel.getProblem());
        ArrayList<ArrayList<Integer>> board = solutionModel.getProblem();

        ArrayList<Integer> allCellValues = new ArrayList<>();
        for (int x = 0; x < 9; x++)
            allCellValues.addAll(Arrays.asList(1,2,3,4,5,6,7,8,9));
        for (Coordinate coord : canonicalPositions)
            allCellValues.remove(Integer.valueOf(board.get(coord.y).get(coord.x)));
        Collections.shuffle(allCellValues);

        //Initial random assignment to unlabeled positions
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (board.get(y).get(x).equals(0)) {
                    board.get(y).set(x, allCellValues.get(0));
                    allCellValues.remove(0);
                }
            }
        }

        //Call recursive solving method to get board state
        ArrayList<ArrayList<Integer>> solvedBoard = stochasticSolve(board, canonicalPositions);
        solutionModel.setSolution(solvedBoard);

        return solutionModel;
    }

    private static ArrayList<ArrayList<Integer>> stochasticSolve(ArrayList<ArrayList<Integer>> board, ArrayList<Coordinate> canonicalPositions) {
        ArrayList<Coordinate> errorCoords = ErrorFinder.getAllErrorCoords(board, canonicalPositions);

        //If there are no errors in the board, the sudoku puzzle is solved
        if (errorCoords.size() == 0)
            return board;

        //Otherwise, new configurations need to be tested (recurse)
        else {
            ArrayList<Integer> errorCellValues = new ArrayList<>();
            for (Coordinate coord : errorCoords)
                errorCellValues.add(board.get(coord.y).get(coord.x));

            //shuffle the values for cells that are in the wrong spot
            Collections.shuffle(errorCellValues);
            for (int v = 0; v < errorCellValues.size(); v++)
                board.get(errorCoords.get(v).y).set(errorCoords.get(v).x, errorCellValues.get(v));

            return stochasticSolve(board, canonicalPositions);
        }
    }


    /*
    get error coords
    check size of errors
        errors = 0?
            board state is solved
            exit
        errors != 0?
            board state not solved
            get values in error coords, shuffle, randomly assign to coords
            recurse
     */
}
