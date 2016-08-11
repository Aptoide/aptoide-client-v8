/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.provider.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import rx.Subscription;

/**
 * Created by trinkes on 7/15/16.
 */
public class DownloadsFragment extends GridRecyclerFragmentWithDecorator {

	private List<Displayable> activeDisplayablesList = new LinkedList<>();
	private List<Displayable> completedDisplayablesList = new LinkedList<>();
	private Subscription subscription;
	private InstallManager installManager;
	private DownloadServiceHelper downloadManager;

	public static DownloadsFragment newInstance() {
		return new DownloadsFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final PermissionManager permissionManager = new PermissionManager();
		downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);
		installManager = new InstallManager(permissionManager, getContext().getPackageManager(), new DownloadInstallationProvider(downloadManager));
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		super.load(refresh, savedInstanceState);
		if (subscription == null || subscription.isUnsubscribed()) {

			subscription = downloadManager.getAllDownloads().compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(downloads -> {
				activeDisplayablesList.clear();
				completedDisplayablesList.clear();
				for (final Download download : downloads) {
					if (download.getOverallDownloadStatus() == Download.PROGRESS || download.getOverallDownloadStatus() == Download.IN_QUEUE || download
							.getOverallDownloadStatus() == Download.PENDING) {
						activeDisplayablesList.add(new ActiveDownloadDisplayable(download, downloadManager));
					} else {
						completedDisplayablesList.add(new CompletedDownloadDisplayable(download, installManager, downloadManager));
					}
				}
				if (completedDisplayablesList.size() > 0) {
					completedDisplayablesList.add(0, new StoreGridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(AptoideUtils.StringU.getResString(R
							.string.completed))));
				}
				if (activeDisplayablesList.size() > 0) {
					activeDisplayablesList.add(0, new ActiveDownloadsHeaderDisplayable(AptoideUtils.StringU.getResString(R.string.active), new
							DownloadServiceHelper(AptoideDownloadManager
							.getInstance(), new PermissionManager())));
				}
				setDisplayables();
			});
		}
	}

	public void setDisplayables() {
		LinkedList<Displayable> displayables = new LinkedList<>();
		displayables.addAll(activeDisplayablesList);
		displayables.addAll(completedDisplayablesList);
		setDisplayables(displayables);
	}
}
