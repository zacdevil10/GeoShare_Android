package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

public class GeocodingFromLatLngTask extends AsyncTask<Object, Object, Address> {

    private final Context context;
    private final Double lat;
    private final Double lng;

    public GeocodingFromLatLngTask(Context context, Double lat, Double lng) {
        this.context =  context;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected Address doInBackground(Object... params) {

        Geocoder gc = new Geocoder(context);
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