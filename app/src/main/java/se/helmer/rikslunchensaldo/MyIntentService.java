package se.helmer.rikslunchensaldo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import se.helmer.rikslunchensaldo.model.BankStatement;
import se.helmer.rikslunchensaldo.model.Card;
import se.helmer.rikslunchensaldo.model.Contract;

public class MyIntentService extends IntentService {

    private final static String TAG = "MyIntentService";

    public static final String COMMAND = "command";
    public static final String PAYLOAD = "payload";
    public static final String RECEIVER = "receiver";

    public static final int CHECK_DATABASE = 1;
    public static final int QUERY_RIKSLUNCHEN_THEN_UPDATE = 2;
    public static final int BANKSTATEMENT_UPDATE = 3;
    public static final int BANKSTATEMENT_DELETE = 4;
    public static final int LOG_MESSAGE_SAVE = 5;
    public static final int LOG_MESSAGE_DELETE_ALL = 6;
    public static final int CARD_UPDATE = 7;

    public static final int RETURN_DATA = 100;

    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Utils.log(TAG, "onHandleIntent in");

        int msgCommand = intent.getIntExtra(COMMAND, 0);
        Bundle b = intent.getBundleExtra(PAYLOAD);
        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        Context context = getApplicationContext();
        final DbSession session = new DbSession(context);

        switch (msgCommand) {
            case CHECK_DATABASE:
                // First check if there is a card already
                Card card = session.getCard();
                if (card == null) {
                    Bundle prefs = Utils.getSharedPreferences(getApplicationContext());
                    String cardId = prefs.getString(Contract.Card.CARD_ID, "");
                    if (cardId.length() > 0) { // Upgrade from previous version
                        card = session.createOrUpdateCard(prefs);
                        session.createLogMessage("Upgrading database: Using card id " + cardId +
                                " listed in shared preferences");
                        Utils.clearPreferences(getApplicationContext()); // Prefer database from now on
                    }
                }
                if (card != null) {
                    b = session.fetchSummary();
                }
                break;
            case CARD_UPDATE:
                session.createLogMessage("Saving card id=" +
                        b.getString(Contract.Card.CARD_ID));
                Card card2 = session.createOrUpdateCard(b);
                b = session.fetchSummary();
                break;
            case QUERY_RIKSLUNCHEN_THEN_UPDATE:
                final String cardId = b.getString(Contract.Card.CARD_ID);
                final String message = b.getString(Contract.BankStatement.MESSAGE);
                session.createLogMessage("Rikslunchen balance=" +
                        b.getString(Contract.BankStatement.BALANCE));
                Card card3 = session.createOrUpdateCard(b);
                BankStatement t = session.createOrUpdateBankStatement(b);
                b = session.fetchSummary();
                b.putString(Contract.BankStatement.MESSAGE, message);
                break;

            case BANKSTATEMENT_UPDATE:
                session.createLogMessage("Updating bankstatement " +
                        b.getInt(Contract.BankStatement._ID));
                session.updateBankStatement(b);
                break;

            case BANKSTATEMENT_DELETE:
                int id = b.getInt(Contract.BankStatement._ID);
                session.deleteBankStatement(id);
                session.createLogMessage("Deleted bankstatent " + id);
                break;

            case LOG_MESSAGE_SAVE:
                session.createLogMessage(b);
                break;

            case LOG_MESSAGE_DELETE_ALL:
                session.deleteLogMessages();
                break;
            default:
                break;
        }

        getApplicationContext().getContentResolver().notifyChange(Contract.BankStatement.contentUri, null);
        getApplicationContext().getContentResolver().notifyChange(Contract.LogMessage.contentUri, null);

        session.close();

        Utils.refreshWidgets(this);

        if (receiver != null) {
            receiver.send(RETURN_DATA,b);
        }


    }

    @Override
    public void onDestroy() {
        Utils.log(TAG, "Destroy intent service");
        super.onDestroy();
    }
}
