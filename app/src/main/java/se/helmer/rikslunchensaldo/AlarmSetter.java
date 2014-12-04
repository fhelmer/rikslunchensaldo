package se.helmer.rikslunchensaldo;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;


public class AlarmSetter extends BroadcastReceiver {

    private static String TAG = "Rikslunchen.AlarmSetter";

    @Override
    public void onReceive(Context context, Intent intent1) {
        // This is called on boot-complete, so here we set up the repeating auto updates
        Log.d(TAG,"onReceive in");
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.FETCH);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        // We also fetch balance on boot complete?
        RikslunchenDatasource rd = new RikslunchenDatasource();
        ResultReceiver receiver = new AlarmResultReceiver(new Handler(),context);
        SharedPreferences sharedPrefs =
                context.getSharedPreferences(SummaryFragment.APP_SHARED_PREFS, Activity.MODE_PRIVATE);

        String cardId = sharedPrefs.getString(RikslunchenDatasource.CARD_ID, "");
        Log.d(TAG,"About to fetch using "+cardId);
        if (false && cardId.length() > 0) {
            rd.query(context,receiver,cardId);
        }
    }


}