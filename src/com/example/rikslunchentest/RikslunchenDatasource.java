package com.example.rikslunchentest;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RikslunchenDatasource {

    UpdateResult ur;

    public void query(Context c,String card_id) {

        WebView webView = new WebView(c);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyAccessor(), "accessor");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:" +
                        "cardUtil.getCardData('32271843', function(cardInfo) {"
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
            ur.update(R.id.balance, "Balance: "+ v);
        }

        public void setLastTopDate(String v) {
            Log.d("tst","topdate="+v);
            ur.update(R.id.lasttopupdate, "Last Top-up-date: "+v);
        }
    }
}
