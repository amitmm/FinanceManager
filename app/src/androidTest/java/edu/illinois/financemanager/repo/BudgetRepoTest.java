package edu.illinois.financemanager.repo;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.LargeTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.illinois.financemanager.object.Budget;


public class BudgetRepoTest extends AndroidTestCase {

    private static final long testBudgetUID = 987654321L;
    private static final double testBudgetAmount = 500.0;
    private static final double updateBudgetAmount = 1000.0;
    private static final Date testDate = new Date();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

    private RenamingDelegatingContext testContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testContext = new RenamingDelegatingContext(getContext(), "test_context");

        BudgetRepo bRepo = new BudgetRepo(testContext);
        Budget budget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        while (budget != null) {
            bRepo.delete(dateFormat.format(testDate), testBudgetUID);
            budget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        }
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        BudgetRepo bRepo = new BudgetRepo(testContext);
        Budget budget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        while (budget != null) {
            bRepo.delete(dateFormat.format(testDate), testBudgetUID);
            budget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        }
    }

    @LargeTest
    public void testInsertDelete() throws Exception {
        BudgetRepo bRepo = new BudgetRepo(testContext);
        Budget newBudget = new Budget();
        newBudget.userID = testBudgetUID;
        newBudget.amount = testBudgetAmount;
        newBudget.date = dateFormat.format(testDate);
        bRepo.insert(newBudget);

        Budget checkBudget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        assertEquals(dateFormat.format(testDate), checkBudget.date);
        assertEquals(String.valueOf(testBudgetUID), String.valueOf(checkBudget.userID));
        assertEquals(String.valueOf(testBudgetAmount), String.valueOf(checkBudget.amount));

        bRepo.delete(dateFormat.format(testDate), testBudgetUID);
        checkBudget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        assertEquals(null, checkBudget);
    }

    @LargeTest
    public void testUpdate() throws Exception {
        BudgetRepo bRepo = new BudgetRepo(testContext);
        Budget newBudget = new Budget();
        newBudget.userID = testBudgetUID;
        newBudget.amount = testBudgetAmount;
        newBudget.date = dateFormat.format(testDate);
        bRepo.insert(newBudget);

        Budget checkBudget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        assertEquals(dateFormat.format(testDate), checkBudget.date);
        assertEquals(String.valueOf(testBudgetUID), String.valueOf(checkBudget.userID));
        assertEquals(String.valueOf(testBudgetAmount), String.valueOf(checkBudget.amount));

        checkBudget.userID = testBudgetUID;
        checkBudget.amount = updateBudgetAmount;
        bRepo.update(checkBudget);

        checkBudget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        assertEquals(dateFormat.format(testDate), checkBudget.date);
        assertEquals(String.valueOf(testBudgetUID), String.valueOf(checkBudget.userID));
        assertEquals(String.valueOf(updateBudgetAmount), String.valueOf(checkBudget.amount));

        bRepo.delete(dateFormat.format(testDate), testBudgetUID);
        checkBudget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        assertEquals(null, checkBudget);
    }

    @LargeTest
    public void testGetBudgetByDate() throws Exception {
        BudgetRepo bRepo = new BudgetRepo(testContext);
        Budget newBudget = new Budget();
        newBudget.userID = testBudgetUID;
        newBudget.amount = testBudgetAmount;
        newBudget.date = dateFormat.format(testDate);
        bRepo.insert(newBudget);

        Budget checkBudget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        assertEquals(dateFormat.format(testDate), checkBudget.date);
        assertEquals(String.valueOf(testBudgetUID), String.valueOf(checkBudget.userID));
        assertEquals(String.valueOf(testBudgetAmount), String.valueOf(checkBudget.amount));

        bRepo.delete(dateFormat.format(testDate), testBudgetUID);
        checkBudget = bRepo.getBudgetByDate(dateFormat.format(testDate), testBudgetUID);
        assertEquals(null, checkBudget);
    }
}
