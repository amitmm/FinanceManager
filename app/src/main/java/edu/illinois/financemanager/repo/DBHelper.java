package edu.illinois.financemanager.repo;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.illinois.financemanager.object.Budget;
import edu.illinois.financemanager.object.Category;
import edu.illinois.financemanager.object.Reminder;
import edu.illinois.financemanager.object.Transaction;
import edu.illinois.financemanager.object.User;

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 14;

    private static final String TAG = "DBHelper";

    // Database Name
    private static final String DATABASE_NAME = "financemanager.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "create database table");

        //All necessary tables you like to create will create here
        String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + Category.TABLE + "( "
                + Category.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Category.KEY_NAME + " TEXT, "
                + Category.KEY_PID + " INTEGER, "
                + Category.KEY_CID + " INTEGER, "
                + Category.KEY_UID + " INTEGER )";

        String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + Transaction.TABLE + "( "
                + Transaction.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Transaction.KEY_UID + " INTEGER, "
                + Transaction.KEY_TYPE + " TEXT, "
                + Transaction.KEY_AMOUNT + " REAL, "
                + Transaction.KEY_MESSAGE + " TEXT, "
                + Transaction.KEY_CATEGORY + " TEXT, "
                + Transaction.KEY_DATE + " TEXT, "
                + Transaction.KEY_PIC + " TEXT )";

        String CREATE_TABLE_USER = "CREATE TABLE " + User.TABLE + "( "
                + User.KEY_ID + " INTEGER, "
                + User.KEY_NAME + " TEXT, "
                + User.KEY_EMAIL + " TEXT )";

        String CREATE_TABLE_BUDGET = "CREATE TABLE " + Budget.TABLE + "( "
                + Budget.KEY_UID + " INTEGER, "
                + Budget.KEY_AMOUNT + " REAL, "
                + Budget.KEY_DATE + " TEXT )";

        String CREATE_TABLE_REMINDER = "CREATE TABLE " + Reminder.TABLE + "( "
                + Reminder.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Reminder.KEY_MSG + " TEXT, "
                + Reminder.KEY_AMT + " REAL, "
                + Reminder.KEY_DTE + " TEXT, "
                + Reminder.KEY_TME + " TEXT, "
                + Reminder.KEY_RPT + " INTEGER, "
                + Reminder.KEY_UID + " INTEGER )";

        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_BUDGET);
        db.execSQL(CREATE_TABLE_REMINDER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Category.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Transaction.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Budget.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Reminder.TABLE);

        // Create tables again
        onCreate(db);
    }
}
