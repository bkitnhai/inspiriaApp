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
package com.google.samples.apps.topeka.widget.quiz;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.samples.apps.topeka.Database.DatabaseHelper;
import com.google.samples.apps.topeka.Database.DbBitmapUtility;
import com.google.samples.apps.topeka.ImageSupport.GetAlImages;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.adapter.OptionsQuizAdapter;
import com.google.samples.apps.topeka.model.Category;
import com.google.samples.apps.topeka.model.MyPlayer;
import com.google.samples.apps.topeka.model.quiz.FourQuarterQuiz;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class FourQuarterQuizView extends AbsQuizView<FourQuarterQuiz> {

    private static final String KEY_ANSWER = "ANSWER";
    private int mAnswered = -1;
    private GridView mAnswerView;
    public int mac;

    public FourQuarterQuizView(Context context, Category category, FourQuarterQuiz quiz, int mac) {

        super(context, category, quiz, mac);
        this.mac = mac;
        _("FourQuarterQuizView::::mac"+this.mac);
    }

    @Override
    protected View createQuizContentView() {
        _("createQuizContentView");
        _("FourQuarterQuizView::::mac1111"+this.mac);
        mAnswerView = new GridView(getContext());
        mAnswerView.setSelector(R.drawable.selector_button);
        mAnswerView.setNumColumns(2);
        mAnswerView.setAdapter(new OptionsQuizAdapter(getQuiz().getOptions(), macTemp,//Sets the data behind this GridView mAnswerView.
                R.layout.item_answer,getContext(),true));
        mAnswerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                allowAnswer();
                mAnswered = position;
            }
        });
        return mAnswerView;
    }
    @Override
    public Bundle getUserInput() {
        _("getUserInput");
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ANSWER, mAnswered);
        return bundle;
    }

    @Override
    public void setUserInput(Bundle savedInput) {
        _("setUserInput");
        if (savedInput == null) {
            return;
        }
        mAnswered = savedInput.getInt(KEY_ANSWER);
        if (mAnswered != -1) {
            if (isLaidOut()) {
                setUpUserInput();
            } else {
                addOnLayoutChangeListener(new OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                            int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        v.removeOnLayoutChangeListener(this);
                        setUpUserInput();
                    }
                });
            }
        }
    }

    private void setUpUserInput() {
        _("setUpUserInput");
                mAnswerView.performItemClick(mAnswerView.getChildAt(mAnswered), mAnswered,
                        mAnswerView.getAdapter().getItemId(mAnswered));
        mAnswerView.getChildAt(mAnswered).setSelected(true);
        mAnswerView.setSelection(mAnswered);
    }

    @Override
    protected boolean isAnswerCorrect() {
        return getQuiz().isAnswerCorrect(new int[]{mAnswered});
    }



    public void _(String s){
        Log.i("MyApp", "FourQuarterQuizView" + "#######" + s);
    }
}
