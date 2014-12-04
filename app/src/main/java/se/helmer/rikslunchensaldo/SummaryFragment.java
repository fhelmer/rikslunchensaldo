package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView mBalanceField;
    TextView mLasttopupField;
    EditText mCardIdField;
    TextView mLatestUpdate;
    CheckBox mAutoUpdate;
    AdView mAdView;

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mPrefsEditor;

    public static final String APP_SHARED_PREFS = "se.helmer.riksbankensaldo_prefs";

    private OnFragmentInteractionListener mListener;

    ResultReceiver mReceiver;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(String param1, String param2) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.old_activity_main, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        mSharedPrefs =
                getActivity().getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPrefsEditor = mSharedPrefs.edit();

        mBalanceField = (TextView) getView().findViewById(R.id.balance);
        mLasttopupField = (TextView) getView().findViewById(R.id.topupdate);
        mCardIdField = (EditText) getView().findViewById(R.id.editCardId);
        mLatestUpdate = (TextView) getView().findViewById(R.id.latestupdate);
        mAutoUpdate = (CheckBox) getView().findViewById(R.id.autoupdatecb);

        Button b = (Button) getView().findViewById(R.id.button_fetch);

        mReceiver = new ActivityResultReceiver(new Handler());

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLatestUpdate.setText("Querying...");
                mPrefsEditor.putString("card_id", mCardIdField.getText().toString());
                mPrefsEditor.commit();
                RikslunchenDatasource rd = new RikslunchenDatasource();
                rd.query(getActivity(),mReceiver,mCardIdField.getText().toString());

            }
        });

        mAutoUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPrefsEditor.putBoolean(RikslunchenDatasource.AUTO_UPDATE, isChecked);
                mPrefsEditor.commit();
                if (isChecked) {
                    RikslunchenDatasource rd = new RikslunchenDatasource();
                    rd.query(getActivity(),mReceiver,mCardIdField.getText().toString());
                }
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

        mBalanceField.setText(mSharedPrefs.getString(RikslunchenDatasource.BALANCE, ""));
        mLasttopupField.setText(mSharedPrefs.getString(RikslunchenDatasource.LAST_TOPUP_DATE, ""));
        mCardIdField.setText(mSharedPrefs.getString(RikslunchenDatasource.CARD_ID, ""));
        mLatestUpdate.setText(mSharedPrefs.getString(RikslunchenDatasource.LATEST_UPDATE, ""));
        mAutoUpdate.setChecked(mSharedPrefs.getBoolean(RikslunchenDatasource.AUTO_UPDATE, false));
        mAdView.resume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    void updateWidget(Class<?> cls, int widgetid) {
        Intent intent = new Intent(getActivity(),cls);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = {widgetid};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        getActivity().sendBroadcast(intent);
    }

    class ActivityResultReceiver extends ResultReceiver {
        public ActivityResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle b) {

            mBalanceField.setText(b.getString(RikslunchenDatasource.BALANCE));
            mLasttopupField.setText(b
                    .getString(RikslunchenDatasource.LAST_TOPUP_DATE));
            mLatestUpdate.setText(b
                    .getString(RikslunchenDatasource.LATEST_UPDATE));

            Utils.saveSharedPreferences(getActivity(), b);

            updateWidget(RikslunchenWidgetProvider.class,R.xml.widget_info);
            updateWidget(RikslunchenWidgetProviderSmall.class,R.xml.small_widget_info);
        }
    }


}
