package uk.co.appsbystudio.geoshare.friends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.pages.FriendsFragment;
import uk.co.appsbystudio.geoshare.friends.pages.FriendsPendingFragment;
import uk.co.appsbystudio.geoshare.friends.pages.FriendsRequestFragment;

public class FriendsManagerFragment extends Fragment {

    public FriendsManagerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_manager, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new FriendsFragment();
                    case 1:
                        return new FriendsRequestFragment();
                    case 2:
                        return new FriendsPendingFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Friends";
                    case 1:
                        return "Requests";
                    case 2:
                        return "Pending";
                    default:
                        return null;
                }
            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_header);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorPrimary));

        return view;
    }

}
