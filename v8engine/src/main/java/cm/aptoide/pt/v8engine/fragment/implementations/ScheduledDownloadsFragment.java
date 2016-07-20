/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.FragmentEvent;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 19/07/16.
 */
public class ScheduledDownloadsFragment extends GridRecyclerFragment {

	private static final String TAG = ScheduledDownloadsFragment.class.getSimpleName();

	private TextView emptyData;
	private Subscription subscription;

	public ScheduledDownloadsFragment() {
	}

	public static ScheduledDownloadsFragment newInstance() {
		return new ScheduledDownloadsFragment();
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		Logger.d(TAG, String.format("refresh excluded updates? %s", refresh ? "yes" : "no"));
		fetchScheduledDownloads();
	}

	@Override
	public int getContentViewId() {
		return R.layout.fragment_with_toolbar;
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		emptyData = (TextView) view.findViewById(R.id.empty_data);
		setHasOptionsMenu(true);
	}

	private void fetchScheduledDownloads() {
		subscription = Database.ScheduledQ.getAll(realm)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(scheduledDownloads -> {

				});
	}
}
