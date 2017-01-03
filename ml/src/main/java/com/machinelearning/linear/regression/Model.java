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
