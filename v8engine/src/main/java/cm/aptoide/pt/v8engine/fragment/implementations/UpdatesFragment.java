/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.RollbackInstallManager;
import cm.aptoide.pt.v8engine.install.provider.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.install.provider.RollbackActionFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.InstalledAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdatesHeaderDisplayable;
import com.trello.rxlifecycle.FragmentEvent;
import io.realm.RealmResults;
import java.util.LinkedList;
import java.util.List;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 16-05-2016.
 */
public class UpdatesFragment extends GridRecyclerSwipeFragment {

	private List<Displayable> updatesDisplayablesList = new LinkedList<>();
	private List<Displayable> installedDisplayablesList = new LinkedList<>();
	private Subscription installedSubscription;
	private Subscription updatesSubscription;
	private Installer installManager;
	private DownloadFactory downloadFactory;
	private DownloadServiceHelper downloadManager;

	public static UpdatesFragment newInstance() {
		UpdatesFragment fragment = new UpdatesFragment();
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PermissionManager permissionManager = new PermissionManager();
		downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);
		installManager = new RollbackInstallManager(
				new InstallManager(permissionManager, getContext().getPackageManager(),
						new DownloadInstallationProvider(downloadManager)),
				RepositoryFactory.getRepositoryFor(Rollback.class), new RollbackActionFactory(),
				new DownloadInstallationProvider(downloadManager));
		downloadFactory = new DownloadFactory();
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		fetchUpdates();
		fetchInstalled();
	}

	@Override
	public void reload() {
		super.reload();
		DataproviderUtils.checkUpdates(listAppsUpdates -> {
			if (listAppsUpdates.getList().size() == 0) {
				finishLoading();
				ShowMessage.asSnack(getView(), R.string.no_updates_available_retoric);
			}
			if (listAppsUpdates.getList().size() == updatesDisplayablesList.size() - 1) {
				ShowMessage.asSnack(getView(), R.string.no_new_updates_available);
			}
		});
	}

	private void fetchUpdates() {
		if (updatesSubscription == null || updatesSubscription.isUnsubscribed()) {
			updatesSubscription = DeprecatedDatabase.UpdatesQ.getAll(realm, false)
					.asObservable()
					.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(updates -> {

						if (updates.size() == updatesDisplayablesList.size() - 1) {
							finishLoading();
						} else {
							updatesDisplayablesList.clear();

							if (updates.size() > 0) {
								updatesDisplayablesList.add(new UpdatesHeaderDisplayable(installManager,
										AptoideUtils.StringU.getResString(R.string.updates)));

								for (Update update : updates) {
									updatesDisplayablesList.add(
											UpdateDisplayable.create(update, installManager, downloadFactory,
													downloadManager));
								}
							}

							setDisplayables();
						}
					}, Throwable::printStackTrace);
		}
	}

	private void fetchInstalled() {
		if (installedSubscription == null || installedSubscription.isUnsubscribed()) {
			RealmResults<Installed> realmResults = DeprecatedDatabase.InstalledQ.getAll(realm);
			installedSubscription = realmResults.asObservable()
					.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
					.subscribe(installeds -> {
						installedDisplayablesList.clear();

						installedDisplayablesList.add(new StoreGridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(AptoideUtils.StringU
								.getResString(R.string.installed_tab))));

						RealmResults<Installed> all = realmResults;
						for (int i = all.size() - 1; i >= 0; i--) {
							if (!DeprecatedDatabase.UpdatesQ.contains(all.get(i).getPackageName(), false, realm)) {
								if (!all.get(i).isSystemApp()) {
									installedDisplayablesList.add(new InstalledAppDisplayable(all.get(i)));
								}
							}
						}

						setDisplayables();
					}, Throwable::printStackTrace);
			if (realmResults.size() == 0) {
				finishLoading();
			}
			finishLoading();
		}
	}

	private void setDisplayables() {
		LinkedList<Displayable> displayables = new LinkedList<>();
		displayables.addAll(updatesDisplayablesList);
		displayables.addAll(installedDisplayablesList);
		setDisplayables(displayables);
	}
}
