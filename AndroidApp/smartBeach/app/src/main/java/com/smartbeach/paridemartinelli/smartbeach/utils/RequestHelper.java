package com.smartbeach.paridemartinelli.smartbeach.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smartbeach.paridemartinelli.smartbeach.MainActivity;

public class RequestHelper {
    private String domain;
    private final Context mainActivity;

    String result;

    public RequestHelper(Context mainActivity, String url) {
        this.domain = url;
        this.mainActivity = mainActivity;
    }

    public void dhtData(String user, String date) {
        //TODO: debug request
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        String url =this.domain + "/dht?user=PM12&date=2018-08-25";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("PINO",response);
                        result = response;
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

    public String getResult() {
        return result;
    }
}
