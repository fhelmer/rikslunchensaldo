package se.helmer.rikslunchensaldo;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import se.helmer.rikslunchensaldo.model.Card;
import se.helmer.rikslunchensaldo.model.Contract;

public class ReconciliationFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static String TAG = "ReconciliationFragment";
    int fragNum;

    private TransactionCursorAdapter m_adapter = null;
    AdView mAdView;
    View mView;
    Button mEditEnterButtons;
    Button mOtherAmountButton;
    Button mReset;

    static ReconciliationFragment init(int val) {
        ReconciliationFragment reconciliationFragment = new ReconciliationFragment();

        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        reconciliationFragment.setArguments(args);

        return reconciliationFragment;
    }

    /**
     * Retrieving this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragNum = getArguments() != null ? getArguments().getInt("val") : 1;
        m_adapter = new TransactionCursorAdapter(this, null);
        getActivity().getLoaderManager().initLoader(MainActivity.RECONCILIATION, null, this);
    }

    /**
     * The Fragment's UI is a simple text view showing its instance number and
     * an associated list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_reconciliation,
                container, false);
        return layoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mEditEnterButtons = (Button) getView().findViewById(R.id.reconcililation_edit_buttons);
        mEditEnterButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EnterButtonActivity.class);
                startActivity(intent);
            }
        });

        mOtherAmountButton =
                (Button) getView().findViewById(R.id.reconcililation_other_amount_button);
        mOtherAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle("Please enter amount");
                alert.setMessage("Amount: ");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString().trim();
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        update(null);

        mAdView = (AdView) getView().findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAdView.destroy();
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        mAdView.resume();
        Utils.debug(TAG, "onResume");
    }


    // Creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = { 
                Contract.Transaction.AMOUNT,
                Contract.Transaction.BALANCE, 
                Contract.Transaction.ESTIMATEDBALANCE, 
                Contract.Transaction.DATE,
                Contract.Transaction.DESCRIPTION};

        String selectionClause = null;
        String[] selectionArgs = null;

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                Contract.Transaction.contentUri, projection, selectionClause, selectionArgs,
                "_id DESC");

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

    public void update(Bundle b) {
        mView = getView();
        if (mView != null) {
            Button a75 = new Button(getActivity());
            a75.setText("75");
            Button a50 = new Button(getActivity());
            a50.setText("50");
            LinearLayout buttonLayout  = (LinearLayout) mView.findViewById(R.id.dynamicButtonLayout);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonLayout.addView(a50, lp);
            buttonLayout.addView(a75, lp);
        }

    }

}