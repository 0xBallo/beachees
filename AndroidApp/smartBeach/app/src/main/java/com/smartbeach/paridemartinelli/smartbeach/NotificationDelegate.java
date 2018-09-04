package com.smartbeach.paridemartinelli.smartbeach;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationDelegate {
    public NotificationDelegate() {
    }

    void createNotification(Context mContext, LinearLayout notificationLinearLayout, String date, String text, String type, Integer icon) {
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

        //Set cardView content padding
        card.setContentPadding(15, 15, 15, 15);

        //Set a background color for CardView
        card.setCardBackgroundColor(Color.parseColor("#B0BEC5"));

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

        //Tipo della notifica
        TextView typeTV = new TextView(mContext);
        LinearLayout.LayoutParams typeTVParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        typeTV.setLayoutParams(typeTVParams);
        typeTV.setText(type);
        typeTV.setTextColor(Color.GRAY);
        typeTV.getLayoutParams();

        //Data e ora della notifica
        TextView dateTV = new TextView(mContext);
        LinearLayout.LayoutParams dateTVParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dateTV.setLayoutParams(dateTVParams);
        dateTV.setText(date);
        dateTV.setTextColor(Color.GRAY);
        dateTV.getLayoutParams();

        lnTop.addView(typeTV);
        lnTop.addView(dateTV);

        //Sezione centrale della notifica
        LinearLayout lnCentral = new LinearLayout(mContext);
        lnCentral.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lnCentralParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        lnCentral.setLayoutParams(lnCentralParams);

        //Icona della notifica
        ImageView im = new ImageView(mContext);
        LinearLayout.LayoutParams imParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        im.setLayoutParams(imParams);
        im.setImageResource(icon);

        //Testo della notifica
        TextView textTV = new TextView(mContext);
        LinearLayout.LayoutParams textTVParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textTV.setLayoutParams(textTVParams);
        textTV.setText(text);
        textTV.setTextColor(Color.BLACK);

        textTV.getLayoutParams();
        textTV.requestLayout();

        lnCentral.addView(im);
        lnCentral.addView(textTV);

        //TODO: migliorare la grafica on spazi e colori

        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) im.getLayoutParams();
        margin.setMargins(20, 0, 0, 0);
        im.requestLayout();
        margin.setMargins(20, 0, 0, 0);
        margin.setMargins(20, 0, 0, 0);
        dateTV.requestLayout();

        ln.addView(lnTop);
        ln.addView(lnCentral);

        //TODO: scegliere se inserire il bottone di cancellazione delle notifiche o se cancellarle automaticamente dopo tot tempo dal db

        //Add ellement in the correct position
        card.addView(ln);
        notificationLinearLayout.addView(card);
    }
}