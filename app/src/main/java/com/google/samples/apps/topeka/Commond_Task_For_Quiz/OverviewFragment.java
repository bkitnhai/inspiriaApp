package com.google.samples.apps.topeka.Commond_Task_For_Quiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.samples.apps.topeka.R;

import java.util.ArrayList;

public class OverviewFragment extends Fragment {

	ProgressBar pBar;
	LinearLayout layout;

	interface Compeletion {
		void onComplete(ArrayList<Team> data);
	}
 	// it is similar a class that implement interface.
	Compeletion completion = new Compeletion() {

		@Override
		public void onComplete(ArrayList<Team> data) {
			pBar.setVisibility(View.INVISIBLE);

		}
	};
	public OverviewFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_host, null);

		pBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		layout = (LinearLayout) view.findViewById(R.id.rankingsLinearLayout);
		TextView weekTV = (TextView) view.findViewById(R.id.weekTextView);

		return view;
	}

	@Override
	public void onDestroy() {
	}
}
