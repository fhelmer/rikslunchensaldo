package se.helmer.rikslunchensaldo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RikslunchenDatasource {

	private static String TAG = "RikslunchenDatasource";

    public void query(Context c,ResultReceiver receiver, final String card_id) {
    	Log.d(TAG,"Querying "+card_id);
        WebView webView = new WebView(c);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyAccessor(receiver), "accessor");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            	Log.d(TAG,"onPageFinished:"+url);
                view.loadUrl("javascript:" +
                        "cardUtil.getCardData('"+card_id+"', document.getElementById('public_key').value, "
                        + "function(cardInfo) {"
                        + "        accessor.setData(cardInfo.valid,cardInfo.message,cardInfo.balance,cardInfo.lastTopupDateStr);"
                        + "        return true;" + "    });");

            }
        });

        webView.loadUrl("http://www.rikslunchen.se/riks-cp/check_balance.html");
    }

    class MyAccessor {
    	private final ResultReceiver receiver;
        public MyAccessor(ResultReceiver r) {
        	receiver = r;
        }

        public void test(String txt) {
            Log.d(TAG,"test:"+txt);
        }

        public void setData(boolean valid, String message,String balance, String lasttopupdate) {
        	Bundle b = new Bundle();
        	b.putString("balance", balance);
        	b.putString("lasttopupdate", lasttopupdate);

            Calendar cal = Calendar.getInstance();
            cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.US);
            String latestupdate = "Updated:"+sdf.format(cal.getTime());

            if (!valid) {
                latestupdate = message;
            }

        	b.putString("latestupdate",latestupdate);
        	receiver.send(0, b);
        	Log.d(TAG,"Updated "+latestupdate);
        }
    }
}
