package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("username", new ReturnData().getUsername(context))
                .addFormDataPart("REST_API_TOKEN", new ReturnData().getpID(context))
                .addFormDataPart("image", "image.png", RequestBody.create(MediaType.parse("image/png"), image))
                .build();

        Request request = new Request.Builder()
                .url("https://geoshare.appsbystudio.co.uk/api/user/img/")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.code());
                ((MainActivity) context).refreshPicture(new ReturnData().getUsername(context), true);
                response.close();
            }
        });

        return null;
    }
}
