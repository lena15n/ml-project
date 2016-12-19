package com.machinelearning;
/*
 * Encog(tm) Java Examples v3.4
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-examples
 *
 * Copyright 2008-2016 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information on Heaton Research copyrights, licenses
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */

import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.bot.BotError;
import org.encog.bot.BotUtil;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.logging.EncogLogging;
import org.encog.util.simple.EncogUtility;
import org.encog.util.text.Base64;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LungCancerClassification {
    public static String DATA_URL = "http://archive.ics.uci.edu/ml/machine-learning-databases/00277/ThoraricSurgery.arff";//"https://archive.ics.uci.edu/ml/machine-learning-databases/iris/iris.data";
    private String tempPath;

    public static void downloadPageWithoutSomeRows(URL url, File file) {
        try {
            PrintStream printStream = new PrintStream(file);
            Scanner scanner;
            scanner = new Scanner(url.openStream());
            String buf = "";
            while (buf.equals("") || buf.charAt(0) != 'D') {
                buf = scanner.nextLine();
            }

            try {
                do {
                    printStream.println(buf);
                    buf = scanner.nextLine();
                } while (buf != null);
            }
            catch (NoSuchElementException e) {
                System.err.println("End of file :)");
            }

            printStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File downloadData(String[] args) throws MalformedURLException {
        if (args.length != 0) {
            tempPath = args[0];
        } else {
            tempPath = System.getProperty("java.io.tmpdir");
        }

        URL url = new URL(DATA_URL);

        File lungCancerFile = new File(tempPath, "lung-cancer.csv");
        downloadPageWithoutSomeRows(url, lungCancerFile);
        System.out.println("Downloading Lung Cancer dataset to: " + lungCancerFile);

        return new File(tempPath, "lung-cancer - Copy.csv");//lungCancerFile;
    }

    public void run(String[] args) {
        try {
            // Download the data that we will attempt to model.
            File lungCancerDataFile = downloadData(args);

            // Define the format of the data file.
            // This area will change, depending on the columns and
            // format of the file that you are trying to model.
            VersatileDataSource source = new CSVDataSource(lungCancerDataFile, false,
                    CSVFormat.DECIMAL_POINT);
            VersatileMLDataSet data = new VersatileMLDataSet(source);
            //data.defineSourceColumn("sepal-length", 0, ColumnType.continuous);
            data.defineSourceColumn("Forced vital capacity - FVC", 0, ColumnType.continuous);
            data.defineSourceColumn("Volume", 1, ColumnType.continuous);
            data.defineSourceColumn("Age at surgery", 2, ColumnType.continuous);
            data.defineSourceColumn("DGN", 3, ColumnType.nominal);

            // Define the column that we are trying to predict.
            ColumnDefinition outputColumn = data.defineSourceColumn("1 year survival period", 4,
                    ColumnType.nominal);

            // Analyze the data, determine the min/max/mean/sd of every column.
            data.analyze();

            // Map the prediction column to the output of the model, and all
            // other columns to the input.
            data.defineSingleOutputOthersInput(outputColumn);

            // Create feedforward neural network as the model type. MLMethodFactory.TYPE_FEEDFORWARD.
            // You could also other model types, such as:
            // MLMethodFactory.SVM:  Support Vector Machine (SVM)
            // MLMethodFactory.TYPE_RBFNETWORK: RBF Neural Network
            // MLMethodFactor.TYPE_NEAT: NEAT Neural Network
            // MLMethodFactor.TYPE_PNN: Probabilistic Neural Network
            EncogModel model = new EncogModel(data);
            model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);

            // Send any output to the console.
            model.setReport(new ConsoleStatusReportable());

            // Now normalize the data.  Encog will automatically determine the correct normalization
            // type based on the model you chose in the last step.
            data.normalize();

            // Hold back some data for a final validation.
            // Shuffle the data into a random ordering.
            // Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
            model.holdBackValidation(0.3, true, 1001);//-- when all training is end

            // Choose whatever is the default training type for this model.
            model.selectTrainingType(data);

            // Use a 5-fold cross-validated train.  Return the best method found.
            MLRegression bestMethod = (MLRegression) model.crossvalidate(5, true);//-- in training

            // Display the training and validation errors.
            System.out.println("Training error: " + EncogUtility.calculateRegressionError(bestMethod, model.getTrainingDataset()));
            System.out.println("Validation error: " + EncogUtility.calculateRegressionError(bestMethod, model.getValidationDataset()));

            // Display our normalization parameters.
            NormalizationHelper helper = data.getNormHelper();
            System.out.println(helper.toString());

            // Display the final model.
            System.out.println("Final model: " + bestMethod);

            // Loop over the entire, original, dataset and feed it through the model.
            // This also shows how you would process new data, that was not part of your
            // training set.  You do not need to retrain, simply use the NormalizationHelper
            // class.  After you train, you can save the NormalizationHelper to later
            // normalize and denormalize your data.
            ReadCSV csv = new ReadCSV(lungCancerDataFile, false, CSVFormat.DECIMAL_POINT);
            String[] line = new String[4];
            MLData input = helper.allocateInputVector();

            while (csv.next()) {
                StringBuilder result = new StringBuilder();
                line[0] = csv.get(0);
                line[1] = csv.get(1);
                line[2] = csv.get(2);
                line[3] = csv.get(3);
                String correct = csv.get(4);
                helper.normalizeInputVector(line, input.getData(), false);
                MLData output = bestMethod.compute(input);
                String prediction = helper.denormalizeOutputVectorToString(output)[0];

                result.append(Arrays.toString(line));
                result.append(" -> predicted: ");
                result.append(prediction);
                result.append("(correct: ");
                result.append(correct);
                result.append(")");

                System.out.println(result.toString());
            }

            // Delete data file and shut down.
            lungCancerDataFile.delete();
            Encog.getInstance().shutdown();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LungCancerClassification prg = new LungCancerClassification();
        prg.run(args);
    }

}
