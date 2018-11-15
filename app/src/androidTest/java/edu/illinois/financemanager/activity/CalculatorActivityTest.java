package edu.illinois.financemanager.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.illinois.financemanager.R;

public class CalculatorActivityTest extends ActivityInstrumentationTestCase2<CalculatorActivity> {

    private static final String TAG = "ActivityTest";
    private static final String BILL_AMOUNT = "50";
    private static final String TIP_AMOUNT = "20";
    private static final String PEOPLE_AMOUNT = "3";

    private static final String BILL_AMOUNT_DEFAULT = "0.00";
    private static final String TIP_AMOUNT_DEFAULT = "15";
    private static final String PEOPLE_AMOUNT_DEFAULT = "1";

    private CalculatorActivity testActivity;
    private EditText billInput, tipInput, peopleInput;
    private Button tipMinus, tipPlus, peopleMinus, peoplePlus, calculateButton;
    private TextView tipTotal, billTotal;

    public CalculatorActivityTest() {
        super(CalculatorActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Log.d(TAG, "setUp");

        testActivity = getActivity();

        billInput = (EditText) testActivity.findViewById(R.id.bill);
        tipInput = (EditText) testActivity.findViewById(R.id.tip);
        peopleInput = (EditText) testActivity.findViewById(R.id.people);
        tipMinus = (Button) testActivity.findViewById(R.id.tipMinus);
        tipPlus = (Button) testActivity.findViewById(R.id.tipPlus);
        peopleMinus = (Button) testActivity.findViewById(R.id.peopleMinus);
        peoplePlus = (Button) testActivity.findViewById(R.id.peoplePlus);
        calculateButton = (Button) testActivity.findViewById(R.id.calculate);
        tipTotal = (TextView) testActivity.findViewById(R.id.tipTotal);
        billTotal = (TextView) testActivity.findViewById(R.id.billTotal);
    }

    @Override
    protected void tearDown() throws Exception {
        testActivity.finish();
        Log.d(TAG, "tearDown");
        super.tearDown();
    }

    @SmallTest
    public void test0Preconditions() {
        Log.d(TAG, "test0Preconditions");

        assertNotNull("testActivity is null", testActivity);
        assertNotNull("billInput is null", billInput);
        assertNotNull("tipInput is null", tipInput);
        assertNotNull("peopleInput is null", peopleInput);
        assertNotNull("tipMinus is null", tipMinus);
        assertNotNull("tipPlus is null", tipPlus);
        assertNotNull("peopleMinus is null", peopleMinus);
        assertNotNull("peoplePlus is null", peoplePlus);
        assertNotNull("calculateButton is null", calculateButton);
        assertNotNull("tipTotal is null", tipTotal);
        assertNotNull("billTotal is null", billTotal);
    }

    @LargeTest
    public void test1TextInputs() {
        Log.d(TAG, "test1TextInputs");

        assertEquals("Bill check failed", BILL_AMOUNT_DEFAULT, billInput.getText().toString());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                billInput.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 10; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(BILL_AMOUNT);
        getInstrumentation().waitForIdleSync();
        assertEquals("Bill input failed", BILL_AMOUNT, billInput.getText().toString());

        assertEquals("Tip check failed", TIP_AMOUNT_DEFAULT, tipInput.getText().toString());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                tipInput.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 10; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(TIP_AMOUNT);
        getInstrumentation().waitForIdleSync();
        assertEquals("Tip input failed", TIP_AMOUNT, tipInput.getText().toString());

        assertEquals("People check failed", PEOPLE_AMOUNT_DEFAULT, peopleInput.getText().toString());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                peopleInput.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 10; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(PEOPLE_AMOUNT);
        getInstrumentation().waitForIdleSync();
        assertEquals("People input failed", PEOPLE_AMOUNT, peopleInput.getText().toString());
    }

    @LargeTest
    public void test2PlusMinusButtons() {
        Log.d(TAG, "test2PlusMinusButtons");

        assertEquals("Tip check failed", TIP_AMOUNT_DEFAULT, tipInput.getText().toString());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                tipMinus.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, tipMinus);
        getInstrumentation().waitForIdleSync();
        assertEquals("tipMinus button failed", String.valueOf(Integer.parseInt(TIP_AMOUNT_DEFAULT) - 1), tipInput.getText().toString());

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                tipPlus.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, tipPlus);
        getInstrumentation().waitForIdleSync();
        assertEquals("tipMinus button failed", TIP_AMOUNT_DEFAULT, tipInput.getText().toString());

