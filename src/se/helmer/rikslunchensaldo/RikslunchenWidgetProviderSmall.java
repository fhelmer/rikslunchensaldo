package se.helmer.rikslunchensaldo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

public class RikslunchenWidgetProviderSmall extends AppWidgetProvider {

  private static String TAG = "RikslunchenWidgetProviderSmall";

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {

    Log.d(TAG,"Updating widget");
    Utils.updateWidgets(context, R.layout.small_widget_layout, appWidgetManager, appWidgetIds, RikslunchenWidgetProviderSmall.class);


  }

}
