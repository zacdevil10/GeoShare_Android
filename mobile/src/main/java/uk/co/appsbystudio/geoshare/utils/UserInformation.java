package uk.co.appsbystudio.geoshare.utils;

public class UserInformation {

    private String name;
    private String caseFoldedName;
    private String email;

    public UserInformation() {}

    public UserInformation(String name, String caseFoldedName, String email) {
        this.name = name;
        this.caseFoldedName = caseFoldedName;
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCaseFoldedName(String caseFoldedName) {
        this.caseFoldedName = caseFoldedName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getCaseFoldedName() {
        return caseFoldedName;
    }

    public String getEmail() {
        return email;
    }
}
