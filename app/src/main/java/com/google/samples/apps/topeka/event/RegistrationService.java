package com.google.samples.apps.topeka.event;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;import java.lang.Exception;import java.lang.Override;import java.lang.String;

/**
 * Created by NguyenHai on 3/29/2016.
 */
public class RegistrationService extends IntentService {

    public RegistrationService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        _("onHandleIntent");
        InstanceID myID = InstanceID.getInstance(this);
        try {
            String registrationToken = myID.getToken(
                    "26507868442",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
            );
            GcmPubSub subscription = GcmPubSub.getInstance(this);
            subscription.subscribe(registrationToken, "/topics/my_little_topic", null);
            _(registrationToken);
        }catch(Exception e){
            Log.e("REGISTRATION ERROR", e.toString());
        }
    }
    public void _(String s){
        Log.i("MyApp", "RegistrationService" + "##########" + s);
    }
}
