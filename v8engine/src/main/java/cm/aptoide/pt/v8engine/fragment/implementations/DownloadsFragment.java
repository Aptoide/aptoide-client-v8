/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.RollbackInstallManager;
import cm.aptoide.pt.v8engine.install.provider.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.install.provider.RollbackActionFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import com.trello.rxlifecycle.FragmentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 7/15/16.
 */
public class DownloadsFragment extends GridRecyclerFragmentWithDecorator {

	public static final String TAG = "worker";
	private List<Displayable> activeDisplayablesList = new LinkedList<>();
	private List<Displayable> completedDisplayablesList = new LinkedList<>();
	private Subscription subscription;
	private Installer installManager;
	private DownloadServiceHelper downloadManager;
	private List<Download> oldDownloadsList;
	private PermissionManager permissionManager;

	public static DownloadsFragment newInstance() {
		return new DownloadsFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final PermissionManager permissionManager = new PermissionManager();
		downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);
		installManager = new RollbackInstallManager(
				new InstallManager(permissionManager, getContext().getPackageManager(),
						new DownloadInstallationProvider(downloadManager)),
				RepositoryFactory.getRepositoryFor(Rollback.class), new RollbackActionFactory(),
				new DownloadInstallationProvider(downloadManager));

		oldDownloadsList = new ArrayList<>();

	}

	@Override public void load(boolean refresh, Bundle savedInstanceState) {
		super.load(refresh, savedInstanceState);
		if (subscription == null || subscription.isUnsubscribed()) {
			DownloadServiceHelper downloadServiceHelper = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager());
			DownloadAccessor downloadAccessor = AccessorFactory.getAccessorFor(Download.class);
			downloadAccessor.getAll()
					.observeOn(Schedulers.computation())
					.first().map(downloads -> Download.sortDownloads(downloads, Download.DESCENDING))
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(downloads -> updateUi(downloadServiceHelper, downloads),
							Throwable::printStackTrace);

			subscription = downloadServiceHelper.getAllDownloads().sample(250, TimeUnit.MILLISECONDS)
					.filter(downloads -> (shouldUpdateList(downloads, oldDownloadsList)))
					.map(downloads -> oldDownloadsList = downloads)
					.map(downloads -> Download.sortDownloads(downloads, Download.DESCENDING))
					.observeOn(AndroidSchedulers.mainThread())
					.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
					.subscribe(downloads -> updateUi(downloadServiceHelper, downloads));
		}
	}

	@Override
	public void onDestroyView() {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
		super.onDestroyView();
	}

	private void updateUi(DownloadServiceHelper downloadServiceHelper, List<Download> downloads) {
		fillDisplayableList(downloadServiceHelper, downloads);
		setDisplayables();
	}

	private void fillDisplayableList(DownloadServiceHelper downloadServiceHelper, List<Download> downloads) {
		activeDisplayablesList.clear();
		completedDisplayablesList.clear();
		for (final Download download : downloads) {
			if (download.getOverallDownloadStatus() == Download.PROGRESS || download.getOverallDownloadStatus() == Download.IN_QUEUE ||
					download.getOverallDownloadStatus() == Download.PENDING) {
				activeDisplayablesList.add(new ActiveDownloadDisplayable(download, downloadManager));
			} else {
				completedDisplayablesList.add(
						new CompletedDownloadDisplayable(download, installManager, downloadManager));
			}
		}
		Collections.reverse(activeDisplayablesList);
		if (completedDisplayablesList.size() > 0) {
			completedDisplayablesList.add(0, new StoreGridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(AptoideUtils.StringU.getResString(R.string
					.completed))));
		}
		if (activeDisplayablesList.size() > 0) {
			activeDisplayablesList.add(0, new ActiveDownloadsHeaderDisplayable(AptoideUtils.StringU.getResString(R.string.active), downloadServiceHelper));
		}
	}

	/**
	 * this method checks if the downloads from the 2 lists are equivalents (same {@link Download#getAppId()} and {@link Download#getOverallDownloadStatus()}
	 *
	 * @param downloads        list of the most recent downloads list
	 * @param oldDownloadsList list of the old downloads list
	 *
	 * @return true if the lists have different downloads or the download state has change, false otherwise
	 */
	private Boolean shouldUpdateList(@NonNull List<Download> downloads, @NonNull List<Download> oldDownloadsList) {
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
