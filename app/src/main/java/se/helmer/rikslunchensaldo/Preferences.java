package se.helmer.rikslunchensaldo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class Preferences extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener
{

    static String TAG = "Preferences";
    // Some settings related constants

    public static final String SHOW_NOTIFICATIONS = "show_notifications_update";
    public static final String UPDATE_FREQUENCY = "update_frequency_list";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        updateSummary();
    }

    public void updateSummary()
    {
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();

        Preference pref = findPreference(Preferences.UPDATE_FREQUENCY);
        String frequency = prefs
                .getString(Preferences.UPDATE_FREQUENCY, "never");
        pref.setSummary(frequency);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        OnBootReceiver.setAlarm(getActivity());
   }
}