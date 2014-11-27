package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public static String TAG = "Rikslunchensaldo.AlarmReceiver";
    public static String FETCH = "se.helmer.rikslunchensaldo.FETCH";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Call rikslunchen and update shared properties

        String action = intent.getAction();
        if(FETCH.equals(action)) {
            SharedPreferences sharedPrefs =
                    context.getSharedPreferences(PrevMainActivity.APP_SHARED_PREFS, Activity.MODE_PRIVATE);
            String cardId = sharedPrefs.getString(RikslunchenDatasource.CARD_ID, "");
            boolean autoupdate = sharedPrefs.getBoolean(RikslunchenDatasource.AUTO_UPDATE, false);

            Log.d(TAG,"Fetching balance from server using "+cardId+" "+Utils.getCurrentTime());

            RikslunchenDatasource rd = new RikslunchenDatasource();
            ResultReceiver receiver = new AlarmResultReceiver(new Handler(),context);
            Log.d(TAG,"About to fetch using "+cardId+" autoupdate="+autoupdate);
            if (cardId.length() > 0 && autoupdate) {
                rd.query(context,receiver,cardId);
            }

        }
    }

}