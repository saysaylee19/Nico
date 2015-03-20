package com.nicotrax.nicotrax.fragment.onboarding;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nicotrax.nicotrax.OnboardingListener;
import com.nicotrax.nicotrax.OnboardingQuestionsEnum;
import com.nicotrax.nicotrax.R;

public class StillSmokingQuestionFragment extends OnboardingQuestionBaseFragment {

    protected String TAG = "StillSmokingQuestionFragment";
    protected OnboardingQuestionsEnum CODE = OnboardingQuestionsEnum.STILL_SMOKING;
    protected int LAYOUT = R.layout.fragment_still_smoking_question;

    public static StillSmokingQuestionFragment newInstance(OnboardingListener l) {
        StillSmokingQuestionFragment fragment = new StillSmokingQuestionFragment();
        fragment.setListener(l);
        return fragment;
    }

    public StillSmokingQuestionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(LAYOUT, container, false);

        setupUIComponents(rootView);

        //return view
        return rootView;
    }

    @Override
    public void setupUIComponents(View v) {
        // get references to input fields/buttons
        final Button yesButton = (Button) v.findViewById(R.id.yesSmokeButton);
        final Button noButton = (Button) v.findViewById(R.id.noSmokeButton);

        //set input listeners
        yesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.yesSmokeButton){
                    Log.i(TAG, "Pressed yesSmokeButton.");

                    // validate input if needed
                    // --- No input

                    // Fire listener method
                    mListener.onNextPressed(CODE, true); //true parameter indicates yes
                }
            }
        });
        noButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.noSmokeButton){
                    Log.i(TAG, "Pressed yesSmokeButton.");

                    // validate input if needed
                    // --- No input

                    // Fire listener method
                    mListener.onNextPressed(CODE, false); //false parameter indicates no
                }
            }
        });
    }

}
