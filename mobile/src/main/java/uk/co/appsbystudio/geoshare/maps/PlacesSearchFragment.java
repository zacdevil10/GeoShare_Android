package uk.co.appsbystudio.geoshare.maps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.maps.placesadapter.PlacesAdapter;

public class PlacesSearchFragment extends Fragment {

    ArrayList<String> locations = new ArrayList<>();

    public PlacesSearchFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_search, container, false);

        RecyclerView searchList = view.findViewById(R.id.search_result_list);
        searchList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        searchList.setLayoutManager(layoutManager);

        locations.add("Item 1");
        locations.add("Item 2");
        locations.add("Item 3");
        locations.add("Item 4");
        locations.add("Item 5");
        locations.add("Item 6");

        PlacesAdapter placesAdapter = new PlacesAdapter(locations);

        searchList.setAdapter(placesAdapter);

        /*view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).backFromSearch();
                //PlacesSearchFragment.super.getActivity().onBackPressed();
            }
        });*/

        return view;
    }
}
