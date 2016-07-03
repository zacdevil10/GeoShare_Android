package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.database.ReturnData;

public class ImageUpload extends AsyncTask <Bitmap, Void, Void> {

    private final File image;
    private final Context context;

    public ImageUpload(File image, Context context) {
        this.image = image;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Bitmap... params) {
        try {
            HttpParams httpParams = new BasicHttpParams();
            httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost("https://geoshare.appsbystudio.co.uk/api/user/img/");

            MultipartEntity multipartEntity = new MultipartEntity();

            multipartEntity.addPart("username", new StringBody(new ReturnData().getUsername(context)));
            multipartEntity.addPart("REST_API_TOKEN", new StringBody(new ReturnData().getpID(context)));
            multipartEntity.addPart("image", new FileBody(image, "image.png" ,"image/png", null));
            httpPost.setEntity(multipartEntity);

            httpClient.execute(httpPost, new PhotoResponseHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        try {
            URL url = new URL("https://geoshare.appsbystudio.co.uk/api/user/img/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("username", new ReturnData().getUsername(context));
            httpURLConnection.setRequestProperty("REST-API-TOKEN", new ReturnData().getpID(context));
            httpURLConnection.setRequestProperty("content/type", "multipart/form-data");
            httpURLConnection.setRequestProperty("image", context.getCacheDir() + "picture");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //*/


        return null;
    }

    private class PhotoResponseHandler implements ResponseHandler<Object> {
        @Override
        public Object handleResponse(HttpResponse httpResponse) throws IOException {

            int code = httpResponse.getStatusLine().getStatusCode();

            if (code == 204) {
                ((MainActivity) context).refreshPicture();
            }

            System.out.println(code);

            return null;
        }
    }
}
