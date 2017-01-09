package com.machinelearning.nn;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Program {
    private static final int ALL_ROWS_N = 468;//470
    private static final int INITIAL_FEATURES_N = 16;
    private static final int FEATURES_N = 32;
    private static final int VALIDATION_COUNT = 100;
    private static final String DATA_URL = "http://archive.ics.uci.edu/ml/machine-learning-databases/00277/ThoraricSurgery.arff";

    private static ArrayList<Double> accuracy;
    private static ArrayList<Double> precision;
    private static ArrayList<Double> recall;
    private static ArrayList<Double> f1Score;

    private static ArrayList<Double> tempTP;
    private static ArrayList<Double> tempFP;

    public Program() {
        try {
            calculate();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        calculate();
    }

    public static void calculate() throws MalformedURLException {
        accuracy = new ArrayList<>();
        precision = new ArrayList<>();
        recall = new ArrayList<>();
        f1Score = new ArrayList<>();

        tempTP = new ArrayList<>();
        tempFP = new ArrayList<>();

       // calculateForNrows(100);
       // calculateForNrows(150);
        calculateForNrows(200);
       // calculateForNrows(250);
       // calculateForNrows(300);
       // calculateForNrows(350);

    }

    private static void calculateForNrows(int nRows) throws MalformedURLException {
        Object[] tempData = readInput(nRows, 0);
        String[][] initialInput = (String[][]) tempData[0];
        String[][] initialIdealOutputs = (String[][]) tempData[1];

        NNetwork network = new NNetwork(FEATURES_N, FEATURES_N, 1, 0.35, 0.4);//0.2, 0.01);
        Object[] newData = network.makeDataBalanced(initialInput, initialIdealOutputs);
        String[][] balancedInput = (String[][])newData[0];
        String[][] balancedOutput = (String[][])newData[1];
        double[][] input = new double[balancedInput.length][FEATURES_N];
        double[][] idealOutputs = new double[balancedOutput.length][1];

        for (int i = 0; i < balancedInput.length; i++) {
            input[i] = network.normalizeInputData(balancedInput[i]);
            idealOutputs[i] = network.normalizeOutputData(balancedOutput[i]);
        }

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(4);

        double eps = 100.0;
        int k = 0;
        while (eps > 0.05) {// 10%
        //for (int i = 0; i < 7000; i++) {//500 000
            for (int j = 0; j < input.length; j++) {
                network.computeOutputs(input[j]);
                network.calcError(idealOutputs[j]);
                network.learn();
            }
            eps = network.getError(input.length);
            System.out.println("Iter №" + k + ": error = " +
                    percentFormat.format(eps));
            k++;
        }


        /* validation */
        double[][] out = new double[VALIDATION_COUNT][];
        int position = ALL_ROWS_N - VALIDATION_COUNT;
        Object[] testData = readInput(VALIDATION_COUNT, position);
        String[][] testInput =(String[][])testData[0];
        String[][] testOutput = (String[][])testData[1];

        int tp = 0, fp = 0, fn = 0, tn = 0;

        for (int i = 0; i < testInput.length; i++) {
            out[i] = new double[1];
            out[i] = network.computeOutputs(network.normalizeInputData(testInput[i]));
            System.out.println("=" + String.format("%4.4f", out[i][0]));
            double[] testOut = network.normalizeOutputData(testOutput[i]);

            if (out[i][0] > 0.5 && testOut[0] < 0.5) {// predict: death, real: survive
                fp++;
            } else if (out[i][0] > 0.5 && testOut[0] > 0.5) {// predict: death, real: death
                tp++;
            } else if (out[i][0] < 0.5 && testOut[0] > 0.5) {// predict: survive, real: death
                fn++;
            } else if (out[i][0] < 0.5 && testOut[0] < 0.5) {// predict: survive, real: survive
                tn++;
            }
        }

        //accuracy = (TP+TN) / (TP+TN+FP+FN)
        accuracy.add((double)nRows);
        accuracy.add(((tp + tn) * 1.0) / (tp + tn + fp + fn));
        System.out.println("accur: " + accuracy.get(1));

        //precision = TP / (TP+FP) (positive predictive value)
        precision.add((double)nRows);
        precision.add((tp * 1.0) / (tp + fp));
        System.out.println("prec: " + precision.get(1));

        //recall = TP / (TP+FN)
        recall.add((double)nRows);
        recall.add((tp * 1.0) / (tp + fn));
        System.out.println("recall: " + recall.get(1));

        //recall = TP / (TP+FN)
        f1Score.add((double)nRows);
        f1Score.add((2.0 * precision.get(0) * recall.get(0)) / (precision.get(0) + recall.get(0)));
        System.out.println("F1Score: " + f1Score.get(1));

        tempTP.add((double)tp);
        tempFP.add((double)fp);
    }

    public static ArrayList<Double> getAccuracy() {
        return accuracy;
    }

    public static ArrayList<Double> getPrecision() {
        return precision;
    }

    public static ArrayList<Double> getRecall() {
        return recall;
    }

    public static ArrayList<Double> getF1Score() {
        return f1Score;
    }


    /*public static void main(String args[]) throws MalformedURLException {
        int rows = 468;//20;
        readInput(new URL(DATA_URL), rows);

        NNetwork network = new NNetwork(FEATURES_N, FEATURES_N, 1, 0.2, 0.01);//FEATURES_N, FEATURES_N + 1, 1, 0.7, 0.9);
        input = new double[rows][];
        idealOutputs = new double[rows][];

        for (int i = 0; i < rows; i++) {
            input[i] = new double[FEATURES_N];
            idealOutputs[i] = new double[1];
            input[i] = network.normalizeInputData(initialInput[i]);
            idealOutputs[i] = network.normalizeOutputData(initialIdealOutputs[i]);
        }

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(4);

        for (int i = 0; i < 500000; i++) {//10000
            for (int j = 0; j < input.length; j++) {
                network.computeOutputs(input[j]);
                network.calcError(idealOutputs[j]);
                network.learn();
            }
            System.out.println("Iter №" + i + ": error = " +
                    percentFormat.format(network.getError(input.length)));
        }

        System.out.println("Recall:");

        for (int i = 0; i < input.length; i++) {

            for (int j = 0; j < input[0].length; j++) {
                System.out.print(String.format("%4.2f\t", input[i][j]));
            }

            double[] out = network.computeOutputs(input[i]);
            System.out.println("=" + String.format("%4.4f", out[0]));
        }
    }*/

    private static Object[] readInput(int countOfRows, int position) {
        Object[] data = new Object[2];
        String[][] initialInput = new String[countOfRows][];
        String[][] initialIdealOutputs = new String[countOfRows][1];
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("D:\\Magistracy\\DES\\machine learning project\\data-lung.txt"));//url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (scanner != null) {
            String line;
            int current = 0;
            try {
                while ((line = scanner.nextLine()) != null && (line.charAt(0) == '@' || line.equals(""))) {
                    scanner.nextLine();
                    current++;
                }

                while (current < position) {
                    scanner.nextLine();
                    current++;
                }
            } catch (NoSuchElementException e) {
                System.out.println("Read finish");
            }

            for (int i = 0; i < countOfRows; i++) {
                initialInput[i] = new String[INITIAL_FEATURES_N];
                String[] tempArray = scanner.nextLine().split(",");

                for (int j = 0; j < INITIAL_FEATURES_N; j++) {
                    initialInput[i][j] = tempArray[j];
                }

                initialIdealOutputs[i][0] = tempArray[INITIAL_FEATURES_N];
                System.out.println("iter " + i);
            }
        }

        data[0] = initialInput;
        data[1] = initialIdealOutputs;

        return data;
    }
}