package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RikslunchenDatasource {

    interface CallBack {
      void setData(boolean valid, String message, String balance, String lasttopupdate);
    };

    private CallBack callback;
    public RikslunchenDatasource(CallBack callBack) {
        this.callback = callBack;
    }
	private static String TAG = "RikslunchenDatasource";

    private String cardId;

	public static String BALANCE = "balance";

    public void query(final Context c, final String card_id) {
        Utils.info(c, TAG, "Querying "+card_id);
        this.cardId = card_id;
        if (card_id.length() == 0) {
            return;
        }

        final WebView webView = new WebView(c);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new MyAccessor(c), "accessor");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                           SslError error) {
                if (error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
            }


            @Override
            public void onPageFinished(WebView view, String url) {
            	Log.d(TAG,"onPageFinished:"+url+card_id);

                view.loadUrl("javascript:" +
                        "var public_key = document.getElementById('public_key').value;"+
                        "accessor.test(public_key);" +
                        "cardUtil.getCardData('"+card_id+"',public_key, "
                        + "function(cardInfo) {"
                        + "        accessor.setData(cardInfo.valid,cardInfo.message,cardInfo.balance,cardInfo.lastTopupDateStr);"
                        + "        return true;" + " });", null);

            }
        });

        webView.loadUrl("https://www.rikslunchen.se/riks-cp/check_balance.html");
    }

    public class MyAccessor {
    	Context c;

        public MyAccessor(Context context) {
        	c = context;
        }

        @JavascriptInterface
        public void test(String txt) {
            Log.d(TAG, "test:"+txt);
        }

        @JavascriptInterface
        public void setData(boolean valid, String message, String balance, String lasttopupdate) {
            Log.d(TAG,"setData:"+message+balance+lasttopupdate);
            callback.setData(valid, message, balance, lasttopupdate);
        }
    }
}
