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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static com.smartbeach.paridemartinelli.smartbeach.MainActivity.URL;
import static com.smartbeach.paridemartinelli.smartbeach.MainActivity.queue;
import static com.smartbeach.paridemartinelli.smartbeach.MainActivity.user;
import static com.smartbeach.paridemartinelli.smartbeach.R.drawable.icons8_temperatura;

public class NotificationDelegate {
    public NotificationDelegate() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    void createNotification(final Context mContext, final LinearLayout notificationLinearLayout, String date, String text, String type, int typeImage, Integer icon, final String id, final Activity activity, int color) {
        // Initialize a new CardView
        CardView card = new CardView(mContext);
        // Set the CardView layoutParams
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(cardParams);
        //Set CardView Margins
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        layoutParams.setMargins(35, 35, 35, 0);
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
        LinearLayout lnTop = new LinearLayout(mContext);
        lnTop.setOrientation(LinearLayout.HORIZONTAL);
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
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(typeImage);
        imageView.setPadding(10,10, 0,0);

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
        typeTV.setPadding(10,30,0,0);

        //Bottone per cancellare la notifica
        ImageButton deleteIB = new ImageButton(mContext);
        LinearLayout.LayoutParams deleteIBParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        deleteIB.setLayoutParams(deleteIBParams);
        deleteIB.setImageResource(R.drawable.ic_delete_white_24dp);
        deleteIB.setBackgroundColor(Color.TRANSPARENT);
        ViewGroup.MarginLayoutParams deleteIBLayoutParams = (ViewGroup.MarginLayoutParams) deleteIB.getLayoutParams();
        deleteIBLayoutParams.setMargins(700, 0, 0, 0);
        //deleteIB.setForegroundGravity(Gravity.RIGHT);
        deleteIB.requestLayout();
        deleteIB.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                            builder.setTitle("Cancellazione della notifica " + id)
                                                    .setMessage("Sei sicuro di voler eliminare la notifica?")
                                                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                        public void onClick(final DialogInterface dialog, int id) {

                                                            //Rimozione della notifica
                                                            String deleteNotificationsURL = URL + "/notify?user=" + user + "&id=" + id;
                                                            JsonObjectRequest requestNotifications = new JsonObjectRequest(Request.Method.DELETE, deleteNotificationsURL, null, new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    dialog.cancel();
                                                                }
                                                            }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    //TODO: stampare l'errore
                                                                }
                                                            });
                                                            queue.add(requestNotifications);
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