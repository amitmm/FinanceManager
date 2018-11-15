package edu.illinois.financemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.UserRepo;


/**
 * A login screen that offers login via email/password
 * Inherited class of AccountActivity, contains inner AsyncTask TransmitPostTask
 */
public class LoginActivity extends AccountActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createNoDrawer();

        //Inherited field
        urlString = "http://financemanager.web.engr.illinois.edu/index.php";
        //Inherited field used for Logging tag
        activityName = "LoginActivity";

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getId() == R.id.password) {
                    attemptLogin(v);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Callback Method of TransmitPostTask's onPostExecute
     *
     * @param response - JSONObject containing server's response
     */
    @Override
    public void taskCallBack(JSONObject response) {
        if (response != null) {
            try {
                int success = response.getInt("success");
                if (success == 1) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT);
                    toast.show();
                    JSONObject jsonUser = response.getJSONObject("user");
                    User user = new User();
                    user.name = jsonUser.getString("name");
                    user.email = jsonUser.getString("email");
                    user.id = jsonUser.getLong("id");
                    UserRepo ur = new UserRepo(getApplicationContext());
                    ur.insert(user);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
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
    public void attemptLogin(View v) {
        if (!isConnectedToNetwork(getApplicationContext())) {
            Toast toast = Toast.makeText(getApplicationContext(), "No network.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            // Store values at the time of the login attempt.
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            try {
                JSONObject postData = new JSONObject();
                postData.put("tag", "login");
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

    @Override
    public void onBackPressed() {
        //left empty so you cannot press back while on LoginActivity
    }

    public void handleSignUpButton(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}



