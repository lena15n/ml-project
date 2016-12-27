package com.lena.mlapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lena.mlapplication.net.GetTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GetTask.MyAsyncResponse {
    private static final String RECEIVE_ACCURACY_DATA = "1";
    private static final String RECEIVE_PRECISION_DATA = "2";
    private static final String URL = "http://d677e500.ngrok.io/machinelearning-0.0.1-SNAPSHOT/machine-learning/result/";
    private static final String LOGIN = "user";
    private static final String PASSWORD = "admin";
    private ArrayList<Double> accuracyData;
    private ArrayList<Double> precisionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        draw(R.id.chart_accuracy, "Accuracy", new ArrayList<Double>());
       /* Button drawButton = (Button) findViewById(R.id.draw_button);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw(R.id.chart_accuracy, "ПРивет", new ArrayList<Double>());
                // new GetTask(MainActivity.this).execute(RECEIVE_ACCURACY_DATA, URL + "accuracy", LOGIN, PASSWORD);
                // new GetTask(MainActivity.this).execute(RECEIVE_PRECISION_DATA, URL + "precision", LOGIN, PASSWORD);
            }
        });*/
    }

    @Override
    public void processFinish(String code, ArrayList<Double> data) {
        if (code.equals(RECEIVE_ACCURACY_DATA)) {
            accuracyData = data;
        } else if (code.equals(RECEIVE_PRECISION_DATA)) {
            precisionData = data;
        }
    }

    private void draw(int id, String label, ArrayList<Double> data) {
        LineChart lineChart = (LineChart) findViewById(id);
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 17));//x y
        entries.add(new Entry(1f, 22));
        entries.add(new Entry(2f, 10));
        entries.add(new Entry(3f, 7));
        entries.add(new Entry(4f, 8));
        entries.add(new Entry(5f, 5));

        LineDataSet dataset = new LineDataSet(entries, label);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("100");
        labels.add("1000");
        labels.add("10000");
        labels.add("100000");
        labels.add("1000000");
        labels.add("10000000");

        LineData lineData = new LineData(dataset);
        lineChart.setData(lineData);
        Description desc = new Description();
        desc.setText("");
        lineChart.setDescription(desc);
        lineChart.invalidate();

    }
}
