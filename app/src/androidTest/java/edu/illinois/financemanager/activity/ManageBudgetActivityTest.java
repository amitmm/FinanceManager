package edu.illinois.financemanager.activity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Budget;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.BudgetRepo;
import edu.illinois.financemanager.repo.UserRepo;

public class ManageBudgetActivityTest extends ActivityInstrumentationTestCase2<ManageBudgetActivity> {

    private static final String testBudgetAmount = "500";
    private static final String testBudgetAmountNew = "5000";
    private static final String TAG = "ActivityTest";

    private static final String testUserName = "test";
    private static final String testUserEmail = "test@test.test";
    private static final Long testUserID = 987654321L;
    private static boolean flagSetUp = true;
    private static boolean flagTearDown = false;

    private ManageBudgetActivity testActivity;
    private Button testUpdateBudgetButton;
    private Button testCancelBudgetButton;

    public ManageBudgetActivityTest() {
        super(ManageBudgetActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Log.d(TAG, "setUp");

        if (flagSetUp) {
            flagSetUp = false;

            UserRepo uRepo = new UserRepo(getActivity());
            User user = uRepo.getUser();
            if (user != null) {
                uRepo.delete();
            }

            user = new User();
            user.name = testUserName;
            user.email = testUserEmail;
            user.id = testUserID;
            uRepo.insert(user);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
            Date currentDate = new Date();

            BudgetRepo bRepo = new BudgetRepo(getActivity());
            Budget budget = bRepo.getBudgetByDate(dateFormat.format(currentDate), user.id);
            if (budget == null) {
                budget = new Budget();
                budget.amount = 0;
                budget.userID = user.id;
                budget.date = dateFormat.format(currentDate);
                bRepo.insert(budget);
            } else {
                budget.amount = 0;
                budget.date = dateFormat.format(currentDate);
                bRepo.update(budget);
            }
        }

        testActivity = getActivity();
        testUpdateBudgetButton = (Button) testActivity.findViewById(R.id.update_budget_button);
        testCancelBudgetButton = (Button) testActivity.findViewById(R.id.cancel_budget_button);
    }

    @Override
    protected void tearDown() throws Exception {
        testActivity = getActivity();

        UserRepo uRepo = new UserRepo(testActivity);
        User user = uRepo.getUser();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
        Date currentDate = new Date();

        BudgetRepo bRepo = new BudgetRepo(testActivity);
        Budget budget = bRepo.getBudgetByDate(dateFormat.format(currentDate), user.id);
        if (budget == null) {
            budget = new Budget();
            budget.amount = 0;
            budget.userID = user.id;
            budget.date = dateFormat.format(currentDate);
            bRepo.insert(budget);
        } else {
            budget.amount = 0;
            budget.date = dateFormat.format(currentDate);
            bRepo.update(budget);
        }

        if (flagTearDown) {
            uRepo.delete();
        }

        Log.d(TAG, "tearDown");
        super.tearDown();
    }

    @SmallTest
    public void test0Preconditions() {
        Log.d(TAG, "test0Preconditions");

        assertNotNull("testActivity is null", testActivity);
        assertNotNull("testUpdateBudgetButton is null", testUpdateBudgetButton);
        assertNotNull("testCancelBudgetButton is null", testCancelBudgetButton);
    }

    @SmallTest
    public void test1UpdateBudget() {
        // test update new budget amount
        final EditText newBudgetAmount = (EditText) testActivity.findViewById(R.id.new_budget_amount);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                newBudgetAmount.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 10; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(testBudgetAmount);
        getInstrumentation().waitForIdleSync();
        assertEquals("Budget amount input failed", testBudgetAmount, newBudgetAmount.getText().toString());

        // check return to main activity
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testUpdateBudgetButton);
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", MainActivity.class, mainActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // check budget amount updated
        TextView totalText = (TextView) mainActivity.findViewById(R.id.total);
        assertEquals("Wrong budget amount", "$ " + testBudgetAmount + ".00", totalText.getText().toString());

        mainActivity.finish();
    }

    @SmallTest
    public void test2CancelBudget() {
        // test update new budget amount
        final EditText newBudgetAmount = (EditText) testActivity.findViewById(R.id.new_budget_amount);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                newBudgetAmount.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 10; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(testBudgetAmountNew);
        getInstrumentation().waitForIdleSync();
        assertEquals("Budget amount input failed", testBudgetAmountNew, newBudgetAmount.getText().toString());

        // check return to main activity
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testCancelBudgetButton);
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", MainActivity.class, mainActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // check budget amount updated
        TextView totalText = (TextView) mainActivity.findViewById(R.id.total);
        assertEquals("Wrong budget amount", "$ 0.00", totalText.getText().toString());

        mainActivity.finish();

        flagTearDown = true;
    }

}

