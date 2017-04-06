package cm.aptoide.pt.v8engine.view.updates;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEventConverter;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEventConverter;
import cm.aptoide.pt.v8engine.view.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.store.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.updates.installed.InstalledAppDisplayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
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

  private int oldUpdateListHash = 0;

  @NonNull public static UpdatesFragment newInstance() {
    return new UpdatesFragment();
  }

  @Override public void setupViews() {
    super.setupViews();

    installManager = new InstallManager(AptoideDownloadManager.getInstance(),
        new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK));
    analytics = Analytics.getInstance();
    final BodyInterceptor<BaseBody> bodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    downloadInstallEventConverter = new DownloadEventConverter(bodyInterceptor);
    installConverter = new InstallEventConverter(bodyInterceptor);

    updatesDisplayablesList = new LinkedList<>();
    installedDisplayablesList = new LinkedList<>();

    installedRepository = RepositoryFactory.getInstalledRepository();
    updateRepository = RepositoryFactory.getUpdateRepository(getContext());
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

    updateReloadSubscription = updateRepository.sync(true).subscribe(() -> finishLoading(), e -> {
      if (e instanceof RepositoryItemNotFoundException) {
        ShowMessage.asSnack(getView(), R.string.add_store);
      }
      CrashReport.getInstance().log(e);
      finishLoading();
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    if (updateReloadSubscription != null && !updateReloadSubscription.isUnsubscribed()) {
      updateReloadSubscription.unsubscribe();
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // show updates
    updateRepository.getAll(false)
        //.buffer(750, TimeUnit.MILLISECONDS)
        //.flatMap(listOfUpdateList -> Observable.from(listOfUpdateList).takeLast(1))
        .sample(750, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .flatMap(updateList -> {
          clearDisplayables();
          setUpdates(updateList);
          showUpdateMessage(updateList);
          return fetchInstalled().doOnNext(apps -> setInstalled(apps));
        })
        .subscribe(__ -> {
          finishLoading();
          Logger.v(TAG, "listing updates and installed");
        }, err -> {
          Logger.e(TAG, "listing updates or installed threw an exception");
          CrashReport.getInstance().log(err);
          finishLoading();
        });
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    //super.load(create, refresh, savedInstanceState);
    // overridden to avoid calling super, since it removes the displayables automatically
  }

  private void setUpdates(List<Update> updateList) {
    updatesDisplayablesList.clear();

    if (updateList.size() > 0) {
      updatesDisplayablesList.add(new UpdatesHeaderDisplayable(installManager,
          AptoideUtils.StringU.getResString(R.string.updates), analytics,
          downloadInstallEventConverter, installConverter));

      for (Update update : updateList) {
        updatesDisplayablesList.add(
            UpdateDisplayable.newInstance(update, installManager, new DownloadFactory(), analytics,
                downloadInstallEventConverter, installConverter));
      }
    }
    addDisplayables(updatesDisplayablesList, false);
    Logger.v(TAG, "listed updates");
  }

  private Completable showUpdateMessage(List<Update> updates) {
    return Completable.fromCallable(() -> {

      int updateCount = updates != null ? updates.size() : 0;
      int currentUpdateListHash = updates != null ? updates.hashCode() : 0;

      if (updates != null && updates.isEmpty()) {
        ShowMessage.asSnack(getView(), R.string.no_updates_available_retoric);
      } else if (currentUpdateListHash != oldUpdateListHash) {
        ShowMessage.asSnack(getView(), String.format(getString(R.string.new_updates),
            Integer.toString(updateCount))); // using this to avoid changing strings
      } else {
        ShowMessage.asSnack(getView(), R.string.no_new_updates_available);
      }

      oldUpdateListHash = currentUpdateListHash;
      return null;
    });
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
        .filter(item -> !item.isSystemApp())
        .flatMap(item -> filterUpdates(item))
        .toList();
  }

  private void setInstalled(List<Installed> installedApps) {
    installedDisplayablesList.clear();
    installedDisplayablesList.add(new StoreGridHeaderDisplayable(
        new GetStoreWidgets.WSWidget().setTitle(
            AptoideUtils.StringU.getResString(R.string.installed_tab))));

    for (Installed installedApp : installedApps) {
      installedDisplayablesList.add(new InstalledAppDisplayable(installedApp));
    }
    addDisplayables(installedDisplayablesList, false);
    Logger.v(TAG, "listed installed apps");
  }

  /**
   * Filters updates returning the installed app or empty item.
   *
   * @param item App to filter.
   *
   * @return {@link Observable} to a {@link Installed} or empty.
   */
  // TODO: 31/1/2017 sithengineer instead of Observable<Installed> use Single<Installed>
  private Observable<Installed> filterUpdates(Installed item) {
    return updateRepository.contains(item.getPackageName(), false).flatMap(isUpdate -> {
      if (isUpdate) {
        return Observable.empty();
      }
      return Observable.just(item);
    });
  }
}
