package com.google.samples.apps.topeka.event;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.samples.apps.topeka.AR.AR;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.activity.JSONParser;
import com.google.samples.apps.topeka.activity.QuizActivity;
import com.google.samples.apps.topeka.model.Category;
import com.google.samples.apps.topeka.model.MyPlayer;
import com.google.samples.apps.topeka.persistence.TopekaDatabaseHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
//import com.google.samples.apps.topeka.model.Player;

public class EventActivity extends Activity implements View.OnClickListener{
    private static final String EXTRA_PLAYER = "player";
   // public Player player;
    private List<Category> mCategories;
    private EditText code;
    private Context context_temp ;
    private ResponseReceiver receiver;
    private boolean Waiting = false;
    private ShowcaseView showcaseView;
    private int counter = 0;
    public static void start(Context context, MyPlayer MyPlayer) {
        Intent starter = new Intent(context, EventActivity.class);
        starter.putExtra(EXTRA_PLAYER, MyPlayer);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _("onCreate");
        context_temp=this;
        setContentView(R.layout.activity_event);
        code =(EditText) findViewById(R.id.code);
        try {
            mCategories = TopekaDatabaseHelper.getCategories(this, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        Intent i = new Intent(this, RegistrationService.class);
        startService(i);
 /*       For showcaseview*/
        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.AR)))
                .setStyle(R.style.CustomShowcaseTheme2)
                .singleShot(42)
                .setOnClickListener(this)
                .build();
        showcaseView.setContentTitle("Go To Stage");
        showcaseView.setContentText("You can point your camera into marks on the floor to see direction to main stage");

        showcaseView.setButtonText("Next");

    }
    private void setAlpha(float alpha, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            for (View view : views) {
                view.setAlpha(alpha);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (counter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.code)), true);
                showcaseView.setContentTitle("Event Code");
                showcaseView.setContentText("You can use code provided host who is staying at main stage.");
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.go)), true);
                showcaseView.setContentTitle("Enter");
                showcaseView.setContentText("Enter to Event Quiz");
                break;

           /* case 2:
                showcaseView.setTarget(Target.NONE);
                showcaseView.setContentTitle("Check it out");
                showcaseView.setContentText("You don't always need a target to showcase");
                showcaseView.setButtonText("Close");
                setAlpha(0.4f, textView1, textView2, textView3);
                break;*/

            case 2:
                showcaseView.hide();
                setAlpha(1.0f, findViewById(R.id.code), findViewById(R.id.go));
                break;
        }
        counter++;
    }
    public void goClick (View target){
         new check_code_db().execute();
    }
    public void Doit (View v){
        Intent extra = new Intent(this, AR.class);
        startActivity(extra);
    }


    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "com.example.bkitn.nguyenhai.intent.action.MESSAGE_PROCESSED";
        @Override
        public void onReceive(Context context, Intent intent) {

            _(intent.getStringExtra(NotificationsListenerService.PARAM_OUT_MSG));
            if (Waiting) {
                Intent starter = new Intent(context_temp, QuizActivity.class);
                starter.putExtra("Category", "energil");
                startActivity(starter);
            }
            // Update UI, new "message" processed by SimpleIntentService
            /*TextView result = (TextView) findViewById(R.id.txt_result);
            String text = intent.getStringExtra(NotificationsListenerService.PARAM_OUT_MSG);
            result.setText(text);*/
        }

    }
   class check_code_db extends AsyncTask<String, String, String> {
        private Dialog createProduct;
        private String code_temp;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            code_temp=code.getText().toString();
            createProduct= ProgressDialog.show(context_temp, "Please Wait", "Checking Code !");
        }
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("code", code_temp));
            JSONObject json = JSONParser.makeHttpRequest("http://oslophone.com/test/android_connect/check_code.php",
                    "POST", params);
            try {
                int success = json.getInt("success");
                if (success == 1) {
                    return "success";

                } else {
                    return "fail";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String file_url) {
            String s = file_url.trim();
            createProduct.dismiss();
            if (s== "success"){
                Waiting=true;
                Toast.makeText(context_temp,"successfully !", Toast.LENGTH_LONG).show();// successfully created product

            }
            else if (s== "fail"){
                Waiting = false;
                Toast.makeText(context_temp,"Fail !", Toast.LENGTH_LONG).show();// successfully created product
            }

        }

    }
    public void _(String s){
        Log.i("Inspiria", "EventActivity" + "##########" + s);
    }
}
