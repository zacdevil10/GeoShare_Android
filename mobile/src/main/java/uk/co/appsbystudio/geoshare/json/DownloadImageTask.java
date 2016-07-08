package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    private final CircleImageView viewById;
    private final ImageView imageViewById;
    private final Context context;
    private final String name;

    public DownloadImageTask(CircleImageView viewById, ImageView imageViewById, Context context, String name) {
        this.viewById = viewById;
        this.imageViewById = imageViewById;
        this.context = context;
        this.name = name;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0].replace(" ", "%20");
        Bitmap image_bitmap = null;

        File file = new File(String.valueOf(context.getCacheDir()), name + ".png");
        try {
            image_bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Date lastModified = new Date(file.lastModified());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss", Locale.UK);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String date = dateFormat.format(lastModified);

        System.out.println(date + name);

        try {
            URL urlString = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlString.openConnection();
            httpURLConnection.setRequestProperty("If-Modified-Since", date + " UTC");

            int statusCode = httpURLConnection.getResponseCode();

            System.out.println(statusCode + name);

            if (statusCode == 200) {
                try {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    image_bitmap = BitmapFactory.decodeStream(inputStream);

                    File image = new File(context.getCacheDir(), name + ".png");
                    FileOutputStream fileOutputStream = new FileOutputStream(image);
                    image_bitmap.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream);
                    fileOutputStream.close();
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

        if (viewById != null) {
            if (bitmap != null) {
                viewById.setImageBitmap(bitmap);
            } else {
                viewById.setImageBitmap(default_image);
            }
        } else {
            if (bitmap != null) {
                imageViewById.setImageBitmap(bitmap);
            } else {
                imageViewById.setImageBitmap(default_image);
            }
        }

    }
}