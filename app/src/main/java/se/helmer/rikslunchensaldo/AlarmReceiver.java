package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import se.helmer.rikslunchensaldo.model.Card;

public class AlarmReceiver extends BroadcastReceiver {

    public static String TAG = "Rikslunchensaldo.AlarmReceiver";
    public static String FETCH = "se.helmer.rikslunchensaldo.FETCH";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Call rikslunchen and update shared properties

        String action = intent.getAction();
        if(FETCH.equals(action)) {
            boolean autoupdate = false; // sharedPrefs.getBoolean(RikslunchenDatasource.AUTO_UPDATE, false);

            DbSession session = new DbSession(context);
            Card card = session.getCard();
            String cardId = card.getCardId();
            RikslunchenDatasource rd = new RikslunchenDatasource();
            ResultReceiver receiver = new AlarmResultReceiver(new Handler(),context);
            Utils.log(TAG, "About to fetch using " + cardId + " autoupdate=" + autoupdate);
            if (cardId.length() > 0 && autoupdate) {
                rd.query(context,cardId);
            }

        }
    }

}