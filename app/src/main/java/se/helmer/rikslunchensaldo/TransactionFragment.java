package se.helmer.rikslunchensaldo;

import se.helmer.rikslunchensaldo.model.TransactionContentProvider;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class TransactionFragment extends ListFragment implements
	LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = "TransactionFragment";

    private TransactionCursorAdapter m_adapter = null;

    static TransactionFragment newInstance(int num) {
        TransactionFragment fragment = new TransactionFragment();

        Bundle args = new Bundle();
        args.putInt("num", num);

        fragment.setArguments(args);
        return fragment;
    }

    public TransactionFragment() {
        super();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.transactionfragment, container, false);
        assignButtonListeners(v);
    	return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        fillData();
    }

    public  void fillData() {
        Bundle b = getArguments();


        m_adapter = new TransactionCursorAdapter(this, null);
        setListAdapter(m_adapter);
        getActivity().getLoaderManager().initLoader(0, null, this);
        Utils.log(TAG, "fillData");
    }

    private void assignButtonListeners(View v) {
        final MainActivity a = (MainActivity) getActivity();

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    // a.doWork(Message.CMD_DELETE,-1); //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
                }
            }
        };


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume ");
    	super.onResume();
        //

    }

    // Creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = { "_id", "amount", "date", "description" };

        String selectionClause = null;
        String[] selectionArgs = null;

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TransactionContentProvider.CONTENT_URI, projection, selectionClause, selectionArgs,
                null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        m_adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        m_adapter.swapCursor(null);
    }

}