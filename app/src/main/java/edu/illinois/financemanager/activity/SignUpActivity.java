package edu.illinois.financemanager.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.illinois.financemanager.EmailValidator;
import edu.illinois.financemanager.R;


/**
 * Inherited class of AccountActivity, Contains inner TransmitPostTask AsyncTask
 */
public class SignUpActivity extends AccountActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private AutoCompleteTextView mNameView;
    private EditText mPasswordReenterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        createNoDrawer();

        //Inherited field
        urlString = "http://financemanager.web.engr.illinois.edu/index.php";
        //Inherited field for Logging tag
        activityName = "SignUpActivity";

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mNameView = (AutoCompleteTextView) findViewById(R.id.name);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordReenterView = (EditText) findViewById(R.id.password_reenter);
    }

    @Override
    public void taskCallBack(JSONObject response) {
        if (response != null) {
            try {
                int success = response.getInt("success");
                if (success == 1) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Registered!", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
                else {
                    //Error
                    String errorMessage = response.getString("error_msg");
                    Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException je) {
                Log.d(activityName, "JSON Exception " + je);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No response from server.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptSignUp(View v) {

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String name = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordAgain = mPasswordReenterView.getText().toString();
        emailValidator = new EmailValidator();
        if (!password.equals(passwordAgain)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Mismatch passwords.", Toast.LENGTH_SHORT);
            toast.show();
        } else if (!emailValidator.validate(email)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid email.", Toast.LENGTH_SHORT);
            toast.show();
        } else if (password.length() < 5) {
            Toast toast = Toast.makeText(getApplicationContext(), "Password length must be at least 5 characters.", Toast.LENGTH_SHORT);
            toast.show();
        } else if (!isConnectedToNetwork(getApplicationContext())) {
            Toast toast = Toast.makeText(getApplicationContext(), "No network.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            try {
                JSONObject postData = new JSONObject();
                postData.put("tag", "register");
                postData.put("name", name);
                postData.put("email", email);
                postData.put("password", password);

                // perform the user login attempt.
                TransmitPostTask mAuthTask = new TransmitPostTask();
                mAuthTask.execute(postData);
            } catch (JSONException je) {
                Log.d(activityName, "JSON Exception " + je);
            }
        }
    }
}



