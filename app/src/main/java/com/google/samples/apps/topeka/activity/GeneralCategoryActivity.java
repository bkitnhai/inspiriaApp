package com.google.samples.apps.topeka.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.samples.apps.topeka.Database.DatabaseHelper;
import com.google.samples.apps.topeka.Database.DbBitmapUtility;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.adapter.GeneralCategoryAdapter;
import com.google.samples.apps.topeka.event.EventActivity;
import com.google.samples.apps.topeka.helper.NewPreferencesHelper;
import com.google.samples.apps.topeka.model.MyPlayer;
import com.google.samples.apps.topeka.model.Player;
import com.google.samples.apps.topeka.persistence.TopekaDatabaseHelper;
import com.google.samples.apps.topeka.support_class.RowItem;
import com.google.samples.apps.topeka.trail.TrailActivity;
import com.google.samples.apps.topeka.widget.AvatarView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class GeneralCategoryActivity extends Activity implements
         OnItemClickListener {


    private GeneralCategoryAdapter mCategoryAdapter;
    ListView listView;
    private static List<RowItem> rowItems;
    private static final String[] titles = new String[] { "Trail","Event Quiz", "Casual Quiz" };
    public static final Integer[] images = { R.drawable.map,
            R.drawable.event, R.drawable.quiz};

    private static final String EXTRA_PLAYER = "player";
    public  MyPlayer  MyPlayer;

    public static void start(Context context, MyPlayer MyPlayer ) {
        Intent starter = new Intent(context, GeneralCategoryActivity.class);
        starter.putExtra(EXTRA_PLAYER, MyPlayer);

        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_category);
        MyPlayer = getIntent().getParcelableExtra(EXTRA_PLAYER);
        setUpToolbar(MyPlayer);


        try {
            attachCategoryGridFragment();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                signOut();
                return true;
            }
            case R.id.Profile: {
               UserProfile.start(this, MyPlayer);
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("checkin","This will be called after you finish SettingsActivity.");
    }
    private void signOut() {
        NewPreferencesHelper.signOut(this);
        TopekaDatabaseHelper.reset(this);
        SignInActivity.start(this, false, null);
        finishAfterTransition();
    }
    private void setUpToolbar(MyPlayer Player) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_player);
        setActionBar(toolbar);
        getActionBar().setDisplayShowTitleEnabled(false);
        final AvatarView avatarView = (AvatarView) toolbar.findViewById(R.id.avatar);
Log.i("playername", Player.getName());
//        avatarView.setImageBitmap(getAvatar(Player));
        ((TextView) toolbar.findViewById(R.id.title)).setText(Player.getName());

    }
    private Bitmap getAvatar(MyPlayer Player){
        Log.i ("Player", String.valueOf(Player));
        DatabaseHelper newdb = new DatabaseHelper(this);
        byte[] tempbite = newdb.getImage(Player.getName());
        Log.i("tempbite", String.valueOf(tempbite));
        return new DbBitmapUtility().getImage(tempbite);
    }
    private String getDisplayName(Player player) {
        return getString(R.string.player_display_name, player.getFirstName(),
                player.getLastInitial());
    }
    private void attachCategoryGridFragment() throws JSONException {

        rowItems = new ArrayList<>();
        for (int i = 0;i <3; i++){
            RowItem item = new RowItem (images[i],titles[i]);
            rowItems.add(item);
        }

        listView = (ListView) findViewById(R.id.list);
        mCategoryAdapter = new GeneralCategoryAdapter(this, rowItems);
        listView.setAdapter(mCategoryAdapter);
        listView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position==0) {
            startActivity(new Intent(this, TrailActivity.class));
        }
        if (position==1) {
            EventActivity.start(this, MyPlayer);

        }
        if (position==2) {
            CategorySelectionActivity.start(this, MyPlayer);

        }
    }



}
