/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RowReviewDisplayable;

/**
 * Created by sithengineer on 02/08/16.
 */
public class LatestReviewsFragment extends GridRecyclerFragmentWithDecorator implements Endless {

	private static final String TAG = LatestReviewsFragment.class.getSimpleName();
	// on v6, 50 was the limit
	private static final int REVIEWS_LIMIT = 25;
	private static final String STORE_ID = "storeId";

	private ProgressBar progressBar;
	private TextView emptyData;
	//private Subscription subscription;

	private int offset = 0;
	private int limit = 9;
	private long storeId;

	public static LatestReviewsFragment newInstance(long storeId) {
		LatestReviewsFragment fragment = new LatestReviewsFragment();
		Bundle args = new Bundle();
		args.putLong(STORE_ID, storeId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
		}
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

	@Override
	public void loadExtras(Bundle args) {
		super.loadExtras(args);
		this.storeId = args.getLong(STORE_ID, -1);
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		super.load(refresh, savedInstanceState);
		ListFullReviewsRequest.of(storeId, limit, offset).execute(listTopFullReviews -> {
			List<FullReview> reviews = listTopFullReviews.getDatalist().getList();
			List<Displayable> displayables = new LinkedList<>();
			for (final FullReview review : reviews) {
				displayables.add(new RowReviewDisplayable(review));
			}
			setDisplayables(displayables);
			finishLoading();
			progressBar.setVisibility(View.GONE);
			emptyData.setVisibility(View.GONE);
		}, err -> {
			Logger.e(TAG, err.getCause());
		}, false);
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		setHasOptionsMenu(true);
		emptyData = (TextView) view.findViewById(R.id.empty_data);
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public Integer getLimit() {
		return limit;
	}
}
