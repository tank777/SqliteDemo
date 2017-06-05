package com.bgt.sqlitedemo.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bgt.sqlitedemo.localdb.localdb_model.User;

/**
 * Created by Bhavesh on 05-06-2017.
 */

public class PostsDatabaseHelper extends SQLiteOpenHelper {


    private static final String TAG = "PostsDatabaseHelper";
    private static PostsDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "postsDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "userName";

    public static final String ALL_READY_REG = "all ready inserted";
    public static final String SUCCESSFULLY_REG = "user successfully inserted";
    public static final String FAIL = "Fail";


    public static synchronized PostsDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new PostsDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    public PostsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USER_NAME + " TEXT" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    public String saveUser(User user){

        boolean isAllReadyAvailable = checkUserAllReadyAvailable(user.getUserName());

        if (!isAllReadyAvailable) {

            SQLiteDatabase db = getWritableDatabase();

            // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
            // consistency of the database.
            db.beginTransaction();
            try {
                // The user might already exist in the database (i.e. the same user created multiple posts).

                ContentValues values = new ContentValues();
                values.put(KEY_USER_NAME, user.getUserName());

                // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
                db.insertOrThrow(TABLE_USERS, null, values);
                db.setTransactionSuccessful();

                return PostsDatabaseHelper.SUCCESSFULLY_REG;
            } catch (Exception e) {
                Log.d(TAG, "Error while trying to add user to database");
            } finally {
                db.endTransaction();
            }
            return PostsDatabaseHelper.FAIL;

        }
        else {
            return PostsDatabaseHelper.ALL_READY_REG;
        }
    }

    private boolean checkUserAllReadyAvailable(String userName) {


        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();

        String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                KEY_USER_NAME, TABLE_USERS, KEY_USER_NAME);
        Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(userName)});
        try {
            if (cursor.getCount() > 0) {
                return true;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return false;
    }
}
