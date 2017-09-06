package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class GeocodingFromAddressTask extends AsyncTask<Void, Void, LatLng> {

    private final Context context;
    private final String location;

    public GeocodingFromAddressTask(Context context, String location) {
        this.context =  context;
        this.location = location;
    }

    @Override
    protected LatLng doInBackground(Void... params) {
        Geocoder gc = new Geocoder(context);
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
