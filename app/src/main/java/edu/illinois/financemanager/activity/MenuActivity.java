package edu.illinois.financemanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Abstract super class for Activities that require a menu
 */
public abstract class MenuActivity extends ActionBarActivity {

    protected ArrayAdapter<String> mAdapter;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    /**
     * Creates a drawer that handles navigation for all activities
     *
     * @param context        Activity menu is opened in
     * @param mDrawerLayout  layout
     * @param mActivityTitle title of current Activity
     * @param mDrawerList    list
     */
    public void createDrawer(Context context, DrawerLayout mDrawerLayout, String mActivityTitle, ListView mDrawerList) {
        this.mDrawerLayout = mDrawerLayout;
        this.mActivityTitle = mActivityTitle;
        this.mDrawerList = mDrawerList;
        addDrawerItems(context);
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Sets the link titles and what they correspond to
     *
     * @param context Activity menu is opened in
     */
    private void addDrawerItems(Context context) {
        String[] osArray = {"Home", "Search", "View Report", "View Graphs", "Set Budget", "Reminders", "Categories", "Tip/Bill Calculator", "Account", "Log Out"};
        mAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openActivity(position);
            }
        });
    }

    /**
     * Determines what clicking each menu link will do
     *
     * @param position corresponds to location on menu
     */
    protected void openActivity(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        switch (position) {
            case 0: //Home
                if (this.getClass() != MainActivity.class) {
                    Intent a = new Intent(this, MainActivity.class);
                    startActivity(a);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case 1: //Search
                if (this.getClass() != SearchTransactionActivity.class) {
                    Intent b = new Intent(this, SearchTransactionActivity.class);
                    startActivity(b);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case 2: //View Report
                if (this.getClass() != ViewReportActivity.class) {
                    Intent c = new Intent(this, ViewReportActivity.class);
                    startActivity(c);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case 3: //View Graphs
                if (this.getClass() != ViewGraphActivity.class) {
                    Intent i = new Intent(this, ViewGraphActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case 4: //Set Budget
                if (this.getClass() != ManageBudgetActivity.class) {
                    Intent d = new Intent(this, ManageBudgetActivity.class);
                    startActivity(d);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case 5: //Reminders
                if (this.getClass() != ReminderActivity.class) {
                    Intent e = new Intent(this, ReminderActivity.class);
                    startActivity(e);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case 6: //Categories
                if (this.getClass() != CategoryActivity.class) {
                    Intent f = new Intent(this, CategoryActivity.class);
                    startActivity(f);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case 7: //Tip/Bill Calculator
                if (this.getClass() != CalculatorActivity.class) {
                    Intent j = new Intent(this, CalculatorActivity.class);
                    startActivity(j);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case 8: //Account
                if (this.getClass() != ManageAccountActivity.class) {
                    Intent g = new Intent(this, ManageAccountActivity.class);
                    startActivity(g);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case 9: //Log Out
                UserRepo ur = new UserRepo(MenuActivity.this);
                ur.delete();
                Intent h = new Intent(this, LoginActivity.class);
                startActivity(h);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    /**
     * Sets up drawer in a specific activity
     *
     */
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Creates a null mDrawerToggle for cases when no menu is needed
     */
    public void createNoDrawer() {
        mDrawerToggle = null;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null)
            mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null)
            mDrawerToggle.onConfigurationChanged(newConfig);
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

        //activate navigation bar toggle
        return mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

}