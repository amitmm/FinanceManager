package edu.illinois.financemanager.activity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.UserRepo;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String testUserName = "test";
    private static final String testUserEmail = "test@test.test";
    private static final Long testUserID = 987654321L;

    private MainActivity mainActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        UserRepo uRepo = new UserRepo(getActivity());
        User user = uRepo.getUser();
        if (user == null) {
            user = new User();
            user.name = testUserName;
            user.email = testUserEmail;
            user.id = testUserID;
            uRepo.insert(user);
        }

        mainActivity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        UserRepo uRepo = new UserRepo(getActivity());
        uRepo.delete();
        super.tearDown();
    }

    /**
     * Tests if clicking add transaction button opens up AddTransactionActivity
     */
    @MediumTest
    public void testAddTransactionButton() {
        final Button addTrans = (Button) mainActivity.findViewById(R.id.add_transaction);
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(AddTransactionActivity.class.getName(), null, false);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addTrans.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
        AddTransactionActivity addTransactionActivity = (AddTransactionActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", addTransactionActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", AddTransactionActivity.class, addTransactionActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        addTransactionActivity.finish();
    }
}
