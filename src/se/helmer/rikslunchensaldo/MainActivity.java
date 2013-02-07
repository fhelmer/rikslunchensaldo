package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static String TAG = "MainActivity";
    String card_id = "32271843";
    static String cardbalance = "";
    static String lasttopdate = "";

    TextView balanceField;
    TextView lasttopupField;
    RikslunchenDatasource rd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rd = new RikslunchenDatasource(this);
        rd.setResultCallback(new MyResultCallback() );

        balanceField = (TextView) findViewById(R.id.balance);
        lasttopupField = (TextView) findViewById(R.id.topupdate);

        Button b = (Button) findViewById(R.id.button_fetch);
        b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// save data
				rd.query(MainActivity.this,card_id);
			}
		});
    }

    class MyResultCallback implements UpdateResult {
    	@Override
    	public void update(final int viewId, final String data) {
    		Log.d(TAG,"MainActivity update in "+data);
    		runOnUiThread(new Runnable() {
    			@Override
    			public void run() {

    				switch(viewId) {
    				case  R.id.widget_balance:
    					balanceField.setText(data);
    					break;
    				case R.id.widget_lasttopupdate:
    					lasttopupField.setText(data);
    					break;
    				}
    			}
    		});
    		Log.d(TAG,"MainActivity update out");

    	}
    }
}

