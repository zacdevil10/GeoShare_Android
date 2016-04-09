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
    public static final Integer DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS LOGIN_DETAILS(_id TEXT PRIMARY KEY, password TEXT, remember INTEGER)");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS LOGIN_DETAILS");
        onCreate(db);
    }

    // LOGIN_DETAILS
    public long addUsers(UserModel userItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_id", userItem.getUserId());
        values.put("password", userItem.getUserPassword());
        values.put("remember", userItem.getUserRemember());

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
                userModel.setUserId(cursor.getString(cursor.getColumnIndex("_id")));
                userModel.setUserPassword(cursor.getString(cursor.getColumnIndex("password")));
                userModel.setUserRemember(cursor.getInt(cursor.getColumnIndex("remember")));
                userModelList.add(userModel);
            } while (cursor.moveToNext());
        }

        return userModelList;
    }

    public void clearAllUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM LOGIN_DETAILS");
    }

    public void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null & db.isOpen()) {
            db.close();
        }
    }
}
