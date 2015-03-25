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
        View rootView=inflater.inflate(R.layout.activity_profile_display, container, false);
        // Inflate the layout for this fragment
        //Static Progress Bars
        ProgressBar pb_quit= (ProgressBar) rootView.findViewById(R.id.progressBar_quit);
        pb_quit.setProgress(75);
        pb_quit.getProgressDrawable().setColorFilter(R.color.background_color, PorterDuff.Mode.SRC_IN);

        ProgressBar pb_save= (ProgressBar) rootView.findViewById(R.id.progressBar_save);
        pb_save.setProgress(55);
        pb_save.getProgressDrawable().setColorFilter(R.color.background_color, PorterDuff.Mode.SRC_IN);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);


    }
}
