package uk.co.appsbystudio.geoshare.utils;

public class UserInformation {

    private String name;
    private String email;

    public UserInformation() {}

    public UserInformation(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
