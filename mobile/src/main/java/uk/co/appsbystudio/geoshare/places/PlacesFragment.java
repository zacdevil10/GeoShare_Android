package uk.co.appsbystudio.geoshare.places;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;

public class PlacesFragment extends Fragment {

    public PlacesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //final Context context = new ContextThemeWrapper(getActivity(), R.style.fragment_theme);
        //LayoutInflater layoutInflater = inflater.cloneInContext(context);
        View view = inflater.inflate(R.layout.fragment_places, container, false);

        /* SET UP TOOLBAR */
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_48px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        toolbar.setTitle(R.string.places);

        /*final NoSwipeViewPager viewPager = view.findViewById(R.id.view_pager);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        RecentFragment recentFragment = new RecentFragment();
                        recentFragment.setArguments(null);
                        return recentFragment;
                    case 1:
                        return new FavouritesFragment();
                    case 2:
                        return new SharedFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };

        viewPager.setPagingEnabled();

        viewPager.setAdapter(fragmentPagerAdapter);*/

        return view;
    }

}
