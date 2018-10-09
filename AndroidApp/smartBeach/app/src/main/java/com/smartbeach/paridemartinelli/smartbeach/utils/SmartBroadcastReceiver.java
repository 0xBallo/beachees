package com.smartbeach.paridemartinelli.smartbeach.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.LinearLayout;

import com.smartbeach.paridemartinelli.smartbeach.MainActivity;
import com.smartbeach.paridemartinelli.smartbeach.NotificationDelegate;
import com.smartbeach.paridemartinelli.smartbeach.R;

import java.util.Calendar;

public class SmartBroadcastReceiver extends BroadcastReceiver {

    private NotificationDelegate notificationDelegate;
    private LinearLayout notificationLinearLayout;
    private Activity activity;
    private Calendar last;

    public SmartBroadcastReceiver(NotificationDelegate notificationDelegate, LinearLayout notificationLinearLayout, Activity activity) {
        super();
        this.notificationDelegate = notificationDelegate;
        this.notificationLinearLayout = notificationLinearLayout;
        this.activity = activity;
        this.last = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            Log.d("BLE", intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE).toString());
            if (intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE).toString().equals("E1:A4:B8:01:EA:35")) {
                Log.d("BLE", "BEACON LIDO BEACH");
                //TODO:creare oggetto con valori corretti
                Calendar morningStart = Calendar.getInstance();
                morningStart.set(Calendar.HOUR_OF_DAY, 8);
                Calendar morningEnd = Calendar.getInstance();
                morningEnd.set(Calendar.HOUR_OF_DAY, 10);
                Calendar lunchStart = Calendar.getInstance();
                lunchStart.set(Calendar.HOUR_OF_DAY, 12);
                Calendar lunchEnd = Calendar.getInstance();
                lunchEnd.set(Calendar.HOUR_OF_DAY, 14);
                Calendar afternoonStart = Calendar.getInstance();
                afternoonStart.set(Calendar.HOUR_OF_DAY, 17);
                Calendar afternoonEnd = Calendar.getInstance();
                afternoonEnd.set(Calendar.HOUR_OF_DAY, 19);
                Calendar today = Calendar.getInstance();
                Calendar range = Calendar.getInstance();
                range.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY) - 2);
                if (this.last != null) {
                    if ((this.last.before(range))) {
                        if (today.after(morningStart) && today.before(morningEnd)) {
                            //moarning notify
                            sendNotification(intent, "Approfitta dell'offerta su pasta e cappuccino mostrando questa notifica al Bar!");
                            this.last = today;
                        }
                        if (today.after(lunchStart) && today.before(lunchEnd)) {
                            //lunch notify
                            sendNotification(intent, "Solo ora Menù completo di pesce a 25€! Cosa aspetti?");
                            this.last = today;
                        }
                        if (today.after(afternoonStart) && today.after(afternoonEnd)) {
                            //afternoon notify
                            sendNotification(intent, "Questa è l'ora degli amari... questa è l'ora del Campari!! 2€ di coupon sull'aperitivo!");
                            this.last = today;
                        }
                    }
                } else {
                    if (today.after(morningStart) && today.before(morningEnd)) {
                        //moarning notify
                        sendNotification(intent, "Approfitta dell'offerta su pasta e cappuccino mostrando questa notifica al Bar!");
                        this.last = today;
                    }
                    if (today.after(lunchStart) && today.before(lunchEnd)) {
                        //lunch notify
                        sendNotification(intent, "Solo ora Menù completo di pesce a 25€! Cosa aspetti?");
                        this.last = today;
                    }
                    if (today.after(afternoonStart) && today.after(afternoonEnd)) {
                        //afternoon notify
                        sendNotification(intent, "Questa è l'ora degli amari... questa è l'ora del Campari!! 2€ di coupon sull'aperitivo!");
                        this.last = today;
                    }
                }
                //JSONObject response = null;
                //notificationDelegate.createNotification(response, notificationLinearLayout, activity);
            }
        }
    }

    private void sendNotification(Intent intent, String messageBody) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "BEACON";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(activity, channelId)
                        // TODO: cambiare icona
                        .setSmallIcon(R.drawable.ic_star_black_24dp)
                        .setContentTitle("Beacon")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}
