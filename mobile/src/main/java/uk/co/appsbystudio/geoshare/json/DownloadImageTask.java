package uk.co.appsbystudio.geoshare.json;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    private final CircleImageView viewById;

    public DownloadImageTask(CircleImageView viewById) {
        this.viewById = viewById;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {

        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        viewById.setImageBitmap(bitmap);
    }
}
