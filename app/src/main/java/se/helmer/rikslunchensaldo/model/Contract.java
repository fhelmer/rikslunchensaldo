package se.helmer.rikslunchensaldo.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by 23058500 on 2/27/15.
 */
public class Contract {
    public static final String DATABASE_NAME = "MyDatabase";
    public static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "se.helmer.rikslunchensaldo";

    public static class Transaction implements BaseColumns
    {
        public static final String TABLE_NAME = "transactions";
        public static final String CONTENT_URI_PATH = TABLE_NAME;

        public static final String MIMETYPE_TYPE = TABLE_NAME;
        public static final String MIMETYPE_NAME = AUTHORITY + ".provider";

        // field info
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
}
