/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.InstalledAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdatesHeaderDisplayable;
import com.trello.rxlifecycle.FragmentEvent;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 16-05-2016.
 */
public class UpdatesFragment extends GridRecyclerSwipeFragment {

  private static final String TAG = UpdatesFragment.class.getName();

  private List<Displayable> updatesDisplayablesList = new LinkedList<>();
  private List<Displayable> installedDisplayablesList = new LinkedList<>();
  private Subscription installedSubscription;
  private Subscription updatesSubscription;
  private InstallManager installManager;

  public static UpdatesFragment newInstance() {
    UpdatesFragment fragment = new UpdatesFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    installManager = new InstallManager(AptoideDownloadManager.getInstance(),
        new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK),
        AccessorFactory.getAccessorFor(Download.class),
        AccessorFactory.getAccessorFor(Installed.class));
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    fetchUpdates();
    fetchInstalled();
    // FIXME: 26/10/2016 sithengineer doing concurrent calls with the load(...) and reload() methods
    // TODO: 26/10/2016 sithengineer use ONLY the repositories for updates (1st) and installed (2nd)
    // the repositories are responsible to decide to hit the network (with cache hit or miss), store
    // new data and return all the stored data. the chaining of calls to the repositories will solve
    // all this problems including the swipe-to-refresh.
  }

  @Override public void reload() {
    super.reload();

    StoreRepository storeRepository = RepositoryFactory.getRepositoryFor(Store.class);
    UpdateRepository updateRepository = RepositoryFactory.getRepositoryFor(Update.class);
    storeRepository.count()
        .first()
        .flatMap(numberStores -> {
          if (numberStores <= 0) {
            return Observable.error(new RepositoryItemNotFoundException("no stores added"));
          } else {
            return Observable.just(numberStores);
          }
        })
        .flatMap(numberStores -> updateRepository.getUpdates(true))
        .first()
        .observeOn(AndroidSchedulers.mainThread())
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

  private void fetchUpdates() {
    //if (updatesSubscription == null || updatesSubscription.isUnsubscribed()) {
    //  updatesSubscription = DeprecatedDatabase.UpdatesQ.getAllSorted(realm, false)
    //      .asObservable()
    //      .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
    //      .observeOn(AndroidSchedulers.mainThread())
    //      .subscribe(updates -> {
    //
    //        if (updates.size() == updatesDisplayablesList.size() - 1) {
    //          finishLoading();
    //        } else {
    //          updatesDisplayablesList.clear();
    //
    //          if (updates.size() > 0) {
    //            updatesDisplayablesList.add(new UpdatesHeaderDisplayable(installManager,
    //                AptoideUtils.StringU.getResString(R.string.updates)));
    //
    //            for (Update update : updates) {
    //              updatesDisplayablesList.add(
    //                  UpdateDisplayable.create(update, installManager, downloadFactory,
    //                      downloadManager));
    //            }
    //          }
    //
    //          setDisplayables();
    //        }
    //      }, ex -> {
    //        Logger.w(TAG, "finished loading not being called in fetchUpdates");
    //        Logger.printException(ex);
    //        CrashReports.logException(ex);
    //      });
    //}

    if (updatesSubscription == null || updatesSubscription.isUnsubscribed()) {
      UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(Update.class);
      updateAccessor.getAllSorted(false)
          .observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
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
                      UpdateDisplayable.create(update, installManager, new DownloadFactory()));
                }
              }

              setDisplayables();
            }
          }, ex -> {
            Logger.printException(ex);
            CrashReports.logException(ex);
          });
    }
  }

  private void fetchInstalled() {
    if (installedSubscription == null || installedSubscription.isUnsubscribed()) {
      //RealmResults<Installed> realmResults = DeprecatedDatabase.InstalledQ.getAllSorted(realm);
      //installedSubscription = realmResults.asObservable()
      //    .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
      //    .subscribe(installeds -> {
      //      installedDisplayablesList.clear();
      //
      //      installedDisplayablesList.add(new StoreGridHeaderDisplayable(
      //          new GetStoreWidgets.WSWidget().setTitle(
      //              AptoideUtils.StringU.getResString(R.string.installed_tab))));
      //
      //      RealmResults<Installed> all = realmResults;
      //      for (int i = 0; i < all.size(); i++) {
      //        if (!DeprecatedDatabase.UpdatesQ.contains(all.get(i).getPackageName(), false,
      //            realm)) {
      //          if (!all.get(i).isSystemApp()) {
      //            installedDisplayablesList.add(new InstalledAppDisplayable(all.get(i)));
      //          }
      //        }
      //      }
      //
      //      setDisplayables();
      //    }, ex -> {
      //      Logger.w(TAG, "finished loading not being called in fetchInstalled");
      //      Logger.printException(ex);
      //      CrashReports.logException(ex);
      //    });
      //
      //if (realmResults.size() == 0) {
      //  finishLoading();
      //}
      //finishLoading();

      final UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(Update.class);
      final InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
      installedAccessor.getAllSorted()
          .flatMap(
              // hack to make stream of changes complete inside this observable
              listItems ->
                  Observable.from(listItems)
                  .flatMap(item -> filterUpdates(updateAccessor, item))
                  .filter(item -> !item.isSystemApp())
                  .toList()
          ) // filter for installed apps in updates
          .observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(installedApps -> {
            installedDisplayablesList.clear();
            installedDisplayablesList.add(new StoreGridHeaderDisplayable(
                new GetStoreWidgets.WSWidget().setTitle(
                    AptoideUtils.StringU.getResString(R.string.installed_tab))));

            for (Installed installedApp : installedApps) {
              installedDisplayablesList.add(new InstalledAppDisplayable(installedApp));
            }
            setDisplayables();
          }, err -> {
            Logger.e(TAG, "finished loading not being called in fetchInstalled");
            CrashReports.logException(err);
            finishLoading();
          });
    }
  }

  private Observable<Installed> filterUpdates(UpdateAccessor updateAccessor, Installed item) {
    return updateAccessor.contains(item.getPackageName(), false).flatMap(itemIsContained -> {
      if (!itemIsContained) {
        return Observable.just(item);
      }
      return Observable.empty();
    });
  }

  private void setDisplayables() {
    LinkedList<Displayable> displayables = new LinkedList<>();
    displayables.addAll(updatesDisplayablesList);
    displayables.addAll(installedDisplayablesList);
    setDisplayables(displayables);
  }
}
