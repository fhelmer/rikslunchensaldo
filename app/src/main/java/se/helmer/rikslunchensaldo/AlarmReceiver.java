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

    public final static String TAG = "Rikslunchensaldo.AlarmReceiver";
    public final static String FETCH = "se.helmer.rikslunchensaldo.FETCH";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        DbSession session = new DbSession(context);
        if (FETCH.equals(action)) {
            Card card = session.getCard();
            String cardId = card.getCardId();
            RikslunchenDatasource rd = new RikslunchenDatasource();
            if (cardId.isEmpty()) {
                session.createLogMessage("No card Id specified. Check menu -> Enter Card ID");
            } else {
                session.createLogMessage("Automatic query of card " + cardId);
                rd.query(context.getApplicationContext(), cardId);
            }
        }
        session.close();
    }
}

