package uk.co.appsbystudio.geoshare.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.utils.json.UrlUtil;

public class MarkerAnimatorLabelTask extends AsyncTask<Void, Void, String[]> {

    private final Double lat;
    private final Double lng;

    private final Long time;

    private final Marker marker;
    private final ValueAnimator initAnimator;
    private final ValueAnimator endAnimator;

    private String addressString;

    public MarkerAnimatorLabelTask(Marker marker, ValueAnimator initAnimator, ValueAnimator endAnimator, Double lat, Double lng, Long time) {
        this.marker = marker;
        this.initAnimator = initAnimator;
        this.endAnimator = endAnimator;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    @Override
    protected void onPreExecute() {
        initAnimator.setDuration(500);
        initAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                File fileCheck = new File(MainActivity.cacheDir + "/" + marker.getTag() + ".png");
                if (fileCheck.exists()) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + marker.getTag() + ".png");
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.bitmapCanvas(imageBitmap, 512, 155, true, (Integer) valueAnimator.getAnimatedValue(), " \nLoading...\n ")));
                    marker.setAnchor(0.11328125f, 1f);
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.bitmapCanvas(null, 512, 155, true, (Integer) valueAnimator.getAnimatedValue(), " \nLoading...\n ")));
                    marker.setAnchor(0.11328125f, 1f);
                }
            }
        });

        initAnimator.start();

        initAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                initAnimator.removeAllUpdateListeners();
                initAnimator.removeAllListeners();
                super.onAnimationEnd(animation);
            }
        });
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        String[] finalAddress = {"", "", ""};

        String url = UrlUtil.getReverseGeocodingUrl(lat, lng);
        try {
            String jsonResponse = UrlUtil.downloadUrl(url);

            JSONObject jsonObject = new JSONObject(jsonResponse);

            String addressComponents = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getString("formatted_address");

            String[] split = addressComponents.split(",", 2);

            finalAddress[0] = split[0];

            if (split[1].startsWith(" ")) finalAddress[1] = split[1].substring(1);
            else finalAddress[1] = split[1];

            finalAddress[2] = "Updated: " + TimeUtils.convertDate(time);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            finalAddress[0] = "";
            finalAddress[1] = "Error getting address!";
            finalAddress[2] = "";
        }

        return finalAddress;
    }

    @Override
    protected void onPostExecute(String[] address) {
        addressString = address[0] + "\n" + address[1] + "\n" + address[2];

        endAnimator.setDuration(500);
        endAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                File fileCheck = new File(MainActivity.cacheDir + "/" + marker.getTag() + ".png");
                if (fileCheck.exists()) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + marker.getTag() + ".png");
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.bitmapCanvas(imageBitmap, 512, 155, true, (Integer) valueAnimator.getAnimatedValue(), addressString)));
                    marker.setAnchor(0.11328125f, 1f);
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.bitmapCanvas(null, 512, 155, true, (Integer) valueAnimator.getAnimatedValue(), addressString)));
                    marker.setAnchor(0.11328125f, 1f);
                }
            }
        });

        endAnimator.start();

        endAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                endAnimator.removeAllUpdateListeners();
                endAnimator.removeAllListeners();
                super.onAnimationEnd(animation);
            }
        });
    }
}
