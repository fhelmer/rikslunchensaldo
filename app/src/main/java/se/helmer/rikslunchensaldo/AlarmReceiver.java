package se.helmer.rikslunchensaldo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;

import se.helmer.rikslunchensaldo.model.Card;

public class AlarmReceiver extends BroadcastReceiver {

    public static String TAG = "Rikslunchensaldo.AlarmReceiver";
    public static String FETCH = "se.helmer.rikslunchensaldo.FETCH";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        DbSession session = new DbSession(context);
        session.createLogMessage("Processing alarm");
        if (FETCH.equals(action)) {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
            int updateFrequency = SP.getInt(Preferences.UPDATE_FREQUENCY, 0);
            Card card = session.getCard();
            if (updateFrequency > 0 && (card != null)) {
                String cardId = card.getCardId();
                RikslunchenDatasource rd = new RikslunchenDatasource();
                ResultReceiver receiver = new AlarmResultReceiver(new Handler(), context);
                session.createLogMessage(TAG + "About to fetch using " + cardId +
                        " autoupdate=" + updateFrequency);
                if (cardId.length() > 0)
                    rd.query(context, cardId);
            }
        }
        session.close();
    }
}

