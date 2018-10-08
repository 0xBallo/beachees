package com.smartbeach.paridemartinelli.smartbeach.services;

import android.app.IntentService;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.JobIntentService;
import android.widget.Toast;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyBLEScanService extends JobService {


    public MyBLEScanService() {
        super();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        // runs on the main thread, so this Toast will appear
        Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        // perform work here, i.e. network calls asynchronously

        // returning false means the work has been done, return true if the job is being run asynchronously
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // if the job is prematurely cancelled, do cleanup work here

        // return true to restart the job
        return true;
    }

}
