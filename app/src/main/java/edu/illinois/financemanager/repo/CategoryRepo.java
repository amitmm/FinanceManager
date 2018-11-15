package edu.illinois.financemanager.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import edu.illinois.financemanager.object.Category;

public class CategoryRepo {
    private DBHelper dbHelper;

    /**
     * @param context context
     */
    public CategoryRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * insert a new category into the database
     *
     * @param category the new category
     * @return categoryID
     */
    public long insert(Category category) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Category.KEY_NAME, category.name);
        values.put(Category.KEY_PID, category.parentID);
        values.put(Category.KEY_CID, category.childrenID);
        values.put(Category.KEY_UID, category.userID);

        long categoryID = db.insert(Category.TABLE, null, values);

        db.close();

        return categoryID;
    }

    /**
     * delete a category using its id
     *
     * @param categoryID categoryID
     */
    public void delete(long categoryID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Category.TABLE, Category.KEY_ID + " = ?", new String[]{String.valueOf(categoryID)});
        db.close();
    }

    /**
     * update a category
     *
     * @param category the new category
     */
    public void update(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Category.KEY_NAME, category.name);
        values.put(Category.KEY_PID, category.parentID);
        values.put(Category.KEY_CID, category.childrenID);
        values.put(Category.KEY_UID, category.userID);

        db.update(Category.TABLE, values, Category.KEY_ID + " = ?", new String[]{String.valueOf(category.id)});

        db.close();
    }

    /**
     * get all categories using the userID
     *
     * @param userID userID
     * @return ArrayList<Category>
     */
    public ArrayList<Category> getCategoryList(long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Category.TABLE + " WHERE " + Category.KEY_UID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userID)});

        ArrayList<Category> categoryList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.id = cursor.getLong(cursor.getColumnIndex(Category.KEY_ID));
                category.name = cursor.getString(cursor.getColumnIndex(Category.KEY_NAME));
                category.parentID = cursor.getLong(cursor.getColumnIndex(Category.KEY_PID));
                category.childrenID = cursor.getLong(cursor.getColumnIndex(Category.KEY_CID));
                category.userID = cursor.getLong(cursor.getColumnIndex(Category.KEY_UID));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categoryList;
    }

    /**
     * get category by its id
     *
     * @param categoryID categoryID
     * @return Category
     */
    public Category getCategoryByID(long categoryID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Category.TABLE + " WHERE " + Category.KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(categoryID)});

        Category category = new Category();

        if (cursor.moveToFirst()) {
            do {
                category.id = cursor.getLong(cursor.getColumnIndex(Category.KEY_ID));
                category.name = cursor.getString(cursor.getColumnIndex(Category.KEY_NAME));
                category.parentID = cursor.getLong(cursor.getColumnIndex(Category.KEY_PID));
                category.childrenID = cursor.getLong(cursor.getColumnIndex(Category.KEY_CID));
                category.userID = cursor.getLong(cursor.getColumnIndex(Category.KEY_UID));
            } while (cursor.moveToNext());
        } else {
            return null;
        }

        cursor.close();
        db.close();
        return category;
    }

    /**
     * get a category by its name
     *
     * @param categoryName categoryName
     * @return Category
     */
    public Category getCategoryByName(String categoryName, long userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Category.TABLE + " WHERE " + Category.KEY_NAME + " = ?" +
                " AND " + Category.KEY_UID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{categoryName, String.valueOf(userID)});

        Category category = new Category();

        if (cursor.moveToFirst()) {
            do {
                category.id = cursor.getLong(cursor.getColumnIndex(Category.KEY_ID));
                category.name = cursor.getString(cursor.getColumnIndex(Category.KEY_NAME));
                category.parentID = cursor.getLong(cursor.getColumnIndex(Category.KEY_PID));
                category.childrenID = cursor.getLong(cursor.getColumnIndex(Category.KEY_CID));
                category.userID = cursor.getLong(cursor.getColumnIndex(Category.KEY_UID));
            } while (cursor.moveToNext());
        } else {
            return null;
        }

        cursor.close();
        db.close();
        return category;
    }

}
