package com.nicotrax.nicotrax.fragment.dashBoard;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nicotrax.nicotrax.R;

public class ProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile_display, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        ProgressBar pb_quit= (ProgressBar) getActivity().findViewById(R.id.progressBar_quit);
        pb_quit.setProgress(75);
        pb_quit.getProgressDrawable().setColorFilter(R.color.background_color, PorterDuff.Mode.SRC_IN);

        ProgressBar pb_save= (ProgressBar) getActivity().findViewById(R.id.progressBar_save);
        pb_save.setProgress(55);
        pb_save.getProgressDrawable().setColorFilter(R.color.background_color, PorterDuff.Mode.SRC_IN);

        //TextView tv=(TextView)getActivity().findViewById(R.id.textsetprof);
        //System.out.println("Before tv = "+tv.getText());
        //tv.setText("Set Profile Here");
        //System.out.println("Before tv = "+tv.getText());
    }
}
