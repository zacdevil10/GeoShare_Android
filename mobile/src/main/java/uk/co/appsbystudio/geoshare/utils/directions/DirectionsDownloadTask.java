package uk.co.appsbystudio.geoshare.utils.directions;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import uk.co.appsbystudio.geoshare.utils.json.UrlUtil;

public class DirectionsDownloadTask extends AsyncTask<String, String, String> {

    private final GoogleMap googleMap;

    public DirectionsDownloadTask(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    protected String doInBackground(String... url) {
        String data = "";

        try {
            data = UrlUtil.downloadUrl(url[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        DirectionsParserTask parserTask = new DirectionsParserTask(googleMap);
        parserTask.execute(result);

    }
}
