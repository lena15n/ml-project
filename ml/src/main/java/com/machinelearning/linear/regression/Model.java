package com.machinelearning.linear.regression;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Model {
    private ArrayList<Double> bettas;
    private int featuresN = 7;
    private static final int ALL_ROWS_N = 209;
    private String[][] initialInput;
    private String[][] initialIdealOutputs;

    public Model() {
        calculate();
    }

    private void calculate() {
        bettas = new ArrayList<>();
        int inputCount = ALL_ROWS_N  * 3 / 4;
        readInput(inputCount);

        findBettas();
    }

    private void findBettas() {
        double[][] input = new double[initialInput.length][];
        double[][] idealOutputs = new double[initialIdealOutputs.length][];

        for (int row = 0; row < initialInput.length; row++) {
            input[row] = new double[initialInput[row].length];
            idealOutputs[row] = new double[1];

            for (int feature = 0; feature < initialInput[row].length; feature++) {
                System.out.print(initialInput[row][feature] + ",\t");
                input[row][feature] = Double.valueOf(initialInput[row][feature]);
            }

            System.out.println("result: " + initialIdealOutputs[row][0]);
            idealOutputs[row][0] = Double.valueOf(initialIdealOutputs[row][0]);
        }


        double[][] tr = transp(new double[][]{{1,2,3}, {6,5,4}, {9,10,11}});//input
        double[][] mul = multMatrices(new double[][]{{1,2,3}, {6,5,4}, {9,10,11}}, tr);


    }

    private double[][] transp(double[][] matr) {
        double[][] transpMatr = new double[matr[0].length][matr.length];

        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                transpMatr[j][i] = matr[i][j];
            }
        }

        return transpMatr;
    }

    private double[][] multMatrices (double[][] matrA, double[][] matrB) {
        double[][] multMatr = new double[matrA.length][matrB[0].length];

        for (int i = 0; i < matrA.length; i++) {
            for (int j = 0; j < matrB[0].length; j++){
                double sum = 0.0;

                for (int k = 0; k < matrA[0].length; k++) { // or matrB.len
                    sum += matrA[i][k] * matrB[k][j];
                }

                multMatr[i][j] = sum;
            }
        }

        return multMatr;
    }

    public ArrayList<Double> getBettas() {
        return bettas;
    }

    public static void main(String[] args) {
        new Model();
    }

    private void readInput(int countOfRows) {
        initialInput = new String[countOfRows][];
        initialIdealOutputs = new String[countOfRows][1];
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("D:\\Magistracy\\DES\\machine learning project\\machine.data"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (scanner != null) {
            for (int i = 0; i < countOfRows; i++) {
                initialInput[i] = new String[featuresN];
                String[] tempArray = scanner.nextLine().split(",");

                for (int j = 0; j < featuresN; j++) {
                    initialInput[i][j] = tempArray[j + 2]; //не учитываем первые два признака
                }

                initialIdealOutputs[i][0] = tempArray[featuresN + 2];
            }
        }
    }
}
