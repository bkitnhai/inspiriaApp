package com.google.samples.apps.topeka.trail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.trail.Adapter.SampleFragmentPagerAdapter2;
import com.google.samples.apps.topeka.trail.ImageTab.GalleryAdapter;
import com.google.samples.apps.topeka.trail.ImageTab.Image;

import java.util.ArrayList;

public class InforTab extends AppCompatActivity {
    private RecyclerView recyclerView;
    private static final String endpoint = "http://nguyenhainorway.netau.net/imageTestGlide/glide.json";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    Context temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra("id", 1);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        this.setContentView(R.layout.pageslidingtab);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter2(getSupportFragmentManager(), id));

        // Give the pageslidingtab the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }
}