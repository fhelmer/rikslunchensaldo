package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

class AlarmResultReceiver extends ResultReceiver {
    private static String TAG = "AlarmResultReceiver";
    private final Context mContext;
    public AlarmResultReceiver(Handler handler, Context context) {
        super(handler);
        mContext = context;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle b) {
        Log.d(TAG,"Boot complete saving shared preferences");
        Utils.saveSharedPreferences(mContext, b);
    }
}

