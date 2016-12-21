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

package com.google.samples.apps.topeka.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.fragment.QuizFragment;
import com.google.samples.apps.topeka.model.Category;
import com.google.samples.apps.topeka.persistence.TopekaDatabaseHelper;
import com.google.samples.apps.topeka.widget.fab.FloatingActionButton;

import org.json.JSONException;

import static com.google.samples.apps.topeka.adapter.CategoryAdapter.DRAWABLE;

public class QuizActivity extends Activity {

    private static final String TAG = "QuizTask";
    private static final String IMAGE_CATEGORY = "image_category_";
    private static final String STATE_IS_PLAYING = "isPlaying";
    private static final int UNDEFINED = -1;
    private static final String FRAGMENT_TAG = "Quiz";
    private Interpolator mInterpolator;
    private String mCategoryId;
    private QuizFragment mQuizFragment;
    private Toolbar mToolbar;
    private ImageView mQuizFab;
    private boolean mSavedStateIsPlaying;
    private ImageView mIcon;
    private Animator mCircularReveal;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            _("OnClickListener 1" + v.getId());
            switch (v.getId()) {
                case R.id.fab_quiz:
                    startQuizFromClickOn(v);
                    break;
                case R.id.submitAnswer:
                    submitAnswer();
                    break;
                case R.id.quiz_done:
                    finishAfterTransition();
                    break;
                case UNDEFINED:
                    final CharSequence contentDescription = v.getContentDescription();
                    if (contentDescription != null && contentDescription.equals(
                            getString(R.string.up))) {
                        onBackPressed();
                        break;
                    }
                default:
                    throw new UnsupportedOperationException(
                            "OnClick has not been implemented for " + getResources().
                                    getResourceName(v.getId()));
            }
        }
    };

    public static Intent getStartIntent(Context context, Category category) {

        Intent starter = new Intent(context, QuizActivity.class);
        starter.putExtra(Category.TAG, category.getId());
        Log.i("Inspiria", "QuizActivity" + "category.getId()" + category.getId());
        Log.i("Inspiria", "QuizActivity" + "Category.TAG" + Category.TAG);

        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _("onCreate 1");
        // Inflate and set the enter transition for this activity.
        final Transition sharedElementEnterTransition = TransitionInflater.from(this)
                .inflateTransition(R.transition.quiz_enter);
        getWindow().setSharedElementEnterTransition(sharedElementEnterTransition);

        mCategoryId = getIntent().getStringExtra(Category.TAG);
        mInterpolator = AnimationUtils.loadInterpolator(this,
                android.R.interpolator.fast_out_slow_in);
        if (null != savedInstanceState) {
            mSavedStateIsPlaying = savedInstanceState.getBoolean(STATE_IS_PLAYING);
        }
        try {
            populate(mCategoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        _("onResume 1");
        if (mSavedStateIsPlaying) {
            mQuizFragment = (QuizFragment) getFragmentManager().findFragmentByTag(
                    FRAGMENT_TAG);
            findViewById(R.id.quiz_fragment_container).setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        _("onSaveInstanceState 1");
        outState.putBoolean(STATE_IS_PLAYING, mQuizFab.getVisibility() == View.GONE);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        _("onBackPressed 1");
        if (mIcon == null || mQuizFab == null) {
            // Skip the animation if icon or fab are not initialized.
            super.onBackPressed();
            return;
        }

        // Scale the icon and fab to 0 size before calling onBackPressed if it exists.
        mIcon.animate()
                .scaleX(.7f)
                .scaleY(.7f)
                .alpha(0f)
                .setInterpolator(mInterpolator)
                .start();

        mQuizFab.animate()
                .scaleX(0f)
                .scaleY(0f)
                .setInterpolator(mInterpolator)
                .setStartDelay(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        QuizActivity.super.onBackPressed();
                    }
                })
                .start();
    }

    private void startQuizFromClickOn(final View view) {
        _("startQuizFromClickOn 1");
        initQuizFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.quiz_fragment_container, mQuizFragment, FRAGMENT_TAG).commit();
        final View fragmentContainer = findViewById(R.id.quiz_fragment_container);
        /*int centerX = (view.getLeft() + view.getRight()) / 2;
        int centerY = (view.getTop() + view.getBottom()) / 2;
        int finalRadius = Math.max(fragmentContainer.getWidth(), fragmentContainer.getHeight());
        mCircularReveal = ViewAnimationUtils.createCircularReveal(
                fragmentContainer, centerX, centerY, 0, finalRadius);*/
        fragmentContainer.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);

      /*  mCircularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIcon.setVisibility(View.GONE);
                mCircularReveal.removeListener(this);
            }
        });

        mCircularReveal.start();*/

        // the toolbar should not have more elevation than the content while playing
        mToolbar.setElevation(0);
    }

    public void elevateToolbar() {
        _("elevateToolbar 1");
        mToolbar.setElevation(getResources().getDimension(R.dimen.elevation_header));
    }

    private void initQuizFragment() {
        _("initQuizFragment 1");
        mQuizFragment = QuizFragment.newInstance(mCategoryId,
                new QuizFragment.SolvedStateListener() {
                    @Override
                    public void onCategorySolved() {
                        elevateToolbar();
                        displayDoneFab();
                    }

                    private void displayDoneFab() {
                        /* We're re-using the already existing fab and give it some
                         * new values. This has to run delayed due to the queued animation
                         * to hide the fab initially.
                         */
                        if (null != mCircularReveal && mCircularReveal.isRunning()) {
                            mCircularReveal.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    showQuizFabWithDoneIcon();
                                    mCircularReveal.removeListener(this);
                                }
                            });
                        } else {
                            showQuizFabWithDoneIcon();
                        }
                    }

                    private void showQuizFabWithDoneIcon() {
                        mQuizFab.setImageResource(R.drawable.ic_tick);
                        mQuizFab.setId(R.id.quiz_done);
                        mQuizFab.setVisibility(View.VISIBLE);
                        mQuizFab.setScaleX(0f);
                        mQuizFab.setScaleY(0f);
                        mQuizFab.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setInterpolator(mInterpolator)
                                .setListener(null)
                                .start();
                    }
                });
        // the toolbar should not have more elevation than the content while playing
        mToolbar.setElevation(0);
    }

    /**
     * Proceeds the quiz to it's next state.
     */
    public void proceed() {


        submitAnswer();
    }

    private void submitAnswer() {
        _("submitAnswer 1");
        elevateToolbar();
        if (!mQuizFragment.showNextPage()) {
            mQuizFragment.showSummary();
            return;
        }
        mToolbar.setElevation(0);
    }

    private void populate(String categoryId) throws JSONException {
        _("populate 1");
        if (null == categoryId) {
            Log.w(TAG, "Didn't find a category. Finishing");
            finish();
        }
        _(categoryId);
        Category category = TopekaDatabaseHelper.getCategoryWith(this, categoryId);//categoryId can be sport, history, ...
        _("populate 1" + category.getId());
        setTheme(category.getTheme().getStyleId());
        initLayout(category.getId());
        initToolbar(category);
    }

    private void initLayout(String categoryId) {
        _("initLayout 1");
        setContentView(R.layout.activity_quiz);
        mIcon = (ImageView) findViewById(R.id.icon);
        int resId = getResources().getIdentifier(IMAGE_CATEGORY + categoryId, DRAWABLE,
                getApplicationContext().getPackageName());
        mIcon.setImageResource(resId);
        mIcon.setImageResource(resId);
        mIcon.animate()
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setInterpolator(mInterpolator)
                .setStartDelay(300)
                .start();
        mQuizFab = (FloatingActionButton) findViewById(R.id.fab_quiz);
        mQuizFab.setImageResource(R.drawable.ic_play);
        mQuizFab.setVisibility(mSavedStateIsPlaying ? View.GONE : View.VISIBLE);
        mQuizFab.setOnClickListener(mOnClickListener);
        mQuizFab.animate()
                .scaleX(1)
                .scaleY(1)
                .setInterpolator(mInterpolator)
                .setStartDelay(400)
                .start();
    }

    private void initToolbar(Category category) {
        _("initToolbar 1");
        mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_quiz);
        mToolbar.setTitle(category.getName());
        mToolbar.setNavigationOnClickListener(mOnClickListener);
        if (mSavedStateIsPlaying) {
            // the toolbar should not have more elevation than the content while playing
            mToolbar.setElevation(0);
        }
        startQuizFromClickOn(mQuizFab);
    }
    public void _(String s){
        Log.i("Inspiria", "QuizActivity" + "#######" + s);
    }
}
