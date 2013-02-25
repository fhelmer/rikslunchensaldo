package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.RemoteViews;

public class RikslunchenWidgetProvider extends AppWidgetProvider {

  private static final String ACTION_CLICK = "ACTION_CLICK";
  private ResultReceiver mReceiver;
  private static String TAG = "RikslunchenWidgetProvider";
  private String mCardId = "-";

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {

    // String mCard_id; //  = "32271843";
	SharedPreferences sharedPrefs =
			context.getSharedPreferences(MainActivity.APP_SHARED_PREFS, Activity.MODE_PRIVATE);
    mCardId = sharedPrefs.getString("card_id", "");


    // Get all ids
    ComponentName thisWidget = new ComponentName(context,
        RikslunchenWidgetProvider.class);
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    for (int widgetId : allWidgetIds) {
      // Create some random data

      RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
          R.layout.widget_layout);

      mReceiver = new MyWidgetResultReceiver(new Handler(),
    		  context, remoteViews, appWidgetManager,widgetId);
      RikslunchenDatasource rd = new RikslunchenDatasource();

      rd.query(context,mReceiver,mCardId);

      // Register an onClickListener
      Intent intent = new Intent(context, RikslunchenWidgetProvider.class);

      intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

      PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
          0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
  }

    class MyWidgetResultReceiver extends ResultReceiver {
    	RemoteViews rv;
    	AppWidgetManager awm;
    	int widgetId;
    	Context context;

    	public MyWidgetResultReceiver(Handler h,
    			Context c,
    			RemoteViews rv1,
    			AppWidgetManager am,
    			int wi) {
    		super(h);
    		rv = rv1;
    		awm = am;
    		context = c;
    		widgetId = wi;
    	}

    	@Override
    	protected void onReceiveResult(int resultCode, Bundle b) {
    		String balance = b.getString("balance");
    		String lasttopupdate = b.getString("lasttopupdate");
    		String latestupdate = b.getString("latestupdate");
    		Log.d(TAG,"widget onReceiveResult"+balance+lasttopupdate);
    		rv.setTextViewText(R.id.widget_balance,
    						context.getString(R.string.balance) + " : " + balance);
    		rv.setTextViewText(R.id.widget_lasttopupdate,
    				context.getString(R.string.lasttopupdate) + " : " + lasttopupdate);
    		rv.setTextViewText(R.id.widget_card_id,
    				context.getString(R.string.card_id) + " : " + mCardId);
    		rv.setTextViewText(R.id.widget_latestupdate,
                    context.getString(R.string.latestupdate) + " : " + latestupdate);
    		awm.updateAppWidget(widgetId, rv);
    	}
  }

}
