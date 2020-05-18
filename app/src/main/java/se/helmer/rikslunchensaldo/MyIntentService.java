package se.helmer.rikslunchensaldo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
                String result = makePostRequest(session, cardId);
                try {
                    JSONObject balanceJson = new JSONObject(result);
                    b.putString(Contract.BankStatement.BALANCE, balanceJson.getString(Contract.BankStatement.BALANCE));
                } catch (JSONException jse) {
                    session.createLogMessage("Query error parsing response: " + jse);
                }
                b.putString(Contract.Card.LAST_TOPUP_DATE, Utils.getCurrentLocalTimeString());

                if (!result.isEmpty()) {
                    final String message = b.getString(Contract.BankStatement.MESSAGE);
                    session.createLogMessage("Rikslunchen balance=" +
                            b.getString(Contract.BankStatement.BALANCE));
                    Card card3 = session.createOrUpdateCard(b);
                    BankStatement t = session.createOrUpdateBankStatement(b);
                    b = session.fetchSummary();
                    b.putString(Contract.BankStatement.MESSAGE, message);
                } else {
                    b.putString(Contract.BankStatement.BALANCE, "Lunch server error");
                    session.createLogMessage("Query error ");
                }
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

    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };


    // curl -k -d '{"cardid":"35760768"}' -H "Content-Type: application/json" -X POST https://cardholder.lunchkultur.se:9443/isr/services/mobile/quickbalance

    private String makePostRequest(DbSession session, final String card_id) {
        String response = "";
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                    }
            };

            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
            }


            URL url = new URL("https://cardholder.lunchkultur.se:9443/isr/services/mobile/quickbalance");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setHostnameVerifier(DO_NOT_VERIFY);
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("cardid", card_id);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            session.createLogMessage(e.getMessage());
        }

        return response;
    }


    @Override
    public void onDestroy() {
        Utils.log(TAG, "Destroy intent service");
        super.onDestroy();
    }
}
