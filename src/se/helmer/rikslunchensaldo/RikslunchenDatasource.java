package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RikslunchenDatasource {

	private static String TAG = "RikslunchenDatasource";

	public static String BALANCE = "balance";
	public static String LAST_TOPUP_DATE  = "lasttopupdate";
	public static String LATEST_UPDATE = "latestupdate";
	public static String CARD_ID = "card_id";
	public static String AUTO_UPDATE = "auto_update";

    public void query(Context c,ResultReceiver receiver, final String card_id) {
        Log.d(TAG,"Querying "+card_id);
        if (card_id.length() == 0) {
            return;
        }
        WebView webView = new WebView(c);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyAccessor(receiver), "accessor");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            	Log.d(TAG,"onPageFinished:"+url);

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
    	private final ResultReceiver receiver;
        public MyAccessor(ResultReceiver r) {
        	receiver = r;
        }

        @JavascriptInterface
        public void test(String txt) {
            Log.d(TAG,"test:"+txt);
        }

        @JavascriptInterface
        public void setData(boolean valid, String message,String balance, String lasttopupdate) {
        	Bundle b = new Bundle();
            b.putString(BALANCE, balance);
            b.putString(LAST_TOPUP_DATE, lasttopupdate);

            String latestupdate = Utils.getCurrentTime();

            if (!valid) {
                latestupdate = message;
            }

            b.putString(LATEST_UPDATE,latestupdate);
        	receiver.send(0, b);
        	Log.d(TAG,"Updated "+latestupdate);
        }
    }
}
