package uk.co.appsbystudio.geoshare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

    private final String url;
    private final ImageView staticMapView;

    public DownloadImageTask(String url, ImageView staticMapView) {
        this.url = url;
        this.staticMapView = staticMapView;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap staticMap = null;
        try {
            InputStream inputStream = new URL(url).openStream();
            staticMap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return staticMap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        staticMapView.setImageBitmap(result);
    }
}
