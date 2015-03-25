package com.nicotrax.nicotrax.fragment.dashBoard;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicotrax.nicotrax.R;

//Shall delete later
public class DashMainFragment extends Fragment {
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        System.out.println("In DashMainFrag "+getView());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        //transaction.replace(R.id.container_framelayout, fragment);
        transaction.replace(R.id.container_framelayout, fragment);
        transaction.commit();
        getChildFragmentManager().executePendingTransactions();

    }

    public boolean popFragment() {
        boolean isPop = false;
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
            getChildFragmentManager().popBackStack();
        }
        return isPop;
    }

}