package uk.co.appsbystudio.geoshare.places.pages;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.places.placesadapter.RecentAdapter;

public class RecentFragment extends Fragment {

    public RecentFragment() {}

    private final ArrayList<String> cityName = new ArrayList<>();
    private final ArrayList<String> countryName = new ArrayList<>();
    //private final ArrayList<String> locationImageURL = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recent, container, false);

        RecyclerView recentRecyclerView = view.findViewById(R.id.recentList);
        recentRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recentRecyclerView.setLayoutManager(layoutManager);

        cityName.add("London");
        countryName.add("England");
        cityName.add("Paris");
        countryName.add("France");
        cityName.add("New-York City");
        countryName.add("United States of America");

        final RecentAdapter recentAdapter = new RecentAdapter(getActivity(), cityName, countryName);

        recentRecyclerView.setAdapter(recentAdapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                cityName.remove(viewHolder.getAdapterPosition());
                //locationImageURL.remove(viewHolder.getAdapterPosition());
                recentAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

        itemTouchHelper.attachToRecyclerView(recentRecyclerView);

        return view;
    }

}
