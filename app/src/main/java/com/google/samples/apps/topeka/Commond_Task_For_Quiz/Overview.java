package com.google.samples.apps.topeka.Commond_Task_For_Quiz;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.google.samples.apps.topeka.R;

public class Overview extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        if (savedInstanceState == null) {
            OverviewFragment apFragment = new OverviewFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, apFragment).commit();
        }

    }
}
