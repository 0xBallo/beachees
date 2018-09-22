package com.smartbeach.paridemartinelli.smartbeach;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.media.Image;
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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_COARSE_LOCATION = 2;
    private final NotificationDelegate notificationDelegate = new NotificationDelegate();
    private final ChartDelegate chartDelegate = new ChartDelegate(this);
    public static Context mContext;
    public static final String URL = "http://b0e0f2f3.ngrok.io/api";
    public static String user = "PM12";
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


        //TODO: COSA MANCA:
        // 1. SISTEMARE LA HOME CHE NON MI PIACE (CHIEDERE CONSIGLIO A MATTIA)
        // 2. SISTEMARE LA GRAFICA DEL DIALOG NELLA HOME
        // 3. SISTEMARE LA GRAFICA DELLA CANCELLAZIONE DELLE NOTIFICHE
        // 4. POPOLARE IL DB
        // 5. FAR FUNZIONARE NOTIDICHE PUSH
        // 6. NOTIFCHE BEACON
        // 7. TEST


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

        //TODO: controllare il valore, se supera la soglia colorare cerchio e valore di rosso

        //Temperatura e umidità
        tempNowTextView = (TextView) findViewById(R.id.tempNowTextView);
        humNowTextView = (TextView) findViewById(R.id.humNowTextView);
        String dhtNowURL = URL + "/dht/now?user=";
        JsonObjectRequest requestDhtNow = new JsonObjectRequest(Request.Method.GET, dhtNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String temNow = response.getJSONArray("data").getJSONObject(0).getString("temperature");
                    String humNow = response.getJSONArray("data").getJSONObject(0).getString("humidity");
                    tempNowTextView.setText(temNow);
                    humNowTextView.setText(humNow);
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
        final String uvNowURL = URL + "/uva/now?user=";
        JsonObjectRequest requestUvNow = new JsonObjectRequest(Request.Method.GET, uvNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String uvNow = response.getJSONArray("data").getJSONObject(0).getString("uva");
                    uvNowTextView.setText(uvNow);
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
        String seaTempNowURL = URL + "/sea/temp/now?user=";
        JsonObjectRequest requestSeaTempNow = new JsonObjectRequest(Request.Method.GET, seaTempNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String seaTempNow = response.getJSONArray("data").getJSONObject(0).getString("watertemp");
                    seaTempNowTextView.setText(seaTempNow);
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
        String seaTurbNowURL = URL + "/sea/turbidity/now?user=";
        JsonObjectRequest requestSeaTurbNow = new JsonObjectRequest(Request.Method.GET, seaTurbNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String seaTurbNow = response.getJSONArray("data").getJSONObject(0).getString("turbidity");
                    seaTurbNowTextView.setText(seaTurbNow);
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
        String seaRoughNowURL = URL + "/sea/waves/now?user=";
        JsonObjectRequest requestSeaRoughNow = new JsonObjectRequest(Request.Method.GET, seaRoughNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    //TODO: modificare la richiesta e fare in modo che torni un valore unico
                    String seaRoughNow = response.getJSONArray("data").getJSONObject(0).getString("acc");
                    seaRoughTextView.setText(seaRoughNow);
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
        TabHost.TabSpec spec = dashboardTabHost.newTabSpec("Beach data");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Beach data");
        dashboardTabHost.addTab(spec);

        //TAB 2: grafici relativi al mare
        spec = dashboardTabHost.newTabSpec("Sea Data");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Sea Data");
        dashboardTabHost.addTab(spec);

        //Grafico della temperatura
        tempLineChart = (LineChart) findViewById(R.id.tempLineChart);

        chartDelegate.setData(tempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura", yellow);
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
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String tempURL = URL + "/dht?user=" + user + "&date=" + date;
                                JsonObjectRequest requestTemp = new JsonObjectRequest(Request.Method.GET, tempURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "temperature", "Temperatua", "Temperatura troppo elevata", 35f, 45f, 15f, tempLineChart, yellow );
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
        chartDelegate.setData(humLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Umidità", yellow);
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

                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String humURL = URL + "/dht?user=" + user + "&date=" + date;
                                JsonObjectRequest requestHum = new JsonObjectRequest(Request.Method.GET, humURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "humidity", "Umidità", "Umidità troppo elevata", 80f, 90f, 70f, humLineChart, yellow );
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
        chartDelegate.setData(UVLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Raggi UV", yellow);
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

                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String uvaURL = URL + "/uva?user=" + user + "&date=" + date;
                                JsonObjectRequest requestUVA = new JsonObjectRequest(Request.Method.GET, uvaURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "uva", "Raggi UV", "Soglia limite consigliato", 12f, 15f, 0f , UVLineChart, yellow);
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
        chartDelegate.setData(seaTempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura del mare", blue);
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

                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String seaTempURL = URL + "/sea/temp?user=" + user + "&date=" + date;
                                JsonObjectRequest requestSeaTemp = new JsonObjectRequest(Request.Method.GET, seaTempURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "watertemp", "Temperatura del mare", "Mare troppo freddo", 23f, 30f, 21f, seaTempLineChart, blue );
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
        chartDelegate.setData(seaTurbLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Torbidità del mare", blue);
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

                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String seaTurbURL = URL + "/sea/turbidity?user=" + user + "&date=" + date;
                                JsonObjectRequest requestSeaTurb = new JsonObjectRequest(Request.Method.GET, seaTurbURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "turbidity", "Torbidità del mare", "Torbidità elevata", 35f, 46.50f, 20f, seaTurbLineChart, blue );
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
        chartDelegate.setData(roughSeaLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Movimento del mare", blue);
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

                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String seaWavesURL = URL + "/sea/turbidity?user=" + user + "&date=" + date;
                                JsonObjectRequest requestSeaWaves = new JsonObjectRequest(Request.Method.GET, seaWavesURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        chartDelegate.createChart(response, "waves", "Livello mare mosso", "Bandiera rossa", 3f, 5f, 0f, roughSeaLineChart, blue);
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

        //Temperatura e umidità
        String dateUrl = "2018-08-25";

        //temperatura
        String tempURL = URL + "/dht?user=" + user + "&date=" + dateUrl;
        //String tempURL = URL + "/dht?user=" + user;
        JsonObjectRequest requestTemp = new JsonObjectRequest(Request.Method.GET, tempURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "temperature", "Temperatua", "Temperatura troppo elevata", 35f, 45f, 15f, tempLineChart , yellow);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestTemp);

        //umidità
        String humURL = URL + "/dht?user=" + user + "&date=" + dateUrl;
        //String humURL = URL + "/dht?user=" + user;
        JsonObjectRequest requestHum = new JsonObjectRequest(Request.Method.GET, humURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "humidity", "Umidità", "Umidità troppo elevata", 80f, 85f, 70f, humLineChart, yellow );
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
        JsonObjectRequest requestUVA = new JsonObjectRequest(Request.Method.GET, uvaURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "uva", "Raggi UV", "Soglia limite consigliato", 12f, 15f, 0f , UVLineChart, yellow);
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
        JsonObjectRequest requestSeaTemp = new JsonObjectRequest(Request.Method.GET, seaTempURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "watertemp", "Temperatura del mare", "Mare troppo freddo", 23f, 30f, 21f, seaTempLineChart, blue );
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
        JsonObjectRequest requestSeaTurb = new JsonObjectRequest(Request.Method.GET, seaTurbURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "turbidity", "Torbidità del mare", "Torbidità elevata", 35f, 46.50f, 20f, seaTurbLineChart, blue );
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
        JsonObjectRequest requestSeaWaves = new JsonObjectRequest(Request.Method.GET, seaWavesURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "waves", "Livello mare mosso", "Bandiera rossa", 3f, 5f, 0f, roughSeaLineChart, blue);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestSeaWaves);


        //-----------------------------------------------------------//

        //----------------------Sezione notifiche--------------------//
        notificationScrollView = (ScrollView) findViewById(R.id.notificationScrollView);
        notificationLinearLayout = (LinearLayout) findViewById(R.id.notificationLinearLayout);

        //TODO: una volta sistemata la grafica togliere questo e scommentare la parte sotto
        for (int i = 0; i < 20; i++) {

            //TODO: recuperare i dati corretti
            String date = "31-08-18 ore 17:36";
            String text = "Attenzione: Temperatura troppo elevata";
            String typeNotString = "Temperatura";
            int typeNotImage = R.drawable.icons8_temperatura;
            Integer icon = R.drawable.ic_report_problem_black_24dp;
            String id = "dguguefgiefo";
            int color = Color.parseColor("#FBC02D");
            notificationDelegate.createNotification(MainActivity.this, notificationLinearLayout, date, text, typeNotString, typeNotImage, icon, id, MainActivity.this, color);
        };


        /*String notificationsURL = URL + "/notify?user=" + user;
        JsonObjectRequest requestNotifications = new JsonObjectRequest(Request.Method.GET, notificationsURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.getJSONArray("data").length() == 0){

                        TextView noNotificationsTV = new TextView(MainActivity.this);
                        LinearLayout.LayoutParams noNotificationsTVParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );
                        noNotificationsTV.setLayoutParams(noNotificationsTVParams);
                        noNotificationsTV.setText("Nessuna nuova notifica da mostrare");
                        noNotificationsTV.setGravity(Gravity.CENTER);
                        noNotificationsTV.setPadding(15,300,15,15);
                        notificationLinearLayout.addView(noNotificationsTV);



                    }else{
                        for (int i = 0; i < response.getJSONArray("data").length(); i++) {

                            //TODO: recuperare i dati corretti
                            String date = "31-08-18 ore 17:36";
                            String text = "Attenzione: Temperatura troppo elevata";
                            String tipoNot = "Temperatura";
                            Integer icon = R.drawable.ic_report_problem_black_24dp;
                            String id = "dguguefgiefo";
                            notificationDelegate.createNotification(mContext, notificationLinearLayout, date, text, tipoNot, icon, id, MainActivity.this);
                        }
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
        queue.add(requestNotifications);*/

        //-----------------------------------------------------------//

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("BLE", intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE).toString());
                if(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE).toString().equals("E1:A4:B8:01:EA:35")){
                    Log.d("BLE","BEACONNNNNNNNN");
                    //TODO: richiamare il metodo per la creazione della notifica
                }
            }
        }
    };

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }


    //---------------------------------------------------------------------------------------------//
}


