package com.machinelearning.nn;

import java.util.ArrayList;

public class NNetwork {

    protected double globalError;
    protected int inputCount;
    protected int hiddenCount;
    protected int outputCount;
    protected int neuronCount;
    protected int weightCount;
    protected double learnRate;
    protected double fire[];
    protected double matrix[];
    protected double error[];
    protected double accMatrixDelta[];
    protected double thresholds[];
    protected double matrixDelta[];
    protected double accThresholdDelta[];
    protected double thresholdDelta[];
    protected double momentum;
    protected double errorDelta[];


    public NNetwork(int inputCount,
                    int hiddenCount,
                    int outputCount,
                    double learnRate,
                    double momentum) {

        this.learnRate = learnRate;
        this.momentum = momentum;

        this.inputCount = inputCount;
        this.hiddenCount = hiddenCount;
        this.outputCount = outputCount;
        neuronCount = inputCount + hiddenCount + outputCount;
        weightCount = (inputCount * hiddenCount) + (hiddenCount * outputCount);

        fire = new double[neuronCount];
        matrix = new double[weightCount];
        matrixDelta = new double[weightCount];
        thresholds = new double[neuronCount];
        errorDelta = new double[neuronCount];
        error = new double[neuronCount];
        accThresholdDelta = new double[neuronCount];
        accMatrixDelta = new double[weightCount];
        thresholdDelta = new double[neuronCount];

        reset();
    }


    //rms
    public double getError(int len) {
        double err = Math.sqrt(globalError / (len * outputCount));
        globalError = 0; // clear the accumulator
        return err;
    }

    //activation func
    public double threshold(double sum) {
        return 1.0 / (1 + Math.exp(-1.0 * sum)); //Sigmoid function
    }

    public double[] computeOutputs(double input[]) {
        int i, j;
        final int hiddenIndex = inputCount;
        final int outIndex = inputCount + hiddenCount;

        //first layer
        for (i = 0; i < inputCount; i++) {
            fire[i] = input[i];
        }

        //hidden layer
        int inx = 0;

        for (i = hiddenIndex; i < outIndex; i++) {
            double sum = thresholds[i];

            for (j = 0; j < inputCount; j++) {
                sum += fire[j] * matrix[inx++];
            }
            fire[i] = threshold(sum);
        }

        //output
        double[] result = new double[outputCount];

        for (i = outIndex; i < neuronCount; i++) {
            double sum = thresholds[i];

            for (j = hiddenIndex; j < outIndex; j++) {
                sum += fire[j] * matrix[inx++];
            }
            fire[i] = threshold(sum);
            result[i - outIndex] = fire[i];
        }

        return result;
    }

