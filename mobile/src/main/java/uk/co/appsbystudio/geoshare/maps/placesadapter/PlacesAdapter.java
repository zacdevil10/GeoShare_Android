package uk.co.appsbystudio.geoshare.maps.placesadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.R;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder>{
    private final ArrayList namesArray;

    public PlacesAdapter(ArrayList namesArray) {
        this.namesArray = namesArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.places_search_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.locationName.setText((CharSequence) namesArray.get(position));

    }

    @Override
    public int getItemCount() {
        return namesArray.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView locationName;

        ViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.search_result_text);
        }
    }
}