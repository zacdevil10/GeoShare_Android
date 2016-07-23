package uk.co.appsbystudio.geoshare.places.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import uk.co.appsbystudio.geoshare.R;

public class RecentFragment extends Fragment {

    public RecentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        String[] values = new String[] {"London", "France", "Spain", "England", "Paris", "New York", "Canada", "Italy", "Russia", "Moscow", "Argentina", "Madrid", "London", "France", "Spain", "England", "Paris", "New York", "Canada", "Italy", "Russia", "Moscow", "Argentina", "Madrid"};

        ListView listView = (ListView) view.findViewById(R.id.recentList);
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values));

        return view;
    }

}
