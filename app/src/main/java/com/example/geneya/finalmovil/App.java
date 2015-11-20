package com.example.geneya.finalmovil;

import android.app.Application;
import android.widget.Toast;

import com.parse.Parse;

/**
 * Created by Necho on 17/11/2015.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "EqedFZWw1Si2Ls4M4ELK3M4EEEPWRqx9of3O9YBP", "oBMcG3Y36nB6KUFpcuBscjUtyEgc6iozNwB42trS");
        Toast toast1 = Toast.makeText(getApplicationContext(), "Todo bn", Toast.LENGTH_SHORT);
        toast1.show();

    }


}