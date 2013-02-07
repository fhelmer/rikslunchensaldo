package com.example.rikslunchentest;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    String card_id = "32271843";
    static String cardbalance = "";
    static String lasttopdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();

    }

}

