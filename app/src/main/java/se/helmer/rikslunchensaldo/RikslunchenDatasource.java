package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.math.BigDecimal;
import java.sql.SQLException;

import se.helmer.rikslunchensaldo.model.DatabaseHelper;
import se.helmer.rikslunchensaldo.model.Transaction;
import se.helmer.rikslunchensaldo.model.TransactionContentProvider;

public class RikslunchenDatasource {

	private static String TAG = "RikslunchenDatasource";

	public static String BALANCE = "balance";
	public static String LAST_TOPUP_DATE  = "lasttopupdate";
	public static String LATEST_UPDATE = "latestupdate";
	public static String CARD_ID = "card_id";
	public static String AUTO_UPDATE = "auto_update";

    public void query(Context c, final String card_id) {
        Log.d(TAG,"Querying "+card_id);
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
        public void setData(boolean valid, String message,String balance, String lasttopupdate) {
            Log.d(TAG,"setData:"+message+balance+lasttopupdate);
        	Bundle b = new Bundle();
            b.putString(BALANCE, balance);
            b.putString(LAST_TOPUP_DATE, lasttopupdate);

            String latestupdate = Utils.getCurrentLocalTimeString();
            context.getContentResolver().notifyChange(TransactionContentProvider.CONTENT_URI, null);
            if (!valid) {
                latestupdate = message;
            }

            b.putString(LATEST_UPDATE,latestupdate);
        	Log.d(TAG,"Updated "+latestupdate);

            try {
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
                ConnectionSource connectionSource = new AndroidConnectionSource(databaseHelper);
                TableUtils.createTableIfNotExists(connectionSource, Transaction.class);

                Dao<Transaction, Integer> transactionDao =
                        databaseHelper.getTransactionDao();
                // create transaction only if the amount if differenc
                Transaction transaction =
                        new Transaction(message, new BigDecimal(balance));
                if (transactionDao.create(transaction) != 1) {
                    Utils.log("ApplicationTest", "Failure adding Transaction");
                }
                context.getContentResolver().notifyChange(TransactionContentProvider.CONTENT_URI,null);
            } catch (SQLException se) {

            }
        }
    }
}
