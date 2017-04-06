package cm.aptoide.pt.v8engine.view.downloads;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEventConverter;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEventConverter;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.repository.DownloadRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.downloads.completed.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.store.StoreGridHeaderDisplayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 7/15/16.
 */
@Deprecated public class DownloadsFragment extends GridRecyclerFragmentWithDecorator {

  private static final String TAG = DownloadsFragment.class.getName();

  // list of apps in the same state
  private List<Displayable> downloadingDisplayables;
  private List<Displayable> standingByDisplayables;
  private List<Displayable> completedDisplayables;

  private InstallManager installManager;
  private Analytics analytics;
  private InstallEventConverter installConverter;
  private DownloadEventConverter downloadConverter;
  private View noDownloadsView;

  public static DownloadsFragment newInstance() {
    return new DownloadsFragment();
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_fragment_downloads;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    noDownloadsView = view.findViewById(R.id.no_apps_downloaded);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // TODO: 1/2/2017 optimize this listener splitting it in 3 listeners: one for each download state

    DownloadRepository downloadRepo = RepositoryFactory.getDownloadRepository();
    downloadRepo.getAll()
        .observeOn(Schedulers.computation())
        .sample(100, TimeUnit.MILLISECONDS)
        .doOnNext(__ -> {
          downloadingDisplayables.clear();
          standingByDisplayables.clear();
          completedDisplayables.clear();
        })
        .flatMap(data -> Observable.from(data)
            .flatMap(
                downloadProgress -> createDisplayableForDownload(downloadProgress).toObservable())
            .toList())
        // wait for all displayables are created
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(__ -> addListHeaders().andThen(updateUi()).doOnCompleted(() -> {
          Logger.v(TAG, "updated list of download states");
        }).toObservable())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> {
          // does nothing
        }, err -> {
          CrashReport.getInstance().log(err);
        });
  }

  @SuppressLint("MissingSuperCall") @Override
  public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    // not calling super on purpose to avoid cleaning displayables
  }

  private Completable createDisplayableForDownload(Download download) {
    return Completable.fromAction(() -> {
      if (isDownloading(download)) {
        downloadingDisplayables.add(new ActiveDownloadDisplayable(download, installManager));
      } else if (isStandingBy(download)) {
        standingByDisplayables.add(
            new CompletedDownloadDisplayable(download, installManager, downloadConverter, analytics,
                installConverter));
      } else {
        // then it is complete
        completedDisplayables.add(
            new CompletedDownloadDisplayable(download, installManager, downloadConverter, analytics,
                installConverter));
      }
    });
  }

  private Completable addListHeaders() {
    return Completable.fromAction(() -> {

      // add each list header displayable

      if (!downloadingDisplayables.isEmpty()) {
        downloadingDisplayables.add(0,
            new ActiveDownloadsHeaderDisplayable(AptoideUtils.StringU.getResString(R.string.active),
                installManager));
      }

      if (!standingByDisplayables.isEmpty()) {
        standingByDisplayables.add(0, new StoreGridHeaderDisplayable(
            new GetStoreWidgets.WSWidget().setTitle(
                AptoideUtils.StringU.getResString(R.string.stand_by))));
      }
      if (!completedDisplayables.isEmpty()) {
        completedDisplayables.add(0, new StoreGridHeaderDisplayable(
            new GetStoreWidgets.WSWidget().setTitle(
                AptoideUtils.StringU.getResString(R.string.completed))));
      }
    });
  }

  private Completable updateUi() {
    return Completable.fromAction(() -> {
      if (emptyDownloadList()) {
        clearDisplayables();
        finishLoading();
        noDownloadsView.setVisibility(View.VISIBLE);
      } else {
        noDownloadsView.setVisibility(View.GONE);

        clearDisplayables().
            addDisplayables(downloadingDisplayables, false).
            addDisplayables(standingByDisplayables, false).
            addDisplayables(completedDisplayables, true);
      }
    });
  }

  private boolean isDownloading(Download progress) {
    return progress.getOverallDownloadStatus() == Download.PROGRESS;
  }

  private boolean isStandingBy(Download progress) {
    return progress.getOverallDownloadStatus() == Download.ERROR
        || progress.getOverallDownloadStatus() == Download.PENDING
        || progress.getOverallDownloadStatus() == Download.PAUSED
        || progress.getOverallDownloadStatus() == Download.IN_QUEUE;
  }

  private boolean emptyDownloadList() {
    return downloadingDisplayables != null
        && downloadingDisplayables.size() == 0
        && standingByDisplayables != null
        && standingByDisplayables.size() == 0
        && completedDisplayables != null
        && completedDisplayables.size() == 0;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // variables initialization

    downloadingDisplayables = new ArrayList<>();
    standingByDisplayables = new ArrayList<>();
    completedDisplayables = new ArrayList<>();

    installManager = new InstallManager(AptoideDownloadManager.getInstance(),
        new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK));
    analytics = Analytics.getInstance();
    final BodyInterceptor<BaseBody> baseBodyBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    installConverter = new InstallEventConverter(baseBodyBodyInterceptor);
    downloadConverter = new DownloadEventConverter(baseBodyBodyInterceptor);
  }
}
