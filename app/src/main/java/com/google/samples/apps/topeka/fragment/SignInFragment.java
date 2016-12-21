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

package com.google.samples.apps.topeka.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.samples.apps.topeka.Database.DatabaseHelper;
import com.google.samples.apps.topeka.Database.DbBitmapUtility;
import com.google.samples.apps.topeka.ImageSupport.GetAlImages;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.activity.GeneralCategoryActivity;
import com.google.samples.apps.topeka.activity.JSONParser;
import com.google.samples.apps.topeka.adapter.AvatarAdapter;
import com.google.samples.apps.topeka.helper.NewPreferencesHelper;
import com.google.samples.apps.topeka.helper.SQLiteHandler;
import com.google.samples.apps.topeka.helper.TransitionHelper;
import com.google.samples.apps.topeka.model.Avatar;
import com.google.samples.apps.topeka.model.MyPlayer;
import com.google.samples.apps.topeka.widget.fab.DoneFab;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Enable selection of an {@link Avatar} and user name.
 */
public class SignInFragment extends Fragment {

private static final String TAG = "nguyenhai";
    private static final String ARG_EDIT = "EDIT";
    private static final String KEY_SELECTED_AVATAR_INDEX = "selectedAvatarIndex";
   // private Player mPlayer;
    private MyPlayer MyPlayer;
    private EditText mName;
    private EditText mEmail;
    private Avatar mSelectedAvatar = Avatar.ONE;
    private View mSelectedAvatarView;
    private GridView mAvatarGrid;
    private DoneFab mDoneFab;
    private boolean edit;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    String name;
    String email;
    String password;
    String avata;
    public Context context;// = this.getActivity();
    private ImageButton btnCapturePicture;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    Bitmap CaturedBitmap;
    ImageView test;
    public static final String GET_IMAGE_URL="http://oslophone.com/test/android_connect/testimage/getAllImages.php";

    public GetAlImages getAlImages;
    public static SignInFragment newInstance(boolean edit) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_EDIT, edit);
        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int savedAvatarIndex = savedInstanceState.getInt(KEY_SELECTED_AVATAR_INDEX);
            mSelectedAvatar = Avatar.values()[savedAvatarIndex];
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        contentView.addOnLayoutChangeListener(new View.
                OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                setUpGridView(getView());
            }
        });
        return contentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_AVATAR_INDEX, mSelectedAvatar.ordinal());
        super.onSaveInstanceState(outState);
    }
    View view2;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    /*Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned,
    but before any saved state has been restored in to the view.
    This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created.
    The fragment's view hierarchy is not however attached to its parent at this point.*/
    {
        view2 = view;
        test = (ImageView) view.findViewById(R.id.testView);


        assurePlayerInit();
        checkIsInEditMode();

        if (null == MyPlayer || edit) {
            view.findViewById(R.id.empty).setVisibility(View.GONE);
            view.findViewById(R.id.content).setVisibility(View.VISIBLE);
            initContentViews(view);
            initContents();
            Log.i(TAG,"null");
        } else {
            final Activity activity = getActivity();
            GeneralCategoryActivity.start(activity, MyPlayer);
            //CategorySelectionActivity.start(activity, mPlayer);
           // GeneralCategoryActivity.start(activity, mPlayer);
            activity.finish();
            Log.i(TAG, "else");
    }
       // imageView = (ImageView) view.findViewById(R.id.imageview);
        btnCapturePicture= (ImageButton) view.findViewById(R.id.btnCapturePicture);
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
    /**
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,
                CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                CaturedBitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);


            }
        }
    }

    public String getStringImage(Bitmap bmp){
        if (bmp==null) return "null";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void checkIsInEditMode() {
        final Bundle arguments = getArguments();
        //noinspection SimplifiableIfStatement
        if (null == arguments) {
            edit = false;
        } else {
            edit = arguments.getBoolean(ARG_EDIT, false);
        }
    }

    private void initContentViews(final View view) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* no-op */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // showing the floating action button if text is entered
                if (s.length() == 0) {
                    mDoneFab.setVisibility(View.GONE);
                } else {
                    mDoneFab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /* no-op */
            }
        };
