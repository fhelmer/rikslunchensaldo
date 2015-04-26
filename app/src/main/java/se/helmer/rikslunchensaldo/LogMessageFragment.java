package se.helmer.rikslunchensaldo;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
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

    private LogMessageCursorAdapter m_adapter = null;


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
        final Context context = getActivity();

        final Button b = (Button) v.findViewById(R.id.button_fragmentlog);
        if (b != null) {
            b.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:

                                    final Intent intent = new Intent(context, MyIntentService.class);
                                    intent.putExtra("command",
                                            MyIntentService.LOG_MESSAGE_DELETE_ALL);
                                    ComponentName cm = context.startService(intent);

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
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

        m_adapter = new LogMessageCursorAdapter(this, null);
        setListAdapter(m_adapter);
        getActivity().getLoaderManager().initLoader(2, null, this);
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
                Contract.LogMessage.contentUri, projection, selectionClause, selectionArgs,
                "_id DESC");

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        m_adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        m_adapter.swapCursor(null);
    }

}