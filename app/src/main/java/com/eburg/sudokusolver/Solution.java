package com.eburg.sudokusolver;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Solution {
    private int id;
    private ArrayList<ArrayList<Integer>> problem;
    private ArrayList<ArrayList<Integer>> solution;
    private Bitmap image;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<ArrayList<Integer>> getProblem() {
        return problem;
    }

    public void setProblem(ArrayList<ArrayList<Integer>> problem) {
        this.problem = problem;
    }

    public ArrayList<ArrayList<Integer>> getSolution() {
        return solution;
    }

    public void setSolution(ArrayList<ArrayList<Integer>> solution) {
        this.solution = solution;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Solution() {
    }

    public Solution(int id, ArrayList<ArrayList<Integer>> problem, ArrayList<ArrayList<Integer>> solution, Bitmap image) {
        this.id = id;
        this.problem = problem;
        this.solution = solution;
        this.image = image;
    }

    public Solution(int id, ArrayList<ArrayList<Integer>> problem, ArrayList<ArrayList<Integer>> solution, Bitmap image, String date) {
        this.id = id;
        this.problem = problem;
        this.solution = solution;
        this.image = image;
        this.date = date;
    }
}
