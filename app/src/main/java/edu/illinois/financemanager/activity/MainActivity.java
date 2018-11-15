package edu.illinois.financemanager.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Budget;
import edu.illinois.financemanager.object.Transaction;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.BudgetRepo;
import edu.illinois.financemanager.repo.TransactionRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Homepage of the Application
 * Launches LoginActivity if no user is detected
 * Displays budget and recent transactions
 */
public class MainActivity extends MenuActivity {

    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected String mActivityTitle;

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Navigation code
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        mDrawerList = (ListView) findViewById(R.id.navList);
        createDrawer(MainActivity.this, mDrawerLayout, mActivityTitle, mDrawerList);

        //User code
        UserRepo ur = new UserRepo(MainActivity.this);
        User user = ur.getUser();
        //Check if User is logged in
        if (user == null) {
            launchLogin();
        } else {
            TextView welcome = (TextView) findViewById(R.id.welcome);
            welcome.setText("Welcome " + user.name + "!");
            welcome.setVisibility(View.VISIBLE);

            BudgetRepo bRepo = new BudgetRepo(MainActivity.this);
            Calendar currentDate = Calendar.getInstance();
            int month = currentDate.get(Calendar.MONTH) + 1;
            int year = currentDate.get(Calendar.YEAR);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
            DecimalFormat decFormat = new DecimalFormat("0.00");
            Budget budget = bRepo.getBudgetByDate(dateFormat.format(currentDate.getTime()), user.id);

            TextView budgetTotal = (TextView) findViewById(R.id.total);
            TextView remainingView = (TextView) findViewById(R.id.remaining);
            TextView expensesView = (TextView) findViewById(R.id.expense);
            TextView incomeView = (TextView) findViewById(R.id.income);
            TransactionRepo tRepo = new TransactionRepo(MainActivity.this);
            double expensesTotal = 0;
            ArrayList<Transaction> expenseList = tRepo.getTransactionsByTypeMonthYear("Expense", month, year, user.id);
            ArrayList<Transaction> incomeList = tRepo.getTransactionsByTypeMonthYear("Income", month, year, user.id);

            //Get the Expense transactions
            for (Transaction expense : expenseList) {
                expensesTotal += expense.amount;
            }
            expensesView.setText("$ " + decFormat.format(expensesTotal));

            //Get the Income transactions
            double incomeTotal = 0;
            for (Transaction income : incomeList) {
                incomeTotal += income.amount;
            }
            incomeView.setText("$ " + decFormat.format(incomeTotal));

            //Perform budget calculations if necessary
            if (budget == null) {
                budget = new Budget();
                budget.amount = 0;
                budget.userID = user.id;
                budget.date = dateFormat.format(currentDate.getTime());
                bRepo.insert(budget);
            }

            budgetTotal.setText("$ " + decFormat.format(budget.amount));
            double remainingBudget = budget.amount - expensesTotal;
            remainingView.setText("$ " + decFormat.format(remainingBudget));

            tableLayout = (TableLayout) findViewById(R.id.monthlyTransactions);
            ArrayList<Transaction> transactions = tRepo.getTransactionsByMonthYear(month, year, user.id);
            generateTableData(transactions);
        }
    }

    /**
     * Creates and displays a table of all transactions that occurred in the current month
     * Shows type, category, amount, info, and date
     *
     * @param transactions ArrayList of Transaction objects of the current month
     */
    public void generateTableData(ArrayList<Transaction> transactions) {
        DecimalFormat decFormat = new DecimalFormat("0.00");

        TableRow label = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        label.setLayoutParams(lp);
        label.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView typeLabel = new TextView(this);
        typeLabel.setLayoutParams(lp);
        typeLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        typeLabel.setPadding(10, 0, 10, 0);
        typeLabel.setBackgroundColor(Color.parseColor("#006833"));
        typeLabel.setTextColor(Color.WHITE);
        typeLabel.setText("TYPE");

        TextView categoryLabel = new TextView(this);
        categoryLabel.setLayoutParams(lp);
        categoryLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        categoryLabel.setPadding(10, 0, 10, 0);
        categoryLabel.setBackgroundColor(Color.parseColor("#006833"));
        categoryLabel.setTextColor(Color.WHITE);
        categoryLabel.setText("CATEGORY");

        TextView amountLabel = new TextView(this);
        amountLabel.setLayoutParams(lp);
        amountLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        amountLabel.setPadding(10, 0, 10, 0);
        amountLabel.setBackgroundColor(Color.parseColor("#006833"));
        amountLabel.setTextColor(Color.WHITE);
        amountLabel.setText("AMOUNT");

        TextView messageLabel = new TextView(this);
        messageLabel.setLayoutParams(lp);
        messageLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        messageLabel.setPadding(10, 0, 10, 0);
        messageLabel.setSingleLine(false);
        messageLabel.setBackgroundColor(Color.parseColor("#006833"));
        messageLabel.setTextColor(Color.WHITE);
        messageLabel.setText("INFO");

        TextView dateLabel = new TextView(this);
        dateLabel.setLayoutParams(lp);
        dateLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        dateLabel.setPadding(10, 0, 10, 0);
        dateLabel.setBackgroundColor(Color.parseColor("#006833"));
        dateLabel.setTextColor(Color.WHITE);
        dateLabel.setText("DATE");

        label.addView(typeLabel);
        label.addView(categoryLabel);
        label.addView(amountLabel);
        label.addView(messageLabel);
        label.addView(dateLabel);

        tableLayout.addView(label, 0);
        tableLayout.setColumnShrinkable(3, true);
        tableLayout.setColumnStretchable(3, true);

        if (transactions.size() != 0) {
            for (int i = 0; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);
                TableRow row = new TableRow(this);
                row.setLayoutParams(lp);
                row.setGravity(Gravity.CENTER_HORIZONTAL);

                TextView type = new TextView(this);
                type.setLayoutParams(lp);
                type.setPadding(10, 0, 10, 0);
                type.setText(t.type);

                TextView category = new TextView(this);
                category.setLayoutParams(lp);
                category.setPadding(10, 0, 10, 0);
                category.setText(t.category);

                TextView amount = new TextView(this);
                amount.setLayoutParams(lp);
                amount.setPadding(10, 0, 10, 0);
                amount.setText("$" + decFormat.format(t.amount));

                TextView message = new TextView(this);
                message.setLayoutParams(lp);
                message.setPadding(10, 0, 10, 0);
                message.setText(t.message);

                row.addView(type);
                row.addView(category);
                row.addView(amount);
                row.addView(message);

                TextView date = new TextView(this);
                date.setLayoutParams(lp);
                date.setPadding(10, 0, 10, 0);
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd", Locale.US);
                String dateFormatted = DATE_FORMAT.format(t.date);
                date.setText("" + dateFormatted);
                row.addView(date);

                tableLayout.addView(row, i + 1);
            }
        }
    }

    /**
     * Starts the AddTransactionActivity
     * @param v - XML view that button appears on
     */
    public void handleAddTransactionButton(View v) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        startActivity(intent);
    }

    /**
     * Starts the LoginActivity
     */
    public void launchLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
