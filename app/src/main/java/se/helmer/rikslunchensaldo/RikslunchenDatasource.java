package se.helmer.rikslunchensaldo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Date;

public class RikslunchenDatasource {

	private static String TAG = "RikslunchenDatasource";

    private String cardId;

	public static String BALANCE = "balance";
	public static String LAST_TOPUP_DATE  = "lasttopupdate";
	public static String LATEST_UPDATE = "latestupdate";
	public static String CARD_ID = "card_id";
    public static String MESSAGE = "message";

    public void query(Context c, final String card_id) {
        Log.d(TAG,"Querying "+card_id);
        this.cardId = card_id;
        if (card_id.length() == 0) {
            return;
        }
        WebView webView = new WebView(c);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyAccessor(c), "accessor");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            	Log.d(TAG,"onPageFinished:"+url+card_id);

                view.loadUrl("javascript:" +
                        "var public_key = document.getElementById('public_key').value;"+
                        "accessor.test(public_key);"+
                        "cardUtil.getCardData('"+card_id+"',public_key, "
                        + "function(cardInfo) {"
                        + "        accessor.setData(cardInfo.valid,cardInfo.message,cardInfo.balance,cardInfo.lastTopupDateStr);"
                        + "        return true;" + " });");

            }
        });

        webView.loadUrl("http://www.rikslunchen.se/riks-cp/check_balance.html");
    }

    class MyAccessor {
    	private final Context context;

        public MyAccessor(Context c) {
        	context = c;
        }

        @JavascriptInterface
        public void test(String txt) {
            Log.d(TAG,"test:"+txt);
        }

        @JavascriptInterface
        public void setData(boolean valid, String message, String balance, String lasttopupdate) {
            Log.d(TAG,"setData:"+message+balance+lasttopupdate);
        	Bundle b = new Bundle();
            b.putString(BALANCE, balance);
            b.putString(LAST_TOPUP_DATE, lasttopupdate);
            b.putString(MESSAGE, message);
            b.putString(CARD_ID, cardId);

            final Intent intent = new Intent(context, MyIntentService.class);
            intent.putExtra(MyIntentService.COMMAND, MyIntentService.SEND_BALANCE);
            intent.putExtra(MyIntentService.RECEIVER, MainActivity.getReceiver());
            intent.putExtra(MyIntentService.PAYLOAD, b);
            ComponentName cm = context.startService(intent);
        }
    }
}
