/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEventConverter;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEventConverter;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.DefaultDisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.InstalledAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdatesHeaderDisplayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 16-05-2016.
 */
public class UpdatesFragment extends GridRecyclerSwipeFragment {

  private static final String TAG = UpdatesFragment.class.getName();

  private List<Displayable> updatesDisplayablesList = new LinkedList<>();
  private List<Displayable> installedDisplayablesList = new LinkedList<>();
  private InstallManager installManager;
  private Analytics analytics;
  private DownloadEventConverter downloadInstallEventConverter;
  private InstallEventConverter installConverter;

  @NonNull public static UpdatesFragment newInstance() {
    return new UpdatesFragment();
  }

  @Override public void setupViews() {
    super.setupViews();

    installManager = new InstallManager(AptoideDownloadManager.getInstance(),
        new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK),
        AccessorFactory.getAccessorFor(Download.class),
        AccessorFactory.getAccessorFor(Installed.class));
    analytics = Analytics.getInstance();
    downloadInstallEventConverter = new DownloadEventConverter();
    installConverter = new InstallEventConverter();
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);

    fetchUpdates().observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .doOnNext(updateList -> {
          if (updateList.size() == updatesDisplayablesList.size() - 1) {
            finishLoading();
          } else {
            updatesDisplayablesList.clear();

            if (updateList.size() > 0) {
              updatesDisplayablesList.add(new UpdatesHeaderDisplayable(installManager,
                  AptoideUtils.StringU.getResString(R.string.updates), analytics,
                  downloadInstallEventConverter, installConverter));

              for (Update update : updateList) {
                updatesDisplayablesList.add(
                    UpdateDisplayable.create(update, installManager, new DownloadFactory(),
                        analytics, downloadInstallEventConverter, installConverter));
              }
            }
          }
        })
        .flatMap(updates -> fetchInstalled())
        .doOnNext(installedApps -> {
          installedDisplayablesList.clear();
          installedDisplayablesList.add(new StoreGridHeaderDisplayable(
              new GetStoreWidgets.WSWidget().setTitle(
                  AptoideUtils.StringU.getResString(R.string.installed_tab))));

          for (Installed installedApp : installedApps) {
            installedDisplayablesList.add(new InstalledAppDisplayable(installedApp));
          }
          setDisplayables();
        })
        .subscribe(installeds -> {
          Logger.v(TAG, "listing updates and installed apps");
        }, err -> {
          Logger.e(TAG, "finished loading not being called in fetchInstalled");
          CrashReports.logException(err);
          finishLoading();
        });
  }

  @Override public void reload() {
    super.reload();
    reloadData().observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(updates -> {
          if (updates.size() == 0) {
            finishLoading();
            ShowMessage.asSnack(getView(), R.string.no_updates_available_retoric);
          } else if (updates.size() == updatesDisplayablesList.size() - 1) {
            ShowMessage.asSnack(getView(), R.string.no_new_updates_available);
          }
        }, throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            ShowMessage.asSnack(getView(), R.string.add_store);
          } else {
            Logger.e(TAG, throwable);
            CrashReports.logException(throwable);
          }
          finishLoading();
        });
  }

  private Observable<List<Update>> reloadData() {
    StoreRepository storeRepository = RepositoryFactory.getStoreRepository();
    UpdateRepository updateRepository = RepositoryFactory.getUpdateRepository();
    return storeRepository.count().first().flatMap(numberStores -> {
      if (numberStores <= 0) {
        return Observable.error(new RepositoryItemNotFoundException("no stores added"));
      } else {
        return Observable.just(numberStores);
      }
    }).flatMap(numberStores -> updateRepository.getUpdates(true)).first();
  }

  private Observable<List<Update>> fetchUpdates() {
    return RepositoryFactory.getUpdateRepository().getAllSorted(false);
  }

  private Observable<List<Installed>> fetchInstalled() {
    UpdateRepository updateRepo = RepositoryFactory.getUpdateRepository();
    InstalledRepository installedRepo = RepositoryFactory.getInstalledRepository();

    return installedRepo.getAllSorted().flatMap(
        // hack to make stream of changes complete inside this observable
        listItems -> Observable.from(listItems)
            .flatMap(item -> filterUpdates(updateRepo, item))
            .filter(item -> !item.isSystemApp())
            .toList()); // filter for installed apps in updates
  }

  private Observable<Installed> filterUpdates(UpdateRepository updateRepo, Installed item) {
    return updateRepo.contains(item.getPackageName(), false).flatMap(isUpdate -> {
      if (isUpdate) {
        return Observable.empty();
      }
      return Observable.just(item);
    });
  }

  private void setDisplayables() {
    LinkedList<Displayable> displayables = new LinkedList<>();

    if (!updatesDisplayablesList.isEmpty()) {
      displayables.add(new DefaultDisplayableGroup(updatesDisplayablesList));
    }

    if (!installedDisplayablesList.isEmpty()) {
      displayables.add(new DefaultDisplayableGroup(installedDisplayablesList));
    }

    setDisplayables(displayables);
  }
}
