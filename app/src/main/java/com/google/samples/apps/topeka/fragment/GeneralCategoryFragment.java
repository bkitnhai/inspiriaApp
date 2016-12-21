package com.google.samples.apps.topeka.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.activity.GeneralCategoryActivity;
import com.google.samples.apps.topeka.adapter.CategoryAdapter;
import com.google.samples.apps.topeka.support_class.RowItem;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralCategoryFragment extends Fragment {
    private static final String TAG = "GeneralCategoryFragment";
    private CategoryAdapter mCategoryAdapter;
    private static List<RowItem> rowItems;
    private static final String[] titles = new String[] { "TrailActivity",
            "Casual Quiz", "Event Quiz" };
    public static final Integer[] images = { R.drawable.map,
            R.drawable.event, R.drawable.quiz};

    public static GeneralCategoryFragment newInstance() {
        return new GeneralCategoryFragment();
    }

    public static interface WorkoutListListener {
        void itemClicked(long id);
    };
    private WorkoutListListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_general_category2, container, false);
//        return inflater.inflate(R.layout.fragment_categories, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated2");
        try {
            setUpQuizList((ListView) view.findViewById(R.id.GeneralCategory));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onViewCreated continute");
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpQuizList(ListView categoriesView) throws JSONException {
        categoriesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = (GeneralCategoryActivity)getActivity();
                if (listener != null) {
                    listener.itemClicked(id);
                }

            }
        });


        rowItems = new ArrayList<>();
        for (int i = 0;i <3; i++){
            RowItem item = new RowItem (images[i],titles[i]);
            rowItems.add(item);
        }
        //mCategoryAdapter = new CategoryAdapter(getActivity(), rowItems);
        categoriesView.setAdapter(mCategoryAdapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (WorkoutListListener)activity;
    }


}
