package se.helmer.rikslunchensaldo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class RikslunchenWidgetProvider extends AppWidgetProvider {

  private static final String ACTION_CLICK = "ACTION_CLICK";
  RikslunchenDatasource rds;

    @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {

    rds = new RikslunchenDatasource(context);
    String card_id = "32271843";

    // Get all ids
    ComponentName thisWidget = new ComponentName(context,
        RikslunchenWidgetProvider.class);
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    for (int widgetId : allWidgetIds) {
      // Create some random data

      RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
          R.layout.widget_layout);

      rds.setResultCallback(new MyResultCallback(context,remoteViews,appWidgetManager,widgetId));

      rds.query(context, card_id);

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

  class MyResultCallback implements UpdateResult {
      RemoteViews rv;
      AppWidgetManager awm;
      int widgetId;
      Context context;

      MyResultCallback(Context c, RemoteViews rv1, AppWidgetManager am, int wi) {
          rv = rv1;
          awm = am;
          widgetId = wi;
          context = c;
      }

      @Override
      public void update(int viewId, String value) {
    	  switch (viewId) {
    	  case R.id.widget_balance:
    		  rv.setTextViewText(viewId, context.getString(R.string.balance) + ":" + value);
    		  break;
    	  case R.id.widget_lasttopupdate:
    		  rv.setTextViewText(viewId, context.getString(R.string.lasttopupdate) + ":" + value);
    		  break;
    	  }



          awm.updateAppWidget(widgetId, rv);

    }
  }

}
