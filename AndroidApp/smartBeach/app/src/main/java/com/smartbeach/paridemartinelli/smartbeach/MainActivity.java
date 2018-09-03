package com.smartbeach.paridemartinelli.smartbeach;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.LinearLayout.LayoutParams;
import android.util.Log;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
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
import java.util.List;
import java.util.Calendar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GRAY;

public class MainActivity extends AppCompatActivity implements
        OnChartGestureListener,
        OnChartValueSelectedListener{

    private Context mContext;

    //Sezione home
    private ScrollView homeScrollView;

    private TextView currentTempTextView;
    private TextView currentDateTempTextView;
    private ImageView iconTempImageView;
    private TextView warningTempTextView;
    private Button moreInfoTempButton;
    private Button refreshTempButton;

    private TextView currentHumTextView;
    private TextView currentDateHumTextView;
    private ImageView iconHumImageView;
    private TextView warningHumTextView;
    private Button moreInfoHumButton;
    private Button refreshHumButton;

    private TextView currentUVTextView;
    private TextView currentDateUVTextView;
    private ImageView iconUVImageView;
    private TextView warningUVTextView;
    private Button moreInfoUVButton;
    private Button refreshUVButton;

    private TextView currentTempSeaTextView;
    private TextView currentDateTempSeaTextView;
    private ImageView iconTempSeaImageView;
    private TextView warningTempSeaTextView;
    private Button moreInfoTempSeaButton;
    private Button refreshTempSeaButton;

    private TextView currentTurbTextView;
    private TextView currentDateTurbTextView;
    private ImageView iconTurbImageView;
    private TextView warningTurbTextView;
    private Button moreInfoTurbpButton;
    private Button refreshTurbButton;

    private TextView currentRoughSeaTextView;
    private TextView currentDateRoughSeaTextView;
    private ImageView iconRoughSeaImageView;
    private TextView warningRoughSeaTextView;
    private Button moreInfoRoughSeaButton;
    private Button refreshRoughSeaButton;

    //Sezione suggerimenti
    private ScrollView tipsScrollView;

    //Sezione grafici
    private TabHost dashboardTabHost;

    private LineChart tempLineChart;
    private ImageButton dateTempImageButton;
    private DatePickerDialog datePickerDialogTemp;

    private LineChart humLineChart;
    private ImageButton dateHumImageButton;
    private DatePickerDialog datePickerDialogHum;

    private LineChart UVLineChart;
    private ImageButton dateUVImageButton;
    private DatePickerDialog datePickerDialogUV;

    private LineChart seaTempLineChart;
    private ImageButton dateSeaTempImageButton;
    private DatePickerDialog datePickerDialogSeaTemp;

    private LineChart seaTurbLineChart;
    private ImageButton dateTurbImageButton;
    private DatePickerDialog datePickerDialogTurb;

    private LineChart roughSeaLineChart;
    private ImageButton dateRoughSeaImageButton;
    private DatePickerDialog datePickerDialogRoughSea;

    //Sezione notifiche
    private ScrollView notificationScrollView;
    LinearLayout notificationLinearLayout;

    //BottonNavigationView: menu di bottoni in basso
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homeScrollView.setVisibility(View.VISIBLE);
                    dashboardTabHost.setVisibility(View.INVISIBLE);
                    notificationScrollView.setVisibility(View.INVISIBLE);
                    tipsScrollView.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    homeScrollView.setVisibility(View.INVISIBLE);
                    dashboardTabHost.setVisibility(View.VISIBLE);
                    notificationScrollView.setVisibility(View.INVISIBLE);
                    tipsScrollView.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    homeScrollView.setVisibility(View.INVISIBLE);
                    dashboardTabHost.setVisibility(View.INVISIBLE);
                    notificationScrollView.setVisibility(View.VISIBLE);
                    tipsScrollView.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_tips:
                    homeScrollView.setVisibility(View.INVISIBLE);
                    dashboardTabHost.setVisibility(View.INVISIBLE);
                    notificationScrollView.setVisibility(View.INVISIBLE);
                    tipsScrollView.setVisibility(View.VISIBLE);
            }
            return false;
        }
    };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----------------------Sezione home--------------------------//
        homeScrollView = (ScrollView) findViewById(R.id.homeScrollView);

        //Temperatura
        currentTempTextView = (TextView) findViewById(R.id.currentTemp);
        currentDateTempTextView = (TextView) findViewById(R.id.currentDateTemp);
        iconTempImageView = (ImageView) findViewById(R.id.warningTempIcon);
        warningTempTextView  = (TextView) findViewById(R.id.warningTemperature);

        //TODO: richiamare lo stesso metodo per creare tutti gli altri post relativi agli altri dati
        createHomePost(currentTempTextView, currentDateTempTextView, iconTempImageView, warningTempTextView);

        //TODO: implementare il metodo on click per i bottoni (anche quelli relativi agli altri dati
        moreInfoTempButton = (Button) findViewById(R.id.moreInfoButtonTemp);
        moreInfoTempButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        refreshTempButton = (Button) findViewById(R.id.refreshButtonTemp);

        //Umidità
        currentHumTextView = (TextView) findViewById(R.id.currentHum);
        currentDateHumTextView = (TextView) findViewById(R.id.currentDateHum);
        iconHumImageView = (ImageView) findViewById(R.id.warningHumIcon);
        warningHumTextView = (TextView) findViewById(R.id.warningHumidity);
        //createHomePost(currentHumTextView, currentDateHumTextView, iconHumImageView, warningHumTextView);

        //UV
        currentUVTextView = (TextView) findViewById(R.id.currentUV);
        currentDateUVTextView = (TextView) findViewById(R.id.currentDateUV);
        iconUVImageView = (ImageView) findViewById(R.id.warningUVIcon);
        warningUVTextView = (TextView) findViewById(R.id.warningUV);
        //createHomePost(currentUVTextView, currentDateUVTextView, iconUVImageView, warningUVTextView);

        //Temperatura del mare
        currentTempSeaTextView = (TextView) findViewById(R.id.currentTempSea);
        currentDateTempSeaTextView = (TextView) findViewById(R.id.currentDateTempSea);
        iconTempSeaImageView = (ImageView) findViewById(R.id.warningSeaTempIcon);
        warningTempSeaTextView = (TextView) findViewById(R.id.warningTemperatureSea);
        //createHomePost(currentTempSeaTextView, currentDateTempSeaTextView, iconTempSeaImageView, warningTempSeaTextView);

        //Torbidità del mare
        currentTurbTextView = (TextView) findViewById(R.id.currentTurb);
        currentDateTurbTextView = (TextView) findViewById(R.id.currentDateTurb);
        iconTurbImageView = (ImageView) findViewById(R.id.warningTurbIcon);
        warningTurbTextView = (TextView) findViewById(R.id.warningTurb);
        //createHomePost(currentTurbTextView, currentDateTurbTextView, iconTurbImageView, warningTurbTextView);

        //Mare mosso
        currentRoughSeaTextView = (TextView) findViewById(R.id.currentRoughSea);
        currentDateRoughSeaTextView = (TextView) findViewById(R.id.currentDateRoughSea);
        iconRoughSeaImageView = (ImageView) findViewById(R.id.warningRoughSeaIcon);
        warningRoughSeaTextView = (TextView) findViewById(R.id.warningRoughSea);
        //createHomePost(currentRoughSeaTextView, currentDateRoughSeaTextView, iconRoughSeaImageView, warningRoughSeaTextView);

        //-----------------------------------------------------------//

        //----------------------Sezione suggerimenti-----------------//
        tipsScrollView = (ScrollView) findViewById(R.id.tipsScrollView);
        //-----------------------------------------------------------//

        //----------------------Sezione grafici----------------------//
        dashboardTabHost = (TabHost) findViewById(R.id.dashboardTabHost);
        dashboardTabHost.setup();

        //TAB 1: grafici relativi all'utente
        TabHost.TabSpec spec = dashboardTabHost.newTabSpec("MyData");
        spec.setContent(R.id.tab1);
        spec.setIndicator("MyData");
        dashboardTabHost.addTab(spec);

        //TAB 2: grafici relativi al mare
        spec = dashboardTabHost.newTabSpec("Sea Data");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Sea Data");
        dashboardTabHost.addTab(spec);

        //Grafico della temperatura
        tempLineChart = (LineChart) findViewById(R.id.tempLineChart);

        //TODO: recuperare i valori corretti dal db
        ArrayList<String> x = setXAxisValues();
        ArrayList<Entry> y = setYAxisValues();

        setData(tempLineChart, setXAxisValues(),setYAxisValues(), "Temperatura", Color.RED);
        tempLineChart.setDescription("");

        //TODO: inserire i limiti corretti e aggiungere il metodo per ogni grafico (se lo si richiama due volte aggiungo due limiti quello superiore e quello inferiore
        setLimit(120f, "Tempertura troppo elevata", tempLineChart, 220f, -50f);

        //TODO: pensare se inserire due calendari from-to
        dateTempImageButton = (ImageButton) findViewById(R.id.dateTempImageButton);
        dateTempImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialogTemp = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //TODO: recuperare i valori, fare la query e modificare il grafico
                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                ArrayList<String> x = setXAxisValues();
                                ArrayList<Entry> y = setYAxisValues();
                                setData(tempLineChart, setXAxisValues(),setYAxisValues(), "Temperatura 2", Color.RED);
                                tempLineChart.setDescription("");
                                setLimit(120f, "Tempertura troppo elevata", tempLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogTemp.show();
            }
        });

        //Grafico dell'umidità
        humLineChart = (LineChart) findViewById(R.id.humLineChart);
        setData(humLineChart, setXAxisValues(),setYAxisValues(), "Umidità", Color.RED);
        humLineChart.setDescription("");
        dateHumImageButton = (ImageButton) findViewById(R.id.dateHumImageButton);
        dateHumImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialogHum = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //TODO: recuperare i valori, fare la query e modificare il grafico
                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                ArrayList<String> x = setXAxisValues();
                                ArrayList<Entry> y = setYAxisValues();
                                setData(humLineChart, setXAxisValues(),setYAxisValues(), "Temperatura 2", Color.RED);
                                humLineChart.setDescription("");
                                setLimit(120f, "Tempertura troppo elevata", humLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogHum.show();
            }
        });

        //Grafico dei raggi UV
        UVLineChart = (LineChart) findViewById(R.id.UVLineChart);
        setData(UVLineChart, setXAxisValues(), setYAxisValues(), "Raggi UV", Color.RED);
        UVLineChart.setDescription("");
        dateUVImageButton = (ImageButton) findViewById(R.id.dateUVImageButton);
        dateUVImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialogUV = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //TODO: recuperare i valori, fare la query e modificare il grafico
                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                ArrayList<String> x = setXAxisValues();
                                ArrayList<Entry> y = setYAxisValues();
                                setData(UVLineChart, setXAxisValues(),setYAxisValues(), "Temperatura 2", Color.RED);
                                UVLineChart.setDescription("");
                                setLimit(120f, "Tempertura troppo elevata", UVLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogUV.show();
            }
        });

        //Grafico della temperatura del mare
        seaTempLineChart = (LineChart) findViewById(R.id.seaTempLineChart);
        setData(seaTempLineChart, setXAxisValues(), setYAxisValues(), "Temperatura del mare", Color.BLUE);
        seaTempLineChart.setDescription("");
        dateSeaTempImageButton = (ImageButton) findViewById(R.id.dateSeaTempImageButton);
        dateSeaTempImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialogSeaTemp = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //TODO: recuperare i valori, fare la query e modificare il grafico
                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                ArrayList<String> x = setXAxisValues();
                                ArrayList<Entry> y = setYAxisValues();
                                setData(seaTempLineChart, setXAxisValues(),setYAxisValues(), "Temperatura 2", Color.RED);
                                seaTempLineChart.setDescription("");
                                setLimit(120f, "Tempertura troppo elevata", seaTempLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogSeaTemp.show();
            }
        });

        //Grafico della torbidità del mare
        seaTurbLineChart = (LineChart) findViewById(R.id.seaTurbLineChart);
        setData(seaTurbLineChart, setXAxisValues(), setYAxisValues(), "Torbidità del mare", Color.BLUE);
        seaTurbLineChart.setDescription("");
        dateTurbImageButton = (ImageButton) findViewById(R.id.dateTurbImageButton);
        dateTurbImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialogTurb = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //TODO: recuperare i valori, fare la query e modificare il grafico
                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                ArrayList<String> x = setXAxisValues();
                                ArrayList<Entry> y = setYAxisValues();
                                setData(seaTurbLineChart, setXAxisValues(),setYAxisValues(), "Temperatura 2", Color.RED);
                                seaTurbLineChart.setDescription("");
                                setLimit(120f, "Tempertura troppo elevata", seaTurbLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogTurb.show();
            }
        });

        //Grafico del movimento del mare
        roughSeaLineChart = (LineChart) findViewById(R.id.roughSeaLineChart);
        setData(roughSeaLineChart, setXAxisValues(), setYAxisValues(), "Movimento del mare", Color.BLUE);
        roughSeaLineChart.setDescription("");
        dateRoughSeaImageButton = (ImageButton) findViewById(R.id.dateRoughSeaImageButton);
        dateRoughSeaImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialogRoughSea = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //TODO: recuperare i valori, fare la query e modificare il grafico
                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                ArrayList<String> x = setXAxisValues();
                                ArrayList<Entry> y = setYAxisValues();
                                setData(roughSeaLineChart, setXAxisValues(),setYAxisValues(), "Temperatura 2", Color.RED);
                                roughSeaLineChart.setDescription("");
                                setLimit(120f, "Tempertura troppo elevata", roughSeaLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogRoughSea.show();
            }
        });

        //-----------------------------------------------------------//

        //----------------------Sezione notifiche--------------------//
        notificationScrollView = (ScrollView) findViewById(R.id.notificationScrollView);
        notificationLinearLayout = (LinearLayout) findViewById(R.id.notificationLinearLayout);
        mContext = getApplicationContext();

        //TODO:Cliclare per il numero di notifiche e creare la notifica con i valori giusti
        for(int i = 0 ; i < 20; i ++){

            String date = "31-08-18 ore 17:36";
            //TODO: creare icona anche per le notifiche "positive" e per quelle derivanti dal beacon
            String text = "Attenzione: Temperatura troppo elevata";
            String tipoNot = "Temperatura";
            Integer icon = R.drawable.ic_report_problem_black_24dp;
            createNotification(date, text, tipoNot, icon);
        }

        //-----------------------------------------------------------//

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    //------------------------------------------------METODO PER LA CREAZIONE DEI POST NELLA HOME------------------------------------------------//
    private void createHomePost(TextView data, TextView date, ImageView icon, TextView warning) {
        //TODO:recuperare i valori reali dal db
        String currentTemp = "Temperatura 35°C";
        String currentDate = "Data: 1-09-18 16:38";
        /* TODO: Per settare l'icona e il testo si dovrà mettere un in if e controlalre il valore della temperatura,
            se la temperatura supera una certa soglia comparirà una determinata icona e un determinato messaggio;
            FARE STESSA COSA PER TUTTI GLI ALTRI VALORI
         */
        Integer iconTemp = R.drawable.ic_report_problem_black_24dp;
        String warningTemp = "Temperatura abbastanza elevata, si consiglia di rinfrescarsi e di non stare troppo tempo al sole!!";

        data.setText(currentTemp);
        date.setText(currentDate);
        icon.setImageResource(iconTemp);
        warning.setText(warningTemp);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------//

    //------------------------------------------------METODI PER LA CREAZIONE DEL GRAFICO----------------------------------------------------//

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
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            tempLineChart.highlightValues(null);
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
        Log.i("LOWHIGH", "low: " + tempLineChart.getLowestVisibleXIndex()
                + ", high: " + tempLineChart.getHighestVisibleXIndex());

        Log.i("MIN MAX", "xmin: " + tempLineChart.getXChartMin()
                + ", xmax: " + tempLineChart.getXChartMax()
                + ", ymin: " + tempLineChart.getYChartMin()
                + ", ymax: " + tempLineChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    //TODO: ora ci sono dei dati di prova, sostituire con i valori recuperati dal db
    //Asse x
    private ArrayList<String> setXAxisValues(){
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("10");
        xVals.add("20");
        xVals.add("30");
        xVals.add("30.5");
        xVals.add("40");

        return xVals;
    }
    //Asse y
    private ArrayList<Entry> setYAxisValues(){
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(60, 0));
        yVals.add(new Entry(48, 1));
        yVals.add(new Entry(70.5f, 2));
        yVals.add(new Entry(100, 3));
        yVals.add(new Entry(180.9f, 4));

        return yVals;
    }

    //Metodo per popolare i grafici, vuole in input il lineChart (il grafico da popolare, e i parametri mettere sulla x (x) e sulla y (y)
    private void setData(LineChart lineChart, ArrayList<String> x, ArrayList<Entry> y, String label, Integer color) {

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
        set1.setColor(BLACK);
        set1.setCircleColor(BLACK);
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

    //Metodo per segnare un limite sul grafico (soglia di temperatura troppo elevata)
    /** parametri
     * limitVal: valore massimo o minimo per settare la soglia
     * label: descrizione della soglia
     * lineChart grafico in cui inserire la soglia
     * max: valore massimo sull'asse delle y
     * min valore minimo sull'asse delle y
     **/
    private void setLimit(Float limitVal, String label, LineChart lineChart, Float max, Float min){
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

    //----------------------------------------------------------------------------------------------------------------------------------------//

    //------------------------------------------------METOD PER LA CREAZIONE DELLE NOTIFICHE------------------------------------------------//

    private void createNotification(String date, String text, String type, Integer icon) {
        // Initialize a new CardView
        CardView card = new CardView(mContext);

        // Set the CardView layoutParams
        LayoutParams cardParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(cardParams);

        //Set CardView Margins
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        layoutParams.setMargins(35, 35, 35, 0);
        card.requestLayout();

        //Set CardView corner radius
        card.setRadius(9);

        //Set cardView content padding
        card.setContentPadding(15, 15, 15, 15);

        //Set a background color for CardView
        card.setCardBackgroundColor(Color.parseColor("#B0BEC5"));

        //Set the CardView maximum elevation
        card.setMaxCardElevation(15);

        //Set CardView elevation
        card.setCardElevation(9);


        //Notifica
        LinearLayout ln = new LinearLayout(mContext);
        ln.setOrientation(LinearLayout.VERTICAL);
        LayoutParams lnParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        ln.setLayoutParams(lnParams);

        //Sezione alta della notifica
        LinearLayout lnTop = new LinearLayout(mContext);
        lnTop.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams lnTomParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        lnTop.setLayoutParams(lnTomParams);

        //Tipo della notifica
        TextView typeTV = new TextView(mContext);
        LayoutParams typeTVParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        typeTV.setLayoutParams(typeTVParams);
        typeTV.setText(type);
        typeTV.setTextColor(GRAY);
        typeTV.getLayoutParams();

        //Data e ora della notifica
        TextView dateTV = new TextView(mContext);
        LayoutParams dateTVParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        dateTV.setLayoutParams(dateTVParams);
        dateTV.setText(date);
        dateTV.setTextColor(GRAY);
        dateTV.getLayoutParams();

        lnTop.addView(typeTV);
        lnTop.addView(dateTV);

        //Sezione centrale della notifica
        LinearLayout lnCentral = new LinearLayout(mContext);
        lnCentral.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams lnCentralParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        lnCentral.setLayoutParams(lnCentralParams);

        //Icona della notifica
        ImageView im = new ImageView(mContext);
        LayoutParams imParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        im.setLayoutParams(imParams);
        im.setImageResource(icon);

        //Testo della notifica
        TextView textTV = new TextView(mContext);
        LayoutParams textTVParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        textTV.setLayoutParams(textTVParams);
        textTV.setText(text);
        textTV.setTextColor(BLACK);

        textTV.getLayoutParams();
        textTV.requestLayout();

        lnCentral.addView(im);
        lnCentral.addView(textTV);

        //TODO: migliorare la grafica on spazi e colori

        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) im.getLayoutParams();
        margin.setMargins(20, 0, 0, 0);
        im.requestLayout();
        margin.setMargins(20, 0, 0, 0);
        margin.setMargins(20, 0, 0, 0);
        dateTV.requestLayout();

        ln.addView(lnTop);
        ln.addView(lnCentral);

        //TODO: scegliere se inserire il bottone di cancellazione delle notifiche o se cancellarle automaticamente dopo tot tempo dal db

        //Add ellement in the correct position
        card.addView(ln);
        notificationLinearLayout.addView(card);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------//
}


