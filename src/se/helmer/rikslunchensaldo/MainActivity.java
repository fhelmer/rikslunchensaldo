package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static String TAG = "MainActivity";
	private SharedPreferences mSharedPrefs;
	private Editor mPrefsEditor;

	public static final String APP_SHARED_PREFS = "se.helmer.riksbankensaldo_prefs";

    // String mCard_id; //  = "32271843";

    TextView mBalanceField;
    TextView mLasttopupField;
    EditText mCardIdField;
    TextView mLatestUpdate;

    ResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mSharedPrefs =
                getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.mPrefsEditor = mSharedPrefs.edit();

        mBalanceField = (TextView) findViewById(R.id.balance);
        mLasttopupField = (TextView) findViewById(R.id.topupdate);
        mCardIdField = (EditText) findViewById(R.id.editCardId);
        mLatestUpdate = (TextView) findViewById(R.id.latestupdate);

        Button b = (Button) findViewById(R.id.button_fetch);

        mReceiver = new MyActivityResultReceiver(new Handler());

        b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    mLatestUpdate.setText("Querying...");
				mPrefsEditor.putString("card_id", mCardIdField.getText().toString());
				mPrefsEditor.commit();
		        RikslunchenDatasource rd = new RikslunchenDatasource();
				rd.query(MainActivity.this,mReceiver,mCardIdField.getText().toString());

				updateWidget(RikslunchenWidgetProvider.class,R.xml.widget_info);
				updateWidget(RikslunchenWidgetProviderSmall.class,R.xml.small_widget_info);
			}
		});

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        mBalanceField.setText(mSharedPrefs.getString(RikslunchenDatasource.BALANCE, ""));
        mLasttopupField.setText(mSharedPrefs.getString(RikslunchenDatasource.LAST_TOPUP_DATE, ""));
        mCardIdField.setText(mSharedPrefs.getString(RikslunchenDatasource.CARD_ID, ""));
        mLatestUpdate.setText(mSharedPrefs.getString(RikslunchenDatasource.LATEST_UPDATE, ""));


    }

    void updateWidget(Class<?> cls, int widgetid) {
        Intent intent = new Intent(this,cls);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = {widgetid};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    class MyActivityResultReceiver extends ResultReceiver {
    	public MyActivityResultReceiver(Handler handler) {
            super(handler);
        }

    	@Override
    	protected void onReceiveResult(int resultCode, Bundle b) {

            mBalanceField.setText(b.getString(RikslunchenDatasource.BALANCE));
            mLasttopupField.setText(b
                    .getString(RikslunchenDatasource.LAST_TOPUP_DATE));
            mLatestUpdate.setText(b
                    .getString(RikslunchenDatasource.LATEST_UPDATE));

            Utils.saveSharedPreferences(MainActivity.this, b);
    	}
    }
}

