package uk.co.appsbystudio.geoshare.utils;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.R;

class DirectionsParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>{

    private final GoogleMap googleMap;

    DirectionsParserTask(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jsonObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jsonObject = new JSONObject(jsonData[0]);
            DirectionsDataParserTask directionsDataParserTask = new DirectionsDataParserTask();

            routes = directionsDataParserTask.parse(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> results) {
        ArrayList<LatLng> points = null;
        PolylineOptions polylineOptions = null;

        for (int i = 0; i < results.size(); i++) {
            points = new ArrayList<>();
            polylineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = results.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            polylineOptions.addAll(points);
            polylineOptions.width(10);
            polylineOptions.color(Application.getAppContext().getResources().getColor(R.color.colorAccent));
        }

        if (polylineOptions != null) {
            googleMap.addPolyline(polylineOptions);
        }
    }
}
