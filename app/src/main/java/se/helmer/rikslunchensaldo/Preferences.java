package se.helmer.rikslunchensaldo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.security.Permission;
import java.util.HashMap;

public class Preferences extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener
{

    static String TAG = "Preferences";

    // Some settings related constants. Check preferences.xml
    public static final String SHOW_NOTIFICATIONS = "show_notifications";
    public static final String UPDATE_FREQUENCY = "update_frequency_list";
    public static final String START_TIME = "start_time";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        updateSummary();
    }

    private void updateSummary()
    {
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();

        Preference pref = findPreference(Preferences.UPDATE_FREQUENCY);

        String frequency = prefs
                .getString(Preferences.UPDATE_FREQUENCY, "never");

        String[] entries = getActivity().getResources().getStringArray(
                R.array.frequency_list_preference);
        String[] entryValues = getActivity().getResources().getStringArray(
                R.array.frequencyvalues_list_preference);

        HashMap<String, String> entryValuesMap = new HashMap<String, String>();
        for (int i = 0; i < entryValues.length; i++) {
            entryValuesMap.put(entryValues[i], entries[i]);
        }
        pref.setSummary(entryValuesMap.get(frequency));

        Preference startTimePref = findPreference(Preferences.START_TIME);
        String startTime = prefs
                .getString(Preferences.START_TIME, "");
        startTimePref.setSummary(startTime);
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
        updateSummary();
   }
}