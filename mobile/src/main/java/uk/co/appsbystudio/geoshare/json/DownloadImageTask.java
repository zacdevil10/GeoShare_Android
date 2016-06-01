package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        RequestFuture<Bitmap> future = RequestFuture.newFuture();
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = params[0];
        final Bitmap[] mIcon11 = {null};

        ImageRequest imageRequest = new ImageRequest(url, future, 0, 0, null, future){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("If-Modified-Since", "Thu, 2 Jun 2016 00:15:25 GMT");
                return headers;
            }

            @Override
            protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
                System.out.println(response.statusCode);

                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(imageRequest);

        try {
            Bitmap response = null;

            while (response == null) {
                try {
                    response = future.get(30, TimeUnit.SECONDS);

                    System.out.println(response);

                    mIcon11[0] = response;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(context, "Timeout. Please check your internet connection.", Toast.LENGTH_LONG).show();
        }

        return mIcon11[0];
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
