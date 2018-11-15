package edu.illinois.financemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.illinois.financemanager.EmailValidator;
import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Inherited class of AccountActivity, contains inner class TransmitPostTask AsyncTask
 * This activity handles account info changes
 */
public class ManageAccountActivity extends AccountActivity {

    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected String mActivityTitle;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_account);
        mActivityTitle = getTitle().toString();
        mDrawerList = (ListView) findViewById(R.id.navList_account);
        createDrawer(ManageAccountActivity.this, mDrawerLayout, mActivityTitle, mDrawerList);

        //Inherited field
        urlString = "http://financemanager.web.engr.illinois.edu/manage_account.php";
        //Inherited field for Logging tag
        activityName = "ManageAccountActivity";

        UserRepo ur = new UserRepo(ManageAccountActivity.this);
        User user = ur.getUser();

        userId = user.id;
        AutoCompleteTextView nameView = (AutoCompleteTextView) findViewById(R.id.name);
        nameView.setText(user.name);
        AutoCompleteTextView emailView = (AutoCompleteTextView) findViewById(R.id.email);
        emailView.setText(user.email);
    }

    @Override
    public void taskCallBack(JSONObject response) {
        if (response != null) {
            try {
                String status = response.getString("status");
                if (status.equals("Success!")) {
                    //Update user
                    UserRepo ur = new UserRepo(ManageAccountActivity.this);
                    User user = new User();
                    user.id = response.getLong("id");
                    user.name = response.getString("name");
                    user.email = response.getString("email");

                    ur.updateUser(user);
                }
                Toast toast = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } catch (JSONException je) {
                Log.d(activityName, "JSON Exception " + je);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No response from server.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Sends changed information to server to change account details
     *
     * @param v: just a view
     */
    public void updateAccount(View v) {
        //Check network
        if (!isConnectedToNetwork(getApplicationContext())) {
            Toast toast = Toast.makeText(getApplicationContext(), "No network.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            emailValidator = new EmailValidator();
            // Store values at the time of the login attempt.
            EditText currentPasswordView = (EditText) findViewById(R.id.currentPassword);
            String currentPassword = currentPasswordView.getText().toString();

            AutoCompleteTextView nameView = (AutoCompleteTextView) findViewById(R.id.name);
            String name = nameView.getText().toString();

            AutoCompleteTextView emailView = (AutoCompleteTextView) findViewById(R.id.email);
            String email = emailView.getText().toString();

            EditText newPasswordView = (EditText) findViewById(R.id.newPassword);
            String newPassword = newPasswordView.getText().toString();

            EditText passwordReenterView = (EditText) findViewById(R.id.password_reenter);
            String passwordReenter = passwordReenterView.getText().toString();

            //Check if newPassword and passwordReenter match
            if (!newPassword.equals(passwordReenter)) {
                Toast toast = Toast.makeText(getApplicationContext(), "Password Mismatch.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            //Check if newPassword is proper length
            if (newPassword.length() != 0 && newPassword.length() < 5) {
                Toast toast = Toast.makeText(getApplicationContext(), "Password length must be at least 5 characters.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            //Check if valid email
            if (!emailValidator.validate(email)) {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid email.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            try {
                JSONObject postData = new JSONObject();
                postData.put("id", userId);
                postData.put("current_password", currentPassword);
                postData.put("name", name);
                postData.put("email", email);
                postData.put("new_password", newPassword);

                // perform the account update attempt.
                TransmitPostTask mAuthTask = new TransmitPostTask();
                mAuthTask.execute(postData);
            } catch (JSONException je) {
                Log.d(activityName, "JSON Exception " + je);
            }
        }
    }
}
