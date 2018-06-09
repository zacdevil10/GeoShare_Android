package uk.co.appsbystudio.geoshare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

import uk.co.appsbystudio.geoshare.friends.profile.staticmap.ProfileStaticMapView;

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

    private final String url;
    private final ProfileStaticMapView view;

    public DownloadImageTask(String url, ProfileStaticMapView view) {
        this.url = url;
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        String style = "&style=element:geometry|color:0xf5f5f5&style=element:labels.text.fill|color:0x616161&style=element:labels.text.stroke|color:0xf5f5f5&style=feature:administrative|saturation:-100&style=feature:administrative.land_parcel|element:labels.text.fill|color:0xbdbdbd&style=feature:landscape|saturation:-100&style=feature:poi|element:geometry|color:0xeeeeee&style=feature:poi|element:labels.text.fill|color:0x757575&style=feature:poi.park|element:geometry|color:0xe5e5e5&style=feature:poi.park|element:labels.text.fill|color:0x9e9e9e&style=feature:road|element:geometry|color:0xffffff&style=feature:road.arterial|element:labels.text.fill|color:0x757575&style=feature:road.highway|element:geometry|color:0xdadada&style=feature:road.highway|element:labels.text|visibility:simplified&style=feature:road.local|element:labels.text.fill|color:0x9e9e9e&style=feature:transit.line|element:geometry|color:0xe5e5e5&style=feature:transit.station|element:geometry|color:0xeeeeee&style=feature:water|element:geometry|color:0xc9c9c9&style=feature:water|element:labels.text.fill|color:0x9e9e9e";
        Bitmap staticMap = null;
        try {
            InputStream inputStream = new URL(url + style).openStream();
            staticMap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return staticMap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        view.setMapImage(result);
    }
}
