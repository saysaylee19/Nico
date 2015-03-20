package com.nicotrax.nicotrax;

/**
 * Created by rickyh on 3/19/15.
 */
public interface OnboardingListener {
    /**
     * Called when the fragment's 'next' button is pressed.
     * @param questionCode
     * @param data unclassed object data - method will have to determine type based on questionCode
     */
    public void onNextPressed(OnboardingQuestionsEnum questionCode, Object data);

    /**
     * Called when the fragment's 'previous' button is pressed.
     * @param questionCode
     * @param data unclassed object data - method will have to determine type based on questionCode
     */
    public void onPreviousPressed(OnboardingQuestionsEnum questionCode, Object data);
}
