package se.helmer.rikslunchensaldo;

import java.math.BigDecimal;
import java.util.Date;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import se.helmer.rikslunchensaldo.model.Card;
import se.helmer.rikslunchensaldo.model.Contract;
import se.helmer.rikslunchensaldo.model.Transaction;

public class MyIntentService extends IntentService {

    private static String TAG = "MyIntentService";

    public static final String COMMAND = "command";
    public static final String PAYLOAD = "payload";
    public static final String RECEIVER = "receiver";

    public static final int CHECK_DATABASE = 1;
    public static final int SEND_BALANCE = 2;

    public static final int RETURN_DATA = 10;

    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Utils.log(TAG, "onHandleIntent in");

        int msgCommand = intent.getIntExtra(COMMAND, 0);
        Bundle b = intent.getBundleExtra(PAYLOAD);
        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        DbSession session = new DbSession(getBaseContext());

        switch (msgCommand) {
            case CHECK_DATABASE:
                // If we have a balance + card id in shared preferences, populate database
                Bundle prefs = Utils.getSharedPreferences(this);
                String cardId = prefs.getString(RikslunchenDatasource.CARD_ID, "");
                String latestupdatestr ="";
                Card card = null;
                if (cardId.length() > 0) { // Upgrade from previous version
                    card = session.createOrUpdateCard(prefs);
                }

                b = session.fetchSummary(getBaseContext(), card.getCardId());


                break;
            case SEND_BALANCE:
                String message = b.getString(RikslunchenDatasource.MESSAGE);
                if (message == null) {
                    message = "";
                }
                String balance = b.getString(RikslunchenDatasource.BALANCE);
                Transaction t = session.createOrUpdateTransaction(
                        Utils.getCurrentLocalTimeString()+" "+message,
                        new BigDecimal(balance.replace(',','.')));
                getApplicationContext().getContentResolver().notifyChange(Contract.Transaction.contentUri, null);

            default:
                break;
        }

        session.close();
        if (receiver != null) {
            receiver.send(RETURN_DATA,b);
        }


    }

    @Override
    public void onDestroy() {
        Utils.log(TAG, "Destroy intent service");
        super.onDestroy();
    }
}
