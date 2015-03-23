package com.nicotrax.nicotrax.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nicotrax.nicotrax.R;
import com.nicotrax.nicotrax.util.EmailValidator;

public class ProfileCreationActivity extends Activity {

    private Spinner spinner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        //Retrieve Spinner values
        //manageSpinner();
        Button but= (Button) findViewById(R.id.started_button);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.started_button){
                    callOnBoarding();
                }
            }
        });


    }

    private void callOnBoarding(){
        EditText firstNameField = (EditText) findViewById(R.id.first_name);
        EditText lastNameField = (EditText) findViewById(R.id.last_name);
        EditText dateField = (EditText) findViewById(R.id.date_field);
        spinner1 = (Spinner) findViewById(R.id.gender_spinner);
        //System.out.println(" spinner " + String.valueOf(spinner1.getSelectedItem()));


        // Generic Not Null validation
        boolean validationError = false;


        // 1: FirstName is not empty
        if(firstNameField.length() < 1 ){
            validationError = true;
            firstNameField.setError(getString(R.string.validation_error_blank_firstname));
        }

        // 2: Lastname is not empty
        if (lastNameField.length() < 1) {
            validationError = true;
            lastNameField.setError(getString(R.string.validation_error_blank_lastname));
        }

        // 2: Date of Birth is not empty
        if (dateField.length() == 0) {
            validationError = true;
            dateField.setError(getString(R.string.validation_error_blank_dob));
        }

        if(validationError){
            return;
        }

        /* If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(ProfileCreationActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }
        */

        Intent intent=new Intent(getApplicationContext(),OnboardingActivity.class);
        startActivity(intent);
    }

    private void manageSpinner(){

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
