package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.app.NotificationManager;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import se.helmer.rikslunchensaldo.model.Card;
import se.helmer.rikslunchensaldo.model.Contract;

public class Utils {

    private static String TAG = "Utils";

    public static final String mDateFormat = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentLocalTimeString() {
        return getFormattedTimeString(new Date());
    }

    public static String getFormattedTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat, Locale.US);
        return dateFormat.format(date);
    }


    public static void queryRikslunchen(Context context, String cardId) {
        Bundle b = new Bundle();
        b.putString(Contract.Card.CARD_ID, cardId);

        final Intent intent = new Intent(context, MyIntentService.class);
        intent.putExtra(MyIntentService.COMMAND, MyIntentService.QUERY_RIKSLUNCHEN_THEN_UPDATE);
        intent.putExtra(MyIntentService.RECEIVER, MainActivity.getReceiver());
        intent.putExtra(MyIntentService.PAYLOAD, b);
        MyIntentService.enqueueWork(context, intent);
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

    static Bundle getSharedPreferences(Context context) {
        Bundle b = new Bundle();
        SharedPreferences sharedPrefs =
                context.getSharedPreferences(SummaryFragment.APP_SHARED_PREFS,
                        Activity.MODE_PRIVATE);

        b.putString(Contract.BankStatement.BALANCE, sharedPrefs.getString(Contract.BankStatement.BALANCE, ""));
        b.putString(Contract.Card.LAST_TOPUP_DATE, sharedPrefs.getString(Contract.Card.LAST_TOPUP_DATE, ""));
        b.putString(Contract.BankStatement.DATE, sharedPrefs.getString("latestupdate", ""));
        b.putString(Contract.Card.CARD_ID, sharedPrefs.getString(Contract.Card.CARD_ID, ""));

        return b;
    }

    // Previous version used shared preferences. When migrating to database, the preferences
    // are no longer needed
    static void clearPreferences(Context context) {
        info(context, TAG, "Clearing preferences");
        Bundle b = new Bundle();
        SharedPreferences sharedPrefs =
                context.getSharedPreferences(SummaryFragment.APP_SHARED_PREFS, Activity.MODE_PRIVATE);

        Editor prefsEditor = sharedPrefs.edit();
        b.putString(Contract.Card.CARD_ID,"");
        prefsEditor.apply();
    }

    static void updateWidgets(Context context, int layout, AppWidgetManager appWidgetManager, int[] appWidgetIds, Class<?> cls) {

        DbSession session = new DbSession(context);
        Card card  = session.getCard();
        if (card == null) {
            return;
        }
        String cardId = card.getCardId();

        // Get all ids for this class
        ComponentName thisWidget = new ComponentName(context,cls);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
          // Create some random data

          RemoteViews remoteViews = new RemoteViews(context.getPackageName(),layout);

          ResultReceiver receiver = new MyWidgetResultReceiver(new Handler(),
                  context, remoteViews, appWidgetManager,widgetId,cardId);

          String latestupdatestr = card.getLastTopupDate();

          // Get contents from shared preferences and send to widget(s)
            Bundle b = session.fetchSummary();
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

    static public void refreshWidgets(Context context) {
        Intent updateWidgetIntent = new Intent(context, RikslunchenWidgetProvider.class);
        updateWidgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {R.xml.widget_info};
        updateWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(updateWidgetIntent);

        Intent updateSmallWidgetIntent = new Intent(context, RikslunchenWidgetProviderSmall.class);
        updateSmallWidgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] idsmall = {R.xml.small_widget_info};
        updateSmallWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idsmall);
        context.sendBroadcast(updateSmallWidgetIntent);
    }


    public static void sendNotification(Context context, String message) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("LunchSaldo update")
                        .setContentText(message);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
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

    public static void error(String tag, String message) {
        log(tag, "ERROR:"+message);
    }

    public static void warning(String tag, String message) {
        log(tag, "WARNING:"+message);
    }

    public static void info(Context context, String tag, String message) {
        log(tag, "INFO:"+message);
    }

    public static void debug(String tag, String message) {
        log(tag, message);
    }


    public static void logThreadSignature(String tag)
    {
        Log.i(tag, getThreadSignature());
    }
    public static void log(String tag,String msg) {
        Log.i(tag, getThreadSignature() + ":" + msg);
    }

    public static void logToDatabase(Context context, String tag,String msg) {
        Bundle b = new Bundle();
        b.putString(Contract.LogMessage.DESCRIPTION, msg);

        final Intent intent = new Intent(context, MyIntentService.class);
        intent.putExtra(MyIntentService.COMMAND, MyIntentService.LOG_MESSAGE_SAVE);
        intent.putExtra(MyIntentService.RECEIVER, MainActivity.getReceiver());
        intent.putExtra(MyIntentService.PAYLOAD, b);
        MyIntentService.enqueueWork(context, intent);

    }

}
