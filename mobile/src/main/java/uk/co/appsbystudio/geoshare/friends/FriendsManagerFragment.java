package uk.co.appsbystudio.geoshare.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.pages.FriendSearchActivity;
import uk.co.appsbystudio.geoshare.friends.pages.FriendsFragment;
import uk.co.appsbystudio.geoshare.friends.pages.FriendsPendingFragment;

public class FriendsManagerFragment extends Fragment {

    //TODO: long username

    public FriendsManagerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* INFLATE LAYOUT WITH PER-FRAGMENT THEME */
        final Context context = new ContextThemeWrapper(getActivity(), R.style.fragment_theme);
        LayoutInflater layoutInflater = inflater.cloneInContext(context);
        View view = layoutInflater.inflate(R.layout.fragment_friends_manager, container, false);

        /* SET UP TOOLBAR */
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TabLayout friendsTabs = (TabLayout) view.findViewById(R.id.friends_tabs);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_48px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });
        toolbar.setTitle(R.string.friends);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.searchFriends);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FriendSearchActivity.class);
                startActivity(intent);
            }
        });

        /* TOOLBAR TABS FRAGMENT SWAPPING */
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new FriendsFragment();
                    case 1:
                        return new FriendsPendingFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Current";
                    case 1:
                        return "Pending";
                    default:
                        return null;
                }
            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);
        friendsTabs.setupWithViewPager(viewPager);

        return view;
    }
}