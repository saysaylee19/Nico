package com.nicotrax.nicotrax.fragment.dashBoard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicotrax.nicotrax.R;

public class ProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        TextView tv=(TextView)getActivity().findViewById(R.id.textsetprof);
        System.out.println("Before tv = "+tv.getText());
        tv.setText("Set Profile Here");
        System.out.println("Before tv = "+tv.getText());
    }
}
