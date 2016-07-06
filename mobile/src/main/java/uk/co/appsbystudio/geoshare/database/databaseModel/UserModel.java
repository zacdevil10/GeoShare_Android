package uk.co.appsbystudio.geoshare.database.databaseModel;

public class UserModel {

    private String pID;
    private String username;
    private String email;
    private Integer remember;
    private Integer seenTutorial;

    public UserModel() {}

    public UserModel(String pID, String username, String email, Integer remember, Integer seenTutorial) {
        this.pID = pID != null? pID : this.pID;
        this.username = username != null? username : this.username;
        this.email = email != null? email : this.email;
        this.remember = remember != null? remember : this.remember;
        this.seenTutorial = seenTutorial != null? seenTutorial : this.seenTutorial;
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

    public Integer getSeenTutorial() {
        return seenTutorial;
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

    public void setSeenTutorial(Integer seenTutorial) {
        this.seenTutorial = seenTutorial;
    }
}
