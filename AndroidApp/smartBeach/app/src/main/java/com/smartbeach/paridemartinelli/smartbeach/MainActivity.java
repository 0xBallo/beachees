package com.smartbeach.paridemartinelli.smartbeach;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
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
//import com.google.gson.Gson;
import com.smartbeach.paridemartinelli.smartbeach.services.MyBLEScanService;
import com.smartbeach.paridemartinelli.smartbeach.services.MyFirebaseMessagingService;
import com.smartbeach.paridemartinelli.smartbeach.utils.SmartBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_COARSE_LOCATION = 2;
    public ScrollView notificationScrollView;
    public LinearLayout notificationLinearLayout;
    private final NotificationDelegate notificationDelegate = new NotificationDelegate();
    private final ChartDelegate chartDelegate = new ChartDelegate(this);
    public static Context mContext;
    public static final String URL = "http://9a31ab32.ngrok.io/api";
    public static String user = "";
    public static String token;
    public static RequestQueue queue;

    //Sezione Beacon
    private BroadcastReceiver mReceiver = new SmartBroadcastReceiver(notificationDelegate, notificationLinearLayout, MainActivity.this);

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

    private RecyclerView mRecyclerViewBeach;
    private RecyclerView.Adapter mAdapterBeach;
    private RecyclerView.LayoutManager mLayoutManagerBeach;

    public LineChart tempLineChart;
    private ImageButton dateTempImageButton;
    private DatePickerDialog datePickerDialogTemp;
    private RelativeLayout rl_temp;

    public LineChart humLineChart;
    private ImageButton dateHumImageButton;
    private DatePickerDialog datePickerDialogHum;

    public LineChart UVLineChart;
    private ImageButton dateUVImageButton;
    private DatePickerDialog datePickerDialogUV;

    public LineChart seaTempLineChart;
    private ImageButton dateSeaTempImageButton;
    private DatePickerDialog datePickerDialogSeaTemp;

    public LineChart seaTurbLineChart;
    private ImageButton dateTurbImageButton;
    private DatePickerDialog datePickerDialogTurb;


    public LineChart roughSeaLineChart;
    private ImageButton dateRoughSeaImageButton;
    private DatePickerDialog datePickerDialogRoughSea;

    int yellow = Color.parseColor("#FBC02D");
    int blue = Color.parseColor("#29B6F6");

    //Sezione notifiche
    private TextView bedgeNot;



    /*public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);

        //final MenuItem menuItem = menu.findItem(R.id.navigation_notifications);
        textCartItemCount = findViewById(R.id.badge);
        MenuItem menu = findViewById(R.id.navigation_notifications);


        setupBadge();

        return true;
    }*/


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

        if (MainActivity.token != null && !MainActivity.token.isEmpty()) {
            MyFirebaseMessagingService.sendRegistrationToServer(MainActivity.user, MainActivity.token);
        }


        //---------------------Sezione beacon-----------------------------//
        //TODO: commentato per effettuare test con simulatore (scommentare)

        //Controllo se il dispositivo supporta il bluethooth
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Bluetooth Non supportato", Toast.LENGTH_SHORT).show();
            //    finish();
        } else if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            //Controllo se il dispositivo ha il bluetooth acceso, se non è acceso gli chiedo di accenderlo
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_COARSE_LOCATION);
        }
        //Chiedo all'utente i permessi per la localizzazione e poi faccio partire il processo per la ricerca dei dispositivi bluetooth
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.REQUEST_COARSE_LOCATION);
        } else {
            startBGService();
        }


        //-------------------------------------------------------------//

        //----------------------Sezione home--------------------------//
        homeScrollView = findViewById(R.id.homeScrollView);

        //bottone info spiaggia (secondo me non serve)
        //moreInfoBeachButton = (ImageButton) findViewById(R.id.moreInfoBeachButton);

        //Temperatura e umidità
        tempNowTextView = findViewById(R.id.tempNowTextView);
        humNowTextView = findViewById(R.id.humNowTextView);
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
                    if (tempNowInt >= 35) {
                        tempNowTextView.setTextColor(Color.RED);
                    } else {
                        tempNowTextView.setTextColor(Color.parseColor("#FBC02D"));
                    }
                    humNowTextView.setText(humNow);
                    if (humNowInt >= 80) {
                        humNowTextView.setTextColor(Color.RED);
                    } else {
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
        uvNowTextView = findViewById(R.id.uvNowTextView);
        final String uvNowURL = URL + "/uva/now?user=" + user;
        JsonObjectRequest requestUvNow = new JsonObjectRequest(Request.Method.GET, uvNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    float uvNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("uva"));
                    int uvNowInt = Math.round(uvNowFloat);
                    String uvNow = String.valueOf(uvNowInt);
                    uvNowTextView.setText(uvNow);
                    if (uvNowFloat >= 12) {
                        uvNowTextView.setTextColor(Color.RED);
                    } else {
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
        seaTempNowTextView = findViewById(R.id.tempSeaNowTextView);
        String seaTempNowURL = URL + "/sea/temp/now?user=" + user;
        JsonObjectRequest requestSeaTempNow = new JsonObjectRequest(Request.Method.GET, seaTempNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    float seaTempNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("watertemp"));
                    int seaTempNowInt = Math.round(seaTempNowFloat);
                    String seaTempNow = String.valueOf(seaTempNowInt) + "°C";
                    seaTempNowTextView.setText(seaTempNow);
                    if (seaTempNowInt >= 27 || seaTempNowInt <= 23) {
                        seaTempNowTextView.setTextColor(Color.RED);
                    } else {
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
        seaTurbNowTextView = findViewById(R.id.turbSeaNowTextView);
        String seaTurbNowURL = URL + "/sea/turbidity/now?user=" + user;
        JsonObjectRequest requestSeaTurbNow = new JsonObjectRequest(Request.Method.GET, seaTurbNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    float seaTurbNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("turbidity"));
                    int seaTurbNowInt = Math.round(seaTurbNowFloat);
                    String seaTurbNow = String.valueOf(seaTurbNowInt);
                    seaTurbNowTextView.setText(seaTurbNow);
                    if (seaTurbNowInt >= 35) {
                        seaTurbNowTextView.setTextColor(Color.RED);
                    } else {
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
        seaRoughTextView = findViewById(R.id.roughSeaNowTextView);
        String seaRoughNowURL = URL + "/sea/waves/now?user=" + user;
        JsonObjectRequest requestSeaRoughNow = new JsonObjectRequest(Request.Method.GET, seaRoughNowURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    float seaWavesNowFloat = Float.parseFloat(response.getJSONArray("data").getJSONObject(0).getString("waves"));
                    int seaWavesNowInt = Math.round(seaWavesNowFloat);
                    String seaWavesNow = String.valueOf(seaWavesNowInt);
                    seaRoughTextView.setText(seaWavesNow);
                    if (seaWavesNowInt >= 3) {
                        seaRoughTextView.setTextColor(Color.RED);
                    } else {
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
        dashboardTabHost = findViewById(R.id.dashboardTabHost);
        dashboardTabHost.setup();

        //TAB 1: grafici relativi all'utente
        TabHost.TabSpec spec = dashboardTabHost.newTabSpec("Spiaggia");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Spiaggia");
        dashboardTabHost.addTab(spec);

        //mRecyclerViewBeach = (RecyclerView) findViewById(R.id.my_recycler_view_beach);
        //mRecyclerViewBeach.setHasFixedSize(true);
        //mLayoutManagerBeach = new LinearLayoutManager(this);
        //mRecyclerViewBeach.setLayoutManager(mLayoutManagerBeach);
        //mAdapterBeach = new MyAdapter(myDataset);
        //mRecyclerViewBeach.setAdapter(mAdapterBeach);

        //TAB 2: grafici relativi al mare
        spec = dashboardTabHost.newTabSpec("Mare");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Mare");
        dashboardTabHost.addTab(spec);

        //Grafico della temperatura
        rl_temp = findViewById(R.id.rl_temp);
        tempLineChart = findViewById(R.id.tempLineChart);
        //chartDelegate.setData(tempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura", yellow);
        tempLineChart.setDescription("");
        dateTempImageButton = findViewById(R.id.dateTempImageButton);
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

                            @SuppressLint("ResourceType")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //TODO: risolvere bug: la pagina non si ricarica da sola, quindi i valori non vengono modificati automaticamente (Se si cambia il tab si vede il cambiamento)
                                tempLineChart.clear();
                                //String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                String tempURL = URL + "/dht?user=" + user + "&date=" + date;
                                final String label = "Temperatura " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                getTempChart(tempURL, label);
                                //rl_temp.addView(newLineChart);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogTemp.show();
            }
        });

        //Grafico dell'umidità
        humLineChart = findViewById(R.id.humLineChart);
        //chartDelegate.setData(humLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Umidità", yellow);
        humLineChart.setDescription("");
        dateHumImageButton = findViewById(R.id.dateHumImageButton);
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
                                getHumChart(humURL, label);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogHum.show();
            }
        });

        //Grafico dei raggi UV
        UVLineChart = findViewById(R.id.UVLineChart);
        //chartDelegate.setData(UVLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Raggi UV", yellow);
        UVLineChart.setDescription("");
        dateUVImageButton = findViewById(R.id.dateUVImageButton);
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
                                getUVChart(uvaURL, label);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogUV.show();
            }
        });

        //Grafico della temperatura del mare
        seaTempLineChart = findViewById(R.id.seaTempLineChart);
        //chartDelegate.setData(seaTempLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Temperatura del mare", blue);
        seaTempLineChart.setDescription("");
        dateSeaTempImageButton = findViewById(R.id.dateSeaTempImageButton);
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
                                getTempSeaChart(seaTempURL, label);


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogSeaTemp.show();
            }
        });

        //Grafico della torbidità del mare
        seaTurbLineChart = findViewById(R.id.seaTurbLineChart);
        //chartDelegate.setData(seaTurbLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Torbidità del mare", blue);
        seaTurbLineChart.setDescription("");
        dateTurbImageButton = findViewById(R.id.dateTurbImageButton);
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
                                getTurbChart(seaTurbURL, label);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialogTurb.show();
            }
        });

        //Grafico del movimento del mare
        roughSeaLineChart = findViewById(R.id.roughSeaLineChart);
        //chartDelegate.setData(roughSeaLineChart, chartDelegate.setXAxisValues(), chartDelegate.setYAxisValues(), "Movimento del mare", blue);
        roughSeaLineChart.setDescription("");
        dateRoughSeaImageButton = findViewById(R.id.dateRoughSeaImageButton);
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
                                String seaWavesURL = URL + "/sea/waves?user=" + user + "&date=" + date;
                                final String label = "Livello mare mosso " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                getWavesChart(seaWavesURL, label);

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
        String currentDate = +mDay + "/" + mMonth + "/" + mYear;

        //temperatura
        String tempURL = URL + "/dht?user=" + user;
        final String labelTemp = "Temperatura " + currentDate;
        getTempChart(tempURL, labelTemp);

        //umidità
        String humURL = URL + "/dht?user=" + user;
        final String labelHum = "Umidità " + currentDate;
        getHumChart(humURL, labelHum);

        //raggi UV
        String uvaURL = URL + "/uva?user=" + user;
        final String labelUV = "Raggi UV " + currentDate;
        getUVChart(uvaURL, labelUV);

        //temperatura del mare
        String seaTempURL = URL + "/sea/temp?user=" + user;
        final String labelSeaTemp = "Temperatura del mare " + currentDate;
        getTempSeaChart(seaTempURL, labelSeaTemp);

        //torbidità
        String seaTurbURL = URL + "/sea/turbidity?user=" + user;
        final String labelSeaTurb = "Torbidità " + currentDate;
        getTurbChart(seaTurbURL, labelSeaTurb);

        //mare mosso
        String seaWavesURL = URL + "/sea/waves?user=" + user;
        final String labelWaves = "Livello mare mosso " + currentDate;
        getWavesChart(seaWavesURL, labelWaves);


        //-----------------------------------------------------------//
        //----------------------Sezione notifiche--------------------//
        notificationScrollView = findViewById(R.id.notificationScrollView);
        notificationLinearLayout = findViewById(R.id.notificationLinearLayout);
        bedgeNot = findViewById(R.id.badgeNot);
        String notificationsURL = URL + "/notify?user=" + user;
        JsonObjectRequest requestNotifications = new JsonObjectRequest(Request.Method.GET, notificationsURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("response ", String.valueOf(response));
                notificationDelegate.createNotification(response, notificationLinearLayout, MainActivity.this, bedgeNot);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print(error);
            }
        });
        queue.add(requestNotifications);


        //-----------------------------------------------------------//

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void getWavesChart(String seaWavesURL, final String labelWaves) {
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
    }

    private void getTurbChart(String seaTurbURL, final String labelSeaTurb) {
        JsonObjectRequest requestSeaTurb = new JsonObjectRequest(Request.Method.GET, seaTurbURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "turbidity", labelSeaTurb, "Torbidità elevata", 35f, 46.50f, 20f, seaTurbLineChart, blue);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestSeaTurb);
    }

    private void getTempSeaChart(String seaTempURL, final String labelSeaTemp) {
        JsonObjectRequest requestSeaTemp = new JsonObjectRequest(Request.Method.GET, seaTempURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "watertemp", labelSeaTemp, "Mare troppo freddo", 23f, 30f, 21f, seaTempLineChart, blue);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestSeaTemp);
    }

    private void getUVChart(String uvaURL, final String labelUV) {
        JsonObjectRequest requestUVA = new JsonObjectRequest(Request.Method.GET, uvaURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "uva", labelUV, "Soglia limite consigliato", 12f, 15f, 5f, UVLineChart, yellow);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestUVA);
    }

    private void getHumChart(String humURL, final String labelHum) {
        JsonObjectRequest requestHum = new JsonObjectRequest(Request.Method.GET, humURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "humidity", labelHum, "Umidità troppo elevata", 80f, 85f, 70f, humLineChart, yellow);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestHum);
    }

    private void getTempChart(String tempURL, final String labelTemp) {
        JsonObjectRequest requestTemp = new JsonObjectRequest(Request.Method.GET, tempURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                chartDelegate.createChart(response, "temperature", labelTemp, "Temperatura troppo elevata", 35f, 40f, 21f, tempLineChart, yellow);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: stampare l'errore
            }
        });
        queue.add(requestTemp);
    }

    //-------------------------METODI PER LA RICERCA DEL BEACON------------------------------------//

    //TODO: ricontrolla perchè forse nel cercare di creare il delegate ho fatto su del casino
    //TODO: creare la classe delegate con tutti queti metodi

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void startBGService() {
        //lancia il servizio bluetooth
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        /*PersistableBundle bundle = new PersistableBundle();
        Gson g = new Gson();
        bundle.putString("RECEIVER", g.toJson(getmReceiver()));*/

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        registerReceiver(this.mReceiver, filter);

        JobInfo jobInfo = new JobInfo.Builder(11, new ComponentName(this, MyBLEScanService.class))
                // only add if network access is required
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                //.setExtras(bundle)
                .build();

        jobScheduler.schedule(jobInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MainActivity.REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startBGService(); // --->
                } else {
                    //TODO re-request
                }
                break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
    }

    /*private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("BLE", intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE).toString());
                if(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE).toString().equals("E1:A4:B8:01:EA:35")){
                    Log.d("BLE","BEACON LIDO BEACH");
                    //TODO:creare oggetto con valori corretti
                    JSONObject response = null;
                    notificationDelegate.createNotification(response, notificationLinearLayout, MainActivity.this);
                }
            }
        }
    };*/
}


