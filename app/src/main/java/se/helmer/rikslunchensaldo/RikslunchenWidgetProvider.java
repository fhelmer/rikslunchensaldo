package se.helmer.rikslunchensaldo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

public class RikslunchenWidgetProvider extends AppWidgetProvider {

  private static String TAG = "RikslunchenWidgetProvider";


  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {
    Log.d(TAG,"Updating widget");
    Utils.updateWidgets(context, R.layout.widget_layout, appWidgetManager, appWidgetIds, RikslunchenWidgetProvider.class);

  }
}
