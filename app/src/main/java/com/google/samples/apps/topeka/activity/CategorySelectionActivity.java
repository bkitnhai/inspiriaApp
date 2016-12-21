/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.topeka.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.samples.apps.topeka.AndroidDatabaseManager;
import com.google.samples.apps.topeka.Database.DatabaseHelper;
import com.google.samples.apps.topeka.Database.DbBitmapUtility;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.fragment.CategorySelectionFragment;
import com.google.samples.apps.topeka.helper.PreferencesHelper;
import com.google.samples.apps.topeka.model.MyPlayer;
import com.google.samples.apps.topeka.persistence.TopekaDatabaseHelper;
import com.google.samples.apps.topeka.widget.AvatarView;

import org.json.JSONException;

public class CategorySelectionActivity extends Activity {
    private static final String EXTRA_PLAYER = "player";
    private static final String TAG = "CategoryActivity";
    public static void start(Context context, MyPlayer MyPlayer, ActivityOptions options) {
        Intent starter = new Intent(context, CategorySelectionActivity.class);
        starter.putExtra(EXTRA_PLAYER, MyPlayer);
        context.startActivity(starter, options.toBundle());
    }

    public static void start(Context context, MyPlayer MyPlayer) {
        Intent starter = new Intent(context, CategorySelectionActivity.class);
        starter.putExtra(EXTRA_PLAYER, MyPlayer);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_selection);
        MyPlayer MyPlayer = getIntent().getParcelableExtra(EXTRA_PLAYER);
        setUpToolbar(MyPlayer);
        if (savedInstanceState == null) {
            attachCategoryGridFragment();
        } else {
            setProgressBarVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView scoreView = (TextView) findViewById(R.id.score);
        int score = 0;
        try {
            score = TopekaDatabaseHelper.getScore(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        scoreView.setText(getString(R.string.x_points, score));
    }

    private void setUpToolbar(MyPlayer Player) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_player);
        setActionBar(toolbar);
        //noinspection ConstantConditions
        getActionBar().setDisplayShowTitleEnabled(false);
        final AvatarView avatarView = (AvatarView) toolbar.findViewById(R.id.avatar);
        //avatarView.setImageDrawable(getDrawable(player.getAvatar().getDrawableId()));

        avatarView.setImageBitmap(getAvatar(Player));
        //((TextView) toolbar.findViewById(R.id.title)).setText(getDisplayName(player));
        ((TextView) toolbar.findViewById(R.id.title)).setText(Player.getName());

    }
    private Bitmap getAvatar(MyPlayer Player){
        DatabaseHelper newdb = new DatabaseHelper(this);
        byte[] tempbite = newdb.getImage(Player.getName());
        Log.i("image", String.valueOf(tempbite));
        return new DbBitmapUtility().getImage(tempbite);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out: {
                Log.i("checkin", "sign_out");
                signOut();
                return true;
            }
            case R.id.AR:{
               Intent dbmanager = new Intent(this,AndroidDatabaseManager.class);
                        startActivity(dbmanager);
            }
            case R.id.Profile:{
                Log.i("checkin", "profileClick");
                Intent userprofile_intent = new Intent(this, UserProfile.class);
                userprofile_intent.putExtra("key", 1); //Optional parameters
                this.startActivity(userprofile_intent);
                finishAfterTransition();
            }
        }

        return super.onOptionsItemSelected(item);
    }
    private void signOut() {
        PreferencesHelper.signOut(this);
        TopekaDatabaseHelper.reset(this);
        SignInActivity.start(this, false, null);
        finishAfterTransition();
    }
    private void attachCategoryGridFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.quiz_container, CategorySelectionFragment.newInstance())
                .commit();
        setProgressBarVisibility(View.GONE);
    }

    private void setProgressBarVisibility(int visibility) {
        findViewById(R.id.progress).setVisibility(visibility);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        Log.i(TAG, "onActivityResult1");
        if ((requestCode == 5) &&
                (resultCode == -1)) {
            String returnString = data.getExtras().getString("returnData");
            Log.i(TAG,returnString);
        }
    }
}

