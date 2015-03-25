package com.nicotrax.nicotrax.model;

import com.parse.ParseUser;
import com.parse.ParseObject;

/**
 * Parse DataManager implementation for Nicotrax App
 */
public class ParseDataManager implements DataManager {

    //Custom ParseUser fields from Onboarding
    public static final String USER_STILL_SMOKING_FIELD = "stillSmoking";
    public static final String USER_START_DATE_FIELD = "startDate";
    public static final String USER_QUIT_DATE_FIELD = "targetQuitDate";
    public static final String USER_CIGS_PER_DAY_FIELD = "cigsPerDay";
    public static final String USER_PRICE_PER_PACK_FIELD = "pricePerPack";

    //Custom ParseUser fields for Profile
    public static final String USER_FIRST_NAME_FIELD = "firstName";
    public static final String USER_LAST_NAME_FIELD = "lastName";
    public static final String USER_BIRTH_DATE_FIELD = "birthDate";
    public static final String USER_GENDER_FIELD = "female";
}
