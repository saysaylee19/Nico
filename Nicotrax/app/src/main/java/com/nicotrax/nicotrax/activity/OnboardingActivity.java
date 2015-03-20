package com.nicotrax.nicotrax.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.nicotrax.nicotrax.OnboardingListener;
import com.nicotrax.nicotrax.OnboardingQuestionsEnum;
import com.nicotrax.nicotrax.R;
import com.nicotrax.nicotrax.fragment.NavigationDrawerFragment;
import com.nicotrax.nicotrax.fragment.onboarding.HowManyCigsQuestionFragment;
import com.nicotrax.nicotrax.fragment.onboarding.PricePerPackQuestionFragment;
import com.nicotrax.nicotrax.fragment.onboarding.StartSmokingQuestionFragment;
import com.nicotrax.nicotrax.fragment.onboarding.StillSmokingQuestionFragment;
import com.nicotrax.nicotrax.fragment.onboarding.StopSmokingQuestionFragment;

/**
 * Created by rickyh on 3/19/15.
 */
public class OnboardingActivity extends FragmentActivity implements OnboardingListener {

    /**
     * Used to store the last screen title.
     */
    private CharSequence mTitle;

    /**
     * Used to store question number
     */
    private int mQuestionNum;

    /**
     * Fields to store user data
     */
    private boolean stillSmoking;
    private String startDate;
    private String quitDate;
    private int cigsPerDay;
    private double pricePerPack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mTitle = getTitle();

        mQuestionNum = 0;

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, StillSmokingQuestionFragment.newInstance(this))
                .commit();
    }

    // maybe don't need this?
//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 1:
//                mTitle = getString(R.string.title_section1);
//                break;
//            case 2:
//                mTitle = getString(R.string.title_section2);
//                break;
//            case 3:
//                mTitle = getString(R.string.title_section3);
//                break;
//        }
//    }


    @Override
    public void onNextPressed(OnboardingQuestionsEnum questionCode, Object data) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFrag;
        // switch on questionCode to decide what to do
        switch (questionCode) {
            case STILL_SMOKING:
                // go to days smoking
                newFrag = StartSmokingQuestionFragment.newInstance(this);
                boolean value = (boolean) data;
                stillSmoking = value;
                break;
            case START_DATE:
                newFrag = HowManyCigsQuestionFragment.newInstance(this);
                String sD = (String) data;
                startDate = sD;
                break;
            case AMOUNT_PER_DAY:
                newFrag = PricePerPackQuestionFragment.newInstance(this);
                int cpd = Integer.parseInt((String) data);
                cigsPerDay = cpd;
                break;
            case PRICE_PER_PACK:
                newFrag = StopSmokingQuestionFragment.newInstance(this);
                double ppp = Double.parseDouble((String) data);
                pricePerPack = ppp;
                break;
            case QUIT_DATE:
                String qD = (String) data;
                quitDate = qD;
                goToDashboard();
                return;
            default:
                Toast.makeText(this, "Invalid question code.", Toast.LENGTH_LONG);
                return;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, newFrag)
                .commit();
    }

    private void goToDashboard() {
        //save data to Parse
        // for now, just log it
        Log.i("Onboarding", "Still smoking? " + stillSmoking);
        Log.i("Onboarding", "Start date: " + startDate);
        Log.i("Onboarding", "Cigs per day: " + cigsPerDay);
        Log.i("Onboarding", "Price per pack: " + pricePerPack);
        Log.i("Onboarding", "Quit date: " + quitDate);

        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(i);
    }

    @Override
    public void onPreviousPressed(OnboardingQuestionsEnum questionCode, Object data) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFrag;
        // switch on questionCode to decide what to do
        switch (questionCode) {
            case STILL_SMOKING:
                // do nothing
                return;
            case START_DATE:
                newFrag = StillSmokingQuestionFragment.newInstance(this);
                break;
            case AMOUNT_PER_DAY:
                newFrag = StartSmokingQuestionFragment.newInstance(this);
                break;
            case PRICE_PER_PACK:
                newFrag = HowManyCigsQuestionFragment.newInstance(this);
                break;
            case QUIT_DATE:
                newFrag = PricePerPackQuestionFragment.newInstance(this);
                break;
            default:
                Toast.makeText(this, "Invalid question code.", Toast.LENGTH_LONG);
                return;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, newFrag)
                .commit();
    }


}
