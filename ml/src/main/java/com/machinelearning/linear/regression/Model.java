package com.machinelearning.linear.regression;

import org.apache.commons.math3.linear.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Model {
    private double[][] bettas;
    private static final int ALL_ROWS_N = 209;
    private ArrayList<Double> trainErrors;
    private ArrayList<Double> validationErrors;

    public Model() {
        calculate();
    }

    private void calculate() {
        trainErrors = new ArrayList<>();
        validationErrors = new ArrayList<>();
        int maxFeaturesN = 14;

        for (int featuresN = 1; featuresN < maxFeaturesN; featuresN++) {
            double trainError = 0.0;
            double validationError = 0.0;

            int position = 0;
            int inputCount = ALL_ROWS_N / 2; // half
            Object[] inputAndOutput = transformData(readInput(inputCount, position), featuresN);
            double[][] input = (double[][]) inputAndOutput[0];
            double[][] output = (double[][]) inputAndOutput[1];
            findBettas(input, output);

            int countOfCalculations = inputCount / 2; // half half
            for (int j = 0; j < countOfCalculations; j++) {
                inputAndOutput = transformData(readInput(ALL_ROWS_N / 4, j), featuresN);
                input = (double[][]) inputAndOutput[0];
                output = (double[][]) inputAndOutput[1];
                trainError += test(input, output);

                position = inputCount;
                inputAndOutput = transformData(readInput(ALL_ROWS_N / 4, position + countOfCalculations), featuresN);
                input = (double[][]) inputAndOutput[0];
                output = (double[][]) inputAndOutput[1];
                validationError += test(input, output);
            }

            trainErrors.add((double) featuresN);
            trainErrors.add(trainError / countOfCalculations);

            validationErrors.add((double) featuresN);
            validationErrors.add(validationError / countOfCalculations);
        }
        System.out.println("--finish--");
    }

    private double test(double[][] testInput, double[][] testIdealOutputs) {
        double estim = 0.0;
        double mse = 0.0;

        for (int i = 0; i < testInput.length; i++) {
            for (int j = 0; j < testInput[0].length; j++) {
                estim += bettas[j][0] * testInput[i][j];
            }

            mse += Math.pow(testIdealOutputs[i][0] - estim, 2);

            estim = 0;

            /*double resultMean = 99.3;
            double error = (testIdealOutputs[i][0] - estim) / resultMean;
            System.out.println("Ideal:\t" + testIdealOutputs[i][0] + ", Estim:\t" + estim + ", Error:\t" + String.format("%4.4f", error));
            estim = 0;*/
        }

        return mse / testInput.length;
    }

    private void findBettas(double[][] input, double[][] idealOutputs) {
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

    private double[][] multMatrices(double[][] matrA, double[][] matrB) {
        double[][] multMatr = new double[matrA.length][matrB[0].length];

        for (int i = 0; i < matrA.length; i++) {
            for (int j = 0; j < matrB[0].length; j++) {
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

    private Object[] transformData(Object[] stringData, int featuresN) {
        Object[] result = new Object[2];
        String[][] stringInput = (String[][]) stringData[0];
        String[][] stringOutput = (String[][]) stringData[1];

        double[][] input = new double[stringInput.length][featuresN + 1];
        double[][] idealOutputs = new double[stringOutput.length][1];

        for (int row = 0; row < stringInput.length; row++) {
            int last = Math.min(stringInput[0].length, featuresN);
            input[row][0] = 1;// 1 1 1 1 - vector

            for (int feature = 0; feature < last; feature++) {
                input[row][feature + 1] = Double.valueOf(stringInput[row][feature]);
            }

            int featureIdx = last + 1;// 1 1 1 1 - vector
            int idx = 1;
            while (featureIdx < featuresN + 1) {//заполняем квадратами предыдущих признаков, начиная с [1]
                input[row][featureIdx] = Math.pow(input[row][idx], 2);
                featureIdx++;
                idx++;
            }

            System.out.println("result: " + stringOutput[row][0]);
            idealOutputs[row][0] = Double.valueOf(stringOutput[row][0]);
        }

        result[0] = input;
        result[1] = idealOutputs;

        return result;
    }

    private Object[] readInput(int countOfRows, int position) {
        Object[] result = new Object[2];
        String[][] initialInput = new String[countOfRows][];
        String[][] initialIdealOutputs = new String[countOfRows][1];
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
                int featuresN = 7;
                initialInput[i] = new String[featuresN];
                String[] tempArray = scanner.nextLine().split(",");

                for (int j = 0; j < featuresN; j++) {
                    initialInput[i][j] = tempArray[j + 2]; //не учитываем первые два признака
                }

                initialIdealOutputs[i][0] = tempArray[featuresN + 2];
            }
        }

        result[0] = initialInput;
        result[1] = initialIdealOutputs;

        return result;
    }

    public ArrayList<Double> getTrainErrors() {
        return trainErrors;
    }

    public ArrayList<Double> getValidationErrors() {
        return validationErrors;
    }
}
