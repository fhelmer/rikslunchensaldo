package se.helmer.rikslunchensaldo;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import se.helmer.rikslunchensaldo.model.Contract;

public class LogMessageFragment extends ListFragment implements
LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = "LogMessageFragment";


    static LogMessageFragment init(int val) {
        LogMessageFragment logMessageFrag = new LogMessageFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        logMessageFrag.setArguments(args);
        return logMessageFrag;
    }

    public LogMessageFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_log_message, container, false);


        final Button b = (Button) v.findViewById(R.id.button_fragmentlog);
        if (b != null) {
            b.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //CrashLog.deleteAll();

                    getActivity().getContentResolver().notifyChange(Contract.LogMessage.contentUri, null);
                }
            });
        }
    	return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        fillData();
    }

    public  void fillData() {
        Bundle b = getArguments();
        // mMode = b.getInt("num");

        /*
        m_adapter = new CrashLogCursorAdapter(mMode, this, null);
        setListAdapter(m_adapter);
        getActivity().getLoaderManager().initLoader(mMode, null, this);
        */
        Utils.log(TAG, "fillData");
    }

        @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Utils.log(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        Utils.log(TAG, "onDetach");

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
    	Log.d(TAG,"onResume ");
    	super.onResume();
    }

 // Creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { Contract.LogMessage._ID,
                                Contract.LogMessage.DATE,
                                Contract.LogMessage.DESCRIPTION };

        Bundle b = getArguments();
        String selectionClause = null;
        String[] selectionArgs = null;

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                Contract.LogMessage.contentUri, projection, selectionClause, selectionArgs, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // m_adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        // m_adapter.swapCursor(null);
    }

}