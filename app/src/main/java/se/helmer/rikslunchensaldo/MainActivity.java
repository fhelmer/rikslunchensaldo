package se.helmer.rikslunchensaldo;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends FragmentActivity
        implements MyResultReceiver.Receiver, ActionBar.TabListener   {
    public static final String TAG = "MainActivity";
    MyAdapter mAdapter;
    ViewPager mPager;
    private static MyResultReceiver mReceiver;

    public static final int SUMMARY = 0;

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
                    default:
                        this.fragments[i] = TransactionListFragment.init(i);
                        break;
                    }
                    Utils.log(
                            TAG,
                            "fragment newInstance "
                                    + this.fragments[i].getTag());
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
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    }

}
