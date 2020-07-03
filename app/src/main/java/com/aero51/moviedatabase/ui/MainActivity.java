package com.aero51.moviedatabase.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.aero51.moviedatabase.R;
import com.aero51.moviedatabase.utils.TabViewListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {
    private CustomViewPager customViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customViewPager = findViewById(R.id.view_pager);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        customViewPager.setAdapter(sectionsPagerAdapter);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(customViewPager);


        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public void onBackPressed() {
        //implement for each tab
        RootEpgTvFragment fragment = (RootEpgTvFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 0);
       // getSupportFragmentManager().get
        if (fragment.getChildFragmentManager().getBackStackEntryCount() != 0) {
            fragment.getChildFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }


    private static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment fragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }


        @Override
        public Fragment getItem(int position) {
            fragment = null;
            switch (position) {
                case 0:

                    fragment = new RootEpgTvFragment();

                    break;
                case 1:
                    fragment = new RootMoviesFragment();

                    break;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Tv guide";
                case 1:
                    return "Movies";

            }
            return null;
        }
    }
}

