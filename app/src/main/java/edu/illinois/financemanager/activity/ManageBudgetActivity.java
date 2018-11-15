package edu.illinois.financemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Budget;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.BudgetRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Activity for managing budget
 */
public class ManageBudgetActivity extends MenuActivity {

    protected User user;
    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected String mActivityTitle;
    private Date currentDate;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
    private BudgetRepo bRepo = new BudgetRepo(ManageBudgetActivity.this);
    private UserRepo uRepo = new UserRepo(ManageBudgetActivity.this);
    private Budget budget;
    private EditText newAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_budget);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_budget);
        mActivityTitle = getTitle().toString();
        mDrawerList = (ListView) findViewById(R.id.navList_budget);
        createDrawer(ManageBudgetActivity.this, mDrawerLayout, mActivityTitle, mDrawerList);

        user = uRepo.getUser();
        currentDate = new Date();

        if (user != null) {
            budget = bRepo.getBudgetByDate(dateFormat.format(currentDate), user.id);
            if (budget == null) {
                budget = new Budget();
                budget.amount = 0;
                budget.userID = user.id;
                budget.date = dateFormat.format(currentDate);
                bRepo.insert(budget);
            }

            newAmount = (EditText) findViewById(R.id.new_budget_amount);
            newAmount.setText(String.format("%.2f", budget.amount));

            Button updateButton = (Button) findViewById(R.id.update_budget_button);
            Button cancelButton = (Button) findViewById(R.id.cancel_budget_button);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (newAmount.getText().toString().equals("")) {
                        Toast.makeText(ManageBudgetActivity.this, "Please Input Amount", Toast.LENGTH_SHORT).show();
                    } else {
                        budget.amount = Double.valueOf(newAmount.getText().toString());
                        currentDate = new Date();
                        budget.date = dateFormat.format(currentDate);
                        bRepo.update(budget);

                        Toast.makeText(ManageBudgetActivity.this, "Budget Amount Updated", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
