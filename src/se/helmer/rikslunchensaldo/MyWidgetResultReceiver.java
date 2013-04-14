package se.helmer.rikslunchensaldo;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.RemoteViews;

class MyWidgetResultReceiver extends ResultReceiver {
    RemoteViews rv;
    AppWidgetManager awm;
    int widgetId;
    Context context;
    String mCardId;

    private static String TAG = "MyWidgetResultReceiver";

    public MyWidgetResultReceiver(Handler h,
            Context c,
            RemoteViews rv1,
            AppWidgetManager am,
            int wi,
            String cardId) {
        super(h);
        rv = rv1;
        awm = am;
        context = c;
        widgetId = wi;
        mCardId = cardId;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle b) {
        String balance = b.getString(RikslunchenDatasource.BALANCE);
        String lasttopupdate = b.getString(RikslunchenDatasource.LAST_TOPUP_DATE);
        String latestupdate = b.getString(RikslunchenDatasource.LATEST_UPDATE);
        Log.d(TAG,"widget onReceiveResult"+balance+lasttopupdate);
        rv.setTextViewText(R.id.widget_balance,
                context.getString(R.string.balance) + " " + balance);
        rv.setTextViewText(R.id.widget_lasttopupdate,
                context.getString(R.string.lasttopupdate) +  " " + lasttopupdate);
        rv.setTextViewText(R.id.widget_card_id,
                context.getString(R.string.card_id) + " " + mCardId);
        rv.setTextViewText(R.id.widget_latestupdate,
                context.getString(R.string.latestupdate) + " " + latestupdate);
        Utils.saveSharedPreferences(context, b);
        awm.updateAppWidget(widgetId, rv);
    }
}
