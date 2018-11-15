package edu.illinois.financemanager.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import edu.illinois.financemanager.object.User;

public class UserRepo {

    private DBHelper dbHelper;

    public UserRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * Adds the User to the database
     *
     * @param user User object to insert into the database
     */
    public void insert(User user) {
        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(User.KEY_ID, user.id);
        values.put(User.KEY_NAME, user.name);
        values.put(User.KEY_EMAIL, user.email);

        // Inserting Row
        db.insert(User.TABLE, null, values);
        db.close();
    }

    /**
     * Removes all users from User table
     */
    public void delete() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(User.TABLE, null, null);
    }

    /**
     * Retrieves the top user in the database. This class assumes that the User table is only populated with one User at a time.
     *
     * @return a User if exists, otherwise null
     */
    public User getUser() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(User.TABLE, null, null, null, null, null, null, "1");

        User user = null;
        //Get the first entry and return a User object
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                user = new User();
                user.id = cursor.getLong(cursor.getColumnIndex(User.KEY_ID));
                user.name = cursor.getString(cursor.getColumnIndex(User.KEY_NAME));
                user.email = cursor.getString(cursor.getColumnIndex(User.KEY_EMAIL));
            }
            cursor.close();
        }

        db.close();

        return user;
    }

    /**
     * Updates the user with the stored id in the User's table
     *
     * @param user - User information to be updated. Update occurs on the id field of this user
     */
    public void updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(User.KEY_NAME, user.name);
        values.put(User.KEY_EMAIL, user.email);


        db.update(User.TABLE, values, User.KEY_ID + " = ?", new String[]{"" + user.id});

        // Closing database connection
        db.close();
    }
}
