package se.helmer.rikslunchensaldo;

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

import se.helmer.rikslunchensaldo.model.RikslunchenDatabaseHelper;

public class MainActivity extends FragmentActivity implements MyResultReceiver.Receiver  {
    static final int ITEMS = 4;
    MyAdapter mAdapter;
    ViewPager mPager;
    private static MyResultReceiver mReceiver;

    public static final int SUMMARY = 1;

    public static ResultReceiver getReceiver() {
        return mReceiver;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        Button button = (Button) findViewById(R.id.first);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });
        button = (Button) findViewById(R.id.last);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(ITEMS - 1);
            }
        });
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show image
                    return ImageFragment.init(position);
                case SUMMARY:
                    return SummaryFragment.init(position);
                default:// Fragment # 2-9 - Will show list
                    return TransactionListFragment.init(position);
            }
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
