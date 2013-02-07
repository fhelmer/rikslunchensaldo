package com.example.rikslunchentest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    String card_id = "32271843";
    static String cardbalance = "";
    static String lasttopdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = new WebView(this);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new MyAccessor(),"accessor");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url){
                view.loadUrl("javascript:"+ // document.getElementById('card_id').value = '32271843';"+
                              // "showAmount();"+
                              "cardUtil.getCardData('32271843', function(cardInfo) {"+
                              "        accessor.setBalance(cardInfo.balance);"+
                              "        accessor.setLastTopDate(cardInfo.lastTopupDateStr);"+
                              "        return true;"+
                              "    });"
                        );
            }
        });

        webView.loadUrl("http://www.rikslunchen.se/riks-cp/check_balance.html");
    }

    class MyAccessor {
        public void setBalance(String v) {
            MainActivity.this.cardbalance = v;
            Log.d("tst","balance="+v);
        }

        public void setLastTopDate(String v) {
            MainActivity.this.lasttopdate = v;
            Log.d("tst","topdate="+v);
        }
    }


}

