package com.google.samples.apps.topeka.trail.Fragment;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.app.AppController;
import com.google.samples.apps.topeka.trail.ImageTab.GalleryAdapter;
import com.google.samples.apps.topeka.trail.ImageTab.Image;
import com.google.samples.apps.topeka.trail.ImageTab.SlideshowDialogFragment;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class PageFragment extends Fragment implements View.OnClickListener  {
    public static final String ARG_PAGE = "ARG_PAGE";
    Activity tempActivity;
    private int mPage;
    private static  String endpoint = "http://nguyenhainorway.netau.net/imageTestGlide/glide.json";
    private static final String endpoint1 = "http://oslophone.com/test/JSON/id1.json";
    private static final String endpoint2 = "http://oslophone.com/test/JSON/id2.json";
    private static final String endpoint3 = "http://oslophone.com/test/JSON/id3.json";
    private ArrayList<Image> images;
   /* private ProgressDialog pDialog;*/
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private String TAG = "xxx" ;
    public  int id;
    private VideoPlayerView mVideoPlayer_1;
    private AssetFileDescriptor mVideoFileDecriptor_sample_1;
    private ImageView mVideoCover;
    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });
    public static PageFragment newInstance(int page,int id ) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt("id", id);

        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tempActivity=getActivity();
        mPage = getArguments().getInt(ARG_PAGE);
        id = getArguments().getInt("id");
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_page, container, false);
        View view;
        if (mPage==1) {
           /* startActivity(new Intent(tempActivity,InforImageTab.class));*/
            view = inflater.inflate(R.layout.layout_for_image, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            /*pDialog = new ProgressDialog(getActivity());*/
            images = new ArrayList<>();
            mAdapter = new GalleryAdapter(getActivity(), images);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getActivity(), recyclerView, new GalleryAdapter.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", images);
                    bundle.putInt("position", position);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            fetchImages();
        }
        else {
            view = inflater.inflate(R.layout.video_player_manager_fragment, container, false);
            try {

                mVideoFileDecriptor_sample_1 = getActivity().getAssets().openFd("video_sample_1.mp4");

            } catch (IOException e) {
                e.printStackTrace();
            }
            mVideoPlayer_1 = (VideoPlayerView)view.findViewById(R.id.video_player_1);
            mVideoPlayer_1.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
                @Override
                public void onVideoPreparedMainThread() {
                    // We hide the cover when video is prepared. Playback is about to start
                    mVideoCover.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoStoppedMainThread() {
                    // We show the cover when video is stopped
                    mVideoCover.setVisibility(View.VISIBLE);
                }

                @Override
                public void onVideoCompletionMainThread() {
                    // We show the cover when video is completed
                    mVideoCover.setVisibility(View.VISIBLE);
                }
            });
            mVideoCover = (ImageView)view.findViewById(R.id.video_cover_1);

            mVideoCover.setOnClickListener(this);
        }
        /*TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("Fragment #" + mPage);*/
        return view;
    }
    private void fetchImages() {

        /*pDialog.setMessage("Downloading json...");
        pDialog.show();*/
        Log.i("checkpoint", String.valueOf(id));
        if (id == 1) endpoint = endpoint1;
        else if (id ==2) endpoint = endpoint2;
        else endpoint = endpoint3;
        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                      /*  pDialog.hide();*/

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));

                                JSONObject url = object.getJSONObject("url");
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
                                image.setTimestamp(object.getString("timestamp"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                /*pDialog.hide();*/
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onClick(View v) {
        mVideoPlayerManager.playNewVideo(null, mVideoPlayer_1, mVideoFileDecriptor_sample_1);
    }
    @Override
    public void onStop() {
        super.onStop();
       /* // in case we exited screen in playback
        mVideoCover.setVisibility(View.VISIBLE);*/

        mVideoPlayerManager.stopAnyPlayback();
    }
}