// SQLite database handler
        db = new SQLiteHandler( getActivity());
        // Progress dialog
        pDialog = new ProgressDialog( getActivity());
        pDialog.setCancelable(false);
        final ImageView testView = (ImageView) view.findViewById(R.id.testView);
        mName = (EditText) view.findViewById(R.id.name);
        mName.addTextChangedListener(textWatcher);
        mEmail = (EditText) view.findViewById(R.id.email);
        mEmail.addTextChangedListener(textWatcher);
        mDoneFab = (DoneFab) view.findViewById(R.id.done);
        mDoneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.done:
                        name = mName.getText().toString().trim();
                        email = mEmail.getText().toString().trim();
                        password = name+email;
                        if (CaturedBitmap==null){
                            /*Drawable myDrawable = ContextCompat.getDrawable(getContext(), mSelectedAvatar.getDrawableId());
*/
//                            Drawable myDrawable = getResources().getDrawable( mSelectedAvatar.getDrawableId());
                            Drawable myDrawable = getResources().getDrawable( mSelectedAvatar.getDrawableId());
                            Bitmap APKicon;
                            if(myDrawable instanceof BitmapDrawable) {
                                APKicon  = ((BitmapDrawable)myDrawable).getBitmap();
                            }else{
                                Bitmap bitmap = Bitmap.createBitmap(myDrawable.getIntrinsicWidth(),myDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                myDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                                myDrawable.draw(canvas);
                                APKicon = bitmap;
                            }
                           // Bitmap myLogo = (Bitmap)((BitmapDrawable) myDrawable).getBitmap();
                            CaturedBitmap=APKicon;
                        }
                        // save player information to MySQL
                        new CreateNewProduct().execute();
                        // create player
                        MyPlayer = new MyPlayer(name, email);
                        // save player information to SQlite to retrive later in other activities
                        saveRePlayer_Macimage (name,email, CaturedBitmap );

                        savePlayer(getActivity());


                        break;
                    default:
                        throw new UnsupportedOperationException(
                                "The onClick method has not been implemented for " + getResources()
                                        .getResourceEntryName(v.getId()));
                }
            }
        });
    }
 private void saveRePlayer_Macimage (String name, String email, Bitmap image){
     DatabaseHelper myPlayerdb = new DatabaseHelper(getActivity());
     DbBitmapUtility contertByte = new DbBitmapUtility();
     byte[] imageTemp = contertByte.getBytes(image);
     myPlayerdb.addEntry(name, email, imageTemp);

     /*byte[] imageReturn;
     imageReturn=myPlayer.getImage(name);

     Bitmap testImage = contertByte.getImage(imageReturn);*/
     // test.setImageBitmap(testImage);

    // get images from MYSQL and save to sqlite
     //getURLs();

 }
    private void removeDoneFab(@Nullable Runnable endAction) {
        mDoneFab.animate()
                .scaleX(0)
                .scaleY(0)
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(endAction)
                .start();
    }

    private void setUpGridView(View container) {
        mAvatarGrid = (GridView) container.findViewById(R.id.avatars);
        mAvatarGrid.setAdapter(new AvatarAdapter(getActivity()));
        mAvatarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedAvatarView = view;
                mSelectedAvatar = Avatar.values()[position];
            }
        });
        mAvatarGrid.setNumColumns(calculateSpanCount());
        mAvatarGrid.setItemChecked(mSelectedAvatar.ordinal(), true);
    }


    private void performSignInWithTransition(View v) {
        final Activity activity = getActivity();

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, true,
                new Pair<>(v, activity.getString(R.string.transition_avatar)));
        ActivityOptions activityOptions = ActivityOptions
                .makeSceneTransitionAnimation(activity, pairs);
        //CategorySelectionActivity.start(activity, mPlayer, activityOptions);
        GeneralCategoryActivity.start(activity, MyPlayer);
        Log.i(TAG, "performsignInwithTransition");
    }

    private void initContents() {
        assurePlayerInit();
        if (null != MyPlayer) {
            mName.setText(MyPlayer.getName());
            mEmail.setText(MyPlayer.getEmail());
        }
    }

    private void assurePlayerInit() {
        if (null == MyPlayer) {
            MyPlayer = NewPreferencesHelper.getPlayer(getActivity());
        }
    }

    private void savePlayer(Activity activity) {
        /*mPlayer = new Player(mName.getText().toString(), mEmail.getText().toString(),
                mSelectedAvatar);*/
        MyPlayer = new MyPlayer(mName.getText().toString(), mEmail.getText().toString());
        NewPreferencesHelper.writeToPreferences(activity, MyPlayer);
    }

    /**
     * Calculates spans for avatars dynamically.
     *
     * @return The recommended amount of columns.
     */
    private int calculateSpanCount() {
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.size_fab);
        int avatarPadding = getResources().getDimensionPixelSize(R.dimen.spacing_double);
        return mAvatarGrid.getWidth() / (avatarSize + avatarPadding);
    }
    private void _(String s){
        Log.d("MyApp", "LoginActivity" + s);
    }

    class CreateNewProduct extends AsyncTask<String, String, String> {
        private Dialog createProduct;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createProduct= ProgressDialog.show(getActivity(),"Please Wait", "Loading");
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("unique_id", name));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("encrypted_password", password));
            params.add(new BasicNameValuePair("salt", email));
            params.add(new BasicNameValuePair("created_at", email));
            params.add(new BasicNameValuePair("updated_at", email));
            params.add(new BasicNameValuePair("image", getStringImage(CaturedBitmap)));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = JSONParser.makeHttpRequest("http://oslophone.com/test/android_connect/create_product.php",
                    "POST", params);

            // check log cat fro response
            Log.d("Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt("success");
                if (success == 3) {
                    return "exist";

                }
                else if (success == 1) {
                    return "success";

                } else {
                    return "fail";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            String s = file_url.trim();
            if (s=="exist"){
                Toast.makeText(getActivity(),"Users are existed !", Toast.LENGTH_LONG).show();
            }
            else if (s== "success"){
                Toast.makeText(getActivity(),"successfully !", Toast.LENGTH_LONG).show();// successfully created product
                if (null == mSelectedAvatarView) {
                            performSignInWithTransition(mAvatarGrid.getChildAt(
                                    mSelectedAvatar.ordinal()));
                        } else {
                            performSignInWithTransition(mSelectedAvatarView);
                        }
                    }
            else if (s== "fail"){
                Toast.makeText(getActivity(),"Fail !", Toast.LENGTH_LONG).show();// successfully created product
            }

        }

    }
    private void getImages(){
        class GetImages extends AsyncTask<Void,Void,Void>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);
                DatabaseHelper Image_Mac = new DatabaseHelper(getActivity());
                Image_Mac.addImageMac(GetAlImages.macs, GetAlImages.bitmaps);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    getAlImages.getAllImages_Mac();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        GetImages getImages = new GetImages();
        getImages.execute();
    }

    private void getURLs() {
        class GetURLs extends AsyncTask<String,Void,String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getActivity(),"Loading...","Please Wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                getAlImages = new GetAlImages(s);
                getImages();
            }

            @Override
            protected String doInBackground(String... strings) {
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }
            }
        }
        GetURLs gu = new GetURLs();
        gu.execute(GET_IMAGE_URL);
    }



}
