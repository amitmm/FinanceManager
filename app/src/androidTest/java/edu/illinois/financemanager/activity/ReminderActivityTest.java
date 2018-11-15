package edu.illinois.financemanager.activity;


import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Reminder;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.ReminderRepo;
import edu.illinois.financemanager.repo.UserRepo;

public class ReminderActivityTest extends ActivityInstrumentationTestCase2<ReminderActivity> {

    private static final String TAG = "ActivityTest";

    private static final String testReminderMessage = "test message";
    private static final String testReminderAmount = "500.00";
    private static final String testReminderDate = "2100-05-05";
    private static final String testReminderTime = "12:00 PM";

    private static final String testReminderMessageNew = "test message new";
    private static final String testReminderAmountNew = "5000.00";
    private static final String testReminderDateNew = "2101-05-05";
    private static final String testReminderTimeNew = "01:00 PM";

    private static final String testUserName = "test";
    private static final String testUserEmail = "test@test.test";
    private static final Long testUserID = 987654321L;
    private static boolean flagSetUp = true;
    private static boolean flagTearDown = false;

    private ReminderActivity testActivity;
    private Button testAddReminderButton;
    private ListView testReminderListView;

    public ReminderActivityTest() {
        super(ReminderActivity.class);
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

            ReminderRepo rRepo = new ReminderRepo(getActivity());
            ArrayList<Reminder> reminderList = rRepo.getReminderList(testUserID);
            for (int i = 0; i < reminderList.size(); i++) {
                rRepo.delete(reminderList.get(i).id);
            }
        }

