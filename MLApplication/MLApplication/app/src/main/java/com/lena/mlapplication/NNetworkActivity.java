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

public class NNetworkActivity extends AppCompatActivity implements GetTask.MyAsyncResponse {
    private static final String RECEIVE_ACCURACY_DATA = "1";
    private static final String RECEIVE_PRECISION_DATA = "2";
    private static final String URL = "http://a8436b9b.ngrok.io/machinelearning-0.0.1-SNAPSHOT/machine-learning/result/";
    private static final String LOGIN = "user";
    private static final String PASSWORD = "admin";
    private ArrayList<Double> accuracyData;
    private ArrayList<Double> precisionData;
    private int graphic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        final Button drawButton = (Button) findViewById(R.id.draw_button);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawButton.setVisibility(View.INVISIBLE);

                new GetTask(NNetworkActivity.this).execute(RECEIVE_ACCURACY_DATA, URL + "accuracy", LOGIN, PASSWORD);
                new GetTask(NNetworkActivity.this).execute(RECEIVE_PRECISION_DATA, URL + "precision", LOGIN, PASSWORD);
            }
        });
    }

    @Override
    public void processFinish(String code, ArrayList<Double> data) {
        if (code.equals(RECEIVE_ACCURACY_DATA)) {
            accuracyData = data;
            draw(R.id.chart_accuracy, "Accuracy", accuracyData);
            RelativeLayout accuracy_layout = (RelativeLayout) findViewById(R.id.chart_accuracy_layout);
            accuracy_layout.setVisibility(View.VISIBLE);

        } else if (code.equals(RECEIVE_PRECISION_DATA)) {
            precisionData = data;
            draw(R.id.chart_precision, "Precision", precisionData);
            RelativeLayout precision_layout = (RelativeLayout) findViewById(R.id.chart_precision_layout);
            precision_layout.setVisibility(View.VISIBLE);
        }
    }

    private void draw(int id, String label, ArrayList<Double> data) {
        LineChart lineChart = (LineChart) findViewById(id);
        ArrayList<Entry> entries = new ArrayList<>();
        int i = 0;
        while (i < data.size() - 1) {
            entries.add(new Entry(data.get(i).floatValue(), data.get(i + 1).floatValue()));
            i += 2;
        }

        LineDataSet dataset = new LineDataSet(entries, label);
        if (graphic > 0) {
            dataset.setColor(Color.GREEN);
            dataset.setCircleColor(Color.GREEN);
        }
        LineData lineData = new LineData(dataset);
        lineChart.setData(lineData);

        Description desc = new Description();
        desc.setText("");
        lineChart.setDescription(desc);
        lineChart.invalidate();

        graphic++;
    }
}
