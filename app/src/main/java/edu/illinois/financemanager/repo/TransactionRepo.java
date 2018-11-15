package edu.illinois.financemanager.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.illinois.financemanager.object.Transaction;


public class TransactionRepo {
    private DBHelper dbHelper;

    /**
     * Constructor
     *
     * @param context context
     */
    public TransactionRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * Inserts transactions into database
     *
     * @param transaction transaction
     * @return int transactionID
     */
    public int insert(Transaction transaction) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Transaction.KEY_MESSAGE, transaction.message);
        values.put(Transaction.KEY_AMOUNT, transaction.amount);
        values.put(Transaction.KEY_TYPE, transaction.type);
        values.put(Transaction.KEY_UID, transaction.userID);
        values.put(Transaction.KEY_CATEGORY, transaction.category);
        values.put(Transaction.KEY_DATE, transaction.date.getTime());
        values.put(Transaction.KEY_PIC, transaction.pic);

        // Inserting Row
        long transactionID = db.insert(Transaction.TABLE, null, values);

        db.close();

        return (int) transactionID;
    }

    /**
     * Deletes transaction from database
     *
     * @param transactionID transactionID
     */
    public void delete(int transactionID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Transaction.TABLE, Transaction.KEY_ID + " = ?", new String[]{String.valueOf(transactionID)});
        db.close();
    }

    /**
     * Updates transactions in database
     *
     * @param transaction transaction
     */
    public void update(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Transaction.KEY_MESSAGE, transaction.message);
        values.put(Transaction.KEY_AMOUNT, transaction.amount);
        values.put(Transaction.KEY_TYPE, transaction.type);
        values.put(Transaction.KEY_UID, transaction.userID);
        values.put(Transaction.KEY_CATEGORY, transaction.category);
        values.put(Transaction.KEY_DATE, transaction.date.getTime());
        values.put(Transaction.KEY_PIC, transaction.pic);

        db.update(Transaction.TABLE, values, Transaction.KEY_ID + " = ?", new String[]{String.valueOf(transaction.id)});

        // Closing database connection
        db.close();
    }

    /**
     * Gets a list of all Transactions of that match the type passed in ("Income" or "Expense")
     *
     * @param type - Either "Expense" or "Income"
     * @return an ArrayList of Transactions
     */
    public ArrayList<Transaction> getAllTransactionByType(String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Transaction.TABLE + " WHERE lower(" + Transaction.KEY_TYPE + ") = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{type.toLowerCase()});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                    transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                    transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                    transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                    transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                    transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                    transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                    transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return transactionList;
    }

    /**
     * Retrieves all transactions from the database
     *
     * @return ArrayList<Transaction>
     */
    public ArrayList<Transaction> getAllTransaction() {

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Transaction.TABLE;

        ArrayList<Transaction> transactionList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                transactionList.add(transaction);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return transactionList;
    }

    /**
     * Get a transaction from the database using its category
     *
     * @param transactionCategory transactionCategory
     * @return ArrayList<Transaction>
     */
    public ArrayList<Transaction> getTransactionByCategory(String transactionCategory, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Transaction.TABLE +
                " WHERE lower(" + Transaction.KEY_CATEGORY + ") = ?" +
                " AND " + Transaction.KEY_UID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{transactionCategory.toLowerCase(), String.valueOf(userID)});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactionList;
    }

    /**
     * Get a transaction from the database using its message
     *
     * @param transactionMessage transactionMessage
     * @return ArrayList<Transaction>
     */
    public ArrayList<Transaction> getTransactionByMessage(String transactionMessage, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Transaction.TABLE +
                " WHERE lower(" + Transaction.KEY_MESSAGE + ") LIKE ?" +
                " AND " + Transaction.KEY_UID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{transactionMessage.toLowerCase(), String.valueOf(userID)});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactionList;
    }

    /**
     * Get a transaction from the database using its amount
     *
     * @param transactionAmount1 transactionAmount1
     * @param transactionAmount2 transactionAmount2
     * @return ArrayList<Transaction>
     */
    public ArrayList<Transaction> getTransactionByAmount(String transactionAmount1, String transactionAmount2, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Transaction.TABLE +
                " WHERE " + Transaction.KEY_AMOUNT + " BETWEEN ? AND ?" +
                " AND " + Transaction.KEY_UID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(transactionAmount1), String.valueOf(transactionAmount2), String.valueOf(userID)});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactionList;
    }

    /**
     * Get a transaction from the database using its Date
     *
     * @param transactionDate1 transactionDate1
     * @param transactionDate2 transactionDate2
     * @return ArrayList<Transaction>
     */
    public ArrayList<Transaction> getTransactionByDate(String transactionDate1, String transactionDate2, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Transaction.TABLE +
                " WHERE " + Transaction.KEY_DATE + " BETWEEN ? AND ?" +
                " AND " + Transaction.KEY_UID + " = ?";

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(Calendar.YEAR, Integer.parseInt(transactionDate1.substring(0, 4)));
        start.set(Calendar.MONTH, Integer.parseInt(transactionDate1.substring(5, 7)) - 1);
        start.set(Calendar.DATE, Integer.parseInt(transactionDate1.substring(8, 10)));
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        end.set(Calendar.YEAR, Integer.parseInt(transactionDate2.substring(0, 4)));
        end.set(Calendar.MONTH, Integer.parseInt(transactionDate2.substring(5, 7)) - 1);
        end.set(Calendar.DATE, Integer.parseInt(transactionDate2.substring(8, 10)));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        String startTime = "" + start.getTime().getTime();
        String endTime = "" + end.getTime().getTime();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{startTime, endTime, String.valueOf(userID)});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactionList;
    }

    /**
     * Gets all transactions of Type type of Category category within the month and year
     *
     * @param type     - Either "Income" or "Expense"
     * @param category - Name of the Category to be queried for
     * @param month    - month of the year, Jan == 1, Feb == 2, etc.
     * @param year     - 4 digit calendar year
     * @return list of Transaction objects (id, amount, date, message)
     */
    public ArrayList<Transaction> getTransactionsByTypeCategoryMonthYear(String type, String category, int month, int year, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Transaction.TABLE +
                " WHERE lower(" + Transaction.KEY_TYPE + ") = ? AND lower(" +
                Transaction.KEY_CATEGORY + ") = ? AND " +
                Transaction.KEY_DATE + " BETWEEN ? AND ?" +
                " AND " + Transaction.KEY_UID + " = ?";

        Calendar start = new GregorianCalendar(year, month - 1, 1);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);

        String startTime = "" + start.getTime().getTime();
        String endTime = "" + (end.getTime().getTime() - 1);
        Cursor cursor = db.rawQuery(query, new String[]{type.toLowerCase(), category.toLowerCase(), startTime, endTime, String.valueOf(userID)});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                    transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                    transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                    transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                    transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                    transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                    transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                    transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return transactionList;
    }

    /**
     * Gets all Transactions of a certain type within the given month and year
     *
     * @param type  - Either "Income" or "Expense"
     * @param month - Jan == 1, Feb == 2, etc.
     * @param year  - 4 digit calendar year
     * @return ArrayList of Transactions with fields id, amount, date, message, and category, List of size 0 if no Transactions found
     */
    public ArrayList<Transaction> getTransactionsByTypeMonthYear(String type, int month, int year, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Transaction.TABLE +
                " WHERE lower(" + Transaction.KEY_TYPE + ") = ? AND " +
                Transaction.KEY_DATE + " BETWEEN ? AND ? " +
                " AND " + Transaction.KEY_UID + " = ? ORDER BY " + Transaction.KEY_DATE + " ASC";

        ArrayList<Transaction> transactionList = new ArrayList<>();

        Calendar start = new GregorianCalendar(year, month - 1, 1);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);

        Cursor cursor = db.rawQuery(query, new String[]{type.toLowerCase(), "" + start.getTime().getTime(), "" + (end.getTime().getTime() - 1), String.valueOf(userID)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                    transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                    transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                    transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                    transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                    transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                    transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                    transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return transactionList;
    }

    /**
     * Gets all Transactions of a certain type on the given date
     *
     * @param type  - Either Income or Expense
     * @param day   - day of the month
     * @param month - month of the year, Jan == 1, Feb == 2, etc.
     * @param year  - 4 digit calendar year
     * @return ArrayList of Transactions with fields id, amount, message, and category, List of size 0 if no Transactions found
     */
    public ArrayList<Transaction> getTransactionsByTypeDate(String type, int day, int month, int year, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Transaction.TABLE +
                " WHERE lower(" + Transaction.KEY_TYPE + ") = ? AND " +
                Transaction.KEY_DATE + " BETWEEN ? AND ?" +
                " AND " + Transaction.KEY_UID + " = ?";

        Calendar start = new GregorianCalendar(year, month - 1, day);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_MONTH, 1);

        Cursor cursor = db.rawQuery(query, new String[]{type.toLowerCase(), "" + start.getTime().getTime(), "" + (end.getTime().getTime() - 1), String.valueOf(userID)});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                    transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                    transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                    transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                    transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                    transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                    transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                    transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return transactionList;
    }

    /**
     * Gets all Transactions of a certain type within the year
     *
     * @param type - Either "Income" or "Expense"
     * @param year - 4 digit calendar year
     * @return ArrayList of Transactions with fields id, amount, date, message, and category, List of size 0 if no Transactions found
     */
    public ArrayList<Transaction> getTransactionsByTypeYear(String type, int year, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Transaction.TABLE +
                " WHERE lower(" + Transaction.KEY_TYPE + ") = ? AND " +
                Transaction.KEY_DATE + " BETWEEN ? AND ? " +
                " AND " + Transaction.KEY_UID + " = ? ORDER BY " + Transaction.KEY_DATE + " ASC";

        Calendar start = new GregorianCalendar(year, 0, 1);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.YEAR, 1);

        Cursor cursor = db.rawQuery(query, new String[]{type.toLowerCase(), "" + start.getTime().getTime(), "" + (end.getTime().getTime() - 1), String.valueOf(userID)});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                    transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                    transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                    transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                    transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                    transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                    transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                    transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return transactionList;
    }

    /**
     * Gets all Transactions within a certain month of a certain year
     *
     * @param month - - month of the year, Jan == 1, Feb == 2, etc.
     * @param year  - 4 digit calendar year
     * @return ArrayList of Transactions with fields id, amount, date, message, and category, type List of size 0 if no Transactions found
     */
    public ArrayList<Transaction> getTransactionsByMonthYear(int month, int year, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Transaction.TABLE + " WHERE " +
                Transaction.KEY_DATE + " BETWEEN ? AND ? " +
                " AND " + Transaction.KEY_UID + " = ? ORDER BY " + Transaction.KEY_DATE + " ASC";

        Calendar start = new GregorianCalendar(year, month - 1, 1);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);

        Cursor cursor = db.rawQuery(query, new String[]{"" + start.getTime().getTime(), "" + (end.getTime().getTime() - 1), String.valueOf(userID)});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                    transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                    transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                    transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                    transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                    transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                    transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                    transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return transactionList;
    }

    /**
     * Gets all Transactions of a certain type within the given month and year
     *
     * @param type  - Either "Expense" or "Income"
     * @param month - Jan == 1, Feb == 2, etc.
     * @param year  - 4 digit calendar year
     * @return ArrayList of Transactions with fields id, amount, date, message, and category sorted by Category in ascending order, List of size 0 if no Transactions found
     */
    public ArrayList<Transaction> getTransactionsByTypeMonthYearSortedCategory(String type, int month, int year, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Transaction.TABLE +
                " WHERE lower(" + Transaction.KEY_TYPE + ") = ? AND " +
                Transaction.KEY_DATE + " BETWEEN ? AND ? " +
                " AND " + Transaction.KEY_UID + " = ? ORDER BY " + Transaction.KEY_DATE + " ASC";

        Calendar start = new GregorianCalendar(year, month - 1, 1);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);

        Cursor cursor = db.rawQuery(query, new String[]{type.toLowerCase(), "" + start.getTime().getTime(), "" + (end.getTime().getTime() - 1), String.valueOf(userID)});

        ArrayList<Transaction> transactionList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.id = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_ID));
                    transaction.message = cursor.getString(cursor.getColumnIndex(Transaction.KEY_MESSAGE));
                    transaction.amount = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_AMOUNT));
                    transaction.type = cursor.getString(cursor.getColumnIndex(Transaction.KEY_TYPE));
                    transaction.userID = cursor.getInt(cursor.getColumnIndex(Transaction.KEY_UID));
                    transaction.category = cursor.getString(cursor.getColumnIndex(Transaction.KEY_CATEGORY));
                    transaction.pic = cursor.getString(cursor.getColumnIndex(Transaction.KEY_PIC));
                    transaction.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Transaction.KEY_DATE))));
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return transactionList;
    }
}

