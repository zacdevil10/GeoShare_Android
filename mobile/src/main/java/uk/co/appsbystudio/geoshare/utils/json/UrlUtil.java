package uk.co.appsbystudio.geoshare.utils.json;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.R;

public class UrlUtil {

    /* DIRECTIONS METHODS */
    public static void getDirectionsUrl(LatLng origin, LatLng dest) {
        String sOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String sDest = "destination=" + dest.latitude + "," + dest.longitude;

        String params = sOrigin + "&" + sDest + "&sensor=false";

        Application.getContext().getString(R.string.server_key);
    }

    /* GEOCODING METHOD */

    //Reverse Geocoding
    public static String getReverseGeocodingUrl(double lat, double lng) {
        String latLng = "latlng=" + lat + "," + lng;

        return "https://maps.googleapis.com/maps/api/geocode/json?" + latLng + "&key=" + Application.getContext().getString(R.string.server_key);
    }

    /*public static String getReverseGeocodingUrl(String lat, String lng) {
        String latLng = "latlng=" + lat + "," + lng;

        return "https://maps.googleapis.com/maps/api/geocode/json?" + latLng + "&key=" + Application.getContext().getString(R.string.server_key);
    }*/

    //Download url result
    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuffer = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) inputStream.close();
            if (urlConnection != null) urlConnection.disconnect();
        }

        return data;
    }

}
