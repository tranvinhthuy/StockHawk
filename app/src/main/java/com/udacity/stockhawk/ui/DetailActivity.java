package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    public static final String SYMBOL_INTENT_KEY = "sik";

    private ArrayList<Integer> listDates = new ArrayList<>();
    private ArrayList<Float> listStockValues = new ArrayList<>();

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mChart = (LineChart) findViewById(R.id.chart);


        Intent i = getIntent();
        if(i==null)
            return;

        String symbol = i.getStringExtra(SYMBOL_INTENT_KEY);
        if(symbol != null)
            new GetDataTask().execute(new String[]{symbol});
    }
    private class GetDataTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Cursor cursor = getContentResolver().query
                    (Contract.Quote.URI, new String[]{Contract.Quote.COLUMN_HISTORY},
                            Contract.Quote.COLUMN_SYMBOL+"=?", params, null);
            int colHistoryIndex = cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY);
            String[] history = {};
            while(cursor.moveToNext()) {
                history = cursor.getString(colHistoryIndex).split("\n");
            }
            for(int i = 0; i < history.length; i++) {
                String[] entry = history[i].split(",");
                listDates.add(i);
                listStockValues.add(Float.parseFloat(entry[1]));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateChartData();
        }
    }

    private void updateChartData() {
        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < listDates.size(); i++) {
            values.add(new Entry((float)listDates.get(i), (float)listStockValues.get(i)));
            if(i==99)
                break;
        }

        LineDataSet lineDataSet = new LineDataSet(values, "Stock graph");

        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(1.75f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setCircleHoleRadius(2.5f);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setHighLightColor(Color.BLUE);
        LineData lineData = new LineData(lineDataSet);
        mChart.setTouchEnabled(false);
        mChart.getXAxis().setTextColor(R.color.colorPrimaryDark);
        mChart.getAxisLeft().setTextColor(R.color.colorPrimaryDark);
        mChart.setData(lineData);
        mChart.invalidate();
    }
}
