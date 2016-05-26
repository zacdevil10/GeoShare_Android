package uk.co.appsbystudio.geoshare.database.databaseModel;

public class UserModel {

    String pID;
    String username;
    String email;
    Integer remember;

    public UserModel() {}

    public UserModel(String pID) {
        this.pID = pID;
    }

    public UserModel(String pID, String username) {
        this.pID = pID;
        this.username = username;
    }

    public UserModel(String pID, String username, String email) {
        this.pID = pID;
        this.username = username;
        this.email = email;
    }

    public UserModel(String pID, String username, String email, Integer remember) {
        this.pID = pID;
        this.username = username;
        this.email = email;
        this.remember = remember;
    }

    public String getpID() {
        return pID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getRemember() {
        return remember;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRemember(Integer remember) {
        this.remember = remember;
    }
}
