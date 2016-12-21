package com.google.samples.apps.topeka.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.samples.apps.topeka.Database.DatabaseHelper;
import com.google.samples.apps.topeka.Database.DbBitmapUtility;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.model.MyPlayer;
//import com.google.samples.apps.topeka.model.Player;
import com.google.samples.apps.topeka.widget.AvatarView;

public class UserProfile extends Activity {
    private static final String EXTRA_PLAYER = "player";
    //public Player player;
    public ImageView cimg;
    public static void start(Context context, MyPlayer MyPlayer) {
        Intent starter = new Intent(context, UserProfile.class);
        starter.putExtra(EXTRA_PLAYER, MyPlayer);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        //setUpToolbar();

        final MyPlayer MyPlayer = getIntent().getParcelableExtra(EXTRA_PLAYER);
        //getDrawable(player.getAvatar().getDrawableId());

        ImageButton follow = (ImageButton) findViewById(R.id.followbuton);
        cimg = (ImageView) findViewById(R.id.userview); // in your onCreate
        cimg.setImageBitmap(getAvatar(MyPlayer));
        final TextView username = (TextView) findViewById(R.id.username);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cimg.setImageResource(player.getAvatar().getDrawableId());
                username.setText(MyPlayer.getName());
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);

            }
        });
    }
    private Bitmap getAvatar(MyPlayer Player){
        DatabaseHelper newdb = new DatabaseHelper(this);
        byte[] tempbite = newdb.getImage(Player.getName());
        return new DbBitmapUtility().getImage(tempbite);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap =BitmapFactory.decodeFile(picturePath);
            /*ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/
            Bitmap cropedBitmap = getCroppedBitmap(bitmap);
            cimg.setImageBitmap(cropedBitmap);
        }
    }
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
/*
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_player_userprofile);
        setActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_userprofile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.camera) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


}
