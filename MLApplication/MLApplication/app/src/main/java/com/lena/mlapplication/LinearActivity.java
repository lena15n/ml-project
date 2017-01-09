package com.lena.mlapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lena.mlapplication.net.GetTask;

import java.util.ArrayList;

public class LinearActivity extends AppCompatActivity implements GetTask.MyAsyncResponse {
    private static final String RECEIVE_TRAIN_ERR_DATA = "3";
    private static final String RECEIVE_VALID_ERR_DATA = "4";
    private static final String URL = "http://b7293f78.ngrok.io/machinelearning-0.0.1-SNAPSHOT/machine-learning/result/";
    private static final String LOGIN = "user";
    private static final String PASSWORD = "admin";
    private ArrayList<Double> trainErrorsData;
    private ArrayList<Double> validationErrorsData;
    private int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear);

        final Button drawButton = (Button) findViewById(R.id.lin_draw_button);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawButton.setVisibility(View.INVISIBLE);

                new GetTask(LinearActivity.this).execute(RECEIVE_TRAIN_ERR_DATA, URL + "train-err", LOGIN, PASSWORD);
                new GetTask(LinearActivity.this).execute(RECEIVE_VALID_ERR_DATA, URL + "validation-err", LOGIN, PASSWORD);
            }
        });
    }

    @Override
    public void processFinish(String code, ArrayList<Double> data) {
        if (code.equals(RECEIVE_TRAIN_ERR_DATA)) {
            trainErrorsData = data;
            result++;
            if (result == 2) {
                draw(R.id.lin_chart, "Errors");
                RelativeLayout trainErrLayout = (RelativeLayout) findViewById(R.id.lin_chart_layout);
                trainErrLayout.setVisibility(View.VISIBLE);
            }

        } else if (code.equals(RECEIVE_VALID_ERR_DATA)) {
            validationErrorsData = data;
            result++;
            if (result == 2) {
                draw(R.id.lin_chart, "Errors");
                RelativeLayout validErrLayout = (RelativeLayout) findViewById(R.id.lin_chart_layout);
                validErrLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void draw(int id, String label) {
        LineChart lineChart = (LineChart) findViewById(id);
        ArrayList<Entry> trainEntries = new ArrayList<>();
        int i = 0;
        while (i < trainErrorsData.size() - 1) {
            trainEntries.add(new Entry(trainErrorsData.get(i).floatValue(), trainErrorsData.get(i + 1).floatValue()));
            i += 2;
        }
        LineDataSet trainDataset = new LineDataSet(trainEntries, "Train errors");
        trainDataset.setColor(Color.GREEN);
        trainDataset.setCircleColor(Color.GREEN);


        ArrayList<Entry> validEntries = new ArrayList<>();
        i = 0;
        while (i < validationErrorsData.size() - 1) {
            validEntries.add(new Entry(validationErrorsData.get(i).floatValue(), validationErrorsData.get(i + 1).floatValue()));
            i += 2;
        }
        LineDataSet validDataset = new LineDataSet(validEntries, "Validation errors");
        validDataset.setColor(Color.BLUE);
        validDataset.setCircleColor(Color.BLUE);



        LineData lineData = new LineData(trainDataset, validDataset);
        lineChart.setData(lineData);

        Description desc = new Description();
        desc.setText(label);
        lineChart.setDescription(desc);
        lineChart.invalidate();
    }
}