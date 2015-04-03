package se.helmer.rikslunchensaldo.model;


import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
import com.tojc.ormlite.android.framework.MatcherController;
import com.tojc.ormlite.android.framework.MimeTypeVnd;

public class LogMessageContentProvider extends OrmLiteSimpleContentProvider<RikslunchenDatabaseHelper> {

    @Override
    protected Class<RikslunchenDatabaseHelper> getHelperClass()
    {
        return RikslunchenDatabaseHelper.class;
    }

    @Override
    public boolean onCreate()
    {
        setMatcherController(new MatcherController()
                        .add(LogMessage.class, MimeTypeVnd.SubType.DIRECTORY, "",
                                Contract.LogMessage.CONTENT_URI_PATTERN_MANY)
                        .add(LogMessage.class, MimeTypeVnd.SubType.ITEM, "#",
                                Contract.LogMessage.CONTENT_URI_PATTERN_ONE)
        );
        return true;
    }
}

