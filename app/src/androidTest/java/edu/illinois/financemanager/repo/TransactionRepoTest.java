package edu.illinois.financemanager.repo;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import edu.illinois.financemanager.object.Transaction;


public class TransactionRepoTest extends AndroidTestCase {

    private static final Long testCategoryUID = 987654321L;
    private RenamingDelegatingContext testContext;
    private Transaction t1; //Jan 1, 2015, school Expense
    private Transaction t2; //Jan 2, 2015, school Expense
    private Transaction t3; //Jan 2, 2014, school Expense
    private Transaction t4; //Jan 2, 2015, food Expense
    private Transaction t5; //Dec 22, 2014, school Expense
    private Transaction t6; //Jan 1, 2014, school Expense

    private Transaction t7; //Jan 2, 2015, work Income
    private Transaction t8; //Feb 1, 2015, work Income
    private Transaction t9; //Feb 2, 2015, work Income
    private Transaction t10; //Mar 1, 2015, work Income

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testContext = new RenamingDelegatingContext(getContext(), "test_context");
        TransactionRepo tRepo = new TransactionRepo(testContext);
        createTransactions();
    }

    private void createTransactions() {
        Calendar date1 = Calendar.getInstance();
        date1.set(2015, 0, 1, 0, 1);
        Calendar date2 = Calendar.getInstance();
        date2.set(2015, 0, 2);
        Calendar date3 = Calendar.getInstance();
        date3.set(2014, 0, 2);
        Calendar date4 = Calendar.getInstance();
        date4.set(2014, 11, 1);
        Calendar date5 = Calendar.getInstance();
        date5.clear();
        date5.set(2014, 0, 1);
        Calendar date6 = Calendar.getInstance();
        date6.set(2015, 1, 1);
        Calendar date7 = Calendar.getInstance();
        date7.set(2015, 1, 2);
        Calendar date8 = Calendar.getInstance();
        date8.set(2015, 2, 1);

        t1 = new Transaction();
        t1.type = "Expense";
        t1.amount = 12.00f;
        t1.message = "Lunch";
        t1.userID = testCategoryUID;
        t1.category = "school";
        t1.date = date1.getTime();

        t2 = new Transaction();
        t2.type = "Expense";
        t2.amount = 25.00f;
        t2.message = "Book";
        t2.userID = testCategoryUID;
        t2.category = "school";
        t2.date = date2.getTime();

        t3 = new Transaction();
        t3.type = "Expense";
        t3.amount = 13.00f;
        t3.message = "Pencils";
        t3.userID = testCategoryUID;
        t3.category = "school";
        t3.date = date3.getTime();

        t4 = new Transaction();
        t4.type = "Expense";
        t4.amount = 23.00f;
        t4.message = "Dinner";
        t4.userID = testCategoryUID;
        t4.category = "food";
        t4.date = date2.getTime();

        t5 = new Transaction();
        t5.type = "Expense";
        t5.amount = 22.0f;
        t5.message = "Party";
        t5.userID = testCategoryUID;
        t5.category = "school";
        t5.date = date4.getTime();

        t6 = new Transaction();
        t6.type = "Expense";
        t6.amount = 2.0f;
        t6.message = "New Year";
        t6.userID = testCategoryUID;
        t6.category = "school";
        t6.date = date5.getTime();

        t7 = new Transaction();
        t7.type = "Income";
        t7.amount = 7.0f;
        t7.message = "T7";
        t7.userID = testCategoryUID;
        t7.category = "work";
        t7.date = date2.getTime();

        t8 = new Transaction();
        t8.type = "Income";
        t8.amount = 8.0f;
        t8.message = "T8";
        t8.userID = testCategoryUID;
        t8.category = "work";
        t8.date = date6.getTime();

        t9 = new Transaction();
        t9.type = "Income";
        t9.amount = 9.0f;
        t9.message = "T9";
        t9.userID = testCategoryUID;
        t9.category = "work";
        t9.date = date7.getTime();

        t10 = new Transaction();
        t10.type = "Income";
        t10.amount = 10.0f;
        t10.message = "T10";
        t10.userID = testCategoryUID;
        t10.category = "work";
        t10.date = date8.getTime();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @MediumTest
    public void testInsertDelete() throws Exception {
        TransactionRepo tRepo = new TransactionRepo(testContext);
        Date testDate = new Date(1425769367789L);
        Transaction newTransaction = new Transaction("income", 50, "made 50 dollars", testCategoryUID, "test Category", testDate, null);
        tRepo.insert(newTransaction);
        Transaction testTransaction = tRepo.getTransactionByAmount("40", "60", testCategoryUID).get(0);
        assertEquals(testTransaction.amount, 50.0f);
        assertEquals(String.valueOf(testTransaction.userID), String.valueOf(testCategoryUID));

        tRepo.delete(testTransaction.id);
    }

    @LargeTest
    public void testGetAllTransaction() throws Exception {
        TransactionRepo tRepo = new TransactionRepo(testContext);
        Date testDate = new Date(1425769367789L);
        Transaction newTransaction = new Transaction("income", 50, "made 50 dollars", testCategoryUID, "test Category1", testDate, null);
        tRepo.insert(newTransaction);

        Transaction newTransaction2 = new Transaction("income", 60, "made 60 dollars", testCategoryUID, "test Category2", testDate, null);
        tRepo.insert(newTransaction2);

        ArrayList<Transaction> transactionList = tRepo.getAllTransaction();
        assertEquals(2, transactionList.size());

        Transaction testTransaction = transactionList.get(0);
        assertEquals(testTransaction.userID, newTransaction.userID);
        assertEquals(testTransaction.message, newTransaction.message);
        assertEquals(testTransaction.category, newTransaction.category);
        assertEquals(testTransaction.type, newTransaction.type);

        testTransaction = transactionList.get(1);
        assertEquals(testTransaction.userID, newTransaction2.userID);
        assertEquals(testTransaction.message, newTransaction2.message);
        assertEquals(testTransaction.category, newTransaction2.category);
        assertEquals(testTransaction.type, newTransaction2.type);
    }

    @LargeTest
    public void testAllTransactionByType() throws Exception {
        TransactionRepo tRepo = new TransactionRepo(testContext);
        Date testDate = new Date(1425769367789L);
        Transaction newTransaction = new Transaction("income", 50, "made 50 dollars", testCategoryUID, "test Category1", testDate, null);
        tRepo.insert(newTransaction);

        Transaction newTransaction2 = new Transaction("income", 60, "made 60 dollars", testCategoryUID, "test Category2", testDate, null);
        tRepo.insert(newTransaction2);

        Transaction newTransaction3 = new Transaction("expense", 60, "spent 60 dollars", testCategoryUID, "test Category3", testDate, null);
        tRepo.insert(newTransaction3);

        ArrayList<Transaction> transactionList = tRepo.getAllTransactionByType("income");
        assertEquals(2, transactionList.size());

        Transaction testTransaction = transactionList.get(0);
        assertEquals(testTransaction.userID, newTransaction.userID);
        assertEquals(testTransaction.message, newTransaction.message);
        assertEquals(testTransaction.type, newTransaction.type);

        testTransaction = transactionList.get(1);
        assertEquals(testTransaction.userID, newTransaction2.userID);
        assertEquals(testTransaction.message, newTransaction2.message);
        assertEquals(testTransaction.type, newTransaction2.type);
    }

    @LargeTest
    public void testGetTransactionByCategory() throws Exception {
        TransactionRepo tRepo = new TransactionRepo(testContext);
        Date testDate = new Date(1425769367789L);
        Transaction newTransaction = new Transaction("income", 70, "made 70 dollars", testCategoryUID, "test Category1", testDate, null);
        tRepo.insert(newTransaction);

        Transaction newTransaction2 = new Transaction("income", 60, "made 60 dollars", testCategoryUID, "test Category1", testDate, null);
        tRepo.insert(newTransaction2);

        Transaction newTransaction3 = new Transaction("expense", 60, "spent 60 dollars", testCategoryUID, "test Category2", testDate, null);
        tRepo.insert(newTransaction3);

        ArrayList<Transaction> transactionList = tRepo.getTransactionByCategory("test Category1".toLowerCase(), testCategoryUID);
        assertEquals(2, transactionList.size());

        Transaction testTransaction = transactionList.get(0);
        assertEquals(testTransaction.userID, newTransaction.userID);
        assertEquals(testTransaction.message, newTransaction.message);
        assertEquals(testTransaction.category, newTransaction.category);
        assertEquals(testTransaction.type, newTransaction.type);

        testTransaction = transactionList.get(1);
        assertEquals(testTransaction.userID, newTransaction2.userID);
        assertEquals(testTransaction.message, newTransaction2.message);
        assertEquals(testTransaction.category, newTransaction2.category);
        assertEquals(testTransaction.type, newTransaction2.type);
    }

    @LargeTest
    public void testGetTransactionByMessage() throws Exception {
        TransactionRepo tRepo = new TransactionRepo(testContext);
        Date testDate = new Date(1425769367789L);
        Transaction newTransaction = new Transaction("income", 50, "made 50 dollars", testCategoryUID, "test Category1", testDate, null);
        tRepo.insert(newTransaction);

        Transaction newTransaction2 = new Transaction("income", 60, "made 60 dollars", testCategoryUID, "test Category2", testDate, null);
        tRepo.insert(newTransaction2);

        Transaction newTransaction3 = new Transaction("expense", 60, "spent 60 dollars", testCategoryUID, "test Category3", testDate, null);
        tRepo.insert(newTransaction3);

        ArrayList<Transaction> transactionList = tRepo.getTransactionByMessage("made 50 dollars", testCategoryUID);
        assertEquals(1, transactionList.size());

        Transaction testTransaction = transactionList.get(0);
        assertEquals(testTransaction.userID, newTransaction.userID);
        assertEquals(testTransaction.message, newTransaction.message);
        assertEquals(testTransaction.category, newTransaction.category);
        assertEquals(testTransaction.type, newTransaction.type);

    }

    @LargeTest
    public void testGetTransactionByAmount() throws Exception {
        TransactionRepo tRepo = new TransactionRepo(testContext);
        Date testDate = new Date(1425769367789L);
        Transaction newTransaction = new Transaction("income", 50, "made 50 dollars", testCategoryUID, "test Category1", testDate, null);
        tRepo.insert(newTransaction);

        Transaction newTransaction2 = new Transaction("income", 100, "made 100 dollars", testCategoryUID, "test Category2", testDate, null);
        tRepo.insert(newTransaction2);

        Transaction newTransaction3 = new Transaction("expense", 60, "spent 60 dollars", testCategoryUID, "test Category3", testDate, null);
        tRepo.insert(newTransaction3);

        ArrayList<Transaction> transactionList = tRepo.getTransactionByAmount("55", "105", testCategoryUID);
        assertEquals(2, transactionList.size());

        Transaction testTransaction = transactionList.get(0);
        assertEquals(testTransaction.userID, newTransaction2.userID);
        assertEquals(testTransaction.message, newTransaction2.message);
        assertEquals(testTransaction.category, newTransaction2.category);
        assertEquals(testTransaction.type, newTransaction2.type);

        testTransaction = transactionList.get(1);
        assertEquals(testTransaction.userID, newTransaction3.userID);
        assertEquals(testTransaction.message, newTransaction3.message);
        assertEquals(testTransaction.category, newTransaction3.category);
        assertEquals(testTransaction.type, newTransaction3.type);
    }

    @LargeTest
    public void testGetTransactionsByTypeCategoryMonthYear() {
        TransactionRepo tr = new TransactionRepo(testContext);
        tr.insert(t1);
        tr.insert(t2);
        tr.insert(t3);
        tr.insert(t4);

        ArrayList<Transaction> queryResult = tr.getTransactionsByTypeCategoryMonthYear("Expense", "school", 1, 2015, testCategoryUID);
        assertEquals(2, queryResult.size());
        assertEquals(12.00f, queryResult.get(0).amount);
        assertTrue(queryResult.get(0).message.equals("Lunch"));
        assertEquals(25.00f, queryResult.get(1).amount);
        assertTrue(queryResult.get(1).message.equals("Book"));
    }

    @LargeTest
    public void testGetTransactionsByTypeCategoryMonthYearDecember() {
        TransactionRepo tr = new TransactionRepo(testContext);
        tr.insert(t1);
        tr.insert(t2);
        tr.insert(t3);
        tr.insert(t4);
        tr.insert(t5);
        tr.insert(t6);

        ArrayList<Transaction> results = tr.getTransactionsByTypeCategoryMonthYear("Expense", "school", 12, 2014, testCategoryUID);
        assertEquals(1, results.size());
        assertEquals(22.0f, results.get(0).amount);
        assertTrue(results.get(0).message.equals("Party"));
    }

    @LargeTest
    public void testGetTransactionsByTypeMonthYear() {
        TransactionRepo tr = new TransactionRepo(testContext);
        tr.insert(t1);
        tr.insert(t2);
        tr.insert(t3);
        tr.insert(t4);
        tr.insert(t5);
        tr.insert(t6);
        tr.insert(t7);
        tr.insert(t8);
        tr.insert(t9);
        tr.insert(t10);

        ArrayList<Transaction> jan2015Expenses = tr.getTransactionsByTypeMonthYear("Expense", 1, 2015, testCategoryUID);
        assertEquals(3, jan2015Expenses.size());
        assertEquals(t1.amount, jan2015Expenses.get(0).amount);
        assertTrue(t1.message.equals(jan2015Expenses.get(0).message));
        assertTrue(t1.category.equals(jan2015Expenses.get(0).category));
        assertEquals(t2.amount, jan2015Expenses.get(1).amount);
        assertTrue(t2.message.equals(jan2015Expenses.get(1).message));
        assertTrue(t2.category.equals(jan2015Expenses.get(1).category));
        assertEquals(t4.amount, jan2015Expenses.get(2).amount);
        assertTrue(t4.message.equals(jan2015Expenses.get(2).message));
        assertTrue(t4.category.equals(jan2015Expenses.get(2).category));

        ArrayList<Transaction> feb2015Income = tr.getTransactionsByTypeMonthYear("Income", 2, 2015, testCategoryUID);
        assertEquals(2, feb2015Income.size());
        assertEquals(t8.amount, feb2015Income.get(0).amount);
        assertTrue(t8.message.equals(feb2015Income.get(0).message));
        assertTrue(t8.category.equals(feb2015Income.get(0).category));
        assertEquals(t9.amount, feb2015Income.get(1).amount);
        assertTrue(t9.message.equals(feb2015Income.get(1).message));
        assertTrue(t9.category.equals(feb2015Income.get(1).category));
    }

    @LargeTest
    public void testGetTransactionsByTypeDate() {
        TransactionRepo tr = new TransactionRepo(testContext);
        tr.insert(t1);
        tr.insert(t2);
        tr.insert(t3);
        tr.insert(t4);
        tr.insert(t5);
        tr.insert(t6);
        tr.insert(t7);
        tr.insert(t8);
        tr.insert(t9);
        tr.insert(t10);

        ArrayList<Transaction> jan22015Expenses = tr.getTransactionsByTypeDate("Expense", 2, 1, 2015, testCategoryUID);
        assertEquals(2, jan22015Expenses.size());
        assertEquals(t2.amount, jan22015Expenses.get(0).amount);
        assertTrue(t2.message.equals(jan22015Expenses.get(0).message));
        assertTrue(t2.category.equals(jan22015Expenses.get(0).category));
        assertEquals(t4.amount, jan22015Expenses.get(1).amount);
        assertTrue(t4.message.equals(jan22015Expenses.get(1).message));
        assertTrue(t4.category.equals(jan22015Expenses.get(1).category));

        ArrayList<Transaction> mar12015Income = tr.getTransactionsByTypeDate("Income", 2, 3, 2015, testCategoryUID);
        assertEquals(0, mar12015Income.size());

        mar12015Income = tr.getTransactionsByTypeDate("Income", 1, 3, 2015, testCategoryUID);
        assertEquals(1, mar12015Income.size());
        assertEquals(t10.amount, mar12015Income.get(0).amount);
        assertTrue(t10.message.equals(mar12015Income.get(0).message));
        assertTrue(t10.category.equals(mar12015Income.get(0).category));
    }

    @LargeTest
    public void testGetTransactionsByTypeYear() {
        TransactionRepo tr = new TransactionRepo(testContext);
        tr.insert(t1);
        tr.insert(t7);
        tr.insert(t8);
        tr.insert(t9);
        tr.insert(t10);

        ArrayList<Transaction> income2015 = tr.getTransactionsByTypeYear("Income", 2015, testCategoryUID);
        assertEquals(4, income2015.size());
        assertEquals(t7.amount, income2015.get(0).amount);
        assertEquals(t8.amount, income2015.get(1).amount);
        assertEquals(t9.amount, income2015.get(2).amount);
        assertEquals(t10.amount, income2015.get(3).amount);
    }

    @LargeTest
    public void testGetTransactionsByTypeMonthYearSortedCategory() {
        TransactionRepo tr = new TransactionRepo(testContext);
        tr.insert(t1);  //Jan 1, 2015, school Expense
        tr.insert(t2);  //Jan 2, 2015, school Expense
        tr.insert(t3);  //Jan 2, 2014, school Expense
        tr.insert(t4);  //Jan 2, 2015, food Expense
        tr.insert(t5);  //Dec 22, 2014, school Expense
        tr.insert(t6);  //Jan 1, 2014, school Expense

        ArrayList<Transaction> sortedList = tr.getTransactionsByTypeMonthYearSortedCategory("Expense", 1, 2015, testCategoryUID);
        assertEquals(3, sortedList.size());
        assertEquals(t1.amount, sortedList.get(0).amount);
        assertEquals(t2.amount, sortedList.get(1).amount);
        assertEquals(t4.amount, sortedList.get(2).amount);
    }

}
