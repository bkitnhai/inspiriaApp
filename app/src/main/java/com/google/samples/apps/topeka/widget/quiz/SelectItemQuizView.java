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
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.adapter.OptionsQuizAdapter;
import com.google.samples.apps.topeka.helper.AnswerHelper;
import com.google.samples.apps.topeka.model.Category;
import com.google.samples.apps.topeka.model.quiz.SelectItemQuiz;

@SuppressLint("ViewConstructor")
public class SelectItemQuizView extends AbsQuizView<SelectItemQuiz> {

    private static final String KEY_ANSWERS = "ANSWERS";

    private boolean[] mAnswers;
    private ListView mListView;
    public int mac;
    public SelectItemQuizView(Context context, Category category, SelectItemQuiz quiz,int mac) {
        super(context, category, quiz,  mac);
        this.mac = mac;
        mAnswers = getAnswers();
        _("SelectItemQuizView");
    }
    Bitmap[] bitmaps;
    @Override
    protected View createQuizContentView() {
        mListView = new ListView(getContext());
        mListView.setDivider(null);
        mListView.setSelector(R.drawable.selector_button);
        Log.i("macbefore2", String.valueOf(mac));
        mListView.setAdapter(
                new OptionsQuizAdapter(getQuiz().getOptions(), mac, R.layout.item_answer_start,
                        getContext(), true));
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                allowAnswer();
                toggleAnswerFor(position);
            }
        });
        return mListView;
    }

    @Override
    protected boolean isAnswerCorrect() {
        final SparseBooleanArray checkedItemPositions = mListView.getCheckedItemPositions();
        final int[] answer = getQuiz().getAnswer();
        return AnswerHelper.isAnswerCorrect(checkedItemPositions, answer);
    }

    @Override
    public Bundle getUserInput() {
        Bundle bundle = new Bundle();
        bundle.putBooleanArray(KEY_ANSWERS, mAnswers);
        return bundle;
    }

    @Override
    public void setUserInput(Bundle savedInput) {
        if (savedInput == null) {
            return;
        }
        mAnswers = savedInput.getBooleanArray(KEY_ANSWERS);
        if (mAnswers == null) {
            return;
        }
        final ListAdapter adapter = mListView.getAdapter();
        for (int i = 0; i < mAnswers.length; i++) {
            mListView.performItemClick(mListView.getChildAt(i), i, adapter.getItemId(i));
        }
    }

    private void toggleAnswerFor(int answerId) {
        getAnswers()[answerId] = !mAnswers[answerId];
    }

    private boolean[] getAnswers() {
        if (null == mAnswers) {
            mAnswers = new boolean[getQuiz().getOptions().length];
        }
        return mAnswers;
    }
    public void _(String s){
        Log.i("MyApp", "SelectItemQuizView" + "#######" + s);
    }
}
