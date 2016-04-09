package uk.co.appsbystudio.geoshare.database.databaseModel;

public class UserModel {

    String id;
    String password;
    Integer remember;

    public UserModel() {}

    public UserModel(String id) {
        this.id = id;
    }

    public UserModel(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public UserModel(String id, String password, Integer remember) {
        this.id = id;
        this.password = password;
        this.remember = remember;
    }

    public void setUserId(String id) {
        this.id = id;
    }

    public void setUserPassword(String password) {
        this.password = password;
    }

    public void setUserRemember(Integer remember) {
        this.remember = remember;
    }

    public String getUserId() {
        return id;
    }

    public String getUserPassword() {
        return password;
    }

    public Integer getUserRemember() {
        return remember;
    }
}
