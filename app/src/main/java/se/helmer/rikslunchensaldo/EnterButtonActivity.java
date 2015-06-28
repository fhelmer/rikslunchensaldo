package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import se.helmer.rikslunchensaldo.model.Contract;

public class EnterButtonActivity extends ListActivity implements
LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = "EnterButtonFragment";

    private EnterButtonCursorAdapter m_adapter = null;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

   public  void fillData() {
        m_adapter = new EnterButtonCursorAdapter(this, null);
        setListAdapter(m_adapter);
        getLoaderManager().initLoader(3, null, this);
        Utils.log(TAG, "fillData");
    }

        @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Utils.log(TAG,"onDestroy");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { Contract.EnterButton._ID,
                                Contract.EnterButton.DESCRIPTION,
                                Contract.EnterButton.AMOUNT };

        String selectionClause = null;
        String[] selectionArgs = null;

        CursorLoader cursorLoader = new CursorLoader(this,
                Contract.EnterButton.contentUri, projection, selectionClause, selectionArgs,
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