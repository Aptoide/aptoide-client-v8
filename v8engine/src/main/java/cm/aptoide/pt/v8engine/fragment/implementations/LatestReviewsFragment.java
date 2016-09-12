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
import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableType;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RowReviewDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sithengineer on 02/08/16.
 */
public class LatestReviewsFragment extends GridRecyclerSwipeFragment {

	private static final String TAG = LatestReviewsFragment.class.getSimpleName();
	// on v6, 50 was the limit
	private static final int REVIEWS_LIMIT = 25;
	private static final String STORE_ID = "storeId";

	private long storeId;
	private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
	private List<Displayable> displayables;



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

		ListFullReviewsRequest listFullReviewsRequest = ListFullReviewsRequest.of(storeId, REVIEWS_LIMIT, 0);
		Action1<ListFullReviews> listFullReviewsAction = listTopFullReviews -> {
			List<FullReview> reviews = listTopFullReviews.getDatalist().getList();
			displayables = new LinkedList<>();
			for (final FullReview review : reviews) {
				displayables.add(new RowReviewDisplayable(review));
			}
			addDisplayables(displayables);
		};

		recyclerView.clearOnScrollListeners();
		endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(this.getAdapter(), listFullReviewsRequest, listFullReviewsAction,
				errorRequestListener, refresh);
		recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
		endlessRecyclerOnScrollListener.onLoadMore(refresh);

	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		setHasOptionsMenu(true);
	}
}
