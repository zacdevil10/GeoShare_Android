package uk.co.appsbystudio.geoshare.database.databaseModel;


public class RecentSearchModel {
    Integer id;
    String term;
    String dateTime;

    public RecentSearchModel() {}

    public RecentSearchModel(Integer id) {
        this.id = id;
    }

    public RecentSearchModel(Integer id, String term) {
        this.id = id;
        this.term = term;
    }

    public RecentSearchModel(Integer id, String term, String dateTime) {
        this.id = id;
        this.term = term;
        this.dateTime = dateTime;
    }

    public Integer getId() {
        return id;
    }

    public String getTerm() {
        return term;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
