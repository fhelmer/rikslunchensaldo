package com.example.rikslunchentest;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class RikslunchenWidgetProvider extends AppWidgetProvider {

  private static final String ACTION_CLICK = "ACTION_CLICK";
  final RikslunchenDatasource rds = new RikslunchenDatasource();

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {

    // Get all ids
    ComponentName thisWidget = new ComponentName(context,
        RikslunchenWidgetProvider.class);
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    for (int widgetId : allWidgetIds) {
      // Create some random data

      RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
          R.layout.widget_layout);

      rds.setResultCallback(new MyResultCallback(remoteViews,appWidgetManager,widgetId));

      rds.query(context, "");

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

      MyResultCallback(RemoteViews rv1, AppWidgetManager am, int wi) {
          rv = rv1;
          awm = am;
          widgetId = wi;
      }

      @Override
      public void update(int viewId, String value) {
          rv.setTextViewText(viewId, value);
          awm.updateAppWidget(widgetId, rv);

    }
  }

}
