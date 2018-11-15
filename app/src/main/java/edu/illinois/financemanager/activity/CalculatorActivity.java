package edu.illinois.financemanager.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;

import edu.illinois.financemanager.R;

/**
 * Activity for calculating tips and splitting bills
 */
public class CalculatorActivity extends MenuActivity {

    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected String mActivityTitle;

    private EditText bill;
    private EditText tip;
    private EditText people;
    private TextView total;
    private TextView totalPerPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        //Navigation code
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        mDrawerList = (ListView) findViewById(R.id.navList);
        createDrawer(CalculatorActivity.this, mDrawerLayout, mActivityTitle, mDrawerList);
        bill = (EditText) findViewById(R.id.bill);
        tip = (EditText) findViewById(R.id.tip);
        people = (EditText) findViewById(R.id.people);
        totalPerPerson = (TextView) findViewById(R.id.tipTotal);
        total = (TextView) findViewById(R.id.billTotal);
    }

    /**
     * Calculates and sets the total bill with tip included and amount each person pays
     *
     * @param v: just a view
     */
    public void handleCalculateButton(View v) {
        validateFields();
        float totalBill = calculateTotal();
        float individualShare = calculateIndividualShare(totalBill);
        DecimalFormat twoPlaces = new DecimalFormat("0.00");
        totalPerPerson.setText("$" + twoPlaces.format(individualShare));
        total.setText("$" + twoPlaces.format(totalBill));
    }

    /**
     * Calculates the total with tip included
     *
     * @return total including tip
     */
    private float calculateTotal() {
        int tipPercent = Integer.parseInt(tip.getText().toString());
        float subTotal = Float.parseFloat(bill.getText().toString());
        return (subTotal) * ((tipPercent / 100.0f) + 1);
    }

    /**
     * Calculates the amount each person needs to pay to cover the bill with tip
     *
     * @param total bill including tip
     * @return amount each person pays
     */
    private float calculateIndividualShare(float total) {
        int persons = Integer.parseInt(people.getText().toString());
        return total / persons;
    }

    /**
     * Decreases the tip percentage by one
     *
     * @param v: just a view
     */
    public void handleTipMinusButton(View v) {
        try {
            int percent = Integer.parseInt(tip.getText().toString());
            percent--;
            if (percent < 0) {
                percent = 0;
            }
            tip.setText("" + percent);
        } catch (NumberFormatException e) {
            tip.setText("15");
        }
    }

    /**
     * Increases the tip percentage by one
     *
     * @param v: just a view
     */
    public void handleTipPlusButton(View v) {
        try {
            int percent = Integer.parseInt(tip.getText().toString());
            percent++;
            tip.setText("" + percent);
        } catch (NumberFormatException e) {
            tip.setText("15");
        }
    }

    /**
     * Decreases the amount of people by one
     *
     * @param v: just a view
     */
    public void handlePeopleMinusButton(View v) {
        try {
            int personCount = Integer.parseInt(people.getText().toString());
            personCount--;
            if (personCount < 1) {
                personCount = 1;
            }
            people.setText("" + personCount);
        } catch (NumberFormatException e) {
            people.setText("1");
        }
    }

    /**
     * Increases the amount of people by one
     *
     * @param v: just a view
     */
    public void handlePeoplePlusButton(View v) {
        try {
            int personCount = Integer.parseInt(people.getText().toString());
            personCount++;
            people.setText("" + personCount);
        } catch (NumberFormatException e) {
            people.setText("1");
        }
    }

    /**
     * Grabs bill, tip, people and ensures they are valid
     * If not valid, then fill with default values
     */
    private void validateFields() {
        tryParseBill();
        tryParseTip();
        tryParsePeople();
    }

    /**
     * Checks that bill is a float
     */
    private void tryParseBill() {
        try {
            float temp = Float.parseFloat(bill.getText().toString());
            Log.d("VALIDATE", String.valueOf(temp));
        } catch (NumberFormatException e) {
            // do nothing
        }
    }

    /**
     * Checks that tip is an int, otherwise set to default of 15
     */
    private void tryParseTip() {
        try {
            int temp = Integer.parseInt(tip.getText().toString());
            Log.d("VALIDATE", String.valueOf(temp));
        } catch (NumberFormatException e) {
            tip.setText("15");
        }
    }

    /**
     * Checks that people is an int, otherwise set to default of 1
     */
    private void tryParsePeople() {
        try {
            int temp = Integer.parseInt(people.getText().toString());
            Log.d("VALIDATE", String.valueOf(temp));
        } catch (NumberFormatException e) {
            people.setText("1");
        }
    }
}
