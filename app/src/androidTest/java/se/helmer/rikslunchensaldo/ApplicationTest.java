package se.helmer.rikslunchensaldo;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.test.ApplicationTestCase;
import android.widget.Toast;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import se.helmer.rikslunchensaldo.model.Card;
import se.helmer.rikslunchensaldo.model.Contract;
import se.helmer.rikslunchensaldo.model.RikslunchenDatabaseHelper;
import se.helmer.rikslunchensaldo.model.Transaction;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    ConnectionSource connectionSource;
    final static String TAG = "ApplicationTest";
    DbSession session;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        session = new DbSession(getContext());
        connectionSource = session.getConnectionSource();
        TableUtils.dropTable(connectionSource, Transaction.class, true);
        TableUtils.dropTable(connectionSource, Card.class, true);

        TableUtils.createTableIfNotExists(connectionSource, Transaction.class);
        TableUtils.createTableIfNotExists(connectionSource, Card.class);
    }

    @Override
    protected void tearDown() throws Exception {
        session.close();
        super.tearDown();
    }

    public ApplicationTest() {
        super(Application.class);
    }

    public void testExample() {
        assertTrue(true);
    }

    public void testCard() {
        Bundle b = new Bundle();
        b.putString(RikslunchenDatasource.CARD_ID,"12345");
        b.putString(RikslunchenDatasource.LAST_TOPUP_DATE,"2015-01-01");
        Card c = session.createOrUpdateCard(b);

        Card c1 = session.getCard();
        assertEquals(c1.getCardId(), "12345");

        b.putString(RikslunchenDatasource.LAST_TOPUP_DATE,"2015-02-02");
        Card c2 = session.createOrUpdateCard(b);

        assertEquals(c2.getLastTopupDate(),"2015-02-02");

    }

    public void testDate() {
        Date localDate = new Date();
        Date gmtDate = Utils.LocalToGmt(localDate);
        Date backToLocal = Utils.GmtToLocal(gmtDate);
        assertEquals(backToLocal.toString(), localDate.toString());
    }

    public void testInsertTransactions() {
        Transaction t1 = session.createOrUpdateTransaction("Descr", "50");
        Transaction t2 = session.createOrUpdateTransaction("Descr", "100.0");
        Transaction t3 = session.createOrUpdateTransaction("Descr", "100.0");
        assertEquals(t2.getId(), t3.getId());
        assertTrue(t2.getAmount().compareTo(new BigDecimal("50")) == 0);
        assertFalse(t1.getId() == t2.getId());
    }

    public void upgradeFromPreviousVersion() {
        Utils.clearPreferences(getContext());
        Bundle prefs = Utils.getSharedPreferences(getContext());
        prefs.putString(RikslunchenDatasource.BALANCE, "123.12");
        Utils.saveSharedPreferences(getContext(), prefs);

        Card card = null;
        Bundle prefs2 = Utils.getSharedPreferences(getContext());
        String cardId = prefs2.getString(RikslunchenDatasource.CARD_ID, "");
        if (cardId.length() > 0) { // Upgrade from previous version
            card = session.createOrUpdateCard(prefs2);
            Utils.clearPreferences(getContext()); // Prefer database from now on
        }
        assertNotNull(card);
        if (card != null) {
            Bundle b = session.fetchSummary(getContext());
            String balance = b.getString(RikslunchenDatasource.BALANCE);
            assertEquals(balance, "123.12");
        }

    }

    public void testDatabase() {


        try {
            Date localDate = new Date();
            Date gmtDate = Utils.LocalToGmt(localDate);
            Transaction transaction = session.createOrUpdateTransaction
                    ("GMT time", "123.23");

            Transaction t3 =
                    session.createOrUpdateTransaction("Description Local", "123.99");
            Transaction t4 =
                    session.createOrUpdateTransaction("Description Original Local", "123.99");

            assertEquals(localDate.toString(),Utils.GmtToLocal(gmtDate).toString());

            Transaction t2 = session.getTransactionDao().queryForId(2);
            if (t2 != null) {
                Utils.log("Transaction: ", t2.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertTrue(true);

    }

    public void testContentProvider() {

        // Run query
        Uri uri = Contract.Transaction.contentUri;
        String[] projection = new String[] { Contract.Transaction._ID,
                Contract.Transaction.DESCRIPTION, Contract.Transaction.AMOUNT };
        String[] selectionArgs = null;

        Cursor c = getContext().getContentResolver().query(uri, projection, null, null, null);

        if (c.moveToFirst()) {
            do {
                Utils.log(TAG,
                        c.getString(c.getColumnIndex(Contract.Transaction._ID)) +
                                ", " + c.getString(c.getColumnIndex(Contract.Transaction.DESCRIPTION)) +
                                ", " + c.getString(c.getColumnIndex(Contract.Transaction.AMOUNT)));

            } while (c.moveToNext());
        }
    }
}