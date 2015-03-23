package com.nicotrax.nicotrax.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.parse.ParseUser;

import com.nicotrax.nicotrax.fragment.NavigationDrawerFragment;
import com.nicotrax.nicotrax.R;
import com.nicotrax.nicotrax.fragment.dashBoard.DashMainFragment;
import com.nicotrax.nicotrax.fragment.dashBoard.GraphContainer;
import com.nicotrax.nicotrax.fragment.dashBoard.HexContainer;
import com.nicotrax.nicotrax.fragment.dashBoard.MapContainer;
import com.nicotrax.nicotrax.fragment.dashBoard.ProfileContainer;


public class DashboardActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    //Tags to associate fragments with tabs
    private static final String TAB_1_TAG = "tab_1";
    private static final String TAB_2_TAG = "tab_2";
    private static final String TAB_3_TAG = "tab_3";
    private static final String TAB_4_TAG = "tab_4";

    private FragmentTabHost mTabHost;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Initialize all the fragments and tabs
        initViews();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

        // if selected item 3 - "Log Out," log out the Parse user
        if(position == 2) {
            Log.i("tag", "Signing Out");
            ParseUser.logOut();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.dashboard, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((DashboardActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    //Initialize all tabs and views
    private void initViews() {
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);


        mTabHost.addTab(setIndicator(this,mTabHost.newTabSpec(TAB_1_TAG),
                R.drawable.tab_indicator_gen,"Stats",R.drawable.hex),HexContainer.class,null);
        mTabHost.addTab(setIndicator(this,mTabHost.newTabSpec(TAB_2_TAG),
                R.drawable.tab_indicator_gen,"Graph",R.drawable.graph),GraphContainer.class,null);
        mTabHost.addTab(setIndicator(this,mTabHost.newTabSpec(TAB_3_TAG),
                R.drawable.tab_indicator_gen,"Map",R.drawable.map),MapContainer.class,null);
        mTabHost.addTab(setIndicator(this,mTabHost.newTabSpec(TAB_4_TAG),
                R.drawable.tab_indicator_gen,"Profile",R.drawable.profile),ProfileContainer.class,null);

    }
    //Safe exit on back press
    public void onBackPressed() {
        boolean isPopFragment = false;
        String currentTabTag = mTabHost.getCurrentTabTag();

        System.out.println("************TabTag "+currentTabTag);

        if (currentTabTag.equals(TAB_1_TAG)) {
            isPopFragment = ((DashMainFragment)getSupportFragmentManager().findFragmentByTag(TAB_1_TAG)).popFragment();
        }
        else if (currentTabTag.equals(TAB_2_TAG)) {
            isPopFragment = ((DashMainFragment)getSupportFragmentManager().findFragmentByTag(TAB_2_TAG)).popFragment();
            System.out.println("**********isPopFragment"+isPopFragment);
        }
        else if (currentTabTag.equals(TAB_3_TAG)) {
            isPopFragment = ((DashMainFragment)getSupportFragmentManager().findFragmentByTag(TAB_3_TAG)).popFragment();
        }
        else if (currentTabTag.equals(TAB_4_TAG)) {
            isPopFragment = ((DashMainFragment)getSupportFragmentManager().findFragmentByTag(TAB_4_TAG)).popFragment();
        }

        if (!isPopFragment) {
                finish();
        }
    }

    //Indicate selected tab
    private TabHost.TabSpec setIndicator(Context ctx, TabHost.TabSpec spec,
                                         int resid, String string, int genresIcon) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.tab_item, null);
        v.setBackgroundResource(resid);
        TextView tv = (TextView)v.findViewById(R.id.txt_tabtxt);
        ImageView img = (ImageView)v.findViewById(R.id.img_tabtxt);

        tv.setText(string);
        img.setBackgroundResource(genresIcon);
        return spec.setIndicator(v);
    }


}
