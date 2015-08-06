package se.helmer.rikslunchensaldo;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import se.helmer.rikslunchensaldo.model.Card;


public class AlarmSetter extends BroadcastReceiver {

    private static String TAG = "Rikslunchen.AlarmSetter";

    @Override
    public void onReceive(Context context, Intent intent1) {
        // This is called on boot-complete, so here we set up the repeating auto updates
        // Also when switching the auto_update boolean, it needs to be called
        Log.d(TAG,"onReceive in");
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.FETCH);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance(); // Contains current time
        calendar.set(Calendar.HOUR, 14);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        int hours = SP.getInt("fetchInterval", 12);

        boolean autoUpdate = SP.getBoolean("auto_update", false);

        DbSession session = new DbSession(context);
        Card card = session.getCard();

        if (autoUpdate) {
            long interval = AlarmManager.INTERVAL_DAY;

            switch (hours) {
                case 1:
                    interval = AlarmManager.INTERVAL_HOUR;
                    break;
                case 12:
                    interval = AlarmManager.INTERVAL_HALF_DAY;
                    break;
                case 24:
                    interval = AlarmManager.INTERVAL_DAY;
                    break;

            }

            alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval,
                    pendingIntent);
            session.createLogMessage(TAG + " hours between fetch " + hours + calendar);

        }

    }


}