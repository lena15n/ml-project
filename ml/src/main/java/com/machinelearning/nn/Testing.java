package com.machinelearning.nn;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Testing {
    private static final int INPUT_N = 470;
    private static final int FEATURES_N = 16;
    private static final String DATA_URL = "http://archive.ics.uci.edu/ml/machine-learning-databases/00277/ThoraricSurgery.arff";
    private static String[][] input;
    private static String[][] idealOutputs;

    public static void main(String args[]) throws MalformedURLException {
        int rows = 20;
        readInput(new URL(DATA_URL), rows);
        /*double xorIdeal[][] =
                {{0.0}, {1.0}, {1.0}, {0.0}};

        System.out.println("Learn:");

        Network network = new Network(2, 3, 1, 0.7, 0.9);

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(4);


        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < xorInput.length; j++) {
                network.computeOutputs(xorInput[j]);
                network.calcError(xorIdeal[j]);
                network.learn();
            }
            System.out.println("Trial #" + i + ",Error:" +
                    percentFormat.format(network.getError(xorInput.length)));
        }

        System.out.println("Recall:");

        for (int i = 0; i < xorInput.length; i++) {

            for (int j = 0; j < xorInput[0].length; j++) {
                System.out.print(xorInput[i][j] + ":");
            }

            double out[] = network.computeOutputs(xorInput[i]);
            System.out.println("=" + out[0]);
        }*/
    }

    private static void readInput(URL url, int countOfRows) {
        input = new String[countOfRows][];
        idealOutputs = new String[1][countOfRows];
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
                input[i] = new String[FEATURES_N];
                String[] tempArray = scanner.nextLine().split(",");

                for (int j = 0; j < FEATURES_N; j++) {
                    input[i][j] = tempArray[j];
                }

                idealOutputs[0][i] = tempArray[FEATURES_N];
                System.out.println("iter " + i);
            }
        }
    }
}