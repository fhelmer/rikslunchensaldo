package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static String TAG = "MainActivity";

    String mCard_id = "32271843";

    TextView mBalanceField;
    TextView mLasttopupField;

    ResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBalanceField = (TextView) findViewById(R.id.balance);
        mLasttopupField = (TextView) findViewById(R.id.topupdate);

        Button b = (Button) findViewById(R.id.button_fetch);

        mReceiver = new MyActivityResultReceiver(new Handler());

        b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(GetBalanceIntentService.GETBALANCE_INTENT,
						null, MainActivity.this,
		                GetBalanceIntentService.class);

		        intent.putExtra("receiver", mReceiver);
		        intent.putExtra("card_id", mCard_id);
		        ComponentName cm = startService(intent);
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
    	}
    }
}

