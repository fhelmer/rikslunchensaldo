package se.helmer.rikslunchensaldo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import se.helmer.rikslunchensaldo.model.Card;


public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = "OnBootReceiver";

    public static void setAlarm(Context ctxt) {
        AlarmManager mgr = (AlarmManager) ctxt
                .getSystemService(Context.ALARM_SERVICE);

        // Get current time

        Calendar cal = Calendar.getInstance();

        String[] entries = ctxt.getResources().getStringArray(
                R.array.frequencyvalues_list_preference);
        long[] values = { 0, AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HALF_DAY, AlarmManager.INTERVAL_DAY };
        HashMap<String, Long> hm = new HashMap<String, Long>();
        for (int i = 0; i < entries.length; i++) {
            hm.put(entries[i], values[i]);
        }

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ctxt);

        String frequency = prefs.getString(Preferences.UPDATE_FREQUENCY,
                entries[0]);

        // never, hour, halfday, daily

        Log.d(TAG, frequency);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.MINUTE, 1);
        }

        long freq = hm.get(frequency);

        SharedPreferences.Editor editor = prefs.edit();

        if (freq == 0) {
            cancelAlarm(ctxt);
            Log.d(TAG, "Cancelling alarm");
            editor.putString(Preferences.UPDATE_FREQUENCY,
                    "No automatic check (Select an update frequency to start)");
        } else {
            mgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    freq, getPendingIntent(ctxt));

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
            String t = formatter.format(cal.getTime());
            editor.putString(Preferences.UPDATE_FREQUENCY, String.format(
                    "Balance check frequency: %s (starting %s)", frequency, t));
            Log.d(TAG, "setRepeating alarm=" + cal + " " + frequency);
        }
        editor.apply();
    }

    public static void cancelAlarm(Context ctxt) {
        AlarmManager mgr = (AlarmManager) ctxt
                .getSystemService(Context.ALARM_SERVICE);

        mgr.cancel(getPendingIntent(ctxt));
    }

    private static PendingIntent getPendingIntent(Context ctxt) {
        Intent i = new Intent(ctxt, AlarmReceiver.class);

        return (PendingIntent.getBroadcast(ctxt, 0, i, 0));
    }

    @Override
    public void onReceive(Context context, Intent intent1) {
        setAlarm(context);
    }
}