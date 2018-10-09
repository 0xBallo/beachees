package com.smartbeach.paridemartinelli.smartbeach.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyBLEScanService extends JobService {

    //private BroadcastReceiver mReceiver;

    public MyBLEScanService() {
        super();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        // runs on the main thread, so this Toast will appear
        Log.i("SMARTBEACHES","Start BLE Service!");
        // perform work here, i.e. network calls asynchronously
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /*String data = params.getExtras().getString("RECEIVER");
        Gson g = new Gson();
        this.mReceiver = g.fromJson(data, BroadcastReceiver.class);*/
        mBluetoothAdapter.startDiscovery();
        // returning false means the work has been done, return true if the job is being run asynchronously
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // if the job is prematurely cancelled, do cleanup work here
        // return true to restart the job
        return true;
    }

   /* protected void proceedDiscovery() {
       IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        registerReceiver(this.mReceiver, filter);

        this.mBluetoothAdapter.startDiscovery();
    }*/

}
