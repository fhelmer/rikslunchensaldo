package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RikslunchenDatasource {

	private static String TAG = "RikslunchenDatasource";
	Context context;
    UpdateResult ur;

    public RikslunchenDatasource(Context c) {
    	context = c;
	}

    public void query(Context c,final String card_id) {
    	Log.d(TAG,"Querying "+card_id);
        WebView webView = new WebView(c);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyAccessor(), "accessor");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            	Log.d(TAG,"onPageFinished:"+url);
                view.loadUrl("javascript:" +
                        "cardUtil.getCardData('"+card_id+"', function(cardInfo) {"
                        + "        accessor.setBalance(cardInfo.balance);"
                        + "        accessor.setLastTopDate(cardInfo.lastTopupDateStr);"
                        + "        return true;" + "    });");
            }
        });

        webView.loadUrl("http://www.rikslunchen.se/riks-cp/check_balance.html");
    }

    public void setResultCallback(UpdateResult ur) {
        this.ur = ur;
    }

    class MyAccessor {

        public MyAccessor() {
            // TODO Auto-generated constructor stub
        }

        public void setBalance(String v) {
            Log.d("tst","balance="+v);
            ur.update(R.id.widget_balance, v);
        }

        public void setLastTopDate(String v) {
            Log.d("tst","topdate="+v);
            ur.update(R.id.widget_lasttopupdate, v);
        }
    }
}
