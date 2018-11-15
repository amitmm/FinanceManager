package edu.illinois.financemanager.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Budget;
import edu.illinois.financemanager.object.Transaction;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.BudgetRepo;
import edu.illinois.financemanager.repo.TransactionRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Activity for displaying transactions in a table format
 */
public class ViewReportActivity extends MenuActivity {

    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected String mActivityTitle;
    ArrayList<Transaction> transactions;
    ArrayList<Float> budgets;
    private Spinner dropdownMonth, dropdownYear, dropdownType;
    private ProgressBar spinner;
    private ScrollView table;
    private TableLayout tableLayout;
    private TransactionRepo transactionRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_view_report);

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_report);
            mActivityTitle = getTitle().toString();
            mDrawerList = (ListView) findViewById(R.id.navList_report);
            createDrawer(ViewReportActivity.this, mDrawerLayout, mActivityTitle, mDrawerList);

            transactionRepo = new TransactionRepo(ViewReportActivity.this);

            dropdownMonth = (Spinner) findViewById(R.id.report_month);
            String[] months = new String[]{"All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
            dropdownMonth.setAdapter(adapterMonth);

            dropdownYear = (Spinner) findViewById(R.id.report_year);
            TransactionRepo tRepo = new TransactionRepo(ViewReportActivity.this);
            ArrayList<Transaction> transactions = tRepo.getAllTransaction();
            DateFormat sdf = new SimpleDateFormat("yyyy", Locale.US);
            ArrayList<String> transactionYears = new ArrayList<>();
            for (int i = 0; i < transactions.size(); i++) {
                String year = sdf.format(transactions.get(i).date);
                if (!transactionYears.contains(year))
                    transactionYears.add(year);
            }
            if (transactionYears.size() == 0)
                transactionYears.add(sdf.format(new Date()));
            String[] years = transactionYears.toArray(new String[transactionYears.size()]);
            ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
            dropdownYear.setAdapter(adapterYear);

            dropdownType = (Spinner) findViewById(R.id.report_type);
            String[] types = new String[]{"Expense", "Income", "Budget"};
            ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
            dropdownType.setAdapter(adapterType);

            //disables Month dropdown when set to Budget
            dropdownType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (position == 2) {
                        dropdownMonth.setSelection(0);
                        dropdownMonth.getSelectedView().setEnabled(false);
                        dropdownMonth.setEnabled(false);
                    } else {
                        dropdownMonth.getSelectedView().setEnabled(true);
                        dropdownMonth.setEnabled(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            table = (ScrollView) findViewById(R.id.table);
            tableLayout = (TableLayout) findViewById(R.id.report);
            spinner = (ProgressBar) findViewById(R.id.progress_bar);
            spinner.setVisibility(View.GONE);
        }
        tableLayout.removeAllViewsInLayout();
    }

    /**
     * Gets an array of floats for the Budget of each month in the given year
     *
     * @param year - 4 digit year
     * @return an ArrayList of floats for the Budget of each month, if no Budgets are found, a zero is stored for that month's index
     */
    private ArrayList<Float> getAnnualBudget(int year) {
        ArrayList<Float> floatArray = new ArrayList<>();
        Calendar currentMonth = Calendar.getInstance();
        UserRepo ur = new UserRepo(ViewReportActivity.this);
        User user = ur.getUser();
        long userId = user.id;
        BudgetRepo br = new BudgetRepo(ViewReportActivity.this);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
        for (int month = 0; month < 12; month++) {
            currentMonth.set(year, month, 1);
            Date date = currentMonth.getTime();
            Budget budget = br.getBudgetByDate(sdf.format(date), userId);
            if (budget == null) {
                floatArray.add(0.0f);
            } else {
                float amount = (float) budget.amount;
                floatArray.add(amount);
            }
        }
        return floatArray;
    }

    /**
     * Sets the ArrayList<Transaction> transactions instance variable equal to a new list of Transactions
     *
     * @param month - Jan == 1, Feb == 2, etc. if month == 0 then all Transactions for the year are returned
     * @param year  - 4 digit year
     * @param type  - "Expense" or "Income"
     */
    private void generateTableData(int month, int year, String type) {
        UserRepo ur = new UserRepo(ViewReportActivity.this);
        User user = ur.getUser();
        if (type.equals("Budget")) {
            budgets = getAnnualBudget(year);
        } else if (month < 1 || month > 12) {
            transactions = transactionRepo.getTransactionsByTypeYear(type, year, user.id);
        } else {
            transactions = transactionRepo.getTransactionsByTypeMonthYearSortedCategory(type, month, year, user.id);
        }
    }

    /**
     * Creates and adds all rows to the table
     * When "Budget" is selected, displays Month and Amount
     * When "Income"/"Expense" is selected, displays Category, Amount, Info, and Date
     *
     * @param type Budget, Income, or Expense
     */
    private void addTableRows(String type) {
        DecimalFormat decFormat = new DecimalFormat("0.00");

        if (type.equals("Budget")) {
            TableRow label = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            label.setLayoutParams(lp);
            label.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView monthLabel = new TextView(this);
            monthLabel.setLayoutParams(lp);
            monthLabel.setPadding(10, 0, 10, 0);
            monthLabel.setBackgroundColor(Color.parseColor("#006833"));
            monthLabel.setTextColor(Color.WHITE);
            monthLabel.setText("MONTH");
            monthLabel.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView amountLabel = new TextView(this);
            amountLabel.setLayoutParams(lp);
            amountLabel.setPadding(10, 0, 10, 0);
            amountLabel.setBackgroundColor(Color.parseColor("#006833"));
            amountLabel.setTextColor(Color.WHITE);
            amountLabel.setText("AMOUNT");

            label.addView(monthLabel);
            label.addView(amountLabel);

            tableLayout.addView(label, 0);
            tableLayout.setColumnShrinkable(0, true);
            tableLayout.setColumnStretchable(0, true);
            tableLayout.setColumnShrinkable(1, true);
            tableLayout.setColumnStretchable(1, true);

            if (budgets.size() != 0) {
                for (int i = 0; i < budgets.size(); i++) {
                    float budget = budgets.get(i);
                    TableRow row = new TableRow(this);
                    row.setLayoutParams(lp);

                    TextView monthName = new TextView(this);
                    monthName.setLayoutParams(lp);
                    monthName.setPadding(10, 0, 10, 0);
                    monthName.setText(getMonth(i + 1));

                    TextView amount = new TextView(this);
                    amount.setLayoutParams(lp);
                    amount.setPadding(10, 0, 10, 0);
                    amount.setText("$" + decFormat.format(budget));

                    row.addView(monthName);
                    row.addView(amount);

                    tableLayout.addView(row, i + 1);
                }
            }
        } else {
            TableRow label = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            label.setLayoutParams(lp);
            label.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView categoryLabel = new TextView(this);
            categoryLabel.setLayoutParams(lp);
            categoryLabel.setPadding(10, 0, 10, 0);
            categoryLabel.setBackgroundColor(Color.parseColor("#006833"));
            categoryLabel.setTextColor(Color.WHITE);
            categoryLabel.setText("CATEGORY");
            categoryLabel.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView amountLabel = new TextView(this);
            amountLabel.setLayoutParams(lp);
            amountLabel.setPadding(10, 0, 10, 0);
            amountLabel.setBackgroundColor(Color.parseColor("#006833"));
            amountLabel.setTextColor(Color.WHITE);
            amountLabel.setText("AMOUNT");
            amountLabel.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView messageLabel = new TextView(this);
            messageLabel.setLayoutParams(lp);
            messageLabel.setPadding(10, 0, 10, 0);
            messageLabel.setSingleLine(false);
            messageLabel.setBackgroundColor(Color.parseColor("#006833"));
            messageLabel.setTextColor(Color.WHITE);
            messageLabel.setText("INFO");
            messageLabel.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView dateLabel = new TextView(this);
            dateLabel.setLayoutParams(lp);
            dateLabel.setPadding(10, 0, 10, 0);
            dateLabel.setBackgroundColor(Color.parseColor("#006833"));
            dateLabel.setTextColor(Color.WHITE);
            dateLabel.setText("DATE");
            dateLabel.setGravity(Gravity.CENTER_HORIZONTAL);

            label.addView(categoryLabel);
            label.addView(amountLabel);
            label.addView(messageLabel);
            label.addView(dateLabel);

            tableLayout.addView(label, 0);
            tableLayout.setColumnShrinkable(2, true);
            tableLayout.setColumnStretchable(2, true);

            if (transactions.size() != 0) {
                for (int i = 0; i < transactions.size(); i++) {
                    Transaction t = transactions.get(i);
                    TableRow row = new TableRow(this);
                    row.setLayoutParams(lp);

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

                    row.addView(category);
                    row.addView(amount);
                    row.addView(message);

                    TextView date = new TextView(this);
                    date.setLayoutParams(lp);
                    date.setPadding(10, 0, 10, 0);
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                    String dateFormatted = DATE_FORMAT.format(t.date);
                    date.setText("" + dateFormatted);
                    row.addView(date);

                    tableLayout.addView(row, i + 1);
                }
            }
        }
    }

    public void handleViewButton(View v) {
        tableLayout.removeAllViewsInLayout();
        new Task().execute();
    }

    /**
     * Gets the integer corresponding to a month name
     *
     * @param month January, February, etc.
     * @return integer
     */
    private int getMonth(String month) {
        switch (month) {
            case "January":
                return 1;
            case "February":
                return 2;
            case "March":
                return 3;
            case "April":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
                return 8;
            case "September":
                return 9;
            case "October":
                return 10;
            case "November":
                return 11;
            case "December":
                return 12;
            default: //All
                return 0;
        }
    }


    /**
     * Gets the month name corresponding to an integer
     *
     * @param month 1, 2, 3, etc.
     * @return January, February, March, etc.
     */
    private String getMonth(int month) {
        if (month == 1)
            return "January";
        else if (month == 2)
            return "February";
        else if (month == 3)
            return "March";
        else if (month == 4)
            return "April";
        else if (month == 5)
            return "May";
        else if (month == 6)
            return "June";
        else if (month == 7)
            return "July";
        else if (month == 8)
            return "August";
        else if (month == 9)
            return "September";
        else if (month == 10)
            return "October";
        else if (month == 11)
            return "November";
        else if (month == 12)
            return "December";
        else
            return "Month does not exist";
    }

    /**
     * Loads all charts before displaying
     */
    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            table.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            String type = String.valueOf(dropdownType.getSelectedItem());
            addTableRows(type);
            spinner.setVisibility(View.GONE);
            table.setVisibility(View.VISIBLE);
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String monthName = String.valueOf(dropdownMonth.getSelectedItem());
            int month = getMonth(monthName);
            int year = Integer.parseInt(String.valueOf(dropdownYear.getSelectedItem()));
            String type = String.valueOf(dropdownType.getSelectedItem());
            generateTableData(month, year, type);
            return null;
        }
    }
}
