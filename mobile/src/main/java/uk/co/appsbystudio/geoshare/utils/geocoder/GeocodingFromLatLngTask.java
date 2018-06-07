package uk.co.appsbystudio.geoshare.utils.geocoder;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import uk.co.appsbystudio.geoshare.utils.json.UrlUtil;

public class GeocodingFromLatLngTask extends AsyncTask<Void, Void, String> {

    private final Double lat;
    private final Double lng;

    public GeocodingFromLatLngTask(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String finalAddress = null;

        String url = UrlUtil.getReverseGeocodingUrl(lat, lng);
        try {
            String jsonResponse = UrlUtil.downloadUrl(url);

            System.out.println(jsonResponse);

            JSONObject jsonObject = new JSONObject(jsonResponse);

            finalAddress = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getString("formatted_address");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return finalAddress;
    }
}