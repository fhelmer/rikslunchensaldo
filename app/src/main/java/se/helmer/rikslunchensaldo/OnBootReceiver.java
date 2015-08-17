package se.helmer.rikslunchensaldo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = "OnBootReceiver";

    public static void setAlarm(Context ctxt) {
        AlarmManager mgr = (AlarmManager) ctxt
                .getSystemService(Context.ALARM_SERVICE);

        String[] entryValues = ctxt.getResources().getStringArray(
                R.array.frequencyvalues_list_preference);

        String[] entries = ctxt.getResources().getStringArray(
                R.array.frequency_list_preference);

        long[] values = { 0,
                AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HALF_DAY,
                AlarmManager.INTERVAL_DAY };

        HashMap<String, Long> entryValueIntervals = new HashMap<String, Long>();
        HashMap<String, String> entryValuesMap = new HashMap<String, String>();
        for (int i = 0; i < entryValues.length; i++) {
            entryValueIntervals.put(entryValues[i], values[i]);
            entryValuesMap.put(entryValues[i], entries[i]);
        }

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ctxt);

        // never, hour, halfday, daily
        String frequency = prefs.getString(Preferences.UPDATE_FREQUENCY,
                entryValues[0]);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);
        String startTime = prefs.getString(Preferences.START_TIME, "");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        try {
            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(formatter.parse(startTime));
            cal.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        } catch (ParseException pe) {
            startTime = formatter.format(cal.getTime());
        }

        long freq = entryValueIntervals.get(frequency);

        SharedPreferences.Editor editor = prefs.edit();

        DbSession session = new DbSession(ctxt);
        if (freq == 0) {
            cancelAlarm(ctxt);
            editor.putString(Preferences.START_TIME, "");
            editor.putString(Preferences.UPDATE_FREQUENCY, entryValues[0]);
            Log.d(TAG, "Cancelling alarm");
            session.createLogMessage("Cancelling alarm");
        } else {
            mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    freq, getPendingIntent(ctxt));

            editor.putString(Preferences.START_TIME, startTime);
            editor.putString(Preferences.UPDATE_FREQUENCY, frequency);
            Log.d(TAG, "setRepeating alarm=" + cal + " " + frequency);
            session.createLogMessage("Set repeating alarm to start " + cal.getTime() +
                    " then " + entryValuesMap.get(frequency));
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
        i.setAction(AlarmReceiver.FETCH);

        return (PendingIntent.getBroadcast(ctxt, 0, i, 0));
    }

    @Override
    public void onReceive(Context context, Intent intent1) {
        setAlarm(context);
    }
}