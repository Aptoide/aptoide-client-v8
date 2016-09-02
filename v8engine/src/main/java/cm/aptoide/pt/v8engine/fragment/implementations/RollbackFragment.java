/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.provider.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterRowDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackFragment extends GridRecyclerFragment {

	private static final String TAG = RollbackFragment.class.getSimpleName();
	private static final AptoideUtils.DateTimeU DATE_TIME_U = AptoideUtils.DateTimeU.getInstance();
	private TextView emptyData;
	private Subscription subscription;
	private DownloadServiceHelper downloadManager;
	private InstallManager installManager;

	public RollbackFragment() { }

	public static Fragment newInstance() {
		return new RollbackFragment();
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(R.string.rollback);
		}
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_clear, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		} else if (itemId == R.id.menu_clear) {
			DeprecatedDatabase.RollbackQ.deleteAll(realm);
			clearDisplayables();
			finishLoading();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		Logger.d(TAG, "refresh rollbacks? " + (refresh ? "yes" : "no"));
		AptoideUtils.ThreadU.runOnUiThread(this::fetchRollbacks);
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

		final PermissionManager permissionManager = new PermissionManager();
		downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);
		installManager = new InstallManager(permissionManager, getContext().getPackageManager(), new DownloadInstallationProvider(downloadManager));
	}

	@UiThread
	private void fetchRollbacks() {
		subscription = DeprecatedDatabase.RollbackQ.getAll(realm).sort(Rollback.TIMESTAMP, Sort.ASCENDING)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(rollbacks -> {

					if (rollbacks == null || rollbacks.isEmpty()) {

						emptyData.setText(R.string.no_rollbacks_msg);
						emptyData.setVisibility(View.VISIBLE);
					} else {

						emptyData.setVisibility(View.GONE);
						sortRollbacksAndAdd(rollbacks);
					}

					finishLoading();
				});
	}

	private String prettyTimestamp(long timestamp) {
		return DATE_TIME_U.getTimeDiffString(getActivity(), timestamp);
	}

	// FIXME: 21/07/2016 slow method. could this be improved ??
	@UiThread
	private void sortRollbacksAndAdd(RealmResults<Rollback> rollbacks) {
		// group by timestamp
		TreeMap<Long,List<Displayable>> arrayOfDisplayables = new TreeMap<>(new Comparator<Long>() {
			@Override
			public int compare(Long lhs, Long rhs) {
				//				long lhsDate = timestampAsLongFromString(lhs);
				//				long rhsDate = timestampAsLongFromString(rhs);
				//				return ((int) (lhsDate - rhsDate));
				return lhs.compareTo(rhs);
			}
		});

		List<Displayable> displayables = null;
		for (Rollback rollback : rollbacks) {
			displayables = arrayOfDisplayables.get(rollback.getTimestamp());
			if (displayables == null) {
				displayables = new LinkedList<>();
				arrayOfDisplayables.put(rollback.getTimestamp(), displayables);
			}
			displayables.add(new RollbackDisplayable(installManager, rollback));
		}

		// display headers and content
		List<Displayable> displayablesToShow = new LinkedList<>();
		for (Map.Entry<Long,List<Displayable>> arrayOfDisplayablesEntry : arrayOfDisplayables.entrySet()) {
			displayablesToShow.add(new FooterRowDisplayable(prettyTimestamp(arrayOfDisplayablesEntry.getKey())));
			displayablesToShow.addAll(arrayOfDisplayablesEntry.getValue());
		}
		setDisplayables(displayablesToShow);
	}
}
