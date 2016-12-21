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

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewAnimator;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.samples.apps.topeka.Database.DatabaseHelper;
import com.google.samples.apps.topeka.Database.DbBitmapUtility;
import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.activity.JSONParser;
import com.google.samples.apps.topeka.adapter.QuizAdapter;
import com.google.samples.apps.topeka.adapter.ScoreAdapter;
import com.google.samples.apps.topeka.helper.ApiLevelHelper;
import com.google.samples.apps.topeka.helper.NewPreferencesHelper;
import com.google.samples.apps.topeka.model.Category;
import com.google.samples.apps.topeka.model.MyPlayer;
import com.google.samples.apps.topeka.model.Theme;
import com.google.samples.apps.topeka.model.quiz.Quiz;
import com.google.samples.apps.topeka.persistence.TopekaDatabaseHelper;
import com.google.samples.apps.topeka.widget.AvatarView;
import com.google.samples.apps.topeka.widget.quiz.AbsQuizView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import com.google.samples.apps.topeka.model.Player;

/**
 * Encapsulates Quiz solving and displays it to the user.
 */
public class QuizFragment extends Fragment {
    private static final int request_code = 5;
    private static final int RESULT_OK = -1;
    private static List<String> mac = null;
    private static final String KEY_USER_INPUT = "USER_INPUT";
    private TextView mProgressText;
    private int mQuizSize;
    private ProgressBar mProgressBar;
    private Category mCategory;
    private AdapterViewAnimator mQuizView;
    private ScoreAdapter mScoreAdapter;
    private QuizAdapter mQuizAdapter;
    private SolvedStateListener mSolvedStateListener;
    private static final String TAG = "QuizTask";
    MyPlayer MyPlayer;
    private String updateScore;
    private String userEmail;

