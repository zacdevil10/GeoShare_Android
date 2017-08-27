package uk.co.appsbystudio.geoshare.places.pages;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.appsbystudio.geoshare.R;

public class SharedFragment extends Fragment {


    public SharedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shared, container, false);

        return view;
    }

}
