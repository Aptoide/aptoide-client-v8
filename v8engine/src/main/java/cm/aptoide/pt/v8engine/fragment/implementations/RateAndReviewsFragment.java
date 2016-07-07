/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import rx.Subscription;

/**
 * Created by sithengineer on 13/05/16.
 */
public class RateAndReviewsFragment extends GridRecyclerSwipeFragment {

	private static final String TAG = RateAndReviewsFragment.class.getSimpleName();

	private static final String APP_ID = "app_id";
	private TextView emptyData;
	private Subscription subscription;

	public static RateAndReviewsFragment newInstance(long appId) {
		RateAndReviewsFragment fragment = new RateAndReviewsFragment();
		Bundle args = new Bundle();
		args.putLong(APP_ID, appId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void load(boolean refresh) {
		Logger.d(TAG, "Other versions should refresh? " + refresh);
		fetchRating();
		fetchReviews();
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		emptyData = (TextView) view.findViewById(R.id.empty_data);
		setHasOptionsMenu(true);
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			// TODO set app name as title bar.setTitle("");
		}
	}

	@Override
	public int getContentViewId() {
		return R.layout.fragment_rate_and_reviews;
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_empty, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void fetchRating() {
		// TODO
	}

	private void fetchReviews() {
		// TODO
		//subscription =  ??
	}
}
