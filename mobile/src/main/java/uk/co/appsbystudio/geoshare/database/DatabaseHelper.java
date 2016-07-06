package uk.co.appsbystudio.geoshare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cache.db";
    private static final Integer DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS USER_DETAILS(id INTEGER PRIMARY KEY, _pID TEXT, username TEXT, email TEXT, remember INTEGER, seenTutorial INTEGER DEFAULT 0)");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USER_DETAILS");
        onCreate(db);
    }

    // LOGIN_DETAILS
    public void addUsers(UserModel userItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_pID", userItem.getpID());
        values.put("username", userItem.getUsername());
        values.put("email", userItem.getEmail());
        values.put("remember", userItem.getRemember());
        values.put("seenTutorial", userItem.getSeenTutorial());

        db.insert("USER_DETAILS", null, values);
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> userModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM USER_DETAILS", null);

        if (cursor.moveToFirst()) {
            do {
                UserModel userModel = new UserModel();
                userModel.setpID(cursor.getString(cursor.getColumnIndex("_pID")));
                userModel.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                userModel.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                userModel.setRemember(cursor.getInt(cursor.getColumnIndex("remember")));
                userModelList.add(userModel);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return userModelList;
    }

    public Integer getSeenTutorial() {
        Integer seenTutorial;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT seenTutorial FROM USER_DETAILS", null);
        cursor.moveToFirst();

        UserModel userModel = new UserModel();
        userModel.setSeenTutorial(cursor.getInt(cursor.getColumnIndex("seenTutorial")));

        seenTutorial = userModel.getSeenTutorial();

        cursor.close();

        return seenTutorial;
    }

    public void clearAllUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM USER_DETAILS");
    }

    public void clearUserSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE USER_DETAILS SET _pID='' ");
    }

    public void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
