package edu.illinois.financemanager.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Budget;
import edu.illinois.financemanager.object.Category;
import edu.illinois.financemanager.object.Transaction;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.BudgetRepo;
import edu.illinois.financemanager.repo.CategoryRepo;
import edu.illinois.financemanager.repo.TransactionRepo;
import edu.illinois.financemanager.repo.UserRepo;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Activity for displaying transactions in bar chart, line chart, and pie chart
 */
public class ViewGraphActivity extends MenuActivity {
    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected String mActivityTitle;
    protected ColumnChartData dataColumn;
    protected LineChartData dataLine;
    ArrayList<Category> categories;
    private Spinner dropdownMonth, dropdownYear, dropdownType;
    private ProgressBar spinner;
    private ScrollView charts;
    private ColumnChartView chartColumn;
    private LineChartView chartLine;
    private PieChartView chartPie;
    private PieChartData dataPie;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = true;
    private TransactionRepo transactionRepo;
    private CategoryRepo categoryRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_view_graph);

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_graph);
            mActivityTitle = getTitle().toString();
            mDrawerList = (ListView) findViewById(R.id.navList_graph);
            createDrawer(ViewGraphActivity.this, mDrawerLayout, mActivityTitle, mDrawerList);

            transactionRepo = new TransactionRepo(ViewGraphActivity.this);
            categoryRepo = new CategoryRepo(ViewGraphActivity.this);

            dropdownMonth = (Spinner) findViewById(R.id.report_month);
            String[] months = new String[]{"All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
            dropdownMonth.setAdapter(adapterMonth);

            dropdownYear = (Spinner) findViewById(R.id.report_year);
            TransactionRepo tRepo = new TransactionRepo(ViewGraphActivity.this);
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

            charts = (ScrollView) findViewById(R.id.charts);
            spinner = (ProgressBar) findViewById(R.id.progress_bar);
            spinner.setVisibility(View.GONE);

            chartColumn = (ColumnChartView) findViewById(R.id.chart_column);
            chartColumn.setOnValueTouchListener(new ColumnValueTouchListener());
            chartColumn.setZoomEnabled(false);
            chartLine = (LineChartView) findViewById(R.id.chart_line);
            chartLine.setOnValueTouchListener(new LineValueTouchListener());
            chartLine.setZoomEnabled(false);
            chartPie = (PieChartView) findViewById(R.id.chart_pie);
            chartPie.setOnValueTouchListener(new PieValueTouchListener());
            chartPie.setZoomEnabled(false);

            chartColumn.setVisibility(View.GONE);
            chartLine.setVisibility(View.GONE);
            chartPie.setVisibility(View.GONE);
        }
    }

    /**
     * Stores the summed expense for each Category in a given month and year in an array
     * Floats are ordered in the same way the CategoryRepo returns its Categories
     *
     * @param month (Jan == 1, Feb == 2, etc.)
     * @param year  year
     * @return array of float values for the sum of each Category or null if there are no categories
     */
    private ArrayList<Float> getExpenseByCategoryMonthYear(int month, int year) {
        UserRepo ur = new UserRepo(ViewGraphActivity.this);
        User user = ur.getUser();
        ArrayList<Category> categories = categoryRepo.getCategoryList(user.id);
        if (categories.size() == 0) {
            return null;
        }
        ArrayList<Float> floatArray = new ArrayList<>();
        for (Category category : categories) {
            ArrayList<Transaction> transactions = transactionRepo.getTransactionsByTypeCategoryMonthYear("Expense", category.name, month, year, user.id);
            float sum = 0.0f;
            for (Transaction transaction : transactions) {
                sum += transaction.amount;
            }
            floatArray.add(sum);
        }
        return floatArray;
    }

    /**
     * Stores the summed income for each Category in a given month and year in an array
     * Floats are ordered in the same way the CategoryRepo returns its Categories
     *
     * @param month (Jan == 1, Feb == 2, etc.)
     * @param year  year
     * @return array of float values for the sum of each Category or null if there are no categories
     */
    private ArrayList<Float> getIncomeByCategoryMonthYear(int month, int year) {
        UserRepo ur = new UserRepo(ViewGraphActivity.this);
        User user = ur.getUser();
        ArrayList<Category> categories = categoryRepo.getCategoryList(user.id);
        if (categories.size() == 0) {
            return null;
        }
        ArrayList<Float> floatArray = new ArrayList<>();
        for (Category category : categories) {
            ArrayList<Transaction> transactions = transactionRepo.getTransactionsByTypeCategoryMonthYear("Income", category.name, month, year, user.id);
            float sum = 0.0f;
            for (Transaction transaction : transactions) {
                sum += transaction.amount;
            }
            floatArray.add(sum);
        }
        return floatArray;
    }

    /**
     * Stores summed income for each Category in a given year in an array
     * Floats are ordered in the same way CategoryRepo returns its Categories
     *
     * @param year (4 digit year)
     * @return array of float values for sum of each Category or null if there are no categories
     */
    private ArrayList<Float> getAnnualIncomeByCategory(int year) {
        ArrayList<Float> income = getIncomeByCategoryMonthYear(1, year);
        if (income != null) {
            for (int i = 2; i <= 12; i++) {
                ArrayList<Float> month = getIncomeByCategoryMonthYear(i, year);
                if (month != null) {
                    for (int j = 0; j < month.size(); j++) {
                        income.set(j, income.get(j) + month.get(j));
                    }
                }
            }
        }
        return income;
    }

    /**
     * Stores summed expenses for each Category in a given year in an array
     * Floats are ordered in the same way CategoryRepo returns its Categories
     *
     * @param year (4 digit year)
     * @return array of float values for sum of each Category or null if there are no categories
     */
    private ArrayList<Float> getAnnualExpenseByCategory(int year) {
        ArrayList<Float> expense = getExpenseByCategoryMonthYear(1, year);
        if (expense != null) {
            for (int i = 2; i <= 12; i++) {
                ArrayList<Float> month = getExpenseByCategoryMonthYear(i, year);
                if (month != null) {
                    for (int j = 0; j < month.size(); j++) {
                        expense.set(j, expense.get(j) + month.get(j));
                    }
                }
            }
        }
        return expense;
    }

    /**
     * Stores the summed expenses for each day in the given month and year in an array
     *
     * @param month (Jan == 1, Feb == 2, etc.)
     * @param year  4 digit year
     * @return array of float values for the expense sum for each day, length of the array is equal to number of days in the month
     */
    private ArrayList<Float> getMonthlyExpenses(int month, int year) {
        UserRepo ur = new UserRepo(ViewGraphActivity.this);
        User user = ur.getUser();
        int daysInMonth = getDaysInMonth(month, year);
        ArrayList<Float> floatArray = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            ArrayList<Transaction> transactions = transactionRepo.getTransactionsByTypeDate("Expense", day, month, year, user.id);
            float sum = 0.0f;
            for (Transaction transaction : transactions) {
                sum += transaction.amount;
            }
            floatArray.add(sum);
        }
        return floatArray;
    }

    /**
     * Stores the summed income for each day in the given month and year in an array
     *
     * @param month (Jan == 1, Feb == 2, etc.)
     * @param year  4 digit year
     * @return array of float values for the income sum for each day, length of the array is equal to number of days in the month
     */
    private ArrayList<Float> getMonthlyIncome(int month, int year) {
        UserRepo ur = new UserRepo(ViewGraphActivity.this);
        User user = ur.getUser();
        int daysInMonth = getDaysInMonth(month, year);
        ArrayList<Float> floatArray = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            ArrayList<Transaction> transactions = transactionRepo.getTransactionsByTypeDate("Income", day, month, year, user.id);
            float sum = 0.0f;
            for (Transaction transaction : transactions) {
                sum += transaction.amount;
            }
            floatArray.add(sum);
        }
        return floatArray;
    }

    /**
     * Gets an array of floats for the Budget of each month in the given year
     *
     * @param year 4 digit year
     * @return an ArrayList of floats for the Budget of each month, if no Budgets are found, a zero is stored for that month's index
     */
    private ArrayList<Float> getAnnualBudget(int year) {
        ArrayList<Float> floatArray = new ArrayList<>();
        Calendar currentMonth = Calendar.getInstance();
        UserRepo ur = new UserRepo(ViewGraphActivity.this);
        User user = ur.getUser();
        long userId = user.id;
        BudgetRepo br = new BudgetRepo(ViewGraphActivity.this);
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
     * Returns the sum of all the floats in an ArrayList
     *
     * @param values values
     * @return sum
     */
    private float sumArrayList(ArrayList<Float> values) {
        float sum = 0.0f;
        for (float value : values)
            sum += value;
        return sum;
    }

    /**
     * Checks if ArrayList contains only zeros
     *
     * @param values values
     * @return boolean
     */
    private boolean containsOnlyZeros(ArrayList<Float> values) {
        for (float v : values) {
            if (v != 0)
                return false;
        }
        return true;
    }

    /**
     * Sets the data in each column and adds it to the column chart
     *
     * @param month (Jan == 1, Feb == 2, etc.)
     * @param year  4 digit year
     * @param type  Expense, Income, or Budget
     */
    private void generateColumnData(int month, int year, String type) {
        if (month == 0 || type.equals("Budget")) {
            ArrayList<Float> budget = getAnnualBudget(year);

            int numColumns = 14;
            // Column can have many subcolumns, using 1 subcolumn in each column here
            List<Column> columns = new ArrayList<>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {
                values = new ArrayList<>();
                if (i == 0 || i == numColumns - 1)
                    values.add(new SubcolumnValue((float) 0, ChartUtils.pickColor())); //add space on sides and offset axis label
                else {
                    if (type.equals("Budget")) {
                        values.add(new SubcolumnValue(budget.get(i - 1), Color.parseColor("#006833")));
                    } else if (month == 0) {
                        if (type.equals("Income"))
                            values.add(new SubcolumnValue(sumArrayList(getMonthlyIncome(i, year)), Color.parseColor("#006833")));
                        else if (type.equals("Expense"))
                            values.add(new SubcolumnValue(sumArrayList(getMonthlyExpenses(i, year)), Color.parseColor("#006833")));
                    }
                }
                Column column = new Column(values);
                column.setHasLabels(hasLabels);
                column.setHasLabelsOnlyForSelected(hasLabelForSelected);
                columns.add(column);
            }

            dataColumn = new ColumnChartData(columns);

            if (hasAxes) {
                Axis axisX = Axis.generateAxisFromRange(1, 12, 1);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Month");
                    axisY.setName("Amount");
                }
                dataColumn.setAxisXBottom(axisX);
                dataColumn.setAxisYLeft(axisY);
            } else {
                dataColumn.setAxisXBottom(null);
                dataColumn.setAxisYLeft(null);
            }

            chartColumn.setColumnChartData(dataColumn);
        } else {
            ArrayList<Float> income = getMonthlyIncome(month, year);
            ArrayList<Float> expense = getMonthlyExpenses(month, year);

            int numColumns = getDaysInMonth(month, year) + 2;
            List<Column> columns = new ArrayList<>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {
                values = new ArrayList<>();
                if (i == 0 || i == numColumns - 1)
                    values.add(new SubcolumnValue((float) 0, ChartUtils.pickColor()));
                else {
                    if (type.equals("Income")) {
                        values.add(new SubcolumnValue(income.get(i - 1), Color.parseColor("#006833")));
                    } else if (type.equals("Expense")) {
                        values.add(new SubcolumnValue(expense.get(i - 1), Color.parseColor("#006833")));
                    }
                }
                Column column = new Column(values);
                column.setHasLabels(hasLabels);
                column.setHasLabelsOnlyForSelected(hasLabelForSelected);
                columns.add(column);
            }

            dataColumn = new ColumnChartData(columns);

            if (hasAxes) {
                Axis axisX = Axis.generateAxisFromRange(1, getDaysInMonth(month, year), 1);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Date");
                    axisY.setName("Amount");
                }
                dataColumn.setAxisXBottom(axisX);
                dataColumn.setAxisYLeft(axisY);
            } else {
                dataColumn.setAxisXBottom(null);
                dataColumn.setAxisYLeft(null);
            }

            chartColumn.setColumnChartData(dataColumn);
        }
    }

    /**
     * Sets the data in each point and adds it to the line chart
     *
     * @param month (Jan == 1, Feb == 2, etc.)
     * @param year  4 digit year
     * @param type  Expense, Income, or Budget
     */
    private void generateLineData(int month, int year, String type) {
        if (month == 0 || type.equals("Budget")) {
            ArrayList<Float> budget = getAnnualBudget(year);

            List<Line> lines = new ArrayList<>();
            List<PointValue> values = new ArrayList<>();
            int numPoints = 12;
            for (int i = 1; i <= numPoints; ++i) {
                if (type.equals("Budget")) {
                    values.add(new PointValue(i, budget.get(i - 1)));
                } else { //for income/expense
                    if (type.equals("Income"))
                        values.add(new PointValue(i, sumArrayList(getMonthlyIncome(i, year))));
                    else if (type.equals("Expense"))
                        values.add(new PointValue(i, sumArrayList(getMonthlyExpenses(i, year))));
                }
            }

            Line line = new Line(values);
            line.setColor(Color.parseColor("#006833"));
            line.setShape(ValueShape.CIRCLE);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            lines.add(line);

            dataLine = new LineChartData(lines);

            if (hasAxes) {
                Axis axisX = Axis.generateAxisFromRange(1, 12, 1);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Month");
                    axisY.setName("Amount");
                }
                dataLine.setAxisXBottom(axisX);
                dataLine.setAxisYLeft(axisY);
            } else {
                dataLine.setAxisXBottom(null);
                dataLine.setAxisYLeft(null);
            }

            dataLine.setBaseValue(Float.NEGATIVE_INFINITY);
            chartLine.setLineChartData(dataLine);
        } else {
            ArrayList<Float> income = getMonthlyIncome(month, year);
            ArrayList<Float> expense = getMonthlyExpenses(month, year);

            List<Line> lines = new ArrayList<>();
            List<PointValue> values = new ArrayList<>();
            int numPoints = getDaysInMonth(month, year);
            for (int i = 1; i <= numPoints; ++i) {
                if (type.equals("Income")) {
                    values.add(new PointValue(i, income.get(i - 1)));
                } else if (type.equals("Expense")) {
                    values.add(new PointValue(i, expense.get(i - 1)));
                }
            }

            Line line = new Line(values);
            line.setColor(Color.parseColor("#006833"));
            line.setShape(ValueShape.CIRCLE);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            lines.add(line);

            dataLine = new LineChartData(lines);

            if (hasAxes) {
                Axis axisX = Axis.generateAxisFromRange(1, getDaysInMonth(month, year), 1);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Date");
                    axisY.setName("Amount");
                }
                dataLine.setAxisXBottom(axisX);
                dataLine.setAxisYLeft(axisY);
            } else {
                dataLine.setAxisXBottom(null);
                dataLine.setAxisYLeft(null);
            }

            dataLine.setBaseValue(Float.NEGATIVE_INFINITY);
            chartLine.setLineChartData(dataLine);
        }

    }

    /**
     * Sets the data in each slice and adds it to the pie chart
     *
     * @param month (Jan == 1, Feb == 2, etc.)
     * @param year  4 digit year
     * @param type  Expense, Income, or Budget
     */
    private void generatePieData(int month, int year, String type) {
        UserRepo uRepo = new UserRepo(ViewGraphActivity.this);
        User user = uRepo.getUser();
        categories = categoryRepo.getCategoryList(user.id);
        int numSlices = categories.size();

        if (numSlices == 0)
            chartPie.setVisibility(View.GONE);
        else {
            List<SliceValue> values = new ArrayList<>();
            if (month == 0) {
                if (type.equals("Income")) {
                    ArrayList<Float> income = getAnnualIncomeByCategory(year);
                    if (containsOnlyZeros(income))
                        chartPie.setVisibility(View.GONE);
                    else {
                        for (int i = 0; i < numSlices; ++i) {
                            SliceValue sliceValue = new SliceValue(income.get(i), Color.parseColor("#006833"));
                            values.add(sliceValue);
                        }
                    }
                } else if (type.equals("Expense")) {
                    ArrayList<Float> expense = getAnnualExpenseByCategory(year);
                    if (containsOnlyZeros(expense))
                        chartPie.setVisibility(View.GONE);
                    else {
                        for (int i = 0; i < numSlices; ++i) {
                            SliceValue sliceValue = new SliceValue(expense.get(i), Color.parseColor("#006833"));
                            values.add(sliceValue);
                        }
                    }
                }
            } else {
                if (type.equals("Income")) {
                    ArrayList<Float> income = getIncomeByCategoryMonthYear(month, year);
                    if (containsOnlyZeros(income))
                        chartPie.setVisibility(View.GONE);
                    else {
                        if (income != null) {
                            for (int i = 0; i < numSlices; ++i) {
                                SliceValue sliceValue = new SliceValue(income.get(i), Color.parseColor("#006833"));
                                values.add(sliceValue);
                            }
                        }
                    }
                } else if (type.equals("Expense")) {
                    ArrayList<Float> expense = getExpenseByCategoryMonthYear(month, year);
                    if (containsOnlyZeros(expense))
                        chartPie.setVisibility(View.GONE);
                    else {
                        if (expense != null) {
                            for (int i = 0; i < numSlices; ++i) {
                                SliceValue sliceValue = new SliceValue(expense.get(i), Color.parseColor("#006833"));
                                values.add(sliceValue);
                            }
                        }
                    }
                }
            }

            dataPie = new PieChartData(values);
            dataPie.setHasLabels(true);
            dataPie.setHasLabelsOnlyForSelected(false);
            dataPie.setHasLabelsOutside(false);
            dataPie.setHasCenterCircle(true);
            dataPie.setCenterText1FontSize(20);

            chartPie.setPieChartData(dataPie);
        }
    }

    public void handleViewButton(View v) {
        if (String.valueOf(dropdownType.getSelectedItem()).equals("Budget")) {
            chartPie.setVisibility(View.GONE);
        }
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
            default://All
                return 0;
        }
    }

    /**
     * Gets the number of days in a month of a specific year
     *
     * @param month 1, 2, 3, etc.
     * @param year  4 digit year
     * @return number of days
     */
    private int getDaysInMonth(int month, int year) {
        if (month == 1)
            return 31;
        else if (month == 2) {
            if (year % 4 != 0)
                return 28;
            else if (year % 100 != 0)
                return 29;
            else if (year % 400 != 0)
                return 28;
            else
                return 29;
        } else if (month == 3)
            return 31;
        else if (month == 4)
            return 30;
        else if (month == 5)
            return 31;
        else if (month == 6)
            return 30;
        else if (month == 7)
            return 31;
        else if (month == 8)
            return 31;
        else if (month == 9)
            return 30;
        else if (month == 10)
            return 31;
        else if (month == 11)
            return 30;
        else
            return 31;
    }

    /**
     * Sets what happens when a column is touched
     */
    private class ColumnValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            //Toast.makeText(getApplicationContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // Auto-generated method stub
        }

    }

    /**
     * Sets what happens when a point is touched
     */
    private class LineValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            //Toast.makeText(getApplicationContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // Auto-generated method stub
        }

    }

    /**
     * Sets what happens when a slice is touched
     */
    private class PieValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            dataPie.setCenterText1(categories.get(arcIndex).name);
        }

        @Override
        public void onValueDeselected() {
            dataPie.setCenterText1("");
        }

    }

    /**
     * Loads all charts before displaying
     */
    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            charts.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            spinner.setVisibility(View.GONE);
            charts.setVisibility(View.VISIBLE);
            String type = String.valueOf(dropdownType.getSelectedItem());
            chartColumn.setVisibility(View.VISIBLE);
            chartLine.setVisibility(View.VISIBLE);
            if (!type.equals("Budget")) {
                chartPie.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String monthName = String.valueOf(dropdownMonth.getSelectedItem());
            int month = getMonth(monthName);
            int year = Integer.parseInt(String.valueOf(dropdownYear.getSelectedItem()));
            String type = String.valueOf(dropdownType.getSelectedItem());

            generateColumnData(month, year, type);
            generateLineData(month, year, type);
            if (!type.equals("Budget")) {
                generatePieData(month, year, type);
            }
            return null;
        }
    }
}
