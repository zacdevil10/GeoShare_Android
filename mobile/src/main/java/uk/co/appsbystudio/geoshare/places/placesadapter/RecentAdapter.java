package uk.co.appsbystudio.geoshare.places.placesadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mypopsy.maps.StaticMap;

import java.net.MalformedURLException;
import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.json.MapDownloadTask;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList location;

    public RecentAdapter(Context context, ArrayList location) {
        this.context = context;
        this.location = location;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_card_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.location.setText(location.get(position).toString());

        StaticMap staticMap = new StaticMap().center(String.valueOf(location)).size(320, 200);

        try {
            new MapDownloadTask(holder.mapImage, staticMap.toURL()).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return location.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView location;
        final TextView cityCountry;
        final ImageView mapImage;

        ViewHolder(View itemView) {
            super(itemView);
            location = (TextView) itemView.findViewById(R.id.location);
            cityCountry = (TextView) itemView.findViewById(R.id.city_country);
            mapImage = (ImageView) itemView.findViewById(R.id.mapImage);
        }
    }
}
