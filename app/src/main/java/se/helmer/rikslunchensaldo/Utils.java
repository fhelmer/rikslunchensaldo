package se.helmer.rikslunchensaldo;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import se.helmer.rikslunchensaldo.model.RikslunchenDatabaseHelper;
import se.helmer.rikslunchensaldo.model.Transaction;

public class Utils {

    private static String TAG = "Utils";

    static final String mDateFormat = "yyyy-MM-dd HH:mm:ss";

    static String getCurrentLocalTimeString() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat, Locale.US);
        return dateFormat.format(date);
    }

    public static Date LocalToGmt(Date localDate) {
        Utils.log(TAG,"localDateToGmt"+localDate);
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(mDateFormat, Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat dateFormatLocal = new SimpleDateFormat(mDateFormat, Locale.US);
        Date d = null;
        try {
            d = dateFormatLocal.parse(dateFormatGmt.format(localDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static Date GmtToLocal(Date gmtDate) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(mDateFormat, Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat dateFormatLocal = new SimpleDateFormat(mDateFormat, Locale.US);
        Date d = null;
        try {
            d = dateFormatGmt.parse(dateFormatLocal.format(gmtDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    static void createTestData(Context context) {
        createTransaction(context, "Setup GMT time", "888.23");
    }

    static void createTransaction(Context context, String description, String amount) {
        RikslunchenDatabaseHelper rikslunchenDatabaseHelper = OpenHelperManager.getHelper(context,
                RikslunchenDatabaseHelper.class);
        ConnectionSource connectionSource = new AndroidConnectionSource(rikslunchenDatabaseHelper);

        try {
            Dao<Transaction, Integer> transactionDao =
                    rikslunchenDatabaseHelper.getTransactionDao();
            Date localDate = new Date();
            Date gmtDate = Utils.LocalToGmt(localDate);
            Transaction transaction =
                    new Transaction(description, new BigDecimal(amount));
            transactionDao.create(transaction);
        } catch (SQLException se) {

        }
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
          Intent intent = new Intent(context, OldMainActivity.class);
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
        Log.i(tag, getThreadSignature());
    }
    public static void log(String tag,String msg) {
    	Log.i(tag,getThreadSignature()+":"+msg);
    }

}