        assertEquals("People check failed", PEOPLE_AMOUNT_DEFAULT, peopleInput.getText().toString());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                peoplePlus.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, peoplePlus);
        getInstrumentation().waitForIdleSync();
        assertEquals("tipMinus button failed", String.valueOf(Integer.parseInt(PEOPLE_AMOUNT_DEFAULT) + 1), peopleInput.getText().toString());

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                peopleMinus.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, peopleMinus);
        getInstrumentation().waitForIdleSync();
        assertEquals("tipMinus button failed", PEOPLE_AMOUNT_DEFAULT, peopleInput.getText().toString());
    }

    @LargeTest
    public void test3CalculateOnePerson() {
        assertEquals("tipTotal check failed", "$0.00", tipTotal.getText().toString());
        assertEquals("billTotal check failed", "$0.00", billTotal.getText().toString());

        assertEquals("Bill check failed", BILL_AMOUNT_DEFAULT, billInput.getText().toString());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                billInput.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 10; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(BILL_AMOUNT);
        getInstrumentation().waitForIdleSync();
        assertEquals("Bill input failed", BILL_AMOUNT, billInput.getText().toString());

        assertEquals("tipMinus button failed", TIP_AMOUNT_DEFAULT, tipInput.getText().toString());
        assertEquals("tipMinus button failed", PEOPLE_AMOUNT_DEFAULT, peopleInput.getText().toString());

        testActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                calculateButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();

        float bill = Float.parseFloat(BILL_AMOUNT);
        int tipPercent = Integer.parseInt(tipInput.getText().toString());
        int people = Integer.parseInt(peopleInput.getText().toString());

        float tipTotalCalculated = (bill / people) * ((tipPercent / 100.0F) + 1);
        float billTotalCalculated = bill * ((tipPercent / 100.0F) + 1);
        assertEquals("tipTotal calculate failed", String.format("$%.2f", tipTotalCalculated), tipTotal.getText().toString());
        assertEquals("billTotal calculate failed", String.format("$%.2f", billTotalCalculated), billTotal.getText().toString());
    }

    @LargeTest
    public void test4CalculateThreePeople() {
        assertEquals("tipTotal check failed", "$0.00", tipTotal.getText().toString());
        assertEquals("billTotal check failed", "$0.00", billTotal.getText().toString());

        assertEquals("Bill check failed", BILL_AMOUNT_DEFAULT, billInput.getText().toString());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                billInput.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 10; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(BILL_AMOUNT);
        getInstrumentation().waitForIdleSync();
        assertEquals("Bill input failed", BILL_AMOUNT, billInput.getText().toString());

        assertEquals("tipMinus button failed", TIP_AMOUNT_DEFAULT, tipInput.getText().toString());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                tipPlus.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, tipPlus);
        TouchUtils.clickView(this, tipPlus);
        getInstrumentation().waitForIdleSync();
        assertEquals("tipMinus button failed", String.valueOf(Integer.parseInt(TIP_AMOUNT_DEFAULT) + 2), tipInput.getText().toString());

        assertEquals("tipMinus button failed", PEOPLE_AMOUNT_DEFAULT, peopleInput.getText().toString());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                peoplePlus.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, peoplePlus);
        TouchUtils.clickView(this, peoplePlus);
        getInstrumentation().waitForIdleSync();
        assertEquals("tipMinus button failed", String.valueOf(Integer.parseInt(PEOPLE_AMOUNT_DEFAULT) + 2), peopleInput.getText().toString());

        testActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                calculateButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();

        float bill = Float.parseFloat(BILL_AMOUNT);
        int tipPercent = Integer.parseInt(tipInput.getText().toString());
        int people = Integer.parseInt(peopleInput.getText().toString());

        float tipTotalCalculated = (bill / people) * ((tipPercent / 100.0F) + 1);
        float billTotalCalculated = bill * ((tipPercent / 100.0F) + 1);
        assertEquals("tipTotal calculate failed", String.format("$%.2f", tipTotalCalculated), tipTotal.getText().toString());
        assertEquals("billTotal calculate failed", String.format("$%.2f", billTotalCalculated), billTotal.getText().toString());
    }


}
