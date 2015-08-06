package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import se.helmer.rikslunchensaldo.model.Card;

public class AlarmReceiver extends BroadcastReceiver {

    public static String TAG = "Rikslunchensaldo.AlarmReceiver";
    public static String FETCH = "se.helmer.rikslunchensaldo.FETCH";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (FETCH.equals(action)) {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
            boolean auto_update = SP.getBoolean("auto_update", false);
            DbSession session = new DbSession(context);
            Card card = session.getCard();
            if (auto_update && (card != null)) {
                String cardId = card.getCardId();
                RikslunchenDatasource rd = new RikslunchenDatasource();
                ResultReceiver receiver = new AlarmResultReceiver(new Handler(), context);
                session.createLogMessage(TAG + "About to fetch using " + cardId +
                        " autoupdate=" + auto_update);
                if (cardId.length() > 0)
                    rd.query(context, cardId);
            }
        }
    }
}

