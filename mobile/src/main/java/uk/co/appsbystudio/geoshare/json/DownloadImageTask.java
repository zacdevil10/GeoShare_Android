package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    private final CircleImageView viewById;
    private Context context;

    public DownloadImageTask(CircleImageView viewById, Context context) {
        this.viewById = viewById;
        this.context = context;
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
        Bitmap default_image = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_black_48dp);

        if (bitmap != null) {
            viewById.setImageBitmap(bitmap);
        } else {
            viewById.setImageBitmap(default_image);
        }

    }
}
