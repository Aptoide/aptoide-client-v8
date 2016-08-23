/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
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

import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.download.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ScheduledDownloadDisplayable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 19/07/16.
 */
public class ScheduledDownloadsFragment extends GridRecyclerFragment {

	private static final String TAG = ScheduledDownloadsFragment.class.getSimpleName();

	private TextView emptyData;
	private Subscription subscription;
	private DownloadServiceHelper downloadManager;
	private InstallManager installManager;

	public ScheduledDownloadsFragment() { }

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
		final PermissionManager permissionManager = new PermissionManager();
		downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);
		installManager = new InstallManager(permissionManager, getContext().getPackageManager(), new DownloadInstallationProvider(downloadManager));
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(R.string.setting_schdwntitle);
		}
	}

	private void fetchScheduledDownloads() {
		subscription = Database.ScheduledQ.getAll(realm)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(scheduledDownloads -> {

					if (scheduledDownloads == null || scheduledDownloads.isEmpty()) {
						emptyData.setText(R.string.no_sch_downloads);
						emptyData.setVisibility(View.VISIBLE);
						clearDisplayables();
						finishLoading();
					} else {
						emptyData.setVisibility(View.GONE);
						ArrayList<ScheduledDownloadDisplayable> displayables = new ArrayList<>(scheduledDownloads.size());
						for (final Scheduled scheduledDownload : scheduledDownloads) {
							displayables.add(new ScheduledDownloadDisplayable(installManager, scheduledDownload, realm));
						}
						setDisplayables(displayables);
					}
				}, t -> {
					Logger.e(TAG, t);
					emptyData.setText(R.string.no_sch_downloads);
					emptyData.setVisibility(View.VISIBLE);
					clearDisplayables();
					finishLoading();
				});
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_scheduled_downloads_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}

		if (itemId == R.id.menu_install_selected) {

			final PermissionRequest permissionRequest = ((PermissionRequest) getContext());
			final DownloadServiceHelper downloadServiceHelper = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager());

			int installs = 0;
			BaseAdapter adapter = getAdapter();
			for (int i = 0 ; i < adapter.getItemCount() ; ++i) {
				ScheduledDownloadDisplayable displayable = ((ScheduledDownloadDisplayable) adapter.getDisplayable(i));
				if (displayable.isSelected()) {
					displayable.downloadAndInstall(getContext(), permissionRequest, downloadServiceHelper);
					installs++;
				}
			}
			ShowMessage.asSnack(this.emptyData, installs == 0 ? R.string.schDown_nodownloadselect : R.string.installing_msg);

			return true;
		}

		if (itemId == R.id.menu_select_all) {
			BaseAdapter adapter = getAdapter();
			for (int i = 0 ; i < adapter.getItemCount() ; ++i) {
				((ScheduledDownloadDisplayable) adapter.getDisplayable(i)).setSelected(true);
				adapter.notifyDataSetChanged();
			}
			return true;
		}

		if (itemId == R.id.menu_select_none) {
			BaseAdapter adapter = getAdapter();
			for (int i = 0 ; i < adapter.getItemCount() ; ++i) {
				((ScheduledDownloadDisplayable) adapter.getDisplayable(i)).setSelected(false);
				adapter.notifyDataSetChanged();
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
