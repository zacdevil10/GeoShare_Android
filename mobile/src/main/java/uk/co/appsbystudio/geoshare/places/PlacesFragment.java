package uk.co.appsbystudio.geoshare.places;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
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
import uk.co.appsbystudio.geoshare.places.pages.FavouritesFragment;
import uk.co.appsbystudio.geoshare.places.pages.NearbyFragment;
import uk.co.appsbystudio.geoshare.places.pages.RecentFragment;

public class PlacesFragment extends Fragment {

    public PlacesFragment() {}

    private AHBottomNavigation bottomNavigation;

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

        bottomNavigation = (AHBottomNavigation) view.findViewById(R.id.bottom_bar);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Recent", R.drawable.ic_history_black_48px);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Favourites", R.drawable.ic_favorite_black_48px);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Nearby", R.drawable.ic_location_on_black_48px);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setAccentColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        final NoSwipeViewPager viewPager = (NoSwipeViewPager) view.findViewById(R.id.view_pager);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new RecentFragment();
                    case 1:
                        return new FavouritesFragment();
                    case 2:
                        return new NearbyFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };

        viewPager.setPagingEnabled(false);

        viewPager.setAdapter(fragmentPagerAdapter);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                viewPager.setCurrentItem(position);
            }
        });

        return view;
    }

}
