package com.google.samples.apps.topeka.trail;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.google.samples.apps.topeka.AR.AR;
import com.google.samples.apps.topeka.R;
import com.jiahuan.svgmapview.SVGMapView;
import com.jiahuan.svgmapview.SVGMapViewListener;
import com.jiahuan.svgmapview.core.data.SVGPicture;
import com.jiahuan.svgmapview.core.helper.ImageHelper;
import com.jiahuan.svgmapview.core.helper.map.SVGBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



public class TrailActivity extends ActionBarActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    private BeaconManager beaconManager;
    private Region region;
    public int redNum = 2;
    private SVGMapView mapView;
    Activity temp;
    private static final Map<String, List<String>> PLACES_BY_BEACONS;

    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("40145:13584", new ArrayList<String>() {{
            add("num1");

        }});
        placesByBeacons.put("45443:20699", new ArrayList<String>() {{
            add("num2");
        }});
        placesByBeacons.put("58404:41472", new ArrayList<String>() {{
            add("num3");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    BitmapOverlay locationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail);
        temp = this;
        mapView = (SVGMapView) findViewById(R.id.basic_mapview);
        NewMap(mapView,locationOverlay,redNum);

        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                {
                    if (!list.isEmpty()) {
                        Beacon nearestBeacon = list.get(0);
                        List<String> places = placesNearBeacon(nearestBeacon);
                        String sTemp = String.valueOf(places);
                        // TODO: update the UI here
                        if (sTemp.equals("[num1]")) {
                            if (redNum!=1) {
                                redNum=1;
                                NewMap(mapView,locationOverlay,redNum);
                                new AlertDialog.Builder(temp)
                                        .setTitle("You are near artefact 1")
                                        .setMessage("Do you want to know what is this ?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(temp, InforTab.class);
                                                intent.putExtra("id", 1);
                                                temp.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .show();
                            }
                        }
                        else if (sTemp.equals("[num2]")) {
                            if (redNum!=2) {
                                redNum=2;
                                NewMap(mapView,locationOverlay,redNum);
                                NewMap(mapView,locationOverlay,redNum);
                                new AlertDialog.Builder(temp)
                                        .setTitle("You are near artefact 2")
                                        .setMessage("Do you want to know what is this ?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(temp, InforTab.class);
                                                intent.putExtra("id", 2);
                                                temp.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .show();
                                }
                                /*NewMap(mapView,locationOverlay,redNum);*/
                        }
                        else if (sTemp.equals("[num3]")) {
                            if (redNum!=3) {
                                redNum=3;
                                NewMap(mapView,locationOverlay,redNum);
                                NewMap(mapView,locationOverlay,redNum);
                                new AlertDialog.Builder(temp)
                                        .setTitle("You are near artefact 3")
                                        .setMessage("Do you want to know what is this ?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(temp, InforTab.class);
                                                intent.putExtra("id", 1);
                                                temp.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .show();
                            }
                        }
                        mapView.refresh();
                        Log.d("Airport", "Nearest places: " + places);
                        Log.d("Airport", "num : " + redNum);
                    }
                }
            }

        });
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

    }

    public void NewMap (final SVGMapView mapView, final BitmapOverlay locationOverlay, final int redNum)   {
        mapView.getOverLays().clear();

        mapView.registerMapViewListener(new SVGMapViewListener() {
            BitmapOverlay locationOverlayTemp = locationOverlay;
            @Override
            public void onMapLoadComplete() {
                locationOverlayTemp = new BitmapOverlay(mapView,temp, redNum);
                mapView.getOverLays().add(locationOverlayTemp);
                mapView.refresh();

            }

            @Override
            public void onMapLoadError() {

            }

            @Override
            public void onGetCurrentMap(Bitmap bitmap) {

            }
        });
         mapView.setBrandBitmap(ImageHelper.drawableToBitmap(new SVGBuilder().readFromString(SVGPicture.ICON_TOILET).build().getDrawable(), 1.0f));
        mapView.loadMap(AssetsHelper.getContent(this, "test.svg"));
    }
     public void DoIt (){
         Intent extra = new Intent(this, AR.class);
         startActivity(extra);
     }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void DoIt_Trail(View v) {
        Intent extra = new Intent(this, AR.class);
        startActivity(extra);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();


    }
}

