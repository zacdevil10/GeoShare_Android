package uk.co.appsbystudio.geoshare.database.databaseModel;

public class FirstRunModel {

    Integer seenTutorial;

    public FirstRunModel() {}

    public FirstRunModel(Integer seenTutorial){
        this.seenTutorial = seenTutorial;
    }

    public void setSeenTutorial(Integer seenTutorial) {
        this.seenTutorial = seenTutorial;
    }

    public Integer getSeenTutorial() {
        return seenTutorial;
    }
}
