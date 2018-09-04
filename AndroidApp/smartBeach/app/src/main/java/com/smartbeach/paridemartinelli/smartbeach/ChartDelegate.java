package com.smartbeach.paridemartinelli.smartbeach;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class ChartDelegate implements OnChartGestureListener, OnChartValueSelectedListener {
    private final MainActivity mainActivity;

    public ChartDelegate(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

    }

    //TODO: verificre se ci servono e gestire in modo da non doverne creare uno per ogni grafico (rifattorizzare come per il setData)
    @Override
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            mainActivity.getTempLineChart().highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: "
                + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mainActivity.getTempLineChart().getLowestVisibleXIndex()
                + ", high: " + mainActivity.getTempLineChart().getHighestVisibleXIndex());

        Log.i("MIN MAX", "xmin: " + mainActivity.getTempLineChart().getXChartMin()
                + ", xmax: " + mainActivity.getTempLineChart().getXChartMax()
                + ", ymin: " + mainActivity.getTempLineChart().getYChartMin()
                + ", ymax: " + mainActivity.getTempLineChart().getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }//TODO: ora ci sono dei dati di prova, sostituire con i valori recuperati dal db


    //Asse x
    ArrayList<String> setXAxisValues() {

        ArrayList<String> xVals = new ArrayList<String>();
        /*xVals.add("10");
        xVals.add("20");
        xVals.add("30");
        xVals.add("30.5");
        xVals.add("40");*/

        return xVals;
    }//Asse y

    ArrayList<Entry> setYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        /*yVals.add(new Entry(60, 0));
        yVals.add(new Entry(48, 1));
        yVals.add(new Entry(70.5f, 2));
        yVals.add(new Entry(100, 3));
        yVals.add(new Entry(180.9f, 4));*/

        return yVals;
    }//Metodo per popolare i grafici, vuole in input il lineChart (il grafico da popolare, e i parametri mettere sulla x (x) e sulla y (y)

    void setData(LineChart lineChart, ArrayList<String> x, ArrayList<Entry> y, String label, Integer color) {

        ArrayList<String> xVals = x;
        ArrayList<Entry> yVals = y;

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, label);
        set1.setFillAlpha(110);
        set1.setFillColor(color);

        // set the line to be drawn like this "- - - - - -"
        // set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        lineChart.setData(data);

    }

    /**
     * parametri
     * limitVal: valore massimo o minimo per settare la soglia
     * label: descrizione della soglia
     * lineChart grafico in cui inserire la soglia
     * max: valore massimo sull'asse delle y
     * min valore minimo sull'asse delle y
     **/
    void setLimit(Float limitVal, String label, LineChart lineChart, Float max, Float min) {
        LimitLine limit = new LimitLine(limitVal, label);
        limit.setLineWidth(4f);
        limit.enableDashedLine(10f, 10f, 0f);
        limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limit.setTextSize(10f);

        YAxis leftAxis = lineChart.getAxisLeft();

        // reset all limit lines to avoid overlapping lines
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(limit);
        leftAxis.setAxisMaxValue(max);
        leftAxis.setAxisMinValue(min);

        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);
    }

    public void dhtData(String user, String date) {
        //TODO: debug request
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        String url = MainActivity.URL + "/dht?user=PM12&date=2018-08-25";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("PINO",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mTextView.setText("That didn't work!");
                Log.e("PINO-ERROR",error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}