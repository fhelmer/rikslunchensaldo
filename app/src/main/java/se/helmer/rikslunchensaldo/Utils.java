package se.helmer.rikslunchensaldo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.RemoteViews;

public class Utils {

    private static String TAG = "Utils";

    static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.US);

    static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        String latestupdate = mDateFormat.format(cal.getTime());
        return latestupdate;
    }

    static void saveSharedPreferences(Context context, Bundle b) {
        Log.d(TAG,"Saving shared preferences");
        SharedPreferences sharedPrefs =
                context.getSharedPreferences(SummaryFragment.APP_SHARED_PREFS, Activity.MODE_PRIVATE);

        Editor prefsEditor = sharedPrefs.edit();

        prefsEditor.putString(RikslunchenDatasource.BALANCE,b.getString(RikslunchenDatasource.BALANCE));
        prefsEditor.putString(RikslunchenDatasource.LAST_TOPUP_DATE,b.getString(RikslunchenDatasource.LAST_TOPUP_DATE));
        prefsEditor.putString(RikslunchenDatasource.LATEST_UPDATE,b.getString(RikslunchenDatasource.LATEST_UPDATE));

        prefsEditor.commit();

    }

    static void updateWidgets(Context context, int layout, AppWidgetManager appWidgetManager, int[] appWidgetIds, Class<?> cls) {

        SharedPreferences sharedPrefs =
                context.getSharedPreferences(SummaryFragment.APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        String cardId = sharedPrefs.getString(RikslunchenDatasource.CARD_ID, "");

        // Get all ids for this class
        ComponentName thisWidget = new ComponentName(context,cls);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
          // Create some random data

          RemoteViews remoteViews = new RemoteViews(context.getPackageName(),layout);

          ResultReceiver receiver = new MyWidgetResultReceiver(new Handler(),
                  context, remoteViews, appWidgetManager,widgetId,cardId);

          String latestupdatestr = sharedPrefs.getString(RikslunchenDatasource.LATEST_UPDATE, "");
          Log.d(TAG,"updateWidgets: latestupdatestr=" + latestupdatestr + " cardId="+cardId);

          // Get contents from shared preferences and send to widget(s)
          Bundle b = new Bundle();
          b.putString(RikslunchenDatasource.BALANCE, sharedPrefs.getString(RikslunchenDatasource.BALANCE, ""));
          b.putString(RikslunchenDatasource.LAST_TOPUP_DATE, sharedPrefs.getString(RikslunchenDatasource.LAST_TOPUP_DATE, ""));
          b.putString(RikslunchenDatasource.LATEST_UPDATE, latestupdatestr);
          receiver.send(0,b);

          // Register an onClickListener
          Intent intent = new Intent(context, MainActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

          intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
          intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
          intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

          PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
          remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
          appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        Log.d(TAG,"updateWidgets done");
    }

    public static long getThreadId()
    {
        Thread t = Thread.currentThread();
        return t.getId();
    }
    public static String getThreadSignature()
    {
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();
        long p = t.getPriority();
        String gname = t.getThreadGroup().getName();
        return "N="+name + ",id=" + l + ",p=" + p
                + ":TGN=" + gname;
    }
    public static void logThreadSignature(String tag)
    {
        Log.d(tag, getThreadSignature());
    }
    public static void log(String tag,String msg) {
    	Log.d(tag,getThreadSignature()+":"+msg);
    }

    public static String getTimestamp() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.US);
        String result = sdf.format(cal.getTime());
        return result;
    }

}
