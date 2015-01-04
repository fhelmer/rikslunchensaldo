package se.helmer.rikslunchensaldo.model;

import java.util.Arrays;
import java.util.HashSet;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class TransactionContentProvider extends ContentProvider {

    // database
    private DatabaseHelper database;

    // Used for the UriMacher
    private static final int TRANSACTION = 10;
    private static final int TRANSACTION_ID = 20;

    private static final String AUTHORITY = "se.helmer.risklunchensaldo.transactionprovider";

    private static final String BASE_PATH = "transactions";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, TRANSACTION);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TRANSACTION_ID);
    }

    @Override
    public boolean onCreate() {
        database = DatabaseHelper.getInstance(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return Uri.parse(BASE_PATH + "/" + 0);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        return 0;
    }


}

