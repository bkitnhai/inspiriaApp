package com.google.samples.apps.topeka.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.samples.apps.topeka.R;

public abstract class BaseActivity extends AppCompatActivity {
  private static final String TAG = "common";//"BaseActivity";
  protected Toolbar toolbar;

  protected abstract int getLayoutResId();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate+BaseActivity");
    setContentView(getLayoutResId());

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
    toolbar.setTitle(getTitle());
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
  }
}
