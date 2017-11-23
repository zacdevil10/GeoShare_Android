package uk.co.appsbystudio.geoshare.utils;

import java.io.File;
import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.Application;

public class DeleteUnusedImagesFromCache extends Thread {

    private final ArrayList<String> imageCacheId;

    public DeleteUnusedImagesFromCache(ArrayList<String> imageCacheId) {
        this.imageCacheId = imageCacheId;
    }

    @Override
    public void run() {
        super.run();

        for (String id : imageCacheId) {
            File file = new File(Application.getContext().getCacheDir() + "/" + id + ".png");

            if (file.exists()) {
                file.delete();
            }
        }
    }
}
