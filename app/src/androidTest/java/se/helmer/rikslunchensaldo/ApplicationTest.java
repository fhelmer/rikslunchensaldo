package se.helmer.rikslunchensaldo;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import se.helmer.rikslunchensaldo.model.RikslunchenDatabaseHelper;
import se.helmer.rikslunchensaldo.model.Transaction;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    RikslunchenDatabaseHelper rikslunchenDatabaseHelper;
    ConnectionSource connectionSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RikslunchenDatabaseHelper databaseHelper = OpenHelperManager.getHelper(getContext(), RikslunchenDatabaseHelper.class);
        connectionSource = new AndroidConnectionSource(rikslunchenDatabaseHelper);
        TableUtils.createTableIfNotExists(connectionSource, Transaction.class);
    }

    @Override
    protected void tearDown() throws Exception {
        // TableUtils.dropTable(connectionSource, Transaction.class, true);
        connectionSource.close();
        super.tearDown();
    }

    public ApplicationTest() {
        super(Application.class);
    }

    public void testExample() {
        assertTrue(true);
    }

    public void testDate() {
        Date localDate = new Date();
        Date gmtDate = Utils.LocalToGmt(localDate);
        Date backToLocal = Utils.GmtToLocal(gmtDate);
        assertEquals(backToLocal.toString(), localDate.toString());
    }

    public void testDatabase() {


        try {
            Dao<Transaction, Integer> transactionDao =
                    rikslunchenDatabaseHelper.getTransactionDao();

            Date localDate = new Date();
            Date gmtDate = Utils.LocalToGmt(localDate);
            Transaction transaction =
                    new Transaction("GMT time", new BigDecimal("123.23"));


            if (transactionDao.create(transaction) != 1) {
                Utils.log("ApplicationTest", "Failure adding Transaction");
            }

            Transaction t3 =
                    new Transaction("Description Local", new BigDecimal("123.99"));
            transactionDao.create(t3);

            Transaction t4 =
                    new Transaction("Description Original Local", new BigDecimal("123.99"));
            transactionDao.create(t4);
            assertEquals(localDate.toString(),Utils.GmtToLocal(gmtDate).toString());

            Transaction t2 = transactionDao.queryForId(2);
            if (t2 != null) {
                Utils.log("Transaction: ", t2.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertTrue(true);

    }

}