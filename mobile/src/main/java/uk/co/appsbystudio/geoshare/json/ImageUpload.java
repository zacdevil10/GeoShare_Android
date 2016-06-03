package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

import uk.co.appsbystudio.geoshare.database.ReturnData;

public class ImageUpload extends AsyncTask {

    private DefaultHttpClient httpClient;
    private File image;
    private Context context;

    public ImageUpload(File image, Context context) {
        this.image = image;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Object[] params) {
        try {
            HttpParams httpParams = new BasicHttpParams();
            httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost("http://geoshare.appsbystudio.co.uk/api/user/img/");

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            multipartEntity.addPart("username", new StringBody(new ReturnData().getUsername(context)));
            multipartEntity.addPart("REST_API_TOKEN", new StringBody(new ReturnData().getpID(context)));
            multipartEntity.addPart("image", new FileBody(image));
            httpPost.setEntity(multipartEntity);

            System.out.println(httpPost);

            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class PhotoResponseHandler implements ResponseHandler<Object> {
        @Override
        public Object handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

            HttpEntity httpEntity = httpResponse.getEntity();
            String response = EntityUtils.toString(httpEntity);
            Log.d("Upload", response);

            return null;
        }
    }
}
