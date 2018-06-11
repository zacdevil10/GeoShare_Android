package uk.co.appsbystudio.geoshare.utils.geocoder;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import uk.co.appsbystudio.geoshare.Application;

public class GeocodingFromAddressTask extends AsyncTask<Void, Void, LatLng> {

    private final String location;

    public GeocodingFromAddressTask(String location) {
        this.location = location;
    }

    @Override
    protected LatLng doInBackground(Void... params) {
        Geocoder gc = new Geocoder(Application.Companion.getContext());
        LatLng latLng = null;
        try {
            List<Address> address = gc.getFromLocationName(location, 1);

            for (Address a : address) {
                if (a.hasLatitude() && a.hasLongitude()) {
                    Double lat = a.getLatitude();
                    Double longitude = a.getLongitude();

                    latLng = new LatLng(lat, longitude);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latLng;
    }

    @Override
    protected void onPostExecute(LatLng latLng) {
        super.onPostExecute(latLng);
    }
}