package se.helmer.rikslunchensaldo;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.ResultReceiver;

public class MyIntentService extends IntentService {

    private static String TAG = "MyIntentService";

    public static final int CHECK_DATABASE = 1;

    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Utils.log(TAG, "onHandleIntent in");
        // Message.Command msgCommand = (Message.Command) intent.getSerializableExtra("command");
    //    final ResultReceiver receiver = intent.getParcelableExtra("receiver");
    }

    @Override
    public void onDestroy() {
        Utils.log(TAG, "Destroy intent service");
        super.onDestroy();
    }
}
