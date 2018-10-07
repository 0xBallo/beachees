package com.smartbeach.paridemartinelli.smartbeach;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import com.smartbeach.paridemartinelli.smartbeach.services.MyFirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;

import static com.smartbeach.paridemartinelli.smartbeach.R.color.darkYellow;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_COARSE_LOCATION = 2;
    public ScrollView notificationScrollView;
    public LinearLayout notificationLinearLayout;
    private final NotificationDelegate notificationDelegate = new NotificationDelegate();
    private final ChartDelegate chartDelegate = new ChartDelegate(this);
    public static Context mContext;
    public static final String URL = "http://6726dda0.ngrok.io/api";
    //TODO: recuperare username da login
    public static String user = "";
    public static String token;
    public static RequestQueue queue;
    BluetoothAdapter mBluetoothAdapter;

    //Sezione home
    private ScrollView homeScrollView;
    private ImageButton moreInfoBeachButton;
    private ImageButton moreInfoSeaButton;

    private TextView tempNowTextView;
    private TextView humNowTextView;
    private TextView uvNowTextView;

    private TextView seaTempNowTextView;
    private TextView seaTurbNowTextView;
    private TextView seaRoughTextView;

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
    
    int yellow = Color.parseColor("#FBC02D");
    int blue = Color.parseColor("#29B6F6");


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
                    return true;
                case R.id.navigation_dashboard:
                    homeScrollView.setVisibility(View.INVISIBLE);
                    dashboardTabHost.setVisibility(View.VISIBLE);
                    notificationScrollView.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    homeScrollView.setVisibility(View.INVISIBLE);
                    dashboardTabHost.setVisibility(View.INVISIBLE);
                    notificationScrollView.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();

        if (MainActivity.token != null && !MainActivity.token.isEmpty())
        {
            MyFirebaseMessagingService.sendRegistrationToServer(MainActivity.user, MainActivity.token);
        }


        //TODO: COSA MANCA:
        // 1. SISTEMARE LA HOME CHE NON MI PIACE (CHIEDERE CONSIGLIO A MATTIA)
        // 2. SISTEMARE LA GRAFICA DEL DIALOG NELLA HOME
        // 3. SISTEMARE LA GRAFICA DELLA CANCELLAZIONE DELLE NOTIFICHE
        // 4. FAR FUNZIONARE NOTIDICHE PUSH
        // 5. NOTIFCHE BEACON


        //---------------------Sezione beacon-----------------------------//
        //TODO: commentato per effettuare test con simulatore (scommentare)
        /*mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Controllo se il dispositivo supporta il bluethooth
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Bluetooth Non supportato", Toast.LENGTH_SHORT).show();
            //    finish();
        }

        //Controllo se il dispositivo ha il bluetooth acceso, se non è acceso gli chiedo di accenderlo
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_COARSE_LOCATION);
        }

        //Chiedo all'utente i permessi per la localizzazione e poi faccio partire il processo per la ricerca dei dispositivi bluetooth
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        } else {
            proceedDiscovery();
        }*/

        //-------------------------------------------------------------//

        //----------------------Sezione home--------------------------//
        homeScrollView = (ScrollView) findViewById(R.id.homeScrollView);

        //bottone info spiaggia (secondo me non serve)
        //moreInfoBeachButton = (ImageButton) findViewById(R.id.moreInfoBeachButton);

        //Temperatura e umidità
        tempNowTextView = (TextView) findViewById(R.id.tempNowTextView);
        humNowTextView = (TextView) findViewById(R.id.humNowTextView);
        String dhtNowURL = URL + "/dht/now?user=" + user;
        JsonObjectRequest requestDhtNow = new JsonObjectRequest(Request.Method.GET, dhtNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    float tempNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("temperature"));
                    int tempNowInt = Math.round(tempNowFloat);
                    String tempNow = String.valueOf(tempNowInt) + "°C";
                    float humNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("humidity"));
                    int humNowInt = Math.round(humNowFloat);
                    String humNow = String.valueOf(humNowInt) + "%";
                    tempNowTextView.setText(tempNow);
                    if (tempNowInt >= 35){
                        tempNowTextView.setTextColor(Color.RED);
                    }else{
                        tempNowTextView.setTextColor(Color.parseColor("#FBC02D"));
                    }
                    humNowTextView.setText(humNow);
                    if (humNowInt >= 80){
                        humNowTextView.setTextColor(Color.RED);
                    }else{
                        humNowTextView.setTextColor(Color.parseColor("#FBC02D"));
                    }
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
        queue.add(requestDhtNow);
        tempNowTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Temperatura");
                alertDialog.setMessage("Attenzione: se la temperatura supera i 35°C vuol dire che ha superato la soglia massima");
                alertDialog.setButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        humNowTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Umidità");
                alertDialog.setMessage("Attenzione: se l'umidità è maggiore dell'80% vuol dire che ha superato la soglia massima");
                alertDialog.setButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        //Raggi UV
        uvNowTextView = (TextView) findViewById(R.id.uvNowTextView);
        final String uvNowURL = URL + "/uva/now?user=" + user;
        JsonObjectRequest requestUvNow = new JsonObjectRequest(Request.Method.GET, uvNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    float uvNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("uva"));
                    int uvNowInt = Math.round(uvNowFloat);
                    String uvNow = String.valueOf(uvNowInt);
                    uvNowTextView.setText(uvNow);
                    if(uvNowFloat >= 12){
                        uvNowTextView.setTextColor(Color.RED);
                    }else{
                        uvNowTextView.setTextColor(Color.parseColor("#FBC02D"));
                    }
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
        queue.add(requestUvNow);
        uvNowTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Raggi UV");
                alertDialog.setMessage("Attenzione: se la il valore è maggiore di 12 vuol dire che ha superato la soglia massima");
                alertDialog.setButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        //bottone info mare (secondo me non serve)
        //moreInfoSeaButton = (ImageButton) findViewById(R.id.moreInfoSeaButton);

        //temperatura del mare
        seaTempNowTextView = (TextView) findViewById(R.id.tempSeaNowTextView);
        String seaTempNowURL = URL + "/sea/temp/now?user=" + user;
        JsonObjectRequest requestSeaTempNow = new JsonObjectRequest(Request.Method.GET, seaTempNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    float seaTempNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("watertemp"));
                    int seaTempNowInt = Math.round(seaTempNowFloat);
                    String seaTempNow = String.valueOf(seaTempNowInt) + "°C";
                    seaTempNowTextView.setText(seaTempNow);
                    if(seaTempNowInt >= 27 || seaTempNowInt <= 23){
                        seaTempNowTextView.setTextColor(Color.RED);
                    }else{
                        seaTempNowTextView.setTextColor(Color.parseColor("#29B6F6"));
                    }
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
        queue.add(requestSeaTempNow);
        seaTempNowTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Temperatura del mare");
                alertDialog.setMessage("Attenzione: se la la temeratura del mare supera i 27°C vuol dire che la temperatura del mare è troppo elevata," +
                        "se invece è minore di 23°C vuol dire che il mare è troppo freddo");
                alertDialog.setButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        //tordibità
        seaTurbNowTextView = (TextView) findViewById(R.id.turbSeaNowTextView);
        String seaTurbNowURL = URL + "/sea/turbidity/now?user=" + user;
        JsonObjectRequest requestSeaTurbNow = new JsonObjectRequest(Request.Method.GET, seaTurbNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    float seaTurbNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("turbidity"));
                    int seaTurbNowInt = Math.round(seaTurbNowFloat);
                    String seaTurbNow = String.valueOf(seaTurbNowInt);
                    seaTurbNowTextView.setText(seaTurbNow);
                    if(seaTurbNowInt >= 35){
                        seaTurbNowTextView.setTextColor(Color.RED);
                    }else{
                        seaTurbNowTextView.setTextColor(Color.parseColor("#29B6F6"));
                    }
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
        queue.add(requestSeaTurbNow);
        seaTurbNowTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Torbidità del mare");
                alertDialog.setMessage("Attenzione: se il valore di torbidità supera i 35 vuol dire che il mare è molto sporco");
                alertDialog.setButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        //mare mosso
        seaRoughTextView = (TextView) findViewById(R.id.roughSeaNowTextView);
        String seaRoughNowURL = URL + "/sea/waves/now?user=" + user;
        JsonObjectRequest requestSeaRoughNow = new JsonObjectRequest(Request.Method.GET, seaRoughNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    float seaWavesNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("waves"));
                    int seaWavesNowInt = Math.round(seaWavesNowFloat);
                    String seaWavesNow = String.valueOf(seaWavesNowInt);
                    seaRoughTextView.setText(seaWavesNow);
                    if(seaWavesNowInt >= 3 ){
                        seaRoughTextView.setTextColor(Color.RED);
                    }else{
                        seaRoughTextView.setTextColor(Color.parseColor("#29B6F6"));
                    }

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
        queue.add(requestSeaRoughNow);
        seaRoughTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Mare mosso");
                alertDialog.setMessage("Attenzione: se il valore è 3 vuol dire che il mare è molto mosso, BANDIERA ROSSA");
                alertDialog.setButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });


        //-----------------------------------------------------------//

        //----------------------Sezione grafici----------------------//
        dashboardTabHost = (TabHost) findViewById(R.id.dashboardTabHost);
        dashboardTabHost.setup();

        //TAB 1: grafici relativi all'utente
        TabHost.TabSpec spec = dashboardTabHost.newTabSpec("Spiaggia");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Spiaggia");
        dashboardTabHost.addTab(spec);

        //TAB 2: grafici relativi al mare
        spec = dashboardTabHost.newTabSpec("Mare");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Mare");
        dashboardTabHost.addTab(spec);

        //Grafico della temperatura
        tempLineChart = (LineChart) findViewById(R.id.tempLineChart);
        //chartDelegate.setData(tempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura", yellow);
        tempLineChart.setDescription("");
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

                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                //String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                String tempURL = URL + "/dht?user=" + user + "&date=" + date;
                                final String label = "Temperatura " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                JsonObjectRequest requestTemp = new JsonObjectRequest(Request.Method.GET, tempURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "temperature", label, "Temperatura troppo elevata", 35f, 45f, 15f, tempLineChart, yellow );
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //TODO: stampare l'errore
                                    }
                                });
                                queue.add(requestTemp);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogTemp.show();
            }
        });

        //Grafico dell'umidità
        humLineChart = (LineChart) findViewById(R.id.humLineChart);
        //chartDelegate.setData(humLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Umidità", yellow);
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

                                //String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                String humURL = URL + "/dht?user=" + user + "&date=" + date;
                                final String label = "Umidità " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                JsonObjectRequest requestHum = new JsonObjectRequest(Request.Method.GET, humURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "humidity", label, "Umidità troppo elevata", 80f, 90f, 70f, humLineChart, yellow );
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //TODO: stampare l'errore
                                    }
                                });
                                queue.add(requestHum);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogHum.show();
            }
        });

        //Grafico dei raggi UV
        UVLineChart = (LineChart) findViewById(R.id.UVLineChart);
        //chartDelegate.setData(UVLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Raggi UV", yellow);
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

                                //String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                String uvaURL = URL + "/uva?user=" + user + "&date=" + date;
                                final String label = "Raggi UV " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                JsonObjectRequest requestUVA = new JsonObjectRequest(Request.Method.GET, uvaURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "uva", label, "Soglia limite consigliato", 12f, 15f, 0f , UVLineChart, yellow);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //TODO: stampare l'errore
                                    }
                                });
                                queue.add(requestUVA);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogUV.show();
            }
        });

        //Grafico della temperatura del mare
        seaTempLineChart = (LineChart) findViewById(R.id.seaTempLineChart);
        //chartDelegate.setData(seaTempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura del mare", blue);
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

                                //String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                String seaTempURL = URL + "/sea/temp?user=" + user + "&date=" + date;
                                final String label = " Temperatura del mare " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                JsonObjectRequest requestSeaTemp = new JsonObjectRequest(Request.Method.GET, seaTempURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "watertemp", label, "Mare troppo freddo", 23f, 30f, 21f, seaTempLineChart, blue );
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //TODO: stampare l'errore
                                    }
                                });
                                queue.add(requestSeaTemp);


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogSeaTemp.show();
            }
        });

        //Grafico della torbidità del mare
        seaTurbLineChart = (LineChart) findViewById(R.id.seaTurbLineChart);
        //chartDelegate.setData(seaTurbLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Torbidità del mare", blue);
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

                                //String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                String seaTurbURL = URL + "/sea/turbidity?user=" + user + "&date=" + date;
                                final String label = "Torbidità del mare " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                JsonObjectRequest requestSeaTurb = new JsonObjectRequest(Request.Method.GET, seaTurbURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "turbidity", label, "Torbidità elevata", 35f, 46.50f, 20f, seaTurbLineChart, blue );
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //TODO: stampare l'errore
                                    }
                                });
                                queue.add(requestSeaTurb);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogTurb.show();
            }
        });

        //Grafico del movimento del mare
        roughSeaLineChart = (LineChart) findViewById(R.id.roughSeaLineChart);
        //chartDelegate.setData(roughSeaLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Movimento del mare", blue);
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

                                //String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                String seaWavesURL = URL + "/sea/turbidity?user=" + user + "&date=" + date;
                                final String label = "Livello mare mosso " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                JsonObjectRequest requestSeaWaves = new JsonObjectRequest(Request.Method.GET, seaWavesURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "waves", label, "Bandiera rossa", 3f, 5f, 0f, roughSeaLineChart, blue);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //TODO: stampare l'errore
                                    }
                                });
                                queue.add(requestSeaWaves);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogRoughSea.show();
            }
        });

        //Aggiornamento dati dal server

        //Date
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        String currentDate = + mDay + "/" + mMonth + "/" + mYear;

        //temperatura
        String tempURL = URL + "/dht?user=" + user;
        final String labelTemp = "Temperatura " + currentDate;
        JsonObjectRequest requestTemp = new JsonObjectRequest(Request.Method.GET, tempURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "temperature", labelTemp, "Temperatura troppo elevata", 35f, 40f, 21f, tempLineChart , yellow );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestTemp);

        //umidità
        String humURL = URL + "/dht?user=" + user;
        final String labelHum = "Umidità " + currentDate;
        JsonObjectRequest requestHum = new JsonObjectRequest(Request.Method.GET, humURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "humidity", labelHum, "Umidità troppo elevata", 80f, 85f, 70f, humLineChart, yellow );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestHum);

        //raggi UV
        String uvaURL = URL + "/uva?user=" + user;
        final String labelUV = "Raggi UV " + currentDate;
        JsonObjectRequest requestUVA = new JsonObjectRequest(Request.Method.GET, uvaURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "uva", labelUV, "Soglia limite consigliato", 12f, 15f, 5f , UVLineChart, yellow);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestUVA);

        //temperatura del mare
        String seaTempURL = URL + "/sea/temp?user=" + user;
        final String labelSeaTemp = "Temperatura del mare " + currentDate;
        JsonObjectRequest requestSeaTemp = new JsonObjectRequest(Request.Method.GET, seaTempURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "watertemp", labelSeaTemp, "Mare troppo freddo", 23f, 30f, 21f, seaTempLineChart, blue );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestSeaTemp);

        //torbidità
        String seaTurbURL = URL + "/sea/turbidity?user=" + user;
        final String labelSeaTurb = "Torbidità " + currentDate;
        JsonObjectRequest requestSeaTurb = new JsonObjectRequest(Request.Method.GET, seaTurbURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "turbidity", labelSeaTurb, "Torbidità elevata", 35f, 46.50f, 20f, seaTurbLineChart, blue );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestSeaTurb);

        //mare mosso
        String seaWavesURL = URL + "/sea/waves?user=" + user;
        final String labelWaves = "Livello mare mosso " + currentDate;
        JsonObjectRequest requestSeaWaves = new JsonObjectRequest(Request.Method.GET, seaWavesURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "waves", labelWaves, "Bandiera rossa", 3f, 5f, 0f, roughSeaLineChart, blue);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestSeaWaves);


        //-----------------------------------------------------------//
        notificationScrollView = (ScrollView) findViewById(R.id.notificationScrollView);
        notificationLinearLayout = (LinearLayout) findViewById(R.id.notificationLinearLayout);
        populateNotifications(notificationScrollView, notificationLinearLayout, notificationDelegate, MainActivity.this);


        //-----------------------------------------------------------//

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * Method to refresh notifications view
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void populateNotifications(ScrollView notificationScrollView, final LinearLayout notificationLinearLayout, final NotificationDelegate notificationDelegate, final Activity activity) {
        //----------------------Sezione notifiche--------------------//

        String notificationsURL = URL + "/notify?user=" + user;
        JsonObjectRequest requestNotifications = new JsonObjectRequest(Request.Method.GET, notificationsURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("response ", String.valueOf(response));
                notificationDelegate.createNotification(response, notificationLinearLayout, activity);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error);
            }
        });
        queue.add(requestNotifications);
    }

    /**
     * Getter and Setter
     */

    public LineChart getTempLineChart() {
        return getTempLineChart();
    }

    //-------------------------METODI PER LA RICERCA DEL BEACON------------------------------------//

    //TODO: ricontrolla perchè forse nel cercare di creare il delegate ho fatto su del casino
    //TODO: creare la classe delegate con tutti queti metodi

    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.REQUEST_COARSE_LOCATION);
        }
    }

    protected void proceedDiscovery() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        registerReceiver(getmReceiver(), filter);

        getmBluetoothAdapter().startDiscovery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MainActivity.REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedDiscovery(); // --->
                } else {
                    //TODO re-request
                }
                break;
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(getmReceiver());
    }

    public BroadcastReceiver getmReceiver() {
        return mReceiver;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("BLE", intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE).toString());
                if(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE).toString().equals("E1:A4:B8:01:EA:35")){
                    Log.d("BLE","BEACONNNNNNNNN");
                    //TODO:creare oggetto con valori corretti
                    JSONObject response = null;
                    notificationDelegate.createNotification(response, notificationLinearLayout, MainActivity.this);
                }
            }
        }
    };

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }


    //---------------------------------------------------------------------------------------------//
}


