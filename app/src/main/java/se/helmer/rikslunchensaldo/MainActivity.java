package se.helmer.rikslunchensaldo;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import se.helmer.rikslunchensaldo.model.Card;

public class MainActivity extends FragmentActivity
        implements MyResultReceiver.Receiver, ActionBar.TabListener   {
    public static final String TAG = "MainActivity";
    MyAdapter mAdapter;
    ViewPager mPager;
    private static MyResultReceiver mReceiver;

    public static final int SUMMARY = 0;
    public static final int RECONCILIATION = 1;
    public static final int LOG = 2;

    public static ResultReceiver getReceiver() {
        return mReceiver;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MyAdapter(this, mPager, savedInstanceState, getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    public static class MyAdapter extends FragmentPagerAdapter {

        private final String[] pageTitles = new String[] { "Summary", "Reconciliation","Log" };

        private final FragmentManager mFm;
        private final ViewPager mViewPager;

        private final Fragment[] fragments = new Fragment[pageTitles.length];

        public MyAdapter(Context context, ViewPager vp,
                Bundle savedInstance, FragmentManager fm) {
            super(fm);
            mFm = fm;
            mViewPager = vp;
            Utils.log(TAG, "FragmentPagerAdapter:" + savedInstance);
        }

        @Override
        public Fragment getItem(int i) {
            if (this.fragments[i] == null) {
                Fragment fragment = mFm.findFragmentByTag("android:switcher:"
                        + mViewPager.getId() + ":" + i);
                if (fragment != null) {
                    this.fragments[i] = fragment;
                    Utils.log(TAG, "getItem(): Got fragment from manager");
                } else {
                    switch (i) {
                    case SUMMARY:
                        this.fragments[i] = SummaryFragment.init(i);
                        break;
                    case RECONCILIATION:
                        this.fragments[i] = ReconciliationFragment.init(i);
                        break;
                    case LOG:
                        this.fragments[i] = LogMessageFragment.init(i);
                        break;
                    }
                    Utils.log(TAG, "fragment newInstance " + this.fragments[i].getTag());
                }
            }
            return fragments[i];
        }

        @Override
        public int getCount() {
            return pageTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_ask_for_card_id:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle("Please enter card ID");
                alert.setMessage("The number can be found on the card");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString().trim();
                        RikslunchenDatasource rd = new RikslunchenDatasource();
                        rd.query(getApplicationContext(), value); // 32271843
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
                return true;
            case R.id.action_refresh:
                DbSession session = new DbSession(this);
                Card card = session.getCard();
                if (card == null) {
                    AlertDialog.Builder noCardAlert = new AlertDialog.Builder(this);
                    noCardAlert.setTitle("No card");
                    noCardAlert.setMessage("Please select action menu -> Card ID");
                    noCardAlert.setPositiveButton("OK", null);
                    noCardAlert.show();
                } else {
                    RikslunchenDatasource rd = new RikslunchenDatasource();
                    rd.query(this, card.getCardId()); // 32271843
                }
                session.close();
                return true;

            case R.id.action_settings:
                // openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        SummaryFragment summaryFragment = (SummaryFragment) mAdapter.getItem(SUMMARY);
        summaryFragment.update(resultData);

        
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    protected void onPause() {
        mReceiver.setReceiver(null); // clear receiver so no leaks.
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver.setReceiver(this);

        final Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra(MyIntentService.COMMAND, MyIntentService.CHECK_DATABASE);
        intent.putExtra(MyIntentService.RECEIVER, MainActivity.getReceiver());
        ComponentName cm = startService(intent);
    }

}
