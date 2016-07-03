package uk.co.appsbystudio.geoshare.places;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;

public class PlacesFragment extends Fragment {

    public PlacesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = new ContextThemeWrapper(getActivity(), R.style.fragment_theme);
        LayoutInflater layoutInflater = inflater.cloneInContext(context);
        View view = layoutInflater.inflate(R.layout.fragment_places, container, false);

        /* SET UP TOOLBAR */
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_48px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        toolbar.setTitle(R.string.places);

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) view.findViewById(R.id.bottom_bar);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Recents", R.drawable.ic_history_black_48px);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Favourites", R.drawable.ic_favorite_black_48px);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Nearby", R.drawable.ic_location_on_black_48px);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setAccentColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        return view;
    }
}
