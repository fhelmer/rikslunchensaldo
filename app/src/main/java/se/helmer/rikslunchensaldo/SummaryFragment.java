package se.helmer.rikslunchensaldo;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import se.helmer.rikslunchensaldo.model.Contract;

public class SummaryFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static String TAG = "SummaryFragment";
    private int fragNum;

    private BankStatementCursorAdapter m_adapter = null;

    private TextView mBalanceField;
    private TextView mLasttopupField;
    private TextView mCardIdField;
    TextView mLatestUpdate;
    CheckBox mAutoUpdate;
    private AdView mAdView;
    private View mView;

    public static final String APP_SHARED_PREFS = "se.helmer.riksbankensaldo_prefs";

    ResultReceiver mReceiver;

    static SummaryFragment init(int val) {
        SummaryFragment summaryFrag = new SummaryFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        summaryFrag.setArguments(args);
        return summaryFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragNum = getArguments() != null ? getArguments().getInt("val") : 1;
        m_adapter = new BankStatementCursorAdapter(this, null);
        setListAdapter(m_adapter);
        getActivity().getLoaderManager().initLoader(MainActivity.SUMMARY, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_summary, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    @Override
    public void onDetach() {
        super.onDetach();
    }


    // Creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = { Contract.BankStatement._ID,
                Contract.BankStatement.AMOUNT,
                Contract.BankStatement.BALANCE, Contract.BankStatement.DATE,
                Contract.BankStatement.DESCRIPTION};

        String selectionClause = null;
        String[] selectionArgs = null;

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                Contract.BankStatement.contentUri, projection, selectionClause, selectionArgs,
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

            mBalanceField = (TextView) mView.findViewById(R.id.tvBalance);
            mCardIdField = (TextView) mView.findViewById(R.id.card_id);
            mLasttopupField = (TextView) mView.findViewById(R.id.topupdate);

            if (b != null) {
                String balance = b.getString(Contract.BankStatement.BALANCE);
                String cardId = b.getString(Contract.Card.CARD_ID);
                String lastTopupDate = b.getString(Contract.Card.LAST_TOPUP_DATE);
                if (balance != null) {
                    mBalanceField.setText(balance);
                }
                if (cardId != null) {
                    mCardIdField.setText(cardId);
                }
                if (lastTopupDate != null) {
                    mLasttopupField.setText(lastTopupDate);
                }

            }
        }

    }

}
