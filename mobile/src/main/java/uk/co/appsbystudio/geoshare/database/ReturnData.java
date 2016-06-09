package uk.co.appsbystudio.geoshare.database;

import android.content.Context;

import java.util.List;

import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;

public class ReturnData {

    private DatabaseHelper db;
    private String username;
    private String pID;
    private Integer remember;

    public String getpID(Context context) {
        db = new DatabaseHelper(context);

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pID = id.getpID();
        }

        db.close();

        return pID;
    }

    public String getUsername(Context context) {
        db = new DatabaseHelper(context);

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            username = id.getUsername();
        }

        db.close();

        return username;
    }

    public Integer getRemember(Context context) {
        db = new DatabaseHelper(context);

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            remember = id.getRemember();
        }

        db.close();

        return remember;
    }

    public void clearData(Context context) {
        db = new DatabaseHelper(context);

        db.clearAllUserData();
        db.close();
    }

    public void clearSession(Context context) {
        db = new DatabaseHelper(context);

        db.clearUserSession();
        db.close();
    }
}
