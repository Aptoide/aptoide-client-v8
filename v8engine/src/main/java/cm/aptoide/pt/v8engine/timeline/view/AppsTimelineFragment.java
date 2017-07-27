/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.timeline.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v7.widget.GridLayoutManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.dataprovider.util.ErrorUtils;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.PackageRepository;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.download.DownloadEventConverter;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
import cm.aptoide.pt.v8engine.download.InstallEventConverter;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.TimelineCardFilter;
import cm.aptoide.pt.v8engine.timeline.TimelineRepository;
import cm.aptoide.pt.v8engine.timeline.view.displayable.TimeLineStatsDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.login.TimelineLoginDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineNavigator;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineTabNavigation;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.navigator.TabNavigation;
import cm.aptoide.pt.v8engine.view.navigator.TabNavigator;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.ProgressBarDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.rx.RxEndlessRecyclerView;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class AppsTimelineFragment<T extends BaseAdapter> extends GridRecyclerSwipeFragment<T> {

  public static final int SEARCH_LIMIT = 20;
  public static final int LATEST_INSTALLED_PACKAGES = 20;
  public static final int RANDOM_INSTALLED_PACKAGES = 10;
  private static final String USER_ID_KEY = "USER_ID_KEY";
  private static final String ACTION_KEY = "ACTION";
  private static final String STORE_ID = "STORE_ID";
  private static final String PACKAGE_LIST_KEY = "PACKAGE_LIST";
  private static final String STORE_CONTEXT = "STORE_CONTEXT";
  private static final String LIST_STATE_KEY = "LIST_STATE";
  private DownloadFactory downloadFactory;
  private SpannableFactory spannableFactory;
  private LinksHandlerFactory linksHandlerFactory;
  private DateCalculator dateCalculator;
  private boolean isLoading;
  private int offset;
  private int total;
  private TimelineRepository timelineRepository;
  private PackageRepository packageRepository;
  private List<String> packages;
  private TimelineAnalytics timelineAnalytics;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private long storeId;
  private CardToDisplayable cardToDisplayable;
  private BehaviorRelay<Boolean> refreshSubject;
  private Parcelable listState;
  private Displayable spinnerProgressDisplayable;
  private StoreContext storeContext;
  private TabNavigator tabNavigator;
  private String cardIdPriority;

  public static AppsTimelineFragment newInstance(String action, Long userId, Long storeId,
      StoreContext storeContext) {
    AppsTimelineFragment fragment = new AppsTimelineFragment();

    final Bundle args = new Bundle();
    if (storeId != null) {
      args.putLong(STORE_ID, storeId);
    }
    args.putString(ACTION_KEY, action);
    if (userId != null) {
      args.putLong(USER_ID_KEY, userId);
    }
    args.putSerializable(STORE_CONTEXT, storeContext);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof TabNavigator) {
      tabNavigator = (TabNavigator) activity;
    } else {
      throw new IllegalStateException(
          "Activity must implement " + TabNavigator.class.getSimpleName());
    }
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    if (args.containsKey(STORE_ID)) {
      storeId = args.getLong(STORE_ID);
    }
    storeContext = (StoreContext) args.getSerializable(STORE_CONTEXT);
  }

  @Override public void onDestroyView() {
    listState = getRecyclerView().getLayoutManager()
        .onSaveInstanceState();
    super.onDestroyView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());

    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(PACKAGE_LIST_KEY)) {
        packages = Arrays.asList(savedInstanceState.getStringArray(PACKAGE_LIST_KEY));
      }
    }

    Observable<List<String>> packagesObservable =
        (packages != null) ? Observable.just(packages) : refreshPackages();

    Observable<DataList<Displayable>> displayableObservable = accountManager.accountStatus()
        .first()
        .toSingle()
        .map(account -> account.isLoggedIn())
        .onErrorReturn(throwable -> false)
        .flatMapObservable(loggedIn -> packagesObservable.observeOn(AndroidSchedulers.mainThread())
            .flatMap(packages -> Observable.merge(refreshSubject.flatMap(
                refreshed -> clearView().flatMap(
                    refresh -> getFreshDisplayables(refreshed, packages, loggedIn,
                        cardIdPriority))), getNextDisplayables(packages))));

    displayableObservable.observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .doOnNext(items -> {
          addItems(items);
          restoreListState(savedInstanceState);
        })
        .subscribe(__ -> {
        }, err -> {
          CrashReport.getInstance()
              .log(err);
          finishLoading(err);
        });
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    refreshSubject.call(!create);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if (packages != null) {
      outState.putStringArray(PACKAGE_LIST_KEY, packages.toArray(new String[packages.size()]));
    }

    if (getRecyclerView() != null) {
      outState.putParcelable(LIST_STATE_KEY, getRecyclerView().getLayoutManager()
          .onSaveInstanceState());
    }

    super.onSaveInstanceState(outState);
  }

  private void restoreListState(@Nullable Bundle savedInstanceState) {
    if (listState != null) {
      getRecyclerView().getLayoutManager()
          .onRestoreInstanceState(listState);
      listState = null;
    }
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(LIST_STATE_KEY)) {
        getRecyclerView().getLayoutManager()
            .onRestoreInstanceState(savedInstanceState.getParcelable(LIST_STATE_KEY));
        savedInstanceState.putParcelable(LIST_STATE_KEY, null);
      }
    }
  }

  @UiThread private Observable<Void> clearView() {
    return Observable.fromCallable(() -> {
      clearDisplayables();
      return null;
    });
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Context applicationContext = getContext().getApplicationContext();

    accountManager = ((V8Engine) applicationContext).getAccountManager();
    final BodyInterceptor<BaseBody> bodyInterceptor =
        ((V8Engine) applicationContext).getBaseBodyInterceptorV7();
    final OkHttpClient httpClient = ((V8Engine) applicationContext).getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    final TokenInvalidator tokenInvalidator =
        ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    SharedPreferences sharedPreferences =
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences();
    timelineAnalytics = new TimelineAnalytics(Analytics.getInstance(),
        AppEventsLogger.newLogger(applicationContext), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, V8Engine.getConfiguration()
        .getAppId(), sharedPreferences);
    dateCalculator = new DateCalculator(getContext().getApplicationContext(),
        getContext().getApplicationContext()
            .getResources());
    spannableFactory = new SpannableFactory();
    downloadFactory = new DownloadFactory();
    linksHandlerFactory = new LinksHandlerFactory(getContext());
    packageRepository = ((V8Engine) getContext().getApplicationContext()).getPackageRepository();
    spinnerProgressDisplayable = new ProgressBarDisplayable().setFullRow();
    InstalledRepository installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());

    final PermissionManager permissionManager = new PermissionManager();
    final SocialRepository socialRepository =
        new SocialRepository(accountManager, bodyInterceptor, converterFactory, httpClient,
            timelineAnalytics, tokenInvalidator, sharedPreferences);
    final StoreCredentialsProvider storeCredentialsProvider = new StoreCredentialsProviderImpl(
        AccessorFactory.getAccessorFor(((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
    final InstallManager installManager =
        ((V8Engine) getContext().getApplicationContext()).getInstallManager(
            InstallerFactory.ROLLBACK);

    timelineRepository = new TimelineRepository(getArguments().getString(ACTION_KEY),
        new TimelineCardFilter(new TimelineCardFilter.TimelineCardDuplicateFilter(new HashSet<>()),
            installedRepository), bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences);

    final ConnectivityManager connectivityManager =
        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    final TelephonyManager telephonyManager =
        (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
    final WindowManager windowManager =
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    cardToDisplayable =
        new CardToDisplayableConverter(socialRepository, timelineAnalytics, installManager,
            permissionManager, storeCredentialsProvider,
            new InstallEventConverter(bodyInterceptor, httpClient, converterFactory,
                tokenInvalidator, V8Engine.getConfiguration()
                .getAppId(), sharedPreferences, connectivityManager, telephonyManager),
            Analytics.getInstance(),
            new DownloadEventConverter(bodyInterceptor, httpClient, converterFactory,
                tokenInvalidator, V8Engine.getConfiguration()
                .getAppId(), sharedPreferences, connectivityManager, telephonyManager),
            new AppsTimelineNavigator(getFragmentNavigator(),
                getContext().getString(R.string.timeline_title_likes)), getContext().getResources(),
            Application.getConfiguration()
                .getMarketName(), windowManager, installedRepository);

    refreshSubject = BehaviorRelay.create();

    tabNavigator.navigation()
        .filter(tabNavigation -> tabNavigation.getTab() == TabNavigation.TIMELINE)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(tabNavigation -> cardIdPriority = tabNavigation.getBundle()
            .getString(AppsTimelineTabNavigation.CARD_ID_KEY), err -> CrashReport.getInstance()
            .log(err));
  }

  @NonNull private Observable<List<String>> refreshPackages() {
    return Observable.concat(
        packageRepository.getLatestInstalledPackages(LATEST_INSTALLED_PACKAGES),
        packageRepository.getRandomInstalledPackages(RANDOM_INSTALLED_PACKAGES))
        .toList()
        .doOnNext(packages -> setPackages(packages));
  }

  private void setPackages(List<String> packages) {
    this.packages = packages;
  }

  @NonNull private Observable<DataList<Displayable>> getFreshDisplayables(boolean refresh,
      List<String> packages, boolean loggedIn, String cardIdPriority) {
    return getDisplayableList(packages, 0, refresh, cardIdPriority).flatMap(displayableDatalist -> {
      Long userId =
          getArguments().containsKey(USER_ID_KEY) ? getArguments().getLong(USER_ID_KEY) : null;

      if (loggedIn || userId != null) {
        return getUserTimelineStats(refresh, displayableDatalist, userId);
      } else {
        displayableDatalist.getList()
            .add(0, new TimelineLoginDisplayable().setAccountNavigator(accountNavigator));
        return Observable.just(displayableDatalist);
      }
    });
  }

  @NonNull private Observable<DataList<Displayable>> getUserTimelineStats(boolean refresh,
      DataList<Displayable> displayableDatalist, Long userId) {
    return timelineRepository.getTimelineStats(refresh, userId)
        .map(timelineStats -> {
          TimeLineStatsDisplayable timeLineStatsDisplayable =
              new TimeLineStatsDisplayable(timelineStats, userId, spannableFactory, storeTheme,
                  timelineAnalytics, userId == null,
                  storeContext == StoreContext.home ? 0 : storeId, getContext().getResources());
          displayableDatalist.getList()
              .add(0, timeLineStatsDisplayable);
          return displayableDatalist;
        })
        .onErrorReturn(throwable -> {
          CrashReport.getInstance()
              .log(throwable);
          return displayableDatalist;
        });
  }

  @Override public void reload() {
    super.reload();
    Analytics.AppsTimeline.pullToRefresh();
    cleanCardIdPriority();
  }

  private void cleanCardIdPriority() {
    cardIdPriority = null;
  }

  private Observable<DataList<Displayable>> getNextDisplayables(List<String> packages) {
    return RxEndlessRecyclerView.loadMore(getRecyclerView(), getAdapter())
        .observeOn(AndroidSchedulers.mainThread())
        .filter(item -> onStartLoadNext())
        .concatMap(item -> getDisplayableList(packages, getOffset(), false, cardIdPriority))
        .delay(1, TimeUnit.SECONDS)
        .retryWhen(errors -> errors.delay(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .filter(error -> onStopLoadNext(error)))
        .subscribeOn(AndroidSchedulers.mainThread());
  }

  @NonNull
  private Observable<DataList<Displayable>> getDisplayableList(List<String> packages, int offset,
      boolean refresh, String cardId) {
    return timelineRepository.getTimelineCards(SEARCH_LIMIT, offset, packages, refresh, cardId)
        .flatMap(datalist -> Observable.just(datalist)
            .flatMapIterable(dataList -> dataList.getList())
            .map(card -> cardToDisplayable.convert(card, dateCalculator, spannableFactory,
                downloadFactory, linksHandlerFactory))
            .toList()
            .map(list -> createDisplayableDataList(datalist, list)));
  }

  private DataList<Displayable> createDisplayableDataList(DataList<TimelineCard> datalist,
      List<Displayable> list) {
    DataList<Displayable> displayableDataList = new DataList<>();
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

  @UiThread private void showErrorSnackbar(Throwable error) {
    @StringRes int errorString;
    if (ErrorUtils.isNoNetworkConnection(error)) {
      errorString = R.string.fragment_social_timeline_no_connection;
    } else {
      errorString = R.string.all_message_general_error;
    }
    ShowMessage.asSnack(getView(), errorString);
  }

  private boolean isTotal() {
    return offset >= total;
  }

  public void setTotal(DataList<Displayable> dataList) {
    if (dataList != null && dataList.getTotal() != 0) {
      total = dataList.getTotal();
    }
  }

  private int getOffset() {
    return offset;
  }

  private void setOffset(DataList<Displayable> dataList) {
    if (dataList != null && dataList.getNext() != 0) {
      offset = dataList.getNext();
    }
  }

  protected boolean isLoading() {
    return isLoading;
  }

  protected void setLoading(boolean loading) {
    this.isLoading = loading;
  }

  @UiThread private void removeLoading() {
    if (isLoading) {
      isLoading = false;
      getAdapter().removeDisplayable(spinnerProgressDisplayable);
    }
  }

  @UiThread private void addLoading() {
    if (!isLoading) {
      isLoading = true;
      getAdapter().addDisplayable(spinnerProgressDisplayable);
    }
  }

  @UiThread private void addItems(DataList<Displayable> data) {
    removeLoading();
    setTotal(data);
    setOffset(data);
    addDisplayables(data.getList(), true);
  }

  @UiThread @NonNull private boolean onStopLoadNext(Throwable error) {
    if (isLoading()) {
      removeLoading();
      showErrorSnackbar(error);
      return true;
    }
    return false;
  }

  @UiThread @NonNull private boolean onStartLoadNext() {
    if (!isTotal() && !isLoading()) {
      addLoading();
      Analytics.AppsTimeline.endlessScrollLoadMore();
      return true;
    }
    return false;
  }

  @UiThread public void goToTop() {
    GridLayoutManager layoutManager = ((GridLayoutManager) getRecyclerView().getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      getRecyclerView().scrollToPosition(10);
    }
    getRecyclerView().smoothScrollToPosition(0);
  }
}
