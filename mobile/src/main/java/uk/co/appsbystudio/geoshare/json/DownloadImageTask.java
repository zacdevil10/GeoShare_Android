package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    private final CircleImageView viewById;
    private final Context context;

    public DownloadImageTask(CircleImageView viewById, Context context) {
        this.viewById = viewById;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urlString = params[0].replace(" ", "%20");
        Bitmap image_bitmap = null;

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlString);
        httpGet.addHeader("If-Modified-Since", "Mon, 30 May 2016 00:15:25 GMT");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                try {
                    InputStream inputStream = response.getEntity().getContent();
                    image_bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image_bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Bitmap default_image = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_profile_picture);

        if (bitmap != null) {
            viewById.setImageBitmap(bitmap);
        } else {
            viewById.setImageBitmap(default_image);
        }
    }
}