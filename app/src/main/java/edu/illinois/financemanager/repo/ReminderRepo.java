package edu.illinois.financemanager.repo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import edu.illinois.financemanager.object.Reminder;

public class ReminderRepo {

    private DBHelper dbHelper;

    /**
     * @param context context
     */
    public ReminderRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * insert a new reminder into the database
     *
     * @param reminder the new reminder
     * @return reminderID
     */
    public long insert(Reminder reminder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Reminder.KEY_MSG, reminder.message);
        values.put(Reminder.KEY_AMT, reminder.amount);
        values.put(Reminder.KEY_DTE, reminder.startDate);
        values.put(Reminder.KEY_TME, reminder.startTime);
        values.put(Reminder.KEY_RPT, reminder.repeatID);
        values.put(Reminder.KEY_UID, reminder.userID);

        long reminderID = db.insert(Reminder.TABLE, null, values);
        db.close();

        return reminderID;
    }

    /**
     * delete a reminder using its id
     *
     * @param reminderID reminderID
     */
    public void delete(long reminderID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Reminder.TABLE, Reminder.KEY_ID + " = ?", new String[]{String.valueOf(reminderID)});
        db.close();
    }

    /**
     * update a reminder using its id
     *
     * @param reminder the new reminder
     */
    public void update(Reminder reminder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Reminder.KEY_MSG, reminder.message);
        values.put(Reminder.KEY_AMT, reminder.amount);
        values.put(Reminder.KEY_DTE, reminder.startDate);
        values.put(Reminder.KEY_TME, reminder.startTime);
        values.put(Reminder.KEY_RPT, reminder.repeatID);
        values.put(Reminder.KEY_UID, reminder.userID);

        db.update(Reminder.TABLE, values, Reminder.KEY_ID + " = ?", new String[]{String.valueOf(reminder.id)});

        db.close();
    }

    /**
     * get all reminders in database
     *
     * @param userID userID
     * @return ArrayList<Reminder>
     */
    public ArrayList<Reminder> getReminderList(long userID) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Reminder.TABLE + " WHERE " + Reminder.KEY_UID + " = ?";

        ArrayList<Reminder> reminderList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.id = cursor.getLong(cursor.getColumnIndex(Reminder.KEY_ID));
                reminder.message = cursor.getString(cursor.getColumnIndex(Reminder.KEY_MSG));
                reminder.amount = cursor.getDouble(cursor.getColumnIndex(Reminder.KEY_AMT));
                reminder.startDate = cursor.getString(cursor.getColumnIndex(Reminder.KEY_DTE));
                reminder.startTime = cursor.getString(cursor.getColumnIndex(Reminder.KEY_TME));
                reminder.repeatID = cursor.getLong(cursor.getColumnIndex(Reminder.KEY_RPT));
                reminder.userID = cursor.getLong(cursor.getColumnIndex(Reminder.KEY_UID));
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return reminderList;
    }

    /**
     * get reminder by its id
     *
     * @param reminderID reminderID
     * @return Reminder
     */
    public Reminder getReminderByID(long reminderID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Reminder.TABLE + " WHERE " + Reminder.KEY_ID + " = ?";

        Reminder reminder = new Reminder();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(reminderID)});

        if (cursor.moveToFirst()) {
            do {
                reminder.id = cursor.getLong(cursor.getColumnIndex(Reminder.KEY_ID));
                reminder.message = cursor.getString(cursor.getColumnIndex(Reminder.KEY_MSG));
                reminder.amount = cursor.getDouble(cursor.getColumnIndex(Reminder.KEY_AMT));
                reminder.startDate = cursor.getString(cursor.getColumnIndex(Reminder.KEY_DTE));
                reminder.startTime = cursor.getString(cursor.getColumnIndex(Reminder.KEY_TME));
                reminder.repeatID = cursor.getLong(cursor.getColumnIndex(Reminder.KEY_RPT));
                reminder.userID = cursor.getLong(cursor.getColumnIndex(Reminder.KEY_UID));
            } while (cursor.moveToNext());
        } else {
            return null;
        }

        cursor.close();
        db.close();
        return reminder;
    }
}
