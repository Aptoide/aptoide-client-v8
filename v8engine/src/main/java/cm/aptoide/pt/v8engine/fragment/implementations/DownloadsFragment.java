/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.ArrayList;
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
import cm.aptoide.pt.v8engine.install.download.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import io.realm.RealmResults;
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
	private List<Download> oldDownloadsList;

	public static DownloadsFragment newInstance() {
		return new DownloadsFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final PermissionManager permissionManager = new PermissionManager();
		downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);
		installManager = new InstallManager(permissionManager, getContext().getPackageManager(), new DownloadInstallationProvider(downloadManager));
		oldDownloadsList = new ArrayList<>();
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		super.load(refresh, savedInstanceState);
		if (subscription == null || subscription.isUnsubscribed()) {
			subscription = realm.where(Download.class)
					.findAllSortedAsync("timeStamp")
					.asObservable()
					.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
					.filter(downloads -> shouldUpdateList(downloads, oldDownloadsList))
					.map(downloads -> updateOldDownloadsList(downloads))
					.subscribe(downloads -> {
						activeDisplayablesList.clear();
						completedDisplayablesList.clear();
						for (final Download download : downloads) {
							if (download.getOverallDownloadStatus() == Download.PROGRESS || download.getOverallDownloadStatus() == Download.IN_QUEUE ||
									download.getOverallDownloadStatus() == Download.PENDING) {
								activeDisplayablesList.add(new ActiveDownloadDisplayable(download, downloadManager));
							} else {
								completedDisplayablesList.add(new CompletedDownloadDisplayable(download, installManager, downloadManager));
							}
						}
						if (completedDisplayablesList.size() > 0) {
							completedDisplayablesList.add(0, new StoreGridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(AptoideUtils.StringU
									.getResString(R.string.completed))));
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

	@NonNull
	private RealmResults<Download> updateOldDownloadsList(RealmResults<Download> downloads) {
		oldDownloadsList = new ArrayList<>();
		for (final Download download : downloads) {
			oldDownloadsList.add(download.clone());
		}
		return downloads;
	}

	/**
	 * this method checks if the downloads from the 2 lists are equivalents (same {@link Download#getAppId()} and {@link Download#getOverallDownloadStatus()}
	 *
	 * @param downloads        list of the most recent downloads list
	 * @param oldDownloadsList list of the old downloads list
	 *
	 * @return true if the lists have different downloads or the download state has change, false otherwise
	 */
	private Boolean shouldUpdateList(@NonNull RealmResults<Download> downloads, @NonNull List<Download> oldDownloadsList) {
		if (downloads.size() != oldDownloadsList.size()) {
			return true;
		}
		for (int i = 0 ; i < oldDownloadsList.size() ; i++) {
			int oldIndex = getDownloadFromListById(downloads.get(i), oldDownloadsList);
			int newIndex = getDownloadFromListById(oldDownloadsList.get(i), downloads);
			if (oldIndex < 0 || newIndex < 0 || downloads.get(i).getOverallDownloadStatus() != oldDownloadsList.get(oldIndex).getOverallDownloadStatus()) {
				return true;
			}
		}
		return false;
	}

	private int getDownloadFromListById(Download download, List<Download> oldDownloadsList) {
		for (int i = 0 ; i < oldDownloadsList.size() ; i++) {
			if ((oldDownloadsList.get(i).getAppId() == download.getAppId())) {
				return i;
			}
		}
		return -1;
	}

	public void setDisplayables() {
		LinkedList<Displayable> displayables = new LinkedList<>();
		displayables.addAll(activeDisplayablesList);
		displayables.addAll(completedDisplayablesList);
		setDisplayables(displayables);
	}
}
