package se.helmer.rikslunchensaldo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import se.helmer.rikslunchensaldo.model.RikslunchenDatabaseHelper;
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

        switch (msgCommand) {
            case CHECK_DATABASE:
                // If we have a balance + card id in shared preferences, populate database
                SharedPreferences sharedPrefs =
                        getApplicationContext().getSharedPreferences(
                                SummaryFragment.APP_SHARED_PREFS, Activity.MODE_PRIVATE);

                String cardId = sharedPrefs.getString(RikslunchenDatasource.CARD_ID, "");
                String latestupdatestr ="";
                if (cardId.length() > 0) {
                    // Create transaction with latest + delete preferences
                    /*
        mBalanceField.setText(mSharedPrefs.getString(RikslunchenDatasource.BALANCE, ""));
        mLasttopupField.setText(mSharedPrefs.getString(RikslunchenDatasource.LAST_TOPUP_DATE, ""));
        mCardIdField.setText(mSharedPrefs.getString(RikslunchenDatasource.CARD_ID, ""));
        mLatestUpdate.setText(mSharedPrefs.getString(RikslunchenDatasource.LATEST_UPDATE, ""));
        mAutoUpdate.setChecked(mSharedPrefs.getBoolean(RikslunchenDatasource.AUTO_UPDATE, false));
        */

                    /* Bundle b = new Bundle();
                    b.putString(RikslunchenDatasource.BALANCE, sharedPrefs.getString(RikslunchenDatasource.BALANCE, ""));
                    b.putString(RikslunchenDatasource.LAST_TOPUP_DATE, sharedPrefs.getString(RikslunchenDatasource.LAST_TOPUP_DATE, ""));
                    b.putString(RikslunchenDatasource.LATEST_UPDATE, latestupdatestr);
                    */


                }

                break;
            case SEND_BALANCE:
                Date updated = (Date) b.getSerializable(RikslunchenDatasource.LATEST_UPDATE);
                Utils.createTransaction(getBaseContext(),"Updated: " + updated,
                        b.getString(RikslunchenDatasource.BALANCE));

            default:
                break;
        }


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
