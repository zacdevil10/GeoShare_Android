package uk.co.appsbystudio.geoshare.utils.firebase;

public class UserInformation {

    private String name;
    private String caseFoldedName;

    public UserInformation() {}

    public UserInformation(String name, String caseFoldedName) {
        this.name = name;
        this.caseFoldedName = caseFoldedName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCaseFoldedName() {
        return caseFoldedName;
    }

    public void setCaseFoldedName(String caseFoldedName) {
        this.caseFoldedName = caseFoldedName;
    }
}
