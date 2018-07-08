/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.util.ErrorUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.model.v7.Datalist;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.model.v7.timeline.Recommendation;
import cm.aptoide.pt.model.v7.timeline.Similar;
import cm.aptoide.pt.model.v7.timeline.SocialArticle;
import cm.aptoide.pt.model.v7.timeline.SocialInstall;
import cm.aptoide.pt.model.v7.timeline.SocialRecommendation;
import cm.aptoide.pt.model.v7.timeline.SocialStoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.SocialVideo;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.Video;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.repository.PackageRepository;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineCardFilter;
import cm.aptoide.pt.v8engine.repository.TimelineMetricsManager;
import cm.aptoide.pt.v8engine.repository.TimelineRepository;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.ProgressBarDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.TimeLineStatsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.TimelineLoginDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SimilarDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialRecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialVideoDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.VideoDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.RxEndlessRecyclerView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class AppsTimelineFragment<T extends BaseAdapter> extends GridRecyclerSwipeFragment<T> {

  public static final int SEARCH_LIMIT = 20;
  private static final String ACTION_KEY = "ACTION";
  private static final String PACKAGE_LIST_KEY = "PACKAGE_LIST";
  private DownloadFactory downloadFactory;
  private SpannableFactory spannableFactory;
  private LinksHandlerFactory linksHandlerFactory;
  private DateCalculator dateCalculator;
  private boolean loading;
  private int offset;
  private int total;
  private Subscription subscription;
  private TimelineRepository timelineRepository;
  private PackageRepository packageRepository;
  private List<String> packages;
  private Installer installer;
  private InstallManager installManager;
  private PermissionManager permissionManager;
  private TimelineMetricsManager timelineMetricsManager;
  private SocialRepository socialRepository;
  private boolean isSocialLoginAvailable;

  public static AppsTimelineFragment newInstance(String action, String storeName) {
    AppsTimelineFragment fragment = new AppsTimelineFragment();

    final Bundle args = new Bundle();
    args.putString(ACTION_KEY, action);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dateCalculator = new DateCalculator();
    spannableFactory = new SpannableFactory();
    downloadFactory = new DownloadFactory();
    linksHandlerFactory = new LinksHandlerFactory();
    packageRepository = new PackageRepository(getContext().getPackageManager());
    permissionManager = new PermissionManager();
    installer = new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK);
    timelineRepository = new TimelineRepository(getArguments().getString(ACTION_KEY),
        new TimelineCardFilter(new TimelineCardFilter.TimelineCardDuplicateFilter(new HashSet<>()),
            AccessorFactory.getAccessorFor(Installed.class)));
    installManager = new InstallManager(AptoideDownloadManager.getInstance(), installer);
    timelineMetricsManager = new TimelineMetricsManager(Analytics.getInstance());
    socialRepository = new SocialRepository();
    isSocialLoginAvailable = isAnySocialLoginAvailable();
  }

  /**
   * @return true if any social login is available
   */
  @Partners private boolean isAnySocialLoginAvailable() {
    for (AptoidePreferencesConfiguration.SocialLogin socialLogin : AptoidePreferencesConfiguration.SocialLogin
        .values()) {
      if (Application.getConfiguration().isLoginAvailable(socialLogin)) {
        return true;
      }
    }
    return false;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);

    if (subscription != null) {
      subscription.unsubscribe();
    }

    final Observable<List<String>> packagesObservable;
    final Observable<Datalist<Displayable>> displayableObservable;
    if (create) {
      if (savedInstanceState != null
          && savedInstanceState.getStringArray(PACKAGE_LIST_KEY) != null) {
        packages = Arrays.asList(savedInstanceState.getStringArray(PACKAGE_LIST_KEY));
        packagesObservable = Observable.just(packages);
      } else {
        packagesObservable = refreshPackages();
      }
      displayableObservable = packagesObservable.flatMap(
          packages -> Observable.concat(getFreshDisplayables(refresh, packages),
              getNextDisplayables(packages)));
    } else {

      if (packages != null) {
        packagesObservable = Observable.just(packages);
      } else {
        packagesObservable = refreshPackages();
      }

      if (getAdapter().getItemCount() == 0) {
        displayableObservable = packagesObservable.flatMap(
            packages -> Observable.concat(getFreshDisplayables(refresh, packages),
                getNextDisplayables(packages)));
      } else {
        displayableObservable =
            packagesObservable.flatMap(packages -> getNextDisplayables(packages));
      }
    }

    subscription = displayableObservable.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(items -> addItems(items), throwable -> finishLoading(throwable));
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if (packages != null) {
      outState.putStringArray(PACKAGE_LIST_KEY, packages.toArray(new String[packages.size()]));
    }
    super.onSaveInstanceState(outState);
  }

  @NonNull private Observable<List<String>> refreshPackages() {
    return Observable.concat(packageRepository.getLatestInstalledPackages(10),
        packageRepository.getRandomInstalledPackages(10))
        .toList()
        .doOnNext(packages -> setPackages(packages));
  }

  private void setPackages(List<String> packages) {
    this.packages = packages;
  }

  @NonNull private Observable<Datalist<Displayable>> getFreshDisplayables(boolean refresh,
      List<String> packages) {

    return getDisplayableList(packages, 0, refresh).doOnNext(
        item -> getAdapter().clearDisplayables()).flatMap(displayableDatalist -> {
      if (!displayableDatalist.getList().isEmpty()) {
        if (AptoideAccountManager.isLoggedIn()) {
          return timelineRepository.getTimelineStats(refresh).map(timelineStats -> {
            if (isSocialLoginAvailable) {
              displayableDatalist.getList()
                  .add(0,
                      new TimeLineStatsDisplayable(timelineStats, spannableFactory, storeTheme));
            }
            return displayableDatalist;
          }).onErrorReturn(throwable -> {
            CrashReport.getInstance().log(throwable);
            return displayableDatalist;
          });
        } else {
          if (isSocialLoginAvailable) {
            displayableDatalist.getList().add(0, new TimelineLoginDisplayable());
          }
          return Observable.just(displayableDatalist);
        }
      } else {
        return Observable.just(displayableDatalist);
      }
    }).doOnUnsubscribe(() -> finishLoading());
  }

  @Override public void reload() {
    Analytics.AppsTimeline.pullToRefresh();
    load(true, true, null);
  }

  private Observable<Datalist<Displayable>> getNextDisplayables(List<String> packages) {
    return RxEndlessRecyclerView.loadMore(getRecyclerView(), getAdapter())
        .filter(item -> onStartLoadNext())
        .concatMap(item -> getDisplayableList(packages, getOffset(), false))
        .delay(1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .retryWhen(errors -> errors.delay(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .filter(error -> onStopLoadNext(error)))
        .doOnUnsubscribe(() -> removeLoading())
        .subscribeOn(AndroidSchedulers.mainThread());
  }

  @NonNull
  private Observable<Datalist<Displayable>> getDisplayableList(List<String> packages, int offset,
      boolean refresh) {
    return timelineRepository.getTimelineCards(SEARCH_LIMIT, offset, packages, refresh)
        .flatMap(datalist -> Observable.just(datalist)
            .flatMapIterable(dataList -> dataList.getList())
            .map(card -> cardToDisplayable(card, dateCalculator, spannableFactory, downloadFactory,
                linksHandlerFactory))
            .toList()
            .map(list -> createDisplayableDataList(datalist, list)));
  }

  private Datalist<Displayable> createDisplayableDataList(Datalist<TimelineCard> datalist,
      List<Displayable> list) {
    Datalist<Displayable> displayableDataList = new Datalist<>();
    displayableDataList.setNext(datalist.getNext());
    displayableDataList.setCount(datalist.getCount());
    displayableDataList.setHidden(datalist.getHidden());
    displayableDataList.setTotal(datalist.getTotal());
    displayableDataList.setLimit(datalist.getLimit());
    displayableDataList.setOffset(datalist.getOffset());
    displayableDataList.setLoaded(datalist.isLoaded());
    displayableDataList.setList(list);
    return displayableDataList;
  }

  private void showErrorSnackbar(Throwable error) {
    @StringRes int errorString;
    if (ErrorUtils.isNoNetworkConnection(error)) {
      errorString = R.string.fragment_social_timeline_no_connection;
    } else {
      errorString = R.string.fragment_social_timeline_general_error;
    }
    //Todo: switch to showmessage snack
    Snackbar.make(getView(), errorString, Snackbar.LENGTH_SHORT).show();
  }

  private boolean isTotal() {
    return offset >= total;
  }

  public void setTotal(Datalist<Displayable> dataList) {
    if (dataList != null && dataList.getTotal() != 0) {
      total = dataList.getTotal();
    }
  }

  private int getOffset() {
    return offset;
  }

  private void setOffset(Datalist<Displayable> dataList) {
    if (dataList != null && dataList.getNext() != 0) {
      offset = dataList.getNext();
    }
  }

  private void addLoading() {
    if (!loading) {
      this.loading = true;
      getAdapter().addDisplayable(new ProgressBarDisplayable().setFullRow());
    }
  }

  private void removeLoading() {
    if (loading) {
      loading = false;
      getAdapter().popDisplayable();
    }
  }

  private boolean isLoading() {
    return loading;
  }

  private void addItems(Datalist<Displayable> data) {
    removeLoading();
    addDisplayables(data.getList());
    setTotal(data);
    setOffset(data);
  }

  @NonNull private boolean onStopLoadNext(Throwable error) {
    if (isLoading()) {
      showErrorSnackbar(error);
      removeLoading();
      return true;
    }
    return false;
  }

  @NonNull private boolean onStartLoadNext() {
    if (!isTotal() && !isLoading()) {
      Analytics.AppsTimeline.endlessScrollLoadMore();
      addLoading();
      return true;
    } else if (isTotal()) {
      //TODO - When you reach the end of the endless?
      // Snackbar.make(getView(), "No more cards!", Snackbar.LENGTH_SHORT).show();
    }
    return false;
  }

  @NonNull private Displayable cardToDisplayable(TimelineCard card, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, DownloadFactory downloadFactory,
      LinksHandlerFactory linksHandlerFactory) {
    if (card instanceof Article) {
      return ArticleDisplayable.from((Article) card, dateCalculator, spannableFactory,
          linksHandlerFactory, timelineMetricsManager, socialRepository);
    } else if (card instanceof Video) {
      return VideoDisplayable.from((Video) card, dateCalculator, spannableFactory,
          linksHandlerFactory, timelineMetricsManager, socialRepository);
    } else if (card instanceof SocialArticle) {
      return SocialArticleDisplayable.from(((SocialArticle) card), dateCalculator, spannableFactory,
          linksHandlerFactory, timelineMetricsManager, socialRepository);
    } else if (card instanceof SocialVideo) {
      return SocialVideoDisplayable.from(((SocialVideo) card), dateCalculator, spannableFactory,
          linksHandlerFactory, timelineMetricsManager, socialRepository);
    } else if (card instanceof SocialStoreLatestApps) {
      return SocialStoreLatestAppsDisplayable.from((SocialStoreLatestApps) card, dateCalculator,
          timelineMetricsManager, socialRepository, spannableFactory);
    } else if (card instanceof Feature) {
      return FeatureDisplayable.from((Feature) card, dateCalculator, spannableFactory);
    } else if (card instanceof StoreLatestApps) {
      return StoreLatestAppsDisplayable.from((StoreLatestApps) card, dateCalculator,
          timelineMetricsManager, socialRepository);
    } else if (card instanceof AppUpdate) {
      return AppUpdateDisplayable.from((AppUpdate) card, spannableFactory, downloadFactory,
          dateCalculator, installManager, permissionManager, timelineMetricsManager,
          socialRepository);
    } else if (card instanceof Recommendation) {
      return RecommendationDisplayable.from((Recommendation) card, dateCalculator, spannableFactory,
          timelineMetricsManager, socialRepository);
    } else if (card instanceof Similar) {
      return SimilarDisplayable.from((Similar) card, dateCalculator, spannableFactory,
          timelineMetricsManager, socialRepository);
    } else if (card instanceof SocialInstall) {
      return SocialInstallDisplayable.from((SocialInstall) card, timelineMetricsManager,
          spannableFactory, socialRepository, dateCalculator);
    } else if (card instanceof SocialRecommendation) {
      return SocialRecommendationDisplayable.from((SocialRecommendation) card,
          timelineMetricsManager, spannableFactory, socialRepository, dateCalculator);
    }
    throw new IllegalArgumentException(
        "Only articles, features, store latest apps, app updates, videos, recommendations and similar cards supported.");
  }

  public void goToTop() {
    GridLayoutManager layoutManager = ((GridLayoutManager) getRecyclerView().getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      getRecyclerView().scrollToPosition(10);
    }
    getRecyclerView().smoothScrollToPosition(0);
  }
}
