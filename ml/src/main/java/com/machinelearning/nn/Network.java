package com.machinelearning.nn;

public class Network {

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


    public Network(int inputCount,
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

        for (i = 0; i < inputCount; i++) {
            fire[i] = input[i];
        }

        // first layer
        int inx = 0;

        for (i = hiddenIndex; i < outIndex; i++) {
            double sum = thresholds[i];

            for (j = 0; j < inputCount; j++) {
                sum += fire[j] * matrix[inx++];
            }
            fire[i] = threshold(sum);
        }

        // hidden layer

        double result[] = new double[outputCount];

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
            errorDelta[i] = error[i] * fire[i] * (1 - fire[i]);
        }

        // hidden layer errors
        int winx = inputCount * hiddenCount;

        for (i = outputIndex; i < neuronCount; i++) {
            for (j = hiddenIndex; j < outputIndex; j++) {
                accMatrixDelta[winx] += errorDelta[i] * fire[j];
                error[j] += matrix[winx] * errorDelta[i];
                winx++;
            }
            accThresholdDelta[i] += errorDelta[i];
        }

        // hidden layer deltas
        for (i = hiddenIndex; i < outputIndex; i++) {
            errorDelta[i] = error[i] * fire[i] * (1 - fire[i]);
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