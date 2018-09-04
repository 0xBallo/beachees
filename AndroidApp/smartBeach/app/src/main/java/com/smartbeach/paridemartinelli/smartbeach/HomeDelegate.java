package com.smartbeach.paridemartinelli.smartbeach;

import android.widget.ImageView;
import android.widget.TextView;

public class HomeDelegate {
    public HomeDelegate() {
    }//------------------------------------------------METODO PER LA CREAZIONE DEI POST NELLA HOME------------------------------------------------//

    void createHomePost(TextView data, TextView date, ImageView icon, TextView warning) {
        //TODO:recuperare i valori reali dal db
        String currentTemp = "Temperatura 35°C";
        String currentDate = "Data: 1-09-18 16:38";
        /* TODO: Per settare l'icona e il testo si dovrà mettere un in if e controlalre il valore della temperatura,
            se la temperatura supera una certa soglia comparirà una determinata icona e un determinato messaggio;
            FARE STESSA COSA PER TUTTI GLI ALTRI VALORI
         */
        Integer iconTemp = R.drawable.ic_report_problem_black_24dp;
        String warningTemp = "Temperatura abbastanza elevata, si consiglia di rinfrescarsi e di non stare troppo tempo al sole!!";

        data.setText(currentTemp);
        date.setText(currentDate);
        icon.setImageResource(iconTemp);
        warning.setText(warningTemp);
    }
}