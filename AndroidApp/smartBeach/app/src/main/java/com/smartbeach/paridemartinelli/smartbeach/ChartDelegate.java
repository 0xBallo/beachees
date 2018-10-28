package com.smartbeach.paridemartinelli.smartbeach;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChartDelegate implements OnChartGestureListener, OnChartValueSelectedListener {
    private final MainActivity mainActivity;

    public ChartDelegate(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

    }
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

        /*Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            mainActivity.getTempLineChart().highlightValues(null);*/
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        //Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        //Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
       // Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        //Log.i("Fling", "Chart flinged. VeloX: "+ velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        //Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        //Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        //Log.i("Entry selected", e.toString());
        //Log.i("LOWHIGH", "low: " + mainActivity.getTempLineChart().getLowestVisibleXIndex() + ", high: " + mainActivity.getTempLineChart().getHighestVisibleXIndex());
        //Log.i("MIN MAX", "xmin: " + mainActivity.getTempLineChart().getXChartMin() + ", xmax: " + mainActivity.getTempLineChart().getXChartMax() + ", ymin: " + mainActivity.getTempLineChart().getYChartMin() + ", ymax: " + mainActivity.getTempLineChart().getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        //Log.i("Nothing selected", "Nothing selected.");
    }

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
       /* yVals.add(new Entry(60, 0));
        yVals.add(new Entry(48, 1));
        yVals.add(new Entry(70.5f, 2));
        yVals.add(new Entry(100, 3));
        yVals.add(new Entry(180.9f, 4));*/

        return yVals;
    }

    //Metodo per popolare i grafici, vuole in input il lineChart (il grafico da popolare, e i parametri mettere sulla x (x) e sulla y (y)
    void setData(LineChart lineChart, ArrayList<String> x, ArrayList<Entry> y, String label, Integer color) {

        // create a dataset and give it a type
        LineDataSet set = new LineDataSet(y, label);
        set.setFillAlpha(110);
        set.setFillColor(color);
        set.setColor(Color.BLACK);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        // add the datasets
        dataSets.add(set);

        // create a data object with the datasets
        LineData data = new LineData(x, dataSets);

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
        //leftAxis.setAxisMaxValue(max);
        //leftAxis.setAxisMinValue(min);

        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        lineChart.getAxisRight().setEnabled(false);
    }

    //metodo per creare il grafico
    LineChart createChart(JSONObject response, String nameX, String label, String limitLabel, float limitVal, float max, float min, LineChart lineChart, Integer color) {
        try {
            ArrayList<Entry> y = new ArrayList<Entry>();
            ArrayList<String> x = new ArrayList<String>();

            for (int i = 0; i < response.getJSONArray("data").length(); i++) {

                float yVal = Float.parseFloat(response.getJSONArray("data").getJSONObject(i).getString(nameX));
                String xVal = response.getJSONArray("data").getJSONObject(i).getString("hour");
                y.add(new Entry(yVal, i));
                x.add(xVal);

            }

            setData(lineChart, x, y, label, color);
            setLimit(limitVal, limitLabel, lineChart, max, min);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lineChart;
    }
}