    public double[] normalizeInputData(String[] initialInput) {
        int dgnCount = 7;
        int pre7Count = 2;
        int pre8Count = 2;
        int pre9Count = 2;
        int pre10Count = 2;
        int pre11Count = 2;
        int pre17Count = 2;
        int pre19Count = 2;
        int pre25Count = 2;
        int pre30Count = 2;
        int pre32Count = 2;
        int allFeaturesCount =
                dgnCount + //1. DGN: Diagnosis
                        1 + //2. PRE4: Forced vital capacity - FVC (numeric)
                        1 + //3. PRE5: Volume
                        1 + //4. PRE6: Performance status
                        pre7Count + //5. PRE7: Pain before surgery
                        pre8Count + //6. PRE8: Haemoptysis before surgery
                        pre9Count + //7. PRE9: Dyspnoea before surgery
                        pre10Count + //8. PRE10: Cough before surgery
                        pre11Count + //9. PRE11: Weakness before surgery
                        1 + //10. PRE14: T in clinical TNM - size of the original tumour
                        pre17Count + //11. PRE17: Type 2 DM - diabetes mellitus
                        pre19Count + //12. PRE19: MI up to 6 months
                        pre25Count + //13. PRE25: PAD - peripheral arterial diseases
                        pre30Count + //14. PRE30: Smoking
                        pre32Count + //15. PRE32: Asthma
                        1;  //16. AGE: Age at surgery
        double[] input = new double[allFeaturesCount];//initialInput.length];

//        1. 7 DGN: Diagnosis - specific combination of ICD-10 codes for primary and secondary as well multiple tumours
// if any (DGN3,DGN2,DGN4,DGN6,DGN5,DGN8,DGN1)
        int idx = 0;
        double value = 1;
        switch (initialInput[0]) {
            case "DGN1": {
                idx = 0;
                input[idx] = value;
            }
            break;
            case "DGN2": {
                idx = 1;
                input[idx] = value;
            }
            break;
            case "DGN3": {
                idx = 2;
                input[idx] = value;
            }
            break;
            case "DGN4": {
                idx = 3;
                input[idx] = value;
            }
            break;
            case "DGN5": {
                idx = 4;
                input[idx] = value;
            }
            break;
            case "DGN6": {
                idx = 5;
                input[idx] = value;
            }
            break;
            case "DGN8": {
                idx = 6;
                input[idx] = value;
            }
            break;
        }

        for (int i = 0; i < dgnCount; i++) {
            if (i != idx) {
                input[i] = 0;
            }
        }
//        2. PRE4: Forced vital capacity - FVC (numeric)
        double fvcMax = 6.3;//3.281638298;
        input[7] = Double.valueOf(initialInput[1]) / fvcMax;

//        3. PRE5: Volume that has been exhaled at the end of the first second of forced expiration - FEV1 (numeric)
        double volumeMax = 86.3;//4.568702128;
        input[8] = (Double.valueOf(initialInput[2])) / volumeMax;

//        4. PRE6: Performance status - Zubrod scale (PRZ2,PRZ1,PRZ0)
        switch (initialInput[3]) {//TODO: мб поменять порядок
            case "PRZ2": {
                input[9] = 0;
            } break;
            case "PRZ1": {
                input[9] = 0.5;
            } break;
            case "PRZ0": {
                input[9] = 1;
            } break;
        }
//        5. 2 PRE7: Pain before surgery (T,F)
        if (initialInput[4].equals("T")) {
            input[10] = 1;
            input[11] = 0;
        } else {
            input[10] = 0;
            input[11] = 1;
        }

//        6. 2 PRE8: Haemoptysis before surgery (T,F)
        if (initialInput[5].equals("T")) {
            input[12] = 1;
            input[13] = 0;
        } else {
            input[12] = 0;
            input[13] = 1;
        }

//        7. 2 PRE9: Dyspnoea before surgery (T,F)
        if (initialInput[6].equals("T")) {
            input[14] = 1;
            input[15] = 0;
        } else {
            input[14] = 0;
            input[15] = 1;
        }

//        8. 2 PRE10: Cough before surgery (T,F)
        if (initialInput[7].equals("T")) {
            input[16] = 1;
            input[17] = 0;
        } else {
            input[16] = 0;
            input[17] = 1;
        }

//        9. 2 PRE11: Weakness before surgery (T,F)
        if (initialInput[8].equals("T")) {
            input[18] = 1;
            input[19] = 0;
        } else {
            input[18] = 0;
            input[19] = 1;
        }

//        10. PRE14: T in clinical TNM - size of the original tumour, from OC11 (smallest) to OC14 (largest)
// (OC11,OC14,OC12,OC13)
        double coeff = 1 / 3;
        switch (initialInput[9]) {//TODO: мб поменять порядок
            case "OC11": {
                input[20] = 0 * coeff;
            } break;
            case "OC12": {
                input[20] = 1 * coeff;
            } break;
            case "OC13": {
                input[20] = 2 * coeff;
            } break;
            case "OC14": {
                input[20] = 3 * coeff;
            } break;
        }

//        11. 2 PRE17: Type 2 DM - diabetes mellitus (T,F)
        if (initialInput[10].equals("T")) {
            input[21] = 1;
            input[22] = 0;
        } else {
            input[21] = 0;
            input[22] = 1;
        }

//        12. 2 PRE19: MI up to 6 months (T,F)
        if (initialInput[11].equals("T")) {
            input[23] = 1;
            input[24] = 0;
        } else {
            input[23] = 0;
            input[24] = 1;
        }

//        13. 2 PRE25: PAD - peripheral arterial diseases (T,F)
        if (initialInput[12].equals("T")) {
            input[25] = 1;
            input[26] = 0;
        } else {
            input[25] = 0;
            input[26] = 1;
        }

//        14. 2 PRE30: Smoking (T,F)
        if (initialInput[13].equals("T")) {
            input[27] = 1;
            input[28] = 0;
        } else {
            input[27] = 0;
            input[28] = 1;
        }

//        15. 2 PRE32: Asthma (T,F)
        if (initialInput[14].equals("T")) {
            input[29] = 1;
            input[30] = 0;
        } else {
            input[29] = 0;
            input[30] = 1;
        }

//        16. AGE: Age at surgery (numeric)
        double maxAge = 87;//double ageMean = 62.53404;
        input[31] = Double.valueOf(initialInput[15]) / maxAge;

        return input;
    }

    public double[] normalizeOutputData(String[] initialOutput) {
        double[] output = new double[1];

        if (initialOutput[0].equals("F")) {
            output[0] = 0;
        } else {
            output[0] = 1;
        }

        return output;
    }

