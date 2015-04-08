package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import se.helmer.rikslunchensaldo.model.Card;
import se.helmer.rikslunchensaldo.model.Contract;
import se.helmer.rikslunchensaldo.model.RikslunchenDatabaseHelper;
import se.helmer.rikslunchensaldo.model.Transaction;

/**
 * The purpose is to encapsulate some commanly used database operations here. For db connections,
 * we rely on the OrbSqiteDatabaseHelper which should take care of connection pools etc
 */
public class DbSession {

    private RikslunchenDatabaseHelper rikslunchenDatabaseHelper;
    ConnectionSource connectionSource;

    public DbSession(Context context) {
        rikslunchenDatabaseHelper = OpenHelperManager.getHelper(context,
                RikslunchenDatabaseHelper.class);

        connectionSource = rikslunchenDatabaseHelper.getConnectionSource();

    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    public Dao<Transaction, Integer> getTransactionDao() throws SQLException {
        return rikslunchenDatabaseHelper.getTransactionDao();
    }

    public Dao<Card, Integer> getCardDao() throws SQLException {
        return rikslunchenDatabaseHelper.getCardDao();
    }

    public void close() {

        try {
            connectionSource.close();
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

    Bundle fetchSummary(Context context) {
        Card card = getCard();
        Bundle b = new Bundle();
        Transaction transaction = getLatestTransaction();
        if (transaction != null) {
            b.putString(RikslunchenDatasource.BALANCE, transaction.getBalance().toString());
            b.putString(RikslunchenDatasource.LATEST_UPDATE, transaction.getDate().toString());
        }
        if (card != null) {
            b.putString(RikslunchenDatasource.CARD_ID, card.getCardId());
            b.putString(RikslunchenDatasource.LAST_TOPUP_DATE, card.getLastTopupDate());
        }

        return b;
    }

    Transaction getLatestTransaction() {
        Transaction transaction = null;
        try {

            QueryBuilder<Transaction, Integer> qb = getTransactionDao().queryBuilder();
            qb.selectRaw("MAX(_id)");
            GenericRawResults<String[]> results =
                    getTransactionDao().queryRaw(qb.prepareStatementString());
            String[] values = new String[0];
            values = results.getFirstResult();
            if (values.length > 0 && values[0] != null) {
                transaction = getTransactionDao().queryForId(Integer.valueOf(values[0]));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return transaction;
    }

    Transaction createOrUpdateTransaction(Bundle b) {
        Transaction transaction = getLatestTransaction();
        String amountStr = b.getString(RikslunchenDatasource.BALANCE);
        String description = b.getString(RikslunchenDatasource.MESSAGE);
        BigDecimal balance = new BigDecimal(amountStr.replace(',','.'));
        BigDecimal amount = new BigDecimal("0");
        boolean createNew = true;
        try {
            if (transaction != null) {
                if (transaction.getBalance().compareTo(balance) == 0) {
                    transaction.updateTimestamp();
                    getTransactionDao().update(transaction);
                    createNew = false;
                } else {
                    amount = balance.subtract(transaction.getBalance());
                }
            }

            if (createNew) {
                transaction =
                        new Transaction(description, amount, balance);
                getTransactionDao().create(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.error("createOrUpdateTransaction",""+e);
        }
        return transaction;
    }

}
