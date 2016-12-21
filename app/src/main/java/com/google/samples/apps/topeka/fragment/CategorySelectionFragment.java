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
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.activity.QuizActivity;
import com.google.samples.apps.topeka.adapter.CategoryAdapter;
import com.google.samples.apps.topeka.helper.TransitionHelper;
import com.google.samples.apps.topeka.model.Category;

import org.json.JSONException;

public class CategorySelectionFragment extends Fragment {
    private static final int request_code = 5;
    private static final int RESULT_OK = -1;
    private CategoryAdapter mCategoryAdapter;
    private static final String TAG = "common222222";
    public static CategorySelectionFragment newInstance() {
        return new CategorySelectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        _("onCreateView2");
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        _("onViewCreated2");
        try {
            setUpQuizGrid((GridView) view.findViewById(R.id.categories));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        _("onViewCreated continute");
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpQuizGrid(GridView categoriesView) throws JSONException {
        _("setUpQuizGrid2");
        categoriesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = getActivity();
                startQuizActivityWithTransition(activity, view.findViewById(R.id.category_title),
                        mCategoryAdapter.getItem(position));
            }
        });
        mCategoryAdapter = new CategoryAdapter(getActivity());
        categoriesView.setAdapter(mCategoryAdapter);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent
            data) {

        _("onActivityResult2");
        if ((requestCode == request_code) && (resultCode == RESULT_OK)) {
            String returnString = data.getExtras().getString("returnData");
            _(returnString);
        }

    }
    @Override
    public void onResume() {
        _("onResume2");
        mCategoryAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void startQuizActivityWithTransition(Activity activity, View toolbar,
            Category category) {

        _("startQuizActivityWithTransition2");
        _("activity  "+activity.getClass().getSimpleName());
        _("category  "+String.valueOf(category));
        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        ActivityOptions sceneTransitionAnimation = ActivityOptions
                .makeSceneTransitionAnimation(activity, pairs);
/**
 * Helper class for building an options Bundle that can be used with
 * {@link android.content.Context#startActivity(android.content.Intent, android.os.Bundle)
 * Context.startActivity(Intent, Bundle)} and related methods.
 */
        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        activity.startActivity(QuizActivity.getStartIntent(activity, category), transitionBundle);//The description of the activity to start.
    }
    public void _(String s){
        Log.i("Inspiria", "CategorySelectionFragment"+"############"+s);
    }
}
