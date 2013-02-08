package se.helmer.rikslunchensaldo;

import android.app.Activity;
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

	private static final String APP_SHARED_PREFS = "se.helmer.riksbankensaldo_prefs";

    String mCard_id = "32271843";

    TextView mBalanceField;
    TextView mLasttopupField;
    EditText mCardIdField;

    ResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mSharedPrefs =
        		getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.mPrefsEditor = mSharedPrefs.edit();

        mBalanceField = (TextView) findViewById(R.id.balance);
        mBalanceField.setText(mSharedPrefs.getString("balance", ""));
        mLasttopupField = (TextView) findViewById(R.id.topupdate);
        mLasttopupField.setText(mSharedPrefs.getString("lasttopupdate", ""));
        mCardIdField = (EditText) findViewById(R.id.editCardId);
        mCardIdField.setText(mSharedPrefs.getString("card_id", ""));


        Button b = (Button) findViewById(R.id.button_fetch);

        mReceiver = new MyActivityResultReceiver(new Handler());

        b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPrefsEditor.putString("card_id", mCardIdField.getText().toString());
				mPrefsEditor.commit();
		        RikslunchenDatasource rd = new RikslunchenDatasource();
				rd.query(MainActivity.this,mReceiver,mCard_id);
			}
		});
    }

    class MyActivityResultReceiver extends ResultReceiver {
    	public MyActivityResultReceiver(Handler handler) {
            super(handler);
        }

    	@Override
    	protected void onReceiveResult(int resultCode, Bundle b) {

    		mBalanceField.setText(b.getString("balance"));
    		mLasttopupField.setText(b.getString("lasttopupdate"));

    		mPrefsEditor.putString("balance",b.getString("balance"));
    		mPrefsEditor.putString("lasttopupdate",b.getString("lasttopupdate"));
    		mPrefsEditor.commit();
    	}
    }
}

