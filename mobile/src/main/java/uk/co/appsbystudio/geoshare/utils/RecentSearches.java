package uk.co.appsbystudio.geoshare.utils;

public class RecentSearches {

    private String entry;
    private String timeStamp;
    private boolean isSearch;

    public RecentSearches() {}

    public RecentSearches(String entry, String timeStamp, boolean isSearch) {
        this.entry = entry;
        this.timeStamp = timeStamp;
        this.isSearch = isSearch;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setSearch(boolean search) {
        isSearch = search;
    }

    public String getEntry() {
        return entry;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public boolean isSearch() {
        return isSearch;
    }
}
