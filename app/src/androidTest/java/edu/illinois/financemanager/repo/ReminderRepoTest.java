package edu.illinois.financemanager.repo;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.LargeTest;

import java.util.ArrayList;

import edu.illinois.financemanager.object.Reminder;

public class ReminderRepoTest extends AndroidTestCase {

    private static final String testReminderMessage = "test message";
    private static final double testReminderAmount = 1000.0;
    private static final String testReminderDate = "2015-01-01";
    private static final long testReminderRepeat = 5;
    private static final long testReminderUID = 987654321L;

    private static final String updateReminderMessage = "update message";
    private static final double updateReminderAmount = 500.0;
    private static final String updateReminderDate = "2014-03-03";
    private static final long updateReminderRepeat = 3;
    private static final long updateReminderUID = 123456789L;

    private RenamingDelegatingContext testContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testContext = new RenamingDelegatingContext(getContext(), "test_context");

        ReminderRepo rRepo = new ReminderRepo(testContext);
        ArrayList<Reminder> reminderList = rRepo.getReminderList(testReminderUID);

        for (int i = 0; i < reminderList.size(); i++) {
            rRepo.delete(reminderList.get(i).id);
        }
    }

    @Override
    public void tearDown() throws Exception {
        ReminderRepo rRepo = new ReminderRepo(testContext);
        ArrayList<Reminder> reminderList = rRepo.getReminderList(testReminderUID);

        for (int i = 0; i < reminderList.size(); i++) {
            rRepo.delete(reminderList.get(i).id);
        }

        super.tearDown();
    }

    @LargeTest
    public void testInsertDelete() throws Exception {
        ReminderRepo rRepo = new ReminderRepo(testContext);
        Reminder newReminder = new Reminder();

        newReminder.message = testReminderMessage;
        newReminder.amount = testReminderAmount;
        newReminder.startDate = testReminderDate;
        newReminder.repeatID = testReminderRepeat;
        newReminder.userID = testReminderUID;

        Long reminderID = rRepo.insert(newReminder);
        Reminder checkReminder = rRepo.getReminderByID(reminderID);

        assertEquals(testReminderMessage, checkReminder.message);
        assertEquals(String.valueOf(testReminderAmount), String.valueOf(checkReminder.amount));
        assertEquals(testReminderDate, checkReminder.startDate);
        assertEquals(String.valueOf(testReminderRepeat), String.valueOf(checkReminder.repeatID));
        assertEquals(String.valueOf(testReminderUID), String.valueOf(checkReminder.userID));

        rRepo.delete(reminderID);
        checkReminder = rRepo.getReminderByID(reminderID);
        assertEquals(null, checkReminder);
    }

    @LargeTest
    public void testUpdate() throws Exception {
        ReminderRepo rRepo = new ReminderRepo(testContext);
        Reminder newReminder = new Reminder();

        newReminder.message = testReminderMessage;
        newReminder.amount = testReminderAmount;
        newReminder.startDate = testReminderDate;
        newReminder.repeatID = testReminderRepeat;
        newReminder.userID = testReminderUID;

        Long reminderID = rRepo.insert(newReminder);
        Reminder checkReminder = rRepo.getReminderByID(reminderID);

        assertEquals(testReminderMessage, checkReminder.message);
        assertEquals(String.valueOf(testReminderAmount), String.valueOf(checkReminder.amount));
        assertEquals(testReminderDate, checkReminder.startDate);
        assertEquals(String.valueOf(testReminderRepeat), String.valueOf(checkReminder.repeatID));
        assertEquals(String.valueOf(testReminderUID), String.valueOf(checkReminder.userID));

        checkReminder.message = updateReminderMessage;
        checkReminder.amount = updateReminderAmount;
        checkReminder.startDate = updateReminderDate;
        checkReminder.repeatID = updateReminderRepeat;
        checkReminder.userID = updateReminderUID;

        rRepo.update(checkReminder);
        checkReminder = rRepo.getReminderByID(reminderID);

        assertEquals(updateReminderMessage, checkReminder.message);
        assertEquals(String.valueOf(updateReminderAmount), String.valueOf(checkReminder.amount));
        assertEquals(updateReminderDate, checkReminder.startDate);
        assertEquals(String.valueOf(updateReminderRepeat), String.valueOf(checkReminder.repeatID));
        assertEquals(String.valueOf(updateReminderUID), String.valueOf(checkReminder.userID));

        rRepo.delete(reminderID);
        checkReminder = rRepo.getReminderByID(reminderID);
        assertEquals(null, checkReminder);
    }

    @LargeTest
    public void testGetReminderByID() throws Exception {
        ReminderRepo rRepo = new ReminderRepo(testContext);
        Reminder newReminder = new Reminder();

        newReminder.message = testReminderMessage;
        newReminder.amount = testReminderAmount;
        newReminder.startDate = testReminderDate;
        newReminder.repeatID = testReminderRepeat;
        newReminder.userID = testReminderUID;

        Long reminderID = rRepo.insert(newReminder);
        Reminder checkReminder = rRepo.getReminderByID(reminderID);

        assertEquals(testReminderMessage, checkReminder.message);
        assertEquals(String.valueOf(testReminderAmount), String.valueOf(checkReminder.amount));
        assertEquals(testReminderDate, checkReminder.startDate);
        assertEquals(String.valueOf(testReminderRepeat), String.valueOf(checkReminder.repeatID));
        assertEquals(String.valueOf(testReminderUID), String.valueOf(checkReminder.userID));

        rRepo.delete(reminderID);
        checkReminder = rRepo.getReminderByID(reminderID);
        assertEquals(null, checkReminder);
    }
}
