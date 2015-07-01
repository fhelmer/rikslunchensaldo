package se.helmer.rikslunchensaldo.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {
    public static final String DATABASE_NAME = "MyDatabase";
    public static final int DATABASE_VERSION = 1;

    public static class BankStatement implements BaseColumns
    {
        public static final String AUTHORITY = "se.helmer.rikslunchensaldo.bankstatementprovider";

        public static final String TABLE_NAME = "bankstatements";
        public static final String CONTENT_URI_PATH = TABLE_NAME;

        public static final String MIMETYPE_TYPE = TABLE_NAME;
        public static final String MIMETYPE_NAME = AUTHORITY + ".provider";

        // field info
        public static final String _ID = "_id";
        public static final String DESCRIPTION = "description";
        public static final String BALANCE = "balance";
        public static final String DATE = "date";
        public static final String AMOUNT = "amount";

        // content uri pattern code
        public static final int CONTENT_URI_PATTERN_MANY = 1;
        public static final int CONTENT_URI_PATTERN_ONE = 2;

        // Refer to activity.
        public static final Uri contentUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(CONTENT_URI_PATH)
                .build();
    }

    public static class Transaction implements BaseColumns
    {
        public static final String AUTHORITY = "se.helmer.rikslunchensaldo.transactionprovider";

        public static final String TABLE_NAME = "transactions";
        public static final String CONTENT_URI_PATH = TABLE_NAME;

        public static final String MIMETYPE_TYPE = TABLE_NAME;
        public static final String MIMETYPE_NAME = AUTHORITY + ".provider";

        // field info
        public static final String _ID = "_id";
        public static final String DESCRIPTION = "description";
        public static final String BALANCE = "balance";
        public static final String DATE = "date";
        public static final String ESTIMATEDBALANCE = "estimatedbalance";
        public static final String AMOUNT = "amount";

        // content uri pattern code
        public static final int CONTENT_URI_PATTERN_MANY = 1;
        public static final int CONTENT_URI_PATTERN_ONE = 2;

        // Refer to activity.
        public static final Uri contentUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(CONTENT_URI_PATH)
                .build();
    }

    public static class Card implements BaseColumns
    {
        public static final String AUTHORITY = "se.helmer.rikslunchensaldo.cardprovider";
        public static final String TABLE_NAME = "card";
        public static final String CONTENT_URI_PATH = TABLE_NAME;

        public static final String MIMETYPE_TYPE = TABLE_NAME;
        public static final String MIMETYPE_NAME = AUTHORITY + ".provider";

        // field info
        public static final String _ID = "_id";
        public static final String DESCRIPTION = "description";
        public static final String CARD_ID = "card_id";
        public static final String LAST_TOPUP_DATE = "last_top_update";

        // content uri pattern code
        public static final int CONTENT_URI_PATTERN_MANY = 1;
        public static final int CONTENT_URI_PATTERN_ONE = 2;

        // Refer to activity.
        public static final Uri contentUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(CONTENT_URI_PATH)
                .build();
    }

    public static class EnterButton implements BaseColumns
    {
        public static final String AUTHORITY = "se.helmer.rikslunchensaldo.enterbuttonprovider";
        public static final String TABLE_NAME = "enterbutton";
        public static final String CONTENT_URI_PATH = TABLE_NAME;

        public static final String MIMETYPE_TYPE = TABLE_NAME;
        public static final String MIMETYPE_NAME = AUTHORITY + ".provider";

        // field info
        public static final String _ID = "_id";
        public static final String DESCRIPTION = "description";
        public static final String AMOUNT = "amount";

        public static final String BUTTONAMOUNTS = "buttonamounts";

        // content uri pattern code
        public static final int CONTENT_URI_PATTERN_MANY = 1;
        public static final int CONTENT_URI_PATTERN_ONE = 2;

        // Refer to activity.
        public static final Uri contentUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(CONTENT_URI_PATH)
                .build();
    }

    public static class LogMessage implements BaseColumns
    {
        public static final String AUTHORITY = "se.helmer.rikslunchensaldo.logmessageprovider";

        public static final String TABLE_NAME = "logmessage";
        public static final String CONTENT_URI_PATH = TABLE_NAME;

        public static final String MIMETYPE_TYPE = TABLE_NAME;
        public static final String MIMETYPE_NAME = AUTHORITY + ".provider";

        // field info
        public static final String _ID = "_id";
        public static final String DATE = "date";
        public static final String DESCRIPTION = "description";

        // content uri pattern code
        public static final int CONTENT_URI_PATTERN_MANY = 1;
        public static final int CONTENT_URI_PATTERN_ONE = 2;

        // Refer to activity.
        public static final Uri contentUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(CONTENT_URI_PATH)
                .build();
    }
}
