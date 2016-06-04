package uk.co.appsbystudio.geoshare.places;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

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

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        toolbar.setTitle(R.string.places);

        BottomBar bottomBar = BottomBar.attach(getActivity(), savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.places_bottom_bar, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(@IdRes int menuItemId) {
                switch (menuItemId){
                    case R.id.recent:
                        System.out.println("Recent");
                        break;
                    case R.id.favorites:
                        System.out.println("Favorites");
                        break;
                    case R.id.nearby:
                        System.out.println("Nearby");
                        break;
                }
            }
        });

        bottomBar.setActiveTabColor(getResources().getColor(R.color.colorPrimary));

        return view;
    }

}
