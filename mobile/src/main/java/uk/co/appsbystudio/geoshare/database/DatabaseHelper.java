package uk.co.appsbystudio.geoshare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import uk.co.appsbystudio.geoshare.database.databaseModel.RecentSearchModel;
import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cache.db";
    public static final Integer DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS LOGIN_DETAILS(_pID TEXT, username TEXT, email TEXT, remember INTEGER)");
            db.execSQL("CREATE TABLE IF NOT EXISTS SEARCH_HISTORY(_id TEXT PRIMARY KEY, term TEXT, Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS LOGIN_DETAILS");
        db.execSQL("DROP TABLE IF EXISTS SEARCH_HISTORY");
        onCreate(db);
    }

    // LOGIN_DETAILS
    public long addUsers(UserModel userItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_pID", userItem.getpID());
        values.put("username", userItem.getUsername());
        values.put("email", userItem.getEmail());
        values.put("remember", userItem.getRemember());

        long userItems = db.insert("LOGIN_DETAILS", null, values);

        return userItems;
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> userModelList = new ArrayList<UserModel>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM LOGIN_DETAILS", null);

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

        return userModelList;
    }

    public List<UserModel> getUsername() {
        List<UserModel> userModelList = new ArrayList<UserModel>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT username FROM LOGIN_DETAILS", null);

        if (cursor.moveToFirst()) {
            do {
                UserModel userModel = new UserModel();
                userModel.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                userModelList.add(userModel);
            } while (cursor.moveToNext());
        }

        return userModelList;
    }

    public void clearAllUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM LOGIN_DETAILS");
    }

    public void clearUserSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("");
    }

    //Search History
    public long addSearchHistory(RecentSearchModel recentSearchModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_id", recentSearchModel.getId());
        values.put("term", recentSearchModel.getTerm());
        values.put("Timestamp", recentSearchModel.getDateTime());

        long savedItems = db.insert("SEARCH_HISTORY", null, values);

        return savedItems;
    }

    public List<RecentSearchModel> getSearchHistory() {
        List<RecentSearchModel> recentSearchModelList = new ArrayList<RecentSearchModel>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SEARCH_HISTORY ORDER BY Timestamp DESC", null);

        if (cursor.moveToFirst()) {
            do {
                RecentSearchModel recentSearchModel = new RecentSearchModel();
                recentSearchModel.setTerm(cursor.getString(cursor.getColumnIndex("term")));
                recentSearchModelList.add(recentSearchModel);
            } while (cursor.moveToNext());
        }

        return recentSearchModelList;
    }

    public void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null & db.isOpen()) {
            db.close();
        }
    }
}
