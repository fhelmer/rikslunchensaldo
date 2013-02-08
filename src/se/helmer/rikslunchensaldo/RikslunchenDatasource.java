package se.helmer.rikslunchensaldo;

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
                        "cardUtil.getCardData('"+card_id+"', function(cardInfo) {"
                        + "        accessor.setData(cardInfo.balance,cardInfo.lastTopupDateStr);"
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

        public void setData(String balance, String lasttopupdate) {
        	Bundle b = new Bundle();
        	b.putString("balance", balance);
        	b.putString("lasttopupdate", lasttopupdate);
        	receiver.send(GetBalanceIntentService.GETBALANCE, b);
        }
    }
}
