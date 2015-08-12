package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import se.helmer.rikslunchensaldo.model.BankStatement;
import se.helmer.rikslunchensaldo.model.Card;
import se.helmer.rikslunchensaldo.model.Contract;
import se.helmer.rikslunchensaldo.model.LogMessage;
import se.helmer.rikslunchensaldo.model.RikslunchenDatabaseHelper;

/**
 * The purpose is to encapsulate some commanly used database operations here. For db connections,
 * we rely on the OrbSqiteDatabaseHelper which should take care of connection pools etc
 */
public class DbSession {

    private RikslunchenDatabaseHelper mRikslunchenDatabaseHelper;
    ConnectionSource mConnectionSource;
    private Context mContext;

    public DbSession(Context context) {
        mRikslunchenDatabaseHelper = OpenHelperManager.getHelper(context,
                RikslunchenDatabaseHelper.class);

        mConnectionSource = mRikslunchenDatabaseHelper.getConnectionSource();
        mContext = context;
    }

    public ConnectionSource getConnectionSource() {
        return mConnectionSource;
    }

    public Dao<BankStatement, Integer> getBankStatementDao() throws SQLException {
        return mRikslunchenDatabaseHelper.getBankStatementDao();
    }

    public Dao<Card, Integer> getCardDao() throws SQLException {
        return mRikslunchenDatabaseHelper.getCardDao();
    }

    public Dao<LogMessage, Integer> getLogMessageDao() throws SQLException {
        return mRikslunchenDatabaseHelper.getLogMessageDao();
    }

    public void close() {

        try {
            mConnectionSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        OpenHelperManager.releaseHelper();
    }

    public Card getCard() {
        Card card = null;

        try {
            List<Card> cards = getCardDao().queryForAll();
            if (cards.size() == 1) {
                card = cards.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.error("getCard", e.toString());
        }
        return card;
    }

    Card createOrUpdateCard(Bundle b) {
        Card card = null;

        try {
            card = getCard();
            if (card != null) {
                card.setData(b.getString(RikslunchenDatasource.CARD_ID),
                        b.getString(RikslunchenDatasource.LAST_TOPUP_DATE));
                getCardDao().update(card);
            } else {
                card = new Card(b.getString(RikslunchenDatasource.CARD_ID),
                        b.getString(RikslunchenDatasource.LAST_TOPUP_DATE));
                getCardDao().create(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return card;
    }

    Bundle fetchSummary() {
        Card card = getCard();
        Bundle b = new Bundle();
        BankStatement bankStatement = getLatestBankStatement();
        if (bankStatement != null) {
            b.putString(RikslunchenDatasource.BALANCE, bankStatement.getBalance().toString());
            b.putString(RikslunchenDatasource.LATEST_UPDATE, bankStatement.getDate().toString());
        }
        if (card != null) {
            b.putString(RikslunchenDatasource.CARD_ID, card.getCardId());
            b.putString(RikslunchenDatasource.LAST_TOPUP_DATE, card.getLastTopupDate());
        }

        return b;
    }

    BankStatement getLatestBankStatement() {
        BankStatement bankStatement = null;
        try {
            QueryBuilder<BankStatement, Integer> qb = getBankStatementDao().queryBuilder();
            qb.selectRaw("MAX(_id)");
            GenericRawResults<String[]> results =
                    getBankStatementDao().queryRaw(qb.prepareStatementString());
            String[] values = new String[0];
            values = results.getFirstResult();
            if (values.length > 0 && values[0] != null) {
                bankStatement = getBankStatementDao().queryForId(Integer.valueOf(values[0]));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return bankStatement;
    }

    void deleteBankStatement(int id) {
        try {
            getBankStatementDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    BankStatement createOrUpdateBankStatement(String descr, String balance) {
        Bundle bundle = new Bundle();
        bundle.putString(RikslunchenDatasource.BALANCE, balance);
        bundle.putString(RikslunchenDatasource.MESSAGE, descr);
        return createOrUpdateBankStatement(bundle);
    }

    BankStatement updateBankStatement(Bundle b) {
        int id = b.getInt(Contract.BankStatement._ID);
        BankStatement bankStatement = null;

        try {
            bankStatement = getBankStatementDao().queryForId(id);
            BigDecimal previousBalance = bankStatement.getBalance();
            BigDecimal previousAmount = bankStatement.getAmount();
            BigDecimal amountDiff =
                    previousAmount.subtract(new BigDecimal(b.getString(Contract.BankStatement.AMOUNT)));
            BigDecimal newBalance = previousBalance.subtract(amountDiff);
            b.putString(Contract.BankStatement.BALANCE, newBalance.toString());
            bankStatement.setValues(b);
            getBankStatementDao().update(bankStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankStatement;
    }

    BankStatement createOrUpdateBankStatement(Bundle b) {
        BankStatement bankStatement = getLatestBankStatement();
        String balanceStr = b.getString(RikslunchenDatasource.BALANCE);
        String message = b.getString(RikslunchenDatasource.MESSAGE);
        BigDecimal balance = new BigDecimal(balanceStr.replace(',','.'));
        BigDecimal amount = new BigDecimal("0");
        boolean createNew = true;
        try {
            if (bankStatement != null) {
                if (bankStatement.getBalance().compareTo(balance) == 0) {
                    bankStatement.updateTimestamp();
                    getBankStatementDao().update(bankStatement);
                    createNew = false;
                } else {
                    amount = balance.subtract(bankStatement.getBalance());
                }
            }

            if (createNew) {
                bankStatement =
                        new BankStatement(message, amount, balance);
                getBankStatementDao().create(bankStatement);

                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean showNotifications = SP.getBoolean(Preferences.SHOW_NOTIFICATIONS, false);
                if (showNotifications) {
                    Utils.sendNotification(mContext, bankStatement.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.error("createOrUpdateBankStatement",""+e);
        }
        return bankStatement;
    }


    // LogMessage
    LogMessage createLogMessage(Bundle b) {
        LogMessage logMessage = new LogMessage(b.getString(Contract.LogMessage.DESCRIPTION));
        try {
            getLogMessageDao().create(logMessage);
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.error("createLogMessage","Error saving log message " + e);
        }
        return logMessage;
    }

    LogMessage createLogMessage(String message) {
        Bundle b = new Bundle();
        b.putString(Contract.LogMessage.DESCRIPTION, message);
        return createLogMessage(b);
    }

    public void deleteLogMessages() {
        try {
            TableUtils.clearTable(mConnectionSource, LogMessage.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
