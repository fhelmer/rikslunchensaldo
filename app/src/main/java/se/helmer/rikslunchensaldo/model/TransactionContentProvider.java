package se.helmer.rikslunchensaldo.model;


import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
import com.tojc.ormlite.android.framework.MatcherController;
import com.tojc.ormlite.android.framework.MimeTypeVnd;

public class TransactionContentProvider extends OrmLiteSimpleContentProvider<RikslunchenDatabaseHelper> {

    @Override
    protected Class<RikslunchenDatabaseHelper> getHelperClass()
    {
        return RikslunchenDatabaseHelper.class;
    }

    @Override
    public boolean onCreate()
    {
        setMatcherController(new MatcherController()
                        .add(Transaction.class, MimeTypeVnd.SubType.DIRECTORY, "",
                                Contract.Transaction.CONTENT_URI_PATTERN_MANY)
                        .add(Transaction.class, MimeTypeVnd.SubType.ITEM, "#",
                                Contract.Transaction.CONTENT_URI_PATTERN_ONE)
        );
        return true;
    }
}