        testActivity = getActivity();
        testAddReminderButton = (Button) testActivity.findViewById(R.id.add_reminder);
        testReminderListView = (ListView) testActivity.findViewById(R.id.reminder_list_view);
    }

    @Override
    protected void tearDown() throws Exception {
        Log.d(TAG, "tearDown");

        if (flagTearDown) {
            testActivity = getActivity();
            UserRepo uRepo = new UserRepo(testActivity);
            uRepo.delete();
        }
        super.tearDown();
    }

    @SmallTest
    public void test0Preconditions() {
        Log.d(TAG, "test0Preconditions");

        assertNotNull("testActivity is null", testActivity);
        assertNotNull("testAddReminderButton is null", testAddReminderButton);
        assertNotNull("testReminderListView is null", testReminderListView);
    }

    @LargeTest
    public void test1AddReminder() {
        Log.d(TAG, "test1AddCategory");

        // test add reminder button
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(AddReminderActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testAddReminderButton);
        AddReminderActivity addReminderActivity = (AddReminderActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", addReminderActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", AddReminderActivity.class, addReminderActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // test add new reminder
        final EditText newReminderMessage = (EditText) addReminderActivity.findViewById(R.id.reminder_message);
        final EditText newReminderAmount = (EditText) addReminderActivity.findViewById(R.id.reminder_amount);
        final EditText newReminderDate = (EditText) addReminderActivity.findViewById(R.id.reminder_date);
        final EditText newReminderTime = (EditText) addReminderActivity.findViewById(R.id.reminder_time);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                newReminderMessage.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(testReminderMessage);
        getInstrumentation().waitForIdleSync();
        assertEquals("Reminder message input failed", testReminderMessage, newReminderMessage.getText().toString());

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                newReminderAmount.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(testReminderAmount);
        getInstrumentation().waitForIdleSync();
        assertEquals("Reminder amount input failed", testReminderAmount, newReminderAmount.getText().toString());

        addReminderActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newReminderDate.setText(testReminderDate);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertEquals("Reminder date input failed", testReminderDate, newReminderDate.getText().toString());

        addReminderActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newReminderTime.setText(testReminderTime);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertEquals("Reminder time input failed", testReminderTime, newReminderTime.getText().toString());

        final Button addButton = (Button) addReminderActivity.findViewById(R.id.add_reminder_button);
        receiverActivityMonitor = getInstrumentation().addMonitor(ReminderActivity.class.getName(), null, false);
        addReminderActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
        ReminderActivity reminderActivity = (ReminderActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", reminderActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ReminderActivity.class, reminderActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        ListView listView = (ListView) reminderActivity.findViewById(R.id.reminder_list_view);

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) listView.getAdapter().getItem(0);
        assertEquals("Add reminder failed", testReminderMessage, map.get("line1"));
        assertEquals("Add reminder failed", testReminderAmount, map.get("line2"));

        addReminderActivity.finish();
        reminderActivity.finish();
    }

    @LargeTest
    public void test2UpdateReminder() {
        Log.d(TAG, "test1AddCategory");

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) testReminderListView.getAdapter().getItem(0);
        assertEquals("Reminder not found", testReminderMessage, map.get("line1"));
        assertEquals("Reminder not found", testReminderAmount, map.get("line2"));

        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(ManageReminderActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testReminderListView.getChildAt(0));
        ManageReminderActivity manageReminderActivity = (ManageReminderActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", manageReminderActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ManageReminderActivity.class, manageReminderActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        final EditText newReminderMessage = (EditText) manageReminderActivity.findViewById(R.id.reminder_message);
        final EditText newReminderAmount = (EditText) manageReminderActivity.findViewById(R.id.reminder_amount);
        final EditText newReminderDate = (EditText) manageReminderActivity.findViewById(R.id.reminder_date);
        final EditText newReminderTime = (EditText) manageReminderActivity.findViewById(R.id.reminder_time);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                newReminderMessage.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 20; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(testReminderMessageNew);
        getInstrumentation().waitForIdleSync();
        assertEquals("Reminder message input failed", testReminderMessageNew, newReminderMessage.getText().toString());

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                newReminderAmount.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 20; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(testReminderAmountNew);
        getInstrumentation().waitForIdleSync();
        assertEquals("Reminder amount input failed", testReminderAmountNew, newReminderAmount.getText().toString());

        manageReminderActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newReminderDate.setText(testReminderDateNew);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertEquals("Reminder date input failed", testReminderDateNew, newReminderDate.getText().toString());

        manageReminderActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newReminderTime.setText(testReminderTimeNew);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertEquals("Reminder time input failed", testReminderTimeNew, newReminderTime.getText().toString());

        final Button updateButton = (Button) manageReminderActivity.findViewById(R.id.update_reminder_button);
        receiverActivityMonitor = getInstrumentation().addMonitor(ReminderActivity.class.getName(), null, false);
        manageReminderActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
        ReminderActivity reminderActivity = (ReminderActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", reminderActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ReminderActivity.class, reminderActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        ListView listView = (ListView) reminderActivity.findViewById(R.id.reminder_list_view);

        @SuppressWarnings("unchecked")
        HashMap<String, String> mapNew = (HashMap<String, String>) listView.getAdapter().getItem(0);
        assertEquals("Add reminder failed", testReminderMessageNew, mapNew.get("line1"));
        assertEquals("Add reminder failed", testReminderAmountNew, mapNew.get("line2"));

        manageReminderActivity.finish();
        reminderActivity.finish();
    }

    @LargeTest
    public void test3CancelReminder() {
        Log.d(TAG, "test1AddCategory");

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) testReminderListView.getAdapter().getItem(0);
        assertEquals("Reminder not found", testReminderMessageNew, map.get("line1"));
        assertEquals("Reminder not found", testReminderAmountNew, map.get("line2"));

        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(ManageReminderActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testReminderListView.getChildAt(0));
        ManageReminderActivity manageReminderActivity = (ManageReminderActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", manageReminderActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ManageReminderActivity.class, manageReminderActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        final Button cancelButton = (Button) manageReminderActivity.findViewById(R.id.cancel_reminder_button);
        receiverActivityMonitor = getInstrumentation().addMonitor(ReminderActivity.class.getName(), null, false);
        TouchUtils.clickView(this, cancelButton);
        ReminderActivity reminderActivity = (ReminderActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", reminderActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ReminderActivity.class, reminderActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        ListView listView = (ListView) reminderActivity.findViewById(R.id.reminder_list_view);

        @SuppressWarnings("unchecked")
        HashMap<String, String> mapNew = (HashMap<String, String>) listView.getAdapter().getItem(0);
        assertEquals("Cancel reminder failed", testReminderMessageNew, mapNew.get("line1"));
        assertEquals("Cancel reminder failed", testReminderAmountNew, mapNew.get("line2"));

        manageReminderActivity.finish();
        reminderActivity.finish();
    }

    @LargeTest
    public void test4DeleteReminder() {
        Log.d(TAG, "test1AddCategory");

        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) testReminderListView.getAdapter().getItem(0);
        assertEquals("Reminder not found", testReminderMessageNew, map.get("line1"));
        assertEquals("Reminder not found", testReminderAmountNew, map.get("line2"));

        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(ManageReminderActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testReminderListView.getChildAt(0));
        ManageReminderActivity manageReminderActivity = (ManageReminderActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", manageReminderActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ManageReminderActivity.class, manageReminderActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        final Button deleteButton = (Button) manageReminderActivity.findViewById(R.id.delete_reminder_button);
        receiverActivityMonitor = getInstrumentation().addMonitor(ReminderActivity.class.getName(), null, false);
        TouchUtils.clickView(this, deleteButton);
        ReminderActivity reminderActivity = (ReminderActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", reminderActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ReminderActivity.class, reminderActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        ListView listView = (ListView) reminderActivity.findViewById(R.id.reminder_list_view);
        assertNull("Delete reminder failed", listView.getChildAt(0));

        manageReminderActivity.finish();
        reminderActivity.finish();

        flagTearDown = true;
    }

}
