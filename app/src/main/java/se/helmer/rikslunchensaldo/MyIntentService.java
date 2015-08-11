package se.helmer.rikslunchensaldo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.util.ArrayList;
import java.util.List;

import se.helmer.rikslunchensaldo.model.BankStatement;
import se.helmer.rikslunchensaldo.model.Card;
import se.helmer.rikslunchensaldo.model.Contract;

public class MyIntentService extends IntentService {

    private static String TAG = "MyIntentService";

    public static final String COMMAND = "command";
    public static final String PAYLOAD = "payload";
    public static final String RECEIVER = "receiver";

    public static final int CHECK_DATABASE = 1;
    public static final int SEND_BALANCE = 2;
    public static final int BANKSTATEMENT_UPDATE = 3;
    public static final int BANKSTATEMENT_DELETE = 4;
    public static final int LOG_MESSAGE_SAVE = 5;
    public static final int LOG_MESSAGE_DELETE_ALL = 6;
    public static final int CARD_UPDATE = 7;

    public static final int RETURN_DATA = 100;

    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Utils.log(TAG, "onHandleIntent in");

        int msgCommand = intent.getIntExtra(COMMAND, 0);
        Bundle b = intent.getBundleExtra(PAYLOAD);
        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        Context context = getApplicationContext();
        DbSession session = new DbSession(context);

        switch (msgCommand) {
            case CHECK_DATABASE:
                // First check if there is a card already
                Card card = session.getCard();
                if (card == null) {
                    Bundle prefs = Utils.getSharedPreferences(getApplicationContext());
                    String cardId = prefs.getString(RikslunchenDatasource.CARD_ID, "");
                    if (cardId.length() > 0) { // Upgrade from previous version
                        card = session.createOrUpdateCard(prefs);
                        session.createLogMessage("Upgrading database: Using card id " + cardId +
                                " listed in shared preferences");
                        Utils.clearPreferences(getApplicationContext()); // Prefer database from now on
                    }
                }
                if (card != null) {
                    b = session.fetchSummary(getApplicationContext());
                }
                break;
            case CARD_UPDATE:
                session.createLogMessage("Saving card id=" +
                        b.getString(Contract.Card.CARD_ID));
                Card card2 = session.createOrUpdateCard(b);
                b = session.fetchSummary(getApplicationContext());
                break;
            case SEND_BALANCE:
                session.createLogMessage("Rikslunchen balance=" +
                        b.getString(RikslunchenDatasource.BALANCE));
                Card card3 = session.createOrUpdateCard(b);
                BankStatement t = session.createOrUpdateBankStatement(b);
                b = session.fetchSummary(getApplicationContext());
                break;

            case BANKSTATEMENT_UPDATE:
                session.createLogMessage("Updating bankstatement " +
                        b.getInt(Contract.BankStatement._ID));
                session.updateBankStatement(b);
                break;

            case BANKSTATEMENT_DELETE:
                int id = b.getInt(Contract.BankStatement._ID);
                session.deleteBankStatement(id);
                session.createLogMessage("Deleted transaction " + id);
                break;

            case LOG_MESSAGE_SAVE:
                session.createLogMessage(b);
                break;

            case LOG_MESSAGE_DELETE_ALL:
                session.deleteLogMessages();
                break;
            default:
                break;
        }

        getApplicationContext().getContentResolver().notifyChange(Contract.BankStatement.contentUri, null);
        getApplicationContext().getContentResolver().notifyChange(Contract.LogMessage.contentUri, null);

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
