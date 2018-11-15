package edu.illinois.financemanager.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import edu.illinois.financemanager.object.Budget;


public class BudgetRepo {
    private DBHelper dbHelper;

    /**
     * @param context context
     */
    public BudgetRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * insert a new budget into the database
     *
     * @param budget the new budget
     */
    public void insert(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Budget.KEY_UID, budget.userID);
        values.put(Budget.KEY_AMOUNT, budget.amount);
        values.put(Budget.KEY_DATE, budget.date);

        db.insert(Budget.TABLE, null, values);
        db.close();
    }

    /**
     * delete a budget using user id and date
     *
     * @param budgetDate budgetDate
     * @param userID userID
     */
    public void delete(String budgetDate, long userID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Budget.TABLE, Budget.KEY_DATE + " = ? AND " + Budget.KEY_UID + " = ?", new String[]{budgetDate, String.valueOf(userID)});
        db.close();
    }

    /**
     * update a budget
     *
     * @param budget the new budget
     */
    public void update(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Budget.KEY_UID, budget.userID);
        values.put(Budget.KEY_AMOUNT, budget.amount);
        values.put(Budget.KEY_DATE, budget.date);

        db.update(Budget.TABLE, values, Budget.KEY_DATE + " = ? AND " + Budget.KEY_UID + " = ?", new String[]{budget.date, String.valueOf(budget.userID)});

        db.close();
    }

    /**
     * get a budget using user id and date
     *
     * @param budgetDate budgetDate
     * @param userID userID
     * @return Budget
     */
    public Budget getBudgetByDate(String budgetDate, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Budget.TABLE + " WHERE " +
                Budget.KEY_DATE + " = ?" + " AND " + Budget.KEY_UID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{budgetDate, String.valueOf(userID)});

        Budget budget = new Budget();

        if (cursor.moveToFirst()) {
            do {
                budget.userID = cursor.getLong(cursor.getColumnIndex(Budget.KEY_UID));
                budget.amount = cursor.getDouble(cursor.getColumnIndex(Budget.KEY_AMOUNT));
                budget.date = cursor.getString(cursor.getColumnIndex(Budget.KEY_DATE));
            } while (cursor.moveToNext());
        } else {
            return null;
        }

        cursor.close();
        db.close();
        return budget;
    }
}
