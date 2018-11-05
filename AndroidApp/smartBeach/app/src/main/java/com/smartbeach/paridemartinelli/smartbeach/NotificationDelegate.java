package com.smartbeach.paridemartinelli.smartbeach;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static com.smartbeach.paridemartinelli.smartbeach.MainActivity.URL;
import static com.smartbeach.paridemartinelli.smartbeach.MainActivity.mContext;
import static com.smartbeach.paridemartinelli.smartbeach.MainActivity.queue;
import static com.smartbeach.paridemartinelli.smartbeach.MainActivity.user;
import static com.smartbeach.paridemartinelli.smartbeach.R.drawable.icons8_temperatura;

public class NotificationDelegate {
    public NotificationDelegate() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createNotification(JSONObject response, LinearLayout notificationLinearLayout, Activity activity, TextView badge) {
        try {
            if(response.getJSONArray("data").length() == 0){
                TextView noNotificationsTV = new TextView(activity);
                LinearLayout.LayoutParams noNotificationsTVParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                noNotificationsTV.setLayoutParams(noNotificationsTVParams);
                noNotificationsTV.setText("Nessuna nuova notifica da mostrare");
                noNotificationsTV.setGravity(Gravity.CENTER);
                noNotificationsTV.setPadding(15,300,15,15);
                notificationLinearLayout.addView(noNotificationsTV);
                //badge.setText(Integer.toString(response.getJSONArray("data").length()));

            }else{
                for (int i = 0; i < response.getJSONArray("data").length(); i++) {

                    badge.setText(Integer.toString(response.getJSONArray("data").length()));
                    String date = response.getJSONArray("data").getJSONObject(i).getString("date");
                    String text = response.getJSONArray("data").getJSONObject(i).getJSONObject("notification").getString("body");
                    String typeNotString = response.getJSONArray("data").getJSONObject(i).getJSONObject("notification").getString("title");
                    //int typeNotImage = R.drawable.icons8_temperatura;
                    //int icon = R.drawable.ic_report_problem_black_24dp;
                    //String iconString = "R.drawable." + response.getJSONArray("data").getJSONObject(i).getJSONObject("notification").getString("icon");
                    //int iconInt = Integer.valueOf(iconString);
                    String id = response.getJSONArray("data").getJSONObject(i).getString("_id");
                    int color;
                    int icon;
                    switch (typeNotString){
                        case "Temperatura":
                            color = Color.parseColor("#FBC02D");
                            icon = R.drawable.ic_report_problem_black_24dp;
                            break;
                        case "Umidità":
                            color = Color.parseColor("#FBC02D");
                            icon = R.drawable.ic_report_problem_black_24dp;
                            break;
                        case "Raggi UV":
                            color = Color.parseColor("#FBC02D");
                            icon = R.drawable.ic_report_problem_black_24dp;
                            break;
                        case "Temperatura del mare":
                            color = Color.parseColor("#29B6F6");
                            icon = R.drawable.ic_report_problem_black_24dp;
                            break;
                        case "Torbidità del mare":
                            color = Color.parseColor("#29B6F6");
                            icon = R.drawable.ic_report_problem_black_24dp;
                            break;
                        case "Movimento del mare":
                            color = Color.parseColor("#29B6F6");
                            icon = R.drawable.ic_report_problem_black_24dp;
                            break;
                        default:
                            color = Color.parseColor("#FF4081");
                            icon = R.drawable.ic_lightbulb_outline_black_24dp;

                    }
                    createNotification(activity, notificationLinearLayout, date, text, typeNotString, icon, id, activity, color);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    void createNotification(final Context mContext, final LinearLayout notificationLinearLayout, String date, String text, String type, Integer icon, final String idNotify, final Activity activity, int color) {
        // Initialize a new CardView
        final CardView card = new CardView(mContext);
        // Set the CardView layoutParams
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(cardParams);
        //Set CardView Margins
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        layoutParams.setMargins(35, 35, 35, 10);
        card.requestLayout();
        //Set CardView corner radius
        card.setRadius(9);
        //Set the CardView maximum elevation
        card.setMaxCardElevation(15);
        //Set CardView elevation
        card.setCardElevation(9);


        //Notifica
        LinearLayout ln = new LinearLayout(mContext);
        ln.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        ln.setLayoutParams(lnParams);

        //Sezione alta della notifica
        RelativeLayout lnTop = new RelativeLayout(mContext);
        //lnTop.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lnTomParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lnTop.setLayoutParams(lnTomParams);
        lnTop.setBackgroundColor(color);
        //lnTop.setGravity(Gravity.CENTER);
        lnTop.setPadding(15,15,15,15);
        ln.requestLayout();

        //Immagine del tipo
        /*ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(typeImage);
        imageView.setPadding(10,10, 0,0);*/

        //Tipo della notifica
        TextView typeTV = new TextView(mContext);
        LinearLayout.LayoutParams typeTVParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        typeTV.setLayoutParams(typeTVParams);
        typeTV.setText(type);
        typeTV.setTextColor(Color.WHITE);
        typeTV.getLayoutParams();
        typeTV.setPadding(10,5,0,5);

        //Bottone per cancellare la notifica
        ImageButton deleteIB = new ImageButton(mContext);
        RelativeLayout.LayoutParams deleteIBParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        deleteIBParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        deleteIB.setLayoutParams(deleteIBParams);
        deleteIB.setImageResource(R.drawable.ic_delete_white_24dp);
        deleteIB.setBackgroundColor(Color.TRANSPARENT);
        deleteIB.setPadding(0,5,10, 5);
        deleteIB.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                            builder.setTitle("Cancellazione della notifica " + idNotify)
                                                    .setMessage("Sei sicuro di voler eliminare la notifica?")
                                                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                        public void onClick(final DialogInterface dialog, int id) {

                                                            //Rimozione della notifica
                                                            String deleteNotificationsURL = URL + "/notify/" + idNotify;

                                                            JsonObjectRequest requestNotifications = new JsonObjectRequest(Request.Method.DELETE, deleteNotificationsURL, null, new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    dialog.cancel();
                                                                    notificationLinearLayout.removeView(card);
                                                                    /*notificationLinearLayout.invalidate();
                                                                    notificationLinearLayout.postInvalidate();*/
                                                                    activity.recreate();
                                                                }
                                                            }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    System.out.print(error);
                                                                }
                                                            }){
                                                                @Override
                                                                protected Map<String,String> getParams(){
                                                                    Map<String,String> params = new HashMap<String, String>();
                                                                    params.put("_id",idNotify);
                                                                    return params;
                                                                }
                                                            };
                                                            MainActivity.queue.add(requestNotifications);
                                                        }
                                                    })
                                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                            // Create the AlertDialog object and return it
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    });

        //lnTop.addView(imageView);
        lnTop.addView(typeTV);
        lnTop.addView(deleteIB);

        //Sezione centrale della notifica
        LinearLayout lnCentral = new LinearLayout(mContext);
        lnCentral.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lnCentralParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        lnCentral.setLayoutParams(lnCentralParams);
        lnCentral.setPadding(15,15,15,15);
        lnCentral.requestLayout();
        lnCentral.setBackgroundColor(Color.WHITE);

        //Icona della notifica
        ImageView im = new ImageView(mContext);
        LinearLayout.LayoutParams imParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        im.setLayoutParams(imParams);
        im.setImageResource(icon);
        ViewGroup.MarginLayoutParams imLayoutParams = (ViewGroup.MarginLayoutParams) im.getLayoutParams();
        imLayoutParams.setMargins(20,30, 0,0 );
        im.requestLayout();

        LinearLayout textNotifyLN = new LinearLayout(mContext);
        textNotifyLN.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textNotifyParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textNotifyLN.setLayoutParams(textNotifyParams);
        textNotifyLN.setPadding(15, 15, 15, 15);
        textNotifyLN.requestLayout();

        //Testo della notifica
        TextView textTV = new TextView(mContext);
        LinearLayout.LayoutParams textTVParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textTV.setLayoutParams(textTVParams);
        textTV.setText(text);
        textTV.setTextColor(Color.BLACK);
        ViewGroup.MarginLayoutParams textTVLayoutParams = (ViewGroup.MarginLayoutParams) textTV.getLayoutParams();
        textTVLayoutParams.setMargins(40, 0, 0, 0);
        textTV.requestLayout();

        //Data e ora
        TextView dateTV = new TextView(mContext);
        LinearLayout.LayoutParams dateTVParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dateTV.setLayoutParams(dateTVParams);
        dateTV.setText(date);
        dateTV.setTextColor(Color.GRAY);
        ViewGroup.MarginLayoutParams dateTVLayoutParams = (ViewGroup.MarginLayoutParams) dateTV.getLayoutParams();
        dateTVLayoutParams.setMargins(50, 0, 0, 0);
        dateTV.requestLayout();

        textNotifyLN.addView(textTV);
        textNotifyLN.addView(dateTV);

        lnCentral.addView(im);
        lnCentral.addView(textNotifyLN);

        ln.addView(lnTop);
        ln.addView(lnCentral);

        //Add ellement in the correct position
        card.addView(ln);
        notificationLinearLayout.addView(card);
    }
}