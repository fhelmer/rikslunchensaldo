package se.helmer.rikslunchensaldo.model;


import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
import com.tojc.ormlite.android.framework.MatcherController;
import com.tojc.ormlite.android.framework.MimeTypeVnd;

public class BankStatementContentProvider extends OrmLiteSimpleContentProvider<RikslunchenDatabaseHelper> {

    @Override
    protected Class<RikslunchenDatabaseHelper> getHelperClass()
    {
        return RikslunchenDatabaseHelper.class;
    }

    @Override
    public boolean onCreate()
    {
        setMatcherController(new MatcherController()
                        .add(BankStatement.class, MimeTypeVnd.SubType.DIRECTORY, "",
                                Contract.BankStatement.CONTENT_URI_PATTERN_MANY)
                        .add(BankStatement.class, MimeTypeVnd.SubType.ITEM, "#",
                                Contract.BankStatement.CONTENT_URI_PATTERN_ONE)
        );
        return true;
    }
}

