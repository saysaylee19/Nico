package com.nicotrax.nicotrax.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nicotrax.nicotrax.R;
import com.nicotrax.nicotrax.activity.DashboardActivity;
import com.nicotrax.nicotrax.util.EmailValidator;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button signupButton = (Button) findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.signup_button){
                    registerUser();
                }
            }
        });
    }

    private void registerUser() {
        EditText emailField = (EditText) findViewById(R.id.login_field_email);
        EditText usernameField = (EditText) findViewById(R.id.login_field_username);
        EditText passwordField = (EditText) findViewById(R.id.login_field_password);

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.validation_error_intro));

        // 1: email is valid
        if(emailField.length() == 0){
            validationError = true;
            validationErrorMessage.append(" " + getString(R.string.validation_error_invalid_email));
        } else {
            EmailValidator validator = new EmailValidator();
            if(validator.validate(emailField.getText().toString()) == false){
                validationError = true;
                validationErrorMessage.append(" " + getString(R.string.validation_error_invalid_email));
            }
        }

        // 2: username exists (parse username restrictions?)
        if (usernameField.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.validation_error_join));
            }
            validationError = true;
            validationErrorMessage.append(" " + getString(R.string.validation_error_blank_username));
        }

        // 3: password longer than 8 characters
        if (passwordField.length() < 8) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.validation_error_join));
            }
            validationError = true;
            validationErrorMessage.append(" " + getString(R.string.validation_error_invalid_password));
        }
        validationErrorMessage.append(getString(R.string.validation_error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        //Sign-up New User
        ParseUser myUser = new ParseUser();
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
    }

    private void myUserSavedSuccessfully(){
        Log.i("tag","Parse Test Object saved successfully.");
        Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
        startActivity(intent);
    }

    private void myObjectSaveDidNotSucceed(){
        Log.i("tag","Parse Test Object did not succeed.");
    }
}