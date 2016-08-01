package uk.co.appsbystudio.geoshare.json;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapDownloadTask extends AsyncTask<String, Void, Bitmap> {

    private final ImageView imageViewById;
    private final String url;

    private Bitmap image_bitmap = null;

    public MapDownloadTask(ImageView imageViewById, String url) {
        this.imageViewById = imageViewById;
        this.url = url;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        System.out.println("Getting map image");

        try {
            URL urlString = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlString.openConnection();
                try {
                    InputStream inputStream = httpURLConnection.getInputStream();

                    image_bitmap = BitmapFactory.decodeStream(inputStream);

                } catch (IOException e) {
                    e.printStackTrace();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image_bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            imageViewById.setImageBitmap(bitmap);
        }

    }
}