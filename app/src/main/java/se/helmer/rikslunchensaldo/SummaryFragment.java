package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import se.helmer.rikslunchensaldo.model.Contract;

public class SummaryFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static String TAG = "SummaryFragment";
    int fragNum;

    private TransactionCursorAdapter m_adapter = null;

    TextView mBalanceField;
    TextView mLasttopupField;
    EditText mCardIdField;
    TextView mLatestUpdate;
    CheckBox mAutoUpdate;
    AdView mAdView;
    View mView;

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mPrefsEditor;

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
        m_adapter = new TransactionCursorAdapter(this, null);
        setListAdapter(m_adapter);
        getActivity().getLoaderManager().initLoader(0, null, this);

        Button b = (Button) getView().findViewById(R.id.button_fetch);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBalanceField.setText("Querying...");
                RikslunchenDatasource rd = new RikslunchenDatasource();
                rd.query(getActivity(),"32271843");
            }
        });

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    // Creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = { "_id", "amount", "date", "description" };

        String selectionClause = null;
        String[] selectionArgs = null;

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                Contract.Transaction.contentUri, projection, selectionClause, selectionArgs,
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

    public void update(Bundle b) {
        if (mView != null) {
            mBalanceField = (TextView) getView().findViewById(R.id.tvBalance);
            mCardIdField = (EditText) getView().findViewById(R.id.editCardId);
            mLasttopupField = (TextView) getView().findViewById(R.id.topupdate);

            if (b != null) {
                mBalanceField.setText(b.getString(RikslunchenDatasource.BALANCE));
                mCardIdField.setText(b.getString(RikslunchenDatasource.CARD_ID));
                mLasttopupField.setText(b.getString(RikslunchenDatasource.LAST_TOPUP_DATE));
            }
        }

    }

}
