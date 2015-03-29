package se.helmer.rikslunchensaldo.model;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class RikslunchenDatabaseHelper extends OrmLiteSqliteOpenHelper {

    // the DAO object we use to access the SimpleData table
    private Dao<Transaction, Integer> transactionDao = null;
    private RuntimeExceptionDao<Transaction, Integer> transactionRuntimeDao = null;

    private static RikslunchenDatabaseHelper sSingleton = null;
    private static Context mContext;

    public RikslunchenDatabaseHelper(Context context)
    {
        super(context,
                Contract.DATABASE_NAME,
                null,
                Contract.DATABASE_VERSION
        );
    }

    public static RikslunchenDatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new RikslunchenDatabaseHelper(context);
        }
        return sSingleton;
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(RikslunchenDatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTableIfNotExists(connectionSource, Transaction.class);
            TableUtils.createTableIfNotExists(connectionSource, EnterButton.class);
            TableUtils.createTableIfNotExists(connectionSource, Card.class);

        } catch (SQLException e) {
            Log.e(RikslunchenDatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
        try {
            Log.i(RikslunchenDatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Transaction.class, true);
            TableUtils.dropTable(connectionSource, EnterButton.class, true);
            TableUtils.dropTable(connectionSource, Card.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(RikslunchenDatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Transaction, Integer> getTransactionDao() throws SQLException {
        if (transactionDao == null) {
            transactionDao = getDao(Transaction.class);
        }
        return transactionDao;
    }

    public RuntimeExceptionDao<Transaction, Integer> getRuntimeExceptionTransactionDao() {
        if (transactionRuntimeDao == null) {
            transactionRuntimeDao = getRuntimeExceptionDao(Transaction.class);
        }
        return transactionRuntimeDao;
    }


    @Override
    public void close() {
        super.close();
        transactionDao = null;
        transactionRuntimeDao = null;
    }
}

