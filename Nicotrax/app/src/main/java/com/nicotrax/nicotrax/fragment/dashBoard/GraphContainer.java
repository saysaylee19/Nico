package com.nicotrax.nicotrax.fragment.dashBoard;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicotrax.nicotrax.R;


public class GraphContainer extends DashMainFragment {
    private boolean IsViewInited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_main, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!IsViewInited) {
            IsViewInited = true;
            initView();
        }
    }

    private void initView() {
        replaceFragment(new GraphFragment(), false);

    }



}