package cm.aptoide.pt.v8engine.fragment.implementations;

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
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.InstalledAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdatesHeaderDisplayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 16-05-2016.
 */
public class UpdatesFragment extends GridRecyclerSwipeFragment {

  private static final String TAG = UpdatesFragment.class.getName();

  private List<Displayable> updatesDisplayablesList;
  private List<Displayable> installedDisplayablesList;

  private InstallManager installManager;
  private Analytics analytics;
  private DownloadEventConverter downloadInstallEventConverter;
  private InstallEventConverter installConverter;

  private InstalledRepository installedRepository;
  private UpdateRepository updateRepository;

  private Subscription updateReloadSubscription;

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

    updatesDisplayablesList = new LinkedList<>();
    installedDisplayablesList = new LinkedList<>();

    installedRepository = RepositoryFactory.getInstalledRepository();
    updateRepository = RepositoryFactory.getUpdateRepository();
  }

  @Override public void onViewCreated() {
    super.onViewCreated();

    // show updates
    fetchUpdates().buffer(750, TimeUnit.MILLISECONDS)
        .flatMap(list -> Observable.from(list).takeLast(1))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .doOnNext(list -> clearDisplayables())
        .flatMap(updateList -> setUpdates(updateList))
        .flatMap(aVoid -> fetchInstalled())
        .flatMap(installedApps -> setInstalled(installedApps))
        .subscribe(aVoid -> {
          // does nothing
          finishLoading();
          Logger.v(TAG, "fetchUpdates() -> listing updates and installed");
        }, err -> {
          Logger.e(TAG, "fetchUpdates() -> listing updates or installed threw an exception");
          CrashReports.logException(err);
          finishLoading();
        });
  }

  /**
   * This method is called when pull to refresh is done. An update repository call is made to fetch
   * new updates and this call will hit the network. When new updates are found the listener in the
   * load() method above will be notified of those changes and update the list. The response of
   * this repository call will show a notification according: the number of new updates, no more
   * new updates or no updates at all.
   */
  @Override public void reload() {
    super.reload();

    if (updateReloadSubscription != null && !updateReloadSubscription.isUnsubscribed()) {
      updateReloadSubscription.unsubscribe();
    }

    updateReloadSubscription = updateRepository.getUpdates(true)
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(updates -> {
          Logger.v(TAG, String.format("reloadu() -> finished with %d updates",
              updates != null ? updates.size() : -1));
          if (updates.size() == 0) {
            ShowMessage.asSnack(getView(), R.string.no_updates_available_retoric);
          } else if (updates.size() == updatesDisplayablesList.size() - 1) {
            // FIXME: 27/1/2017 sithengineer this calculation to check if new updates are available is not correct. need to use a set or hash of a sorted list
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

  private Observable<Void> setUpdates(List<Update> updates) {
    return Observable.just(updates).map(updateList -> {

      updatesDisplayablesList.clear();

      if (updateList.size() > 0) {
        updatesDisplayablesList.add(new UpdatesHeaderDisplayable(installManager,
            AptoideUtils.StringU.getResString(R.string.updates), analytics,
            downloadInstallEventConverter, installConverter));

        for (Update update : updateList) {
          updatesDisplayablesList.add(
              UpdateDisplayable.newInstance(update, installManager, new DownloadFactory(),
                  analytics, downloadInstallEventConverter, installConverter));
        }
      }
      addDisplayables(updatesDisplayablesList, false);
      Logger.v(TAG, "listed updates");
      return null;
    });
  }

  private Observable<Void> setInstalled(List<Installed> installeds) {
    return Observable.just(installeds).map(installedApps -> {
      installedDisplayablesList.clear();
      installedDisplayablesList.add(new StoreGridHeaderDisplayable(
          new GetStoreWidgets.WSWidget().setTitle(
              AptoideUtils.StringU.getResString(R.string.installed_tab))));

      for (Installed installedApp : installedApps) {
        installedDisplayablesList.add(new InstalledAppDisplayable(installedApp));
      }
      addDisplayables(installedDisplayablesList, false);
      Logger.v(TAG, "listed installed apps");
      return null;
    });
  }

  private Observable<List<Update>> fetchUpdates() {
    return updateRepository.getAllSorted(false);
  }

  /**
   * Installed apps without any apps with updates pending or system apps.
   *
   * @return {@link Observable} to a {@link List} of {@link Installed} apps
   */
  private Observable<List<Installed>> fetchInstalled() {
    return installedRepository.getAllSorted()
        .first()
        .flatMapIterable(list -> list)
        .flatMap(item -> filterUpdates(item))
        .filter(item -> !item.isSystemApp())
        .toList();
  }

  /**
   * Filters updates returning the installed app or empty item.
   *
   * @param item App to filter.
   * @return {@link Observable} to a {@link Installed} or empty.
   */
  private Observable<Installed> filterUpdates(Installed item) {
    return updateRepository.contains(item.getPackageName(), false).flatMap(isUpdate -> {
      if (isUpdate) {
        return Observable.empty();
      }
      return Observable.just(item);
    });
  }
}
