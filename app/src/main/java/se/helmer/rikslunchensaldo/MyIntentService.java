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
    public static final int TRANSACTION_DELETE = 3;

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
        DbSession session = new DbSession(getApplicationContext());

        switch (msgCommand) {
            case CHECK_DATABASE:
                // First check if there is a card already
                Card card = session.getCard();
                if (card == null) {
                    Bundle prefs = Utils.getSharedPreferences(getApplicationContext());
                    String cardId = prefs.getString(RikslunchenDatasource.CARD_ID, "");
                    if (cardId.length() > 0) { // Upgrade from previous version
                        card = session.createOrUpdateCard(prefs);
                        Utils.info(TAG, "Upgrading: Using card id " + cardId +
                                " listed in shared preferences");
                        Utils.clearPreferences(getApplicationContext()); // Prefer database from now on
                    }
                }
                if (card != null) {
                    b = session.fetchSummary(getApplicationContext());
                }


                break;
            case SEND_BALANCE:
                Card card2 = session.createOrUpdateCard(b);
                Transaction t = session.createOrUpdateTransaction(b);
                b = session.fetchSummary(getApplicationContext());
                getApplicationContext().getContentResolver().notifyChange(Contract.Transaction.contentUri, null);
                break;

            case TRANSACTION_DELETE:
                int id = b.getInt(Contract.Transaction._ID);
                session.deleteTransaction(id);
                getApplicationContext().getContentResolver().notifyChange(Contract.Transaction.contentUri, null);
                break;

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
