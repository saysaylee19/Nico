package com.nicotrax.nicotrax.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nicotrax.nicotrax.R;
import com.nicotrax.nicotrax.util.EmailValidator;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.LogInCallback;

//Imports for storing a SmokeRecord (move based on architecture decisions)
import java.util.GregorianCalendar;
import java.util.Date;

public class LoginActivity extends Activity {

    /* Tracks the current state of the activity - showing login fields or signup fields */
    private boolean loginState;

    private Button signupButton;
    private Button toggleSignupLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginState = false;
        signupButton = (Button) findViewById(R.id.signup_button);
        toggleSignupLoginButton = (Button) findViewById((R.id.toggle_signup_login_button));


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.signup_button){
                    registerUser();
                }
            }
        });

        toggleSignupLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.toggle_signup_login_button){
                    toggleSignupLogin();
                }
            }
        });

        Button bleButton = (Button) findViewById(R.id.connect_ble_device_button);
        bleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.connect_ble_device_button){
                    goToBleActivity();
                }
            }
        });

        Button smokeButton = (Button) findViewById(R.id.store_smoke_record_button);
        smokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.store_smoke_record_button) {
                    storeSmokeRecord();
                }
            }
        });
    }

    private void toggleSignupLogin(){
        if(loginState == false){
            loginState = true;
            EditText emailField = (EditText) findViewById(R.id.login_field_email);
            emailField.setVisibility(View.GONE);
            signupButton.setText(R.string.login_text);
            toggleSignupLoginButton.setText(R.string.toggle_to_signup_text);
        } else {
            loginState = false;
            EditText emailField = (EditText) findViewById(R.id.login_field_email);
            emailField.setVisibility(View.VISIBLE);
            signupButton.setText(R.string.signup_text);
            toggleSignupLoginButton.setText(R.string.toggle_to_login_text);
        }

    }

    private void registerUser() {
        EditText emailField = (EditText) findViewById(R.id.login_field_email);
        EditText usernameField = (EditText) findViewById(R.id.login_field_username);
        EditText passwordField = (EditText) findViewById(R.id.login_field_password);

        //This line is needed to prevent a known bug where setError flag won't disappear for fields w/ certain inputTypes
        usernameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        // Validate the log in data
        boolean validationError = false;

        // 1: email is valid
        if(loginState == false) {
            EmailValidator validator = new EmailValidator();
            if (validator.validate(emailField.getText().toString()) == false) {
                validationError = true;
                emailField.setError(getText(R.string.validation_error_email));
            }
        }

        // 2: username exists (parse username restrictions?)
        if (usernameField.length() < 1) {
            validationError = true;
            usernameField.setError(getText(R.string.validation_error_username));
        }

        // 3: password longer than 8 characters
        if (passwordField.length() < 8) {
            validationError = true;
            passwordField.setError(getText(R.string.validation_error_password));
        }

        if(validationError){
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        if(loginState == false) {
            dialog.setMessage(getString(R.string.progress_signup));
        } else {
            dialog.setMessage(getString(R.string.progress_login));
        }
        dialog.show();

        //Sign-up or Login user dependant on loginState
        ParseUser myUser = new ParseUser();
        if(loginState == false) {
            myUser.setUsername(usernameField.getText().toString());
            myUser.setPassword(passwordField.getText().toString());
            myUser.setEmail(emailField.getText().toString());

            myUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    dialog.dismiss();
                    myUserSavedSuccessfully();
                }
            });
        } else {
            ParseUser.logInInBackground(usernameField.getText().toString(), passwordField.getText().toString(),
                new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        dialog.dismiss();
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                           myUserSavedSuccessfully();
                        }
                    }
            });
        }
    }

    private void myUserSavedSuccessfully(){
        Log.i("tag","Parse Test Object saved successfully.");
        //Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
        Intent intent = new Intent(getApplicationContext(), ProfileCreationActivity.class);
        startActivity(intent);
    }

    private void myObjectSaveDidNotSucceed(){
        Log.i("tag","Parse Test Object did not succeed.");
    }

    private void goToBleActivity() {
        //Push us over to a brand new fullscreen activity to test BLE connectivity
        Intent intent = new Intent(getApplicationContext(), BleActivity.class);
        startActivity(intent);
    }

    private void storeSmokeRecord(){
        ParseObject mySmokeRecord = new ParseObject("SmokeRecord");
        ParseUser myUser = ParseUser.getCurrentUser();

        if(myUser != null) {
            mySmokeRecord.put("userId", ParseUser.getCurrentUser().getObjectId());
        } else {
            Toast.makeText(LoginActivity.this, "Please login before attempting to store a SmokeRecord", Toast.LENGTH_SHORT).show();
            return;
        }

        Date myDate = new Date();
        mySmokeRecord.put("smokeTime", myDate);

        mySmokeRecord.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.i("tag", "New smoke record saved");
                }
            }
        });

        mySmokeRecord.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.i("tag", "New smoke record pinned in local datastore");
                }
            }
        });
    }
}