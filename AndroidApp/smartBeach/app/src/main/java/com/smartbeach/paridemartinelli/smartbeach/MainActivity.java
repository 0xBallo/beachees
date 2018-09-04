package com.smartbeach.paridemartinelli.smartbeach;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{

    private final NotificationDelegate notificationDelegate = new NotificationDelegate();
    private final ChartDelegate chartDelegate = new ChartDelegate(this);
    private final HomeDelegate homeDelegate = new HomeDelegate();
    private Context mContext;
    public static final String URL = "http://bf57077b.ngrok.io/api";
    RequestQueue queue;

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

        Cache cache = new DiskBasedCache(getCacheDir(), 1024*1024);
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();

        //----------------------Sezione home--------------------------//
        homeScrollView = (ScrollView) findViewById(R.id.homeScrollView);

        //Temperatura
        currentTempTextView = (TextView) findViewById(R.id.currentTemp);
        currentDateTempTextView = (TextView) findViewById(R.id.currentDateTemp);
        iconTempImageView = (ImageView) findViewById(R.id.warningTempIcon);
        warningTempTextView  = (TextView) findViewById(R.id.warningTemperature);

        //TODO: richiamare lo stesso metodo per creare tutti gli altri post relativi agli altri dati
        homeDelegate.createHomePost(currentTempTextView, currentDateTempTextView, iconTempImageView, warningTempTextView);

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
        chartDelegate.setData(tempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura", Color.RED);
        tempLineChart.setDescription("");

        //TODO: inserire i limiti corretti e aggiungere il metodo per ogni grafico (se lo si richiama due volte aggiungo due limiti quello superiore e quello inferiore
        chartDelegate.setLimit(120f, "Tempertura troppo elevata", tempLineChart, 220f, -50f);

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
                                ArrayList<String> x = chartDelegate.setXAxisValues();
                                ArrayList<Entry> y = chartDelegate.setYAxisValues();
                                chartDelegate.setData(tempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura 2", Color.RED);
                                tempLineChart.setDescription("");
                                chartDelegate.setLimit(120f, "Tempertura troppo elevata", tempLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogTemp.show();
            }
        });

        //Grafico dell'umidità
        humLineChart = (LineChart) findViewById(R.id.humLineChart);
        chartDelegate.setData(humLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Umidità", Color.RED);
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
                                ArrayList<String> x = chartDelegate.setXAxisValues();
                                ArrayList<Entry> y = chartDelegate.setYAxisValues();
                                chartDelegate.setData(humLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura 2", Color.RED);
                                humLineChart.setDescription("");
                                chartDelegate.setLimit(120f, "Tempertura troppo elevata", humLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogHum.show();
            }
        });

        //Grafico dei raggi UV
        UVLineChart = (LineChart) findViewById(R.id.UVLineChart);
        chartDelegate.setData(UVLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Raggi UV", Color.RED);
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
                                ArrayList<String> x = chartDelegate.setXAxisValues();
                                ArrayList<Entry> y = chartDelegate.setYAxisValues();
                                chartDelegate.setData(UVLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura 2", Color.RED);
                                UVLineChart.setDescription("");
                                chartDelegate.setLimit(120f, "Tempertura troppo elevata", UVLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogUV.show();
            }
        });

        //Grafico della temperatura del mare
        seaTempLineChart = (LineChart) findViewById(R.id.seaTempLineChart);
        chartDelegate.setData(seaTempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura del mare", Color.BLUE);
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
                                ArrayList<String> x = chartDelegate.setXAxisValues();
                                ArrayList<Entry> y = chartDelegate.setYAxisValues();
                                chartDelegate.setData(seaTempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura 2", Color.RED);
                                seaTempLineChart.setDescription("");
                                chartDelegate.setLimit(120f, "Tempertura troppo elevata", seaTempLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogSeaTemp.show();
            }
        });

        //Grafico della torbidità del mare
        seaTurbLineChart = (LineChart) findViewById(R.id.seaTurbLineChart);
        chartDelegate.setData(seaTurbLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Torbidità del mare", Color.BLUE);
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
                                ArrayList<String> x = chartDelegate.setXAxisValues();
                                ArrayList<Entry> y = chartDelegate.setYAxisValues();
                                chartDelegate.setData(seaTurbLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura 2", Color.RED);
                                seaTurbLineChart.setDescription("");
                                chartDelegate.setLimit(120f, "Tempertura troppo elevata", seaTurbLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogTurb.show();
            }
        });

        //Grafico del movimento del mare
        roughSeaLineChart = (LineChart) findViewById(R.id.roughSeaLineChart);
        chartDelegate.setData(roughSeaLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Movimento del mare", Color.BLUE);
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
                                ArrayList<String> x = chartDelegate.setXAxisValues();
                                ArrayList<Entry> y = chartDelegate.setYAxisValues();
                                chartDelegate.setData(roughSeaLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura 2", Color.RED);
                                roughSeaLineChart.setDescription("");
                                chartDelegate.setLimit(120f, "Tempertura troppo elevata", roughSeaLineChart, 220f, -50f);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogRoughSea.show();
            }
        });

        //Aggiornamento dati dal server
        //TODO:fare per tutti i grafici
        //Temperatura
        String user = "PM12";
        String dateUrl = "2018-08-25";
        String dhtURL = URL + "/dht?user=" + user + "&date=" + dateUrl;
        JsonObjectRequest requestDht = new JsonObjectRequest(Request.Method.GET, dhtURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: aggiornare i dati del grafico
                try {
                    Log.i("PINO", response.getJSONArray("data").getJSONObject(0).getString("temperature"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestDht);

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
            notificationDelegate.createNotification(mContext, notificationLinearLayout, date, text, tipoNot, icon);
        }

        //-----------------------------------------------------------//

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * Getter and Setter
     */

    public LineChart getTempLineChart() {
        return tempLineChart;
    }
}


