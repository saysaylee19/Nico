package com.nicotrax.nicotrax.fragment.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nicotrax.nicotrax.OnboardingListener;
import com.nicotrax.nicotrax.OnboardingQuestionsEnum;
import com.nicotrax.nicotrax.R;

public abstract class OnboardingQuestionBaseFragment extends Fragment {

    // listener to call into activity
    protected OnboardingListener mListener = null;

    public OnboardingQuestionBaseFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setListener(OnboardingListener l) {
        mListener = l;
    }

    /**
     * Classes must override this method
     * @param v rootView
     */
    public abstract void setupUIComponents(View v);


}