    public static QuizFragment newInstance(String categoryId,
            SolvedStateListener solvedStateListener) {
        Log.i("MyApp", "QuizFragment" + "#######" + "newInstance");
        if (categoryId == null) {
            throw new IllegalArgumentException("The category can not be null");
        }
        Bundle args = new Bundle();
        args.putString(Category.TAG, categoryId);
        QuizFragment fragment = new QuizFragment();
        if (solvedStateListener != null) {
            fragment.mSolvedStateListener = solvedStateListener;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        _("onCreate");
        String categoryId = getArguments().getString(Category.TAG);
        try {
            mCategory = TopekaDatabaseHelper.getCategoryWith(getActivity(), categoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        _("onCreateView");
        // Create a themed Context and custom LayoutInflater
        // to get nicely themed views in this Fragment.
        final Theme theme = mCategory.getTheme();
        final ContextThemeWrapper context = new ContextThemeWrapper(getActivity(),
                theme.getStyleId());
        final LayoutInflater themedInflater = LayoutInflater.from(context);
        return themedInflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        _("onViewCreated");
        mQuizView = (AdapterViewAnimator) view.findViewById(R.id.quiz_view);
        decideOnViewToDisplay();
        setQuizViewAnimations();
        final AvatarView avatar = (AvatarView) view.findViewById(R.id.avatar);
        setAvatarDrawable(avatar);
        initProgressToolbar(view);
        super.onViewCreated(view, savedInstanceState);
    }
    private void setQuizViewAnimations() {
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        mQuizView.setInAnimation(getActivity(), R.animator.slide_in_bottom);
        mQuizView.setOutAnimation(getActivity(), R.animator.slide_out_top);
    }
    private void initProgressToolbar(View view) {
        _("initProgressToolbar");
        final int firstUnsolvedQuizPosition = mCategory.getFirstUnsolvedQuizPosition();
        final List<Quiz> quizzes = mCategory.getQuizzes();
        Log.i("mQuizSize", String.valueOf(mQuizSize));
        mQuizSize = quizzes.size();
        mProgressText = (TextView) view.findViewById(R.id.progress_text);
        mProgressBar = ((ProgressBar) view.findViewById(R.id.progress));
        mProgressBar.setMax(mQuizSize);

        setProgress(firstUnsolvedQuizPosition);
    }

    private void setProgress(int currentQuizPosition) {

        if (!isAdded()) {
            return;
        }
        mProgressText
                .setText(getString(R.string.quiz_of_quizzes, currentQuizPosition, mQuizSize));
        mProgressBar.setProgress(currentQuizPosition);
    }

    @SuppressWarnings("ConstantConditions")
    private void setAvatarDrawable(AvatarView avatarView) {
        _("setAvatarDrawable");
        //Player player = PreferencesHelper.getPlayer(getActivity());
         MyPlayer = NewPreferencesHelper.getPlayer(getActivity());
        //avatarView.setImageResource(MyPlayer.getAvatar().getDrawableId());
        avatarView.setImageBitmap(getAvatar(MyPlayer));

    }
    private Bitmap getAvatar(MyPlayer Player){
        DatabaseHelper newdb = new DatabaseHelper(getActivity());
        byte[] tempbite = newdb.getImage(Player.getName());
        return new DbBitmapUtility().getImage(tempbite);
    }
    private void decideOnViewToDisplay() {
        _("decideOnViewToDisplay");
        final boolean isSolved = mCategory.isSolved();
        if (isSolved) {
//            showSummary();
            if (null != mSolvedStateListener) {
                mSolvedStateListener.onCategorySolved();
            }
        } else {
            mQuizView.setAdapter(getQuizAdapter());
            Log.i("setSelection", String.valueOf(mCategory.getFirstUnsolvedQuizPosition()));
            mQuizView.setSelection(mCategory.getFirstUnsolvedQuizPosition());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        _("onSaveInstanceState");
        View focusedChild = mQuizView.getFocusedChild();
        if (focusedChild instanceof ViewGroup) {
            View currentView = ((ViewGroup) focusedChild).getChildAt(0);
            if (currentView instanceof AbsQuizView) {
                outState.putBundle(KEY_USER_INPUT, ((AbsQuizView) currentView).getUserInput());
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        _("onViewStateRestored");
        restoreQuizState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    private void restoreQuizState(final Bundle savedInstanceState) {
        _("restoreQuizState");
        if (null == savedInstanceState) {
            return;
        }
        mQuizView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                _("addOnLayoutChangeListener");
                mQuizView.removeOnLayoutChangeListener(this);
                View currentChild = mQuizView.getChildAt(0);
                if (currentChild instanceof ViewGroup) {
                    final View potentialQuizView = ((ViewGroup) currentChild).getChildAt(0);
                    if (potentialQuizView instanceof AbsQuizView) {
                        ((AbsQuizView) potentialQuizView).setUserInput(savedInstanceState.
                                getBundle(KEY_USER_INPUT));
                    }
                }
            }
        });

    }

    private QuizAdapter getQuizAdapter() {// important
        _("getQuizAdapter");
        if (null == mQuizAdapter) {
            mQuizAdapter = new QuizAdapter(getActivity(), mCategory);
        }
        return mQuizAdapter;
    }

    /**
     * Displays the next page.
     *
     * @return <code>true</code> if there's another quiz to solve, else <code>false</code>.
     */
    public boolean showNextPage() {
        _("showNextPage");
        if (null == mQuizView) {
            return false;
        }
        int nextItem = mQuizView.getDisplayedChild() + 1;
        setProgress(nextItem);
        final int count = mQuizView.getAdapter().getCount();
        if (nextItem < count) {
           mQuizView.showNext();
           // mCategory.
            TopekaDatabaseHelper.updateCategory(getActivity(), mCategory);
            return true;
        }
        markCategorySolved();
        return false;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent
            data) {

        _("onActivityResult3");
        for (int i = 0; i < mac.size(); i++) {
            Log.i("DisplayMac", String.valueOf(mac.get(i)));
        }
        if ((requestCode == request_code) && (resultCode == RESULT_OK)) {
            Integer returnString = data.getExtras().getInt("returnData");
            Log.i("CheckMajor", String.valueOf(returnString));
        }

    }
    private void markCategorySolved() {
        _("markCategorySolved");
        mCategory.setSolved(true);
        TopekaDatabaseHelper.updateCategory(getActivity(), mCategory);
    }

    public void showSummary() {
        _("showSummary");
        @SuppressWarnings("ConstantConditions")
        final ListView scorecardView = (ListView) getView().findViewById(R.id.scorecard);
        mScoreAdapter = getScoreAdapter();
        scorecardView.setAdapter(mScoreAdapter);
        scorecardView.setVisibility(View.VISIBLE);
        final TextView score = (TextView) getView().findViewById(R.id.scoreview);
        try {
            score.setText("Your Point: "+(String.valueOf(TopekaDatabaseHelper.getScore(getActivity()))));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mQuizView.setVisibility(View.INVISIBLE);
        score.setVisibility(View.VISIBLE);

        try {
            updateScore=String.valueOf(TopekaDatabaseHelper.getScore(getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userEmail =MyPlayer.getEmail();
            // save player information to MySQL
        new SaveProductDetails().execute();
        // create player
    }

    private ScoreAdapter getScoreAdapter() {
        _("getScoreAdapter");
        if (null == mScoreAdapter) {
            mScoreAdapter = new ScoreAdapter(mCategory);
        }
        return mScoreAdapter;
    }

    /**
     * Interface definition for a callback to be invoked when the quiz is started.
     */
    public interface SolvedStateListener {

        /**
         * This method will be invoked when the category has been solved.
         */
        void onCategorySolved();
    }

    class SaveProductDetails extends AsyncTask<String, String, String> {
        private Dialog createProduct;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createProduct= ProgressDialog.show(getActivity(),"Please Wait", "Loading");
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", userEmail));
            params.add(new BasicNameValuePair("score", updateScore));
            JSONObject json = JSONParser.makeHttpRequest("http://oslophone.com/test/getscore.php",
                    "POST", params);
            try {
                int success = json.getInt("success");
                if (success == 3) {
                    return "exist";
                }
                else if (success == 1) {
                    return "success";

                } else {
                    return "fail";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            String s = file_url.trim();
            createProduct.dismiss();
            if (s== "success"){
                Toast.makeText(getActivity(),"successfully !", Toast.LENGTH_LONG).show();// successfully created product
            }
            else if (s== "fail"){
                Toast.makeText(getActivity(),"Fail !", Toast.LENGTH_LONG).show();// successfully created product
            };
        }
    }

    public void _(String s){
        Log.i("Inspiria","QuizFragment"+"#######"+s );
    }
}