package se.helmer.rikslunchensaldo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    // Some settings related constants

    public static final String SHOW_NOTIFICATIONS = "show_notifications_update";
    public static final String AUTO_UPDATE = "auto_update";
    public static final String START_TIME = "start_time";
    public static final String FETCH_INTERVAL = "fetchInterval";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}