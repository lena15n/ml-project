package com.machinelearning.nn;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Program {
    private static final int ALL_ROWS_N = 470;
    private static final int INITIAL_FEATURES_N = 16;
    private static final int FEATURES_N = 32;
    private static final String DATA_URL = "http://archive.ics.uci.edu/ml/machine-learning-databases/00277/ThoraricSurgery.arff";

    private static String[][] initialInput;
    private static String[][] initialIdealOutputs;

    private static double[][] input;
    private static double[][] idealOutputs;

    public static void main(String args[]) throws MalformedURLException {
        int rows = 20;
        readInput(new URL(DATA_URL), rows);

        NNetwork network = new NNetwork(FEATURES_N, FEATURES_N + 1, 1, 0.7, 0.9);
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

        for (int i = 0; i < 100; i++) {//10000
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
    }

    private static void readInput(URL url, int countOfRows) {
        initialInput = new String[countOfRows][];
        initialIdealOutputs = new String[countOfRows][1];
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("D:\\Magistracy\\DES\\machine learning project\\data-lung.txt"));//url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (scanner != null) {
            String line;
            try {
                while ((line = scanner.nextLine()) != null && (line.charAt(0) == '@' || line.equals(""))) {
                    scanner.nextLine();
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
    }
}