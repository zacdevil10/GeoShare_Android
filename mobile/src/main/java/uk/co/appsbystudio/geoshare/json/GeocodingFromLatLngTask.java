package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import uk.co.appsbystudio.geoshare.Application;

public class GeocodingFromLatLngTask extends AsyncTask<Object, Object, Address> {

    private final Double lat;
    private final Double lng;

    public GeocodingFromLatLngTask(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected Address doInBackground(Object... params) {
        Geocoder gc = new Geocoder(Application.getAppContext(), Locale.getDefault());
        Address finalAddress = null;

        try {
            List<Address> address = gc.getFromLocation(lat, lng, 1);

            finalAddress = address.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return finalAddress;
    }
}