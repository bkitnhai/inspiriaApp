package com.google.samples.apps.topeka.trail.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.trail.Fragment.PageFragment;

/**
 * Created by NguyenHai on 5/3/2016.
 */
public class SampleFragmentPagerAdapter2 extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
    final int PAGE_COUNT = 2;
    private int tabIcons[] = {R.drawable.ic_action_picture, R.drawable.ic_action_more};
    int id;
    public SampleFragmentPagerAdapter2(FragmentManager fm, int id) {
        super(fm);
        this.id = id;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position + 1, id );
    }

    @Override
    public int getPageIconResId(int position) {
        return tabIcons[position];
    }
}
