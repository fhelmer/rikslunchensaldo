package se.helmer.rikslunchensaldo;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.test.ApplicationTestCase;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import se.helmer.rikslunchensaldo.model.BankStatement;
import se.helmer.rikslunchensaldo.model.Card;
import se.helmer.rikslunchensaldo.model.Contract;

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
        TableUtils.dropTable(connectionSource, BankStatement.class, true);
        TableUtils.dropTable(connectionSource, Card.class, true);

        TableUtils.createTableIfNotExists(connectionSource, BankStatement.class);
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

    public void testInsertBankStatements() {
        BankStatement t1 = session.createOrUpdateBankStatement("Descr", "50");
        BankStatement t2 = session.createOrUpdateBankStatement("Descr", "100.0");
        BankStatement t3 = session.createOrUpdateBankStatement("Descr", "100.0");
        assertEquals(t2.getId(), t3.getId());
        assertTrue(t2.getAmount().compareTo(new BigDecimal("50")) == 0);
        assertFalse(t1.getId() == t2.getId());
    }

    public void upgradeFromPreviousVersion() {
        Utils.clearPreferences(getContext());
        Bundle prefs = Utils.getSharedPreferences(getContext());
        prefs.putString(RikslunchenDatasource.BALANCE, "123.12");

        Card card = null;
        Bundle prefs2 = Utils.getSharedPreferences(getContext());
        String cardId = prefs2.getString(RikslunchenDatasource.CARD_ID, "");
        if (cardId.length() > 0) { // Upgrade from previous version
            card = session.createOrUpdateCard(prefs2);
            Utils.clearPreferences(getContext()); // Prefer database from now on
        }
        assertNotNull(card);
        if (card != null) {
            Bundle b = session.fetchSummary();
            String balance = b.getString(RikslunchenDatasource.BALANCE);
            assertEquals(balance, "123.12");
        }

    }

    public void testDatabase() {


        try {
            Date localDate = new Date();
            Date gmtDate = Utils.LocalToGmt(localDate);
            BankStatement bankStatement = session.createOrUpdateBankStatement
                    ("GMT time", "123.23");

            BankStatement t3 =
                    session.createOrUpdateBankStatement("Description Local", "123.99");
            BankStatement t4 =
                    session.createOrUpdateBankStatement("Description Original Local", "123.99");

            assertEquals(localDate.toString(),Utils.GmtToLocal(gmtDate).toString());

            BankStatement t2 = session.getBankStatementDao().queryForId(2);
            if (t2 != null) {
                Utils.log("BankStatement: ", t2.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertTrue(true);

    }

    public void testContentProvider() {

        // Run query
        Uri uri = Contract.BankStatement.contentUri;
        String[] projection = new String[] { Contract.BankStatement._ID,
                Contract.BankStatement.DESCRIPTION, Contract.BankStatement.AMOUNT };
        String[] selectionArgs = null;

        Cursor c = getContext().getContentResolver().query(uri, projection, null, null, null);

        if (c.moveToFirst()) {
            do {
                Utils.log(TAG,
                        c.getString(c.getColumnIndex(Contract.BankStatement._ID)) +
                                ", " + c.getString(c.getColumnIndex(Contract.BankStatement.DESCRIPTION)) +
                                ", " + c.getString(c.getColumnIndex(Contract.BankStatement.AMOUNT)));

            } while (c.moveToNext());
        }
    }
}