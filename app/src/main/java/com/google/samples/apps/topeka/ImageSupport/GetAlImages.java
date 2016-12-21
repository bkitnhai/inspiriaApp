package com.google.samples.apps.topeka.ImageSupport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Belal on 9/19/2015.
 */
public class GetAlImages {

    public static String[] imageURLs;
    public static Bitmap[] bitmaps;
    public static String[] macs;
    public static final String JSON_ARRAY="result";
    public static final String IMAGE_URL = "url";
    public static final String MAC = "mac";
    private String json;
    private JSONArray urls_mac;

    public GetAlImages(String json){
        _("GetAlImages");
        this.json = json;
        try {
            JSONObject jsonObject = new JSONObject(json);
            urls_mac = jsonObject.getJSONArray(JSON_ARRAY);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImage(JSONObject jo){
        _("getImage");
        URL url = null;
        Bitmap image = null;
        try {
            url = new URL(jo.getString(IMAGE_URL));
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return image;
    }
    private String getMac(JSONObject jo){
        _("getMac");
        String mac = null;
        try {
            mac = jo.getString(MAC);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mac;
    }
    public void getAllImages_Mac() throws JSONException {
        _("getAllImages_Mac");
        bitmaps = new Bitmap[urls_mac.length()];

        imageURLs = new String[urls_mac.length()];

        macs = new String[urls_mac.length()];
        for(int i=0;i<urls_mac.length();i++){
            imageURLs[i] = urls_mac.getJSONObject(i).getString(IMAGE_URL);
            JSONObject jsonObject = urls_mac.getJSONObject(i);
            bitmaps[i]=getImage(jsonObject);

            macs[i]=getMac(jsonObject);
        }


    }
    public void _(String s){
        Log.i("test", "GetAlImages"+"#####"+s);
    }
}
