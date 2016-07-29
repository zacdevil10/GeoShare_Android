package uk.co.appsbystudio.geoshare.places.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.places.placesadapter.RecentAdapter;

public class RecentFragment extends Fragment {

    public RecentFragment() {}

    private ArrayList<String> arrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        RecyclerView recentRecyclerView = (RecyclerView) view.findViewById(R.id.recentList);
        recentRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recentRecyclerView.setLayoutManager(layoutManager);

        arrayList.add("London");

        RecentAdapter recentAdapter = new RecentAdapter(getActivity(), arrayList);

        recentRecyclerView.setAdapter(recentAdapter);

        return view;
    }

}
