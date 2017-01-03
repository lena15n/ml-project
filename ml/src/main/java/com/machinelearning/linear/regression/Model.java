package com.machinelearning.linear.regression;

import org.apache.commons.math3.linear.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Model {
    private double[][] bettas;
    private int featuresN = 7;
    private static final int ALL_ROWS_N = 209;
    private String[][] initialInput;
    private String[][] initialIdealOutputs;

    public Model() {
        calculate();
    }

    private void calculate() {
        int position = 0;
        int inputCount = ALL_ROWS_N  * 3 / 4;
        readInput(inputCount, position);
        findBettas(initialInput, initialIdealOutputs);

        position = inputCount;
        readInput(ALL_ROWS_N - inputCount, position);
        test(initialInput, initialIdealOutputs);
    }

    private void test(String[][] initialInput, String[][] initialIdealOutputs) {
        double[][] testInput = new double[initialInput.length][featuresN];
        double[][] testIdealOutputs = new double[initialIdealOutputs.length][1];

        for (int row = 0; row < initialInput.length; row++) {
            for (int feature = 0; feature < featuresN; feature++) {
                System.out.print(initialInput[row][feature] + ",\t");
                testInput[row][feature] = Double.valueOf(initialInput[row][feature]);
            }

            System.out.println("result: " + initialIdealOutputs[row][0]);
            testIdealOutputs[row][0] = Double.valueOf(initialIdealOutputs[row][0]);
        }

        double estim = 0.0;

        for (int i = 0; i < testInput.length; i++) {
            estim += bettas[0][0];

            for (int j = 0; j < testInput[0].length; j++) {
                estim += bettas[j + 1][0] * testInput[i][j];
            }

            int resultMax = 1238;
            double resultMean = 99.3;
            double error = (testIdealOutputs[i][0] - estim) / resultMean;
            System.out.println("Ideal:\t" + testIdealOutputs[i][0] + ", Estim:\t" + estim + ", Error:\t" + error);
            estim = 0;
        }
    }

    private void findBettas(String[][] initialInput, String[][] initialIdealOutputs) {
        double[][] input = new double[initialInput.length][];
        double[][] idealOutputs = new double[initialIdealOutputs.length][];
        int columnsN = initialInput[0].length + 1;

        for (int row = 0; row < initialInput.length; row++) {
            input[row] = new double[columnsN];// 1 1 1 1 - vector
            idealOutputs[row] = new double[1];
            input[row][0] = 1;

            for (int feature = 1; feature < columnsN; feature++) {
                System.out.print(initialInput[row][feature - 1] + ",\t");
                input[row][feature] = Double.valueOf(initialInput[row][feature - 1]);
            }

            System.out.println("result: " + initialIdealOutputs[row][0]);
            idealOutputs[row][0] = Double.valueOf(initialIdealOutputs[row][0]);
        }

        double[][] transpInput = transp(input);
        double[][] invertMatr = findInvertibleMatr(multMatrices(transpInput, input));
        bettas = multMatrices(multMatrices(invertMatr, transpInput), idealOutputs);
    }

    private double[][] findInvertibleMatr(double[][] matr) {
        double[][] invertMatr = new double[matr.length][matr.length];
        for (int i = 0; i < invertMatr.length; i++) {
            for (int j = 0; j < invertMatr.length; j++) {
                invertMatr[i][j] = i == j ? 1 : 0;
            }
        }

        RealMatrix matrix = new Array2DRowRealMatrix(matr);
        RealMatrix y = new Array2DRowRealMatrix(invertMatr);
        DecompositionSolver solver = new LUDecomposition(matrix).getSolver();
        RealMatrix invertM = solver.solve(y);

        return invertM.getData();
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

    public double[][] getBettas() {
        return bettas;
    }

    public static void main(String[] args) {
        new Model();
    }

    private void readInput(int countOfRows, int position) {
        initialInput = new String[countOfRows][];
        initialIdealOutputs = new String[countOfRows][1];
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("D:\\Magistracy\\DES\\machine learning project\\machine.data"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (scanner != null) {
            int k = 0;

            while (k < position) {
                scanner.nextLine();
                k++;
            }

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
