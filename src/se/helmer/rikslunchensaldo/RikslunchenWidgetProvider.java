package se.helmer.rikslunchensaldo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.RemoteViews;

public class RikslunchenWidgetProvider extends AppWidgetProvider {

  private static final String ACTION_CLICK = "ACTION_CLICK";

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {

    String card_id = "32271843";

    // Get all ids
    ComponentName thisWidget = new ComponentName(context,
        RikslunchenWidgetProvider.class);
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    for (int widgetId : allWidgetIds) {
      // Create some random data

      RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
          R.layout.widget_layout);

      /*
      MyWidgetResultReceiver receiver = new MyWidgetResultReceiver(new Handler(),
    		  context, remoteViews, appWidgetManager);

      final Intent gbintent = new Intent(GetBalanceIntentService.GETBALANCE_INTENT,
    		  null, context,
    		  GetBalanceIntentService.class);

      gbintent.putExtra("receiver", receiver);
      gbintent.putExtra("card_id", card_id);
      ComponentName cm = context.startService(gbintent);
       */

      // Register an onClickListener
      Intent intent = new Intent(context, RikslunchenWidgetProvider.class);

      intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

      PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
          0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.balance, pendingIntent);
      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
  }

    class MyWidgetResultReceiver extends ResultReceiver {
    	RemoteViews rv;
    	AppWidgetManager awm;
    	int widgetId;
    	Context context;

    	public MyWidgetResultReceiver(Handler h,Context c, RemoteViews rv1, AppWidgetManager am) {
    		super(h);
    		rv = rv1;
    		awm = am;
    		context = c;
    	}

    	@Override
    	protected void onReceiveResult(int resultCode, Bundle b) {
    		String balance = b.getString("balance");
    		String lasttopupdate = b.getString("lasttopupdate");
    				rv.setTextViewText(R.id.widget_balance,
    						context.getString(R.string.balance) + ":" + balance);
    		rv.setTextViewText(R.id.widget_lasttopupdate,
    				context.getString(R.string.lasttopupdate) + ":" + lasttopupdate);
    		rv.setTextViewText(R.id.widget_card_id,
    				context.getString(R.string.card_id) + ":" + "CARDID");
    		awm.updateAppWidget(widgetId, rv);
    	}
  }

}
