package com.nicotrax.nicotrax.fragment.onboarding;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nicotrax.nicotrax.OnboardingListener;
import com.nicotrax.nicotrax.OnboardingQuestionsEnum;
import com.nicotrax.nicotrax.R;

public class StopSmokingQuestionFragment extends OnboardingQuestionBaseFragment {

    protected String TAG = "StopSmokingQuestionFragment";
    protected OnboardingQuestionsEnum CODE = OnboardingQuestionsEnum.QUIT_DATE;
    protected int LAYOUT = R.layout.fragment_stop_smoking_question;

    public static StopSmokingQuestionFragment newInstance(OnboardingListener l) {
        StopSmokingQuestionFragment fragment = new StopSmokingQuestionFragment();
        fragment.setListener(l);
        return fragment;
    }

    public StopSmokingQuestionFragment() {}

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
        final EditText quitField = (EditText) v.findViewById(R.id.timeWhenQuit);
        final Button nextButton = (Button) v.findViewById(R.id.nextButton);
        final Button backButton = (Button) v.findViewById(R.id.backButton);

        //set input listeners
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.nextButton){
                    Log.i(TAG, "Pressed nextButton.");

                    // validate input if needed
                    String input = quitField.getText().toString();
                    if (validateInput(input)) {
                        // Fire listener method
                        mListener.onNextPressed(CODE, input); //true parameter indicates yes
                    } else {
                        Toast.makeText(getActivity(), "Invalid input.", Toast.LENGTH_SHORT);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.backButton){
                    Log.i(TAG, "Pressed backButton.");

                    // validate input if needed
                    // --- Not needed

                    // Fire listener method
                    mListener.onPreviousPressed(CODE, null); //false parameter indicates no
                }
            }
        });
    }

    // TODO: put this into its own validator
    // TODO: make more robust. like actual validation.
    private boolean validateInput(String input) {
        if (input.length() == 0) {
            return false;
        }
        return true;
    }

}
