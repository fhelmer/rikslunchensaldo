package se.helmer.rikslunchensaldo.model;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import se.helmer.rikslunchensaldo.Utils;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class RikslunchenDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private Dao<BankStatement, Integer> bankStatementDao = null;
    private RuntimeExceptionDao<BankStatement, Integer> bankStatementRuntimeDao = null;

    private Dao<Card, Integer> cardDao = null;
    private RuntimeExceptionDao<Card, Integer> cardRuntimeDao = null;

    private Dao<LogMessage, Integer> logMessageDao = null;
    private RuntimeExceptionDao<LogMessage, Integer> logMessageRuntimeDao = null;

    public RikslunchenDatabaseHelper(Context context)
    {
        super(context,
                Contract.DATABASE_NAME,
                null,
                Contract.DATABASE_VERSION
        );
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Utils.debug(RikslunchenDatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTableIfNotExists(connectionSource, BankStatement.class);
            TableUtils.createTableIfNotExists(connectionSource, Card.class);
            TableUtils.createTableIfNotExists(connectionSource, LogMessage.class);

        } catch (SQLException e) {
            Utils.error(RikslunchenDatabaseHelper.class.getName(), "Can't create database " +  e);
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
        onCreate(db, connectionSource);
    }

    public Dao<BankStatement, Integer> getBankStatementDao() throws SQLException {
        if (bankStatementDao == null) {
            bankStatementDao = getDao(BankStatement.class);
        }
        return bankStatementDao;
    }

    public RuntimeExceptionDao<BankStatement, Integer> getRuntimeExceptionBankStatementDao() {
        if (bankStatementRuntimeDao == null) {
            bankStatementRuntimeDao = getRuntimeExceptionDao(BankStatement.class);
        }
        return bankStatementRuntimeDao;
    }

    public Dao<Card, Integer> getCardDao() throws SQLException {
        if (cardDao == null) {
            cardDao = getDao(Card.class);
        }
        return cardDao;
    }

    public RuntimeExceptionDao<Card, Integer> getRuntimeExceptionCardDao() {
        if (cardRuntimeDao == null) {
            cardRuntimeDao = getRuntimeExceptionDao(Card.class);
        }
        return cardRuntimeDao;
    }
    public Dao<LogMessage, Integer> getLogMessageDao() throws SQLException {
        if (logMessageDao == null) {
            logMessageDao = getDao(LogMessage.class);
        }
        return logMessageDao;
    }

    public RuntimeExceptionDao<LogMessage, Integer> getRuntimeExceptionLogMessageDao() {
        if (logMessageRuntimeDao == null) {
            logMessageRuntimeDao = getRuntimeExceptionDao(LogMessage.class);
        }
        return logMessageRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        bankStatementDao = null;
        bankStatementRuntimeDao = null;
        cardDao = null;
        cardRuntimeDao = null;
        logMessageDao = null;
        logMessageRuntimeDao = null;
    }
}