    public Object[] makeDataBalanced(String[][] in, String[][] out) {
        Object[] corrected = new Object[2];
        int countOfDeathes = 0;
        int tempT = 0;
        int tempF = 0;

        for (int i = 0; i < out.length; i++) {
            if (out[i][0].equals("T")) {//death
                countOfDeathes++;
                tempT++;
            } else {
                tempF++;
            }
        }

        System.out.println("--ini deathes: " + tempT);
        System.out.println("--ini survives: " + tempF);
        tempF = 0;
        tempT = 0;


        //балансируем данные только если надо
        if (out.length > countOfDeathes * 2) {
            ArrayList<Integer> indexesToRemove = new ArrayList<>();
            String[][] input = new String[countOfDeathes * 2][in[0].length];
            String[][] output = new String[countOfDeathes * 2][1];
            int countOfRemoving = out.length - countOfDeathes * 2;

            while (indexesToRemove.size() < countOfRemoving) {
                int k = (int)(Math.random() * out.length);

                if (out[k][0].equals("F") && !indexesToRemove.contains(k)) {// survive
                    indexesToRemove.add(k);
                }
            }


            int idx = 0;
            for (int i = 0; i < out.length; i++) {
                if (!indexesToRemove.contains(i) && idx < input.length) {
                    input[idx] = in[i];
                    output[idx] = out[i];
                    idx++;
                    if (out[i][0].equals("F")) {
                        tempF++;
                    } else {
                        tempT++;
                    }
                }
            }

            System.out.println("deathes: " + tempT);
            System.out.println("survives: " + tempF);

            corrected[0] = input;
            corrected[1] = output;
        }

        return corrected;
    }

    //ошибка на только вычисленных данных
    public void calcError(double ideal[]) {
        int i, j;
        final int hiddenIndex = inputCount;
        final int outputIndex = inputCount + hiddenCount;

        // clear hidden layer errors
        for (i = inputCount; i < neuronCount; i++) {
            error[i] = 0;
        }

        // layer errors and deltas for output layer
        for (i = outputIndex; i < neuronCount; i++) {
            error[i] = ideal[i - outputIndex] - fire[i];
            globalError += error[i] * error[i];
            errorDelta[i] = error[i] * fire[i] * (1 - fire[i]); // * производная функции активации
        }

        // hidden layer errors
        int winx = inputCount * hiddenCount;

        for (i = outputIndex; i < neuronCount; i++) {
            for (j = hiddenIndex; j < outputIndex; j++) {
                accMatrixDelta[winx] += errorDelta[i] * fire[j];
                error[j] += matrix[winx] * errorDelta[i];//ошибка кот-я пришла к нам от i-ого выходн. нейрона
                winx++;
            }
            accThresholdDelta[i] += errorDelta[i];
        }

        // hidden layer deltas
        for (i = hiddenIndex; i < outputIndex; i++) {
            errorDelta[i] = error[i] * fire[i] * (1 - fire[i]);//текущая ошибка для именно этого нейрона скрытого слоя
        }

        // input layer errors
        winx = 0; // offset into weight array
        for (i = hiddenIndex; i < outputIndex; i++) {
            for (j = 0; j < hiddenIndex; j++) {
                accMatrixDelta[winx] += errorDelta[i] * fire[j];
                error[j] += matrix[winx] * errorDelta[i];
                winx++;
            }
            accThresholdDelta[i] += errorDelta[i];
        }
    }

    /**
     * Modify the weight matrix and thresholds based on the last call to
     * calcError.
     */
    public void learn() {
        int i;

        // process the matrix
        for (i = 0; i < matrix.length; i++) {
            matrixDelta[i] = (learnRate * accMatrixDelta[i]) + (momentum * matrixDelta[i]);
            matrix[i] += matrixDelta[i];
            accMatrixDelta[i] = 0;
        }

        // process the thresholds
        for (i = inputCount; i < neuronCount; i++) {
            thresholdDelta[i] = learnRate * accThresholdDelta[i] + (momentum * thresholdDelta[i]);
            thresholds[i] += thresholdDelta[i];
            accThresholdDelta[i] = 0;
        }
    }

    /**
     * Reset the weight matrix and the thresholds.
     */
    public void reset() {
        int i;

        for (i = 0; i < neuronCount; i++) {
            thresholds[i] = 0.5 - (Math.random());
            thresholdDelta[i] = 0;
            accThresholdDelta[i] = 0;
        }
        for (i = 0; i < matrix.length; i++) {
            matrix[i] = 0.5 - (Math.random());
            matrixDelta[i] = 0;
            accMatrixDelta[i] = 0;
        }
    }
}