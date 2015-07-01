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
import android.widget.EditText;

import se.helmer.rikslunchensaldo.model.Contract;

public class EnterButtonActivity extends ListActivity implements
LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = "EnterButtonActivityt";

    private EnterButtonCursorAdapter m_adapter = null;
    private EditText mDescription;
    private EditText mAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_buttons);
        fillData();
    }

    public  void fillData() {
        m_adapter = new EnterButtonCursorAdapter(this, null);
        setListAdapter(m_adapter);
        getLoaderManager().initLoader(MainActivity.ENTER_BUTTON, null, this);
        Utils.log(TAG, "fillData");

        mDescription = (EditText) findViewById(R.id.enter_button_description);
        mAmount = (EditText) findViewById(R.id.enter_button_amount);
        Button addButton = (Button) findViewById(R.id.enter_button_add);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString(Contract.EnterButton.DESCRIPTION, mDescription.getText().toString());
                b.putString(Contract.EnterButton.AMOUNT, mAmount.getText().toString());

                final Intent intent = new Intent(EnterButtonActivity.this, MyIntentService.class);
                intent.putExtra(MyIntentService.COMMAND, MyIntentService.ENTER_BUTTONS_ADD);
                intent.putExtra(MyIntentService.RECEIVER, MainActivity.getReceiver());
                intent.putExtra(MyIntentService.PAYLOAD, b);

                ComponentName cm = startService(intent);
            }
        });

        Button okButton = (Button) findViewById(R.id.enter_button_done);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EnterButtonActivity.this.finish();
            }
        });

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