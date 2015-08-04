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
import android.widget.TableLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

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
    Button mResetButton;

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
        setListAdapter(m_adapter);
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
            public void onClick(final View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle("Enter transaction");

                LayoutInflater li = LayoutInflater.from(getActivity());
                final View dialogView = li.inflate(R.layout.enter_button_dialog, null);
                alert.setView(dialogView);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText editDescription = (EditText) dialogView.findViewById(R.id.enter_button_dialog_description);
                        String inputDescription = editDescription.getText().toString();

                        EditText editAmount = (EditText) dialogView.findViewById(R.id.enter_button_dialog_amount);
                        String inputAmount = editAmount.getText().toString();

                        List<String> transactionInfo = new ArrayList<String>();
                        transactionInfo.add(inputDescription.trim());
                        transactionInfo.add(inputAmount.trim());
                        makeTransaction(transactionInfo);
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

        mResetButton = (Button) getView().findViewById(R.id.reconcililation_reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle("Reset transactions (new estimated balance)");

                LayoutInflater li = LayoutInflater.from(getActivity());
                final View dialogView = li.inflate(R.layout.reconciliation_reset_dialog, null);
                alert.setView(dialogView);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText editAmount =
                                (EditText) dialogView.findViewById(R.id.reset_estimated_amount);
                        String inputAmount = editAmount.getText().toString();

                        Bundle b = new Bundle();
                        b.putString(Contract.Transaction.ESTIMATEDBALANCE, inputAmount);

                        final Intent intent = new Intent(getActivity(), MyIntentService.class);
                        intent.putExtra(MyIntentService.COMMAND, MyIntentService.TRANSACTION_RESET);
                        intent.putExtra(MyIntentService.RECEIVER, MainActivity.getReceiver());
                        intent.putExtra(MyIntentService.PAYLOAD, b);
                        ComponentName cm = getActivity().startService(intent);

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
                Contract.Transaction._ID,
                Contract.Transaction.AMOUNT,
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

    private void makeTransaction(List<String> tagData) {
        Bundle b = new Bundle();

        b.putString(Contract.EnterButton.DESCRIPTION, tagData.get(0));
        b.putString(Contract.EnterButton.AMOUNT, tagData.get(1));

        final Intent intent = new Intent(getActivity(), MyIntentService.class);
        intent.putExtra(MyIntentService.COMMAND, MyIntentService.TRANSACTION_CREATE);
        intent.putExtra(MyIntentService.RECEIVER, MainActivity.getReceiver());
        intent.putExtra(MyIntentService.PAYLOAD, b);
        ComponentName cm = getActivity().startService(intent);
    }

    public void update(Bundle b) {
        mView = getView();
        if (mView != null) {
            if (b != null) {
                LinearLayout buttonLayout  = (LinearLayout) mView.findViewById(R.id.dynamicButtonLayout);
                buttonLayout.removeAllViewsInLayout();
                buttonLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                ArrayList<String> buttonAmounts = b.getStringArrayList(Contract.EnterButton.BUTTONAMOUNTS);
                ArrayList<String> buttonDescriptions = b.getStringArrayList(Contract.EnterButton.BUTTONDESCRIPTIONS);
                if (buttonAmounts != null) {
                    int idx = 0;
                    for (String amountStr : buttonAmounts) {
                        Button button = new Button(getActivity());
                        String description = buttonDescriptions.get(idx);
                        idx += 1;
                        button.setText(amountStr + " " + description);
                        List<String> tag = new ArrayList<String>();
                        tag.add(description);
                        tag.add(amountStr);
                        button.setTag(tag);

                        buttonLayout.addView(button, lp);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Button b1 = (Button) view;
                                List<String> tagData = (List<String>) b1.getTag();
                                makeTransaction(tagData);
                            }
                        });
                    }
                }

            }
        }

    }

}