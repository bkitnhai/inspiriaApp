package com.google.samples.apps.topeka.event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.samples.apps.topeka.event.EventActivity.ResponseReceiver;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by NguyenHai on 3/29/2016.
 */
public class NotificationsListenerService extends GcmListenerService {
    public static final String PARAM_OUT_MSG = "omsg";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        _("onMessageReceived");
        //String body = data.getString("to");
        Bundle notification = data.getBundle("notification");
        String body = notification.getString("body");

      _("from: " + from);
        _("body: " + body);

        sendNotification(body);
    }
    private void sendNotification(String message) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, message);
        sendBroadcast(broadcastIntent);

    }

    public void _(String s){
        Log.i("MyApp", "NotificationsListenerService" + "##########" + s);
    }

}