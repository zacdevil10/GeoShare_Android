package uk.co.appsbystudio.geoshare.nav;

import uk.co.appsbystudio.geoshare.R;

public enum NavItems {

    FRIENDS(R.string.friends, R.drawable.ic_add_white_24dp),
    SETTINGS(R.string.settings, R.drawable.ic_add_white_24dp);

    private final Integer nameID;
    private final Integer imageID;

    NavItems(Integer name, Integer imageID) {
        this.nameID = name;
        this.imageID = imageID;
    }

    public Integer getItemNameID() {
        return nameID;
    }

    public Integer getItemImageID() {
        return imageID;
    }
}

