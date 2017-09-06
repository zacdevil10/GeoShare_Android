package uk.co.appsbystudio.geoshare.places.placesadapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.json.GeocodingFromAddressTask;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{

    private final Context context;
    private final ArrayList cityName;
    private final ArrayList countryName;
    private final ArrayList mapURL;

    public RecentAdapter(Context context, ArrayList<String> cityName, ArrayList countryName, ArrayList mapURL) {
        this.context = context;
        this.cityName = cityName;
        this.countryName = countryName;
        this.mapURL = mapURL;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_card_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.city.setText(cityName.get(position).toString());
        holder.country.setText(countryName.get(position).toString());

        final Intent intent = new Intent("show.on.map");

        holder.showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng = null;
                try {
                    latLng = new GeocodingFromAddressTask(context, cityName.get(holder.getAdapterPosition()).toString()).execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                intent.putExtra("markerState", "default");
                if (latLng != null) {
                    intent.putExtra("lat", latLng.latitude);
                    intent.putExtra("long", latLng.longitude);
                }
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(intent));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView city;
        final TextView country;
        final ImageView mapImage;
        final LinearLayout showOnMap;

        ViewHolder(View itemView) {
            super(itemView);
            city = (TextView) itemView.findViewById(R.id.location);
            country = (TextView) itemView.findViewById(R.id.city_country);
            mapImage = (ImageView) itemView.findViewById(R.id.mapImage);
            showOnMap = (LinearLayout) itemView.findViewById(R.id.showMore);
        }
    }
}
