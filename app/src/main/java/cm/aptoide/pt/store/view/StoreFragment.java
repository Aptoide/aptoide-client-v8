/*
 * Copyright (c) 2016.
 * Modified on 05/07/2016.
 */

package cm.aptoide.pt.store.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.dataprovider.model.v7.store.HomeUser;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.model.v7.store.StoreUserAbstraction;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetHomeRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.search.SuggestionCursorAdapter;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.search.view.AppSearchSuggestionsView;
import cm.aptoide.pt.search.view.SearchSuggestionsPresenter;
import cm.aptoide.pt.share.ShareStoreHelper;
import cm.aptoide.pt.social.view.TimelineFragment;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.store.view.home.HomeFragment;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.ThemeUtils;
import cm.aptoide.pt.view.custom.AptoideViewPager;
import cm.aptoide.pt.view.fragment.BasePagerToolbarFragment;
import com.astuetz.PagerSlidingTabStrip;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by neuro on 06-05-2016.
 */
public class StoreFragment extends BasePagerToolbarFragment {

  private static final String TAG = StoreFragment.class.getName();

  private final int PRIVATE_STORE_REQUEST_CODE = 20;
  protected PagerSlidingTabStrip pagerSlidingTabStrip;
  private AptoideAccountManager accountManager;
  private String storeName;
  private String title;
  private StoreContext storeContext;
  AptoideViewPager.SimpleOnPageChangeListener pageChangeListener =
      new AptoideViewPager.SimpleOnPageChangeListener() {
        @Override public void onPageSelected(int position) {
          if (position == 0) {
            navigationTracker.registerScreen(
                ScreenTagHistory.Builder.build(HomeFragment.class.getSimpleName(), "home",
                    storeContext));
          }
        }
      };
  private String storeTheme;
  private StoreCredentialsProvider storeCredentialsProvider;
  private Event.Name defaultTab;
  @Nullable private Long userId;
  private OpenType openType;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private List<GetStoreTabs.Tab> tabs;
  private Long storeId;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TimelineAnalytics timelineAnalytics;
  private TokenInvalidator tokenInvalidator;
  private StoreAnalytics storeAnalytics;
  private ShareStoreHelper shareStoreHelper;
  private String storeUrl;
  private String iconPath;
  private String marketName;
  private String defaultTheme;
  private Runnable registerViewpagerCurrentItem;
  private SharedPreferences sharedPreferences;

  private AppSearchSuggestionsView appSearchSuggestionsView;
  private CrashReport crashReport;
  private SearchNavigator searchNavigator;
  private TrendingManager trendingManager;
  private SearchAnalytics searchAnalytics;

  public static StoreFragment newInstance(long userId, String storeTheme, OpenType openType) {
    return newInstance(userId, storeTheme, null, openType);
  }

  public static StoreFragment newInstance(long userId, String storeTheme, Event.Name defaultTab,
      OpenType openType) {
    Bundle args = new Bundle();
    args.putLong(BundleKeys.USER_ID.name(), userId);
    args.putSerializable(BundleKeys.STORE_CONTEXT.name(), StoreContext.meta);
    args.putSerializable(BundleKeys.OPEN_TYPE.name(), openType);
    args.putString(BundleKeys.STORE_THEME.name(), storeTheme);
    args.putSerializable(BundleKeys.DEFAULT_TAB_TO_OPEN.name(), defaultTab);
    StoreFragment fragment = new StoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static StoreFragment newInstance(String storeName, String storeTheme,
      Event.Name defaultTab, OpenType openType) {
    StoreFragment storeFragment = newInstance(storeName, storeTheme, openType);
    storeFragment.getArguments()
        .putSerializable(BundleKeys.DEFAULT_TAB_TO_OPEN.name(), defaultTab);
    return storeFragment;
  }

  public static StoreFragment newInstance(String storeName, String storeTheme,
      StoreFragment.OpenType openType) {
    Bundle args = new Bundle();
    args.putString(BundleKeys.STORE_NAME.name(), storeName);
    args.putSerializable(BundleKeys.OPEN_TYPE.name(), openType);
    args.putSerializable(BundleKeys.STORE_CONTEXT.name(), StoreContext.meta);
    args.putString(BundleKeys.STORE_THEME.name(), storeTheme);
    StoreFragment fragment = new StoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static StoreFragment newInstance(String storeName, String storeTheme) {
    return newInstance(storeName, storeTheme, OpenType.GetStore);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", storeContext);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    defaultTheme = application.getDefaultThemeName();
    tokenInvalidator = application.getTokenInvalidator();
    storeCredentialsProvider = new StoreCredentialsProviderImpl(
        AccessorFactory.getAccessorFor(application.getDatabase(),
            cm.aptoide.pt.database.realm.Store.class));
    accountManager = application.getAccountManager();
    bodyInterceptor = application.getAccountSettingsBodyInterceptorPoolV7();
    httpClient = application.getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    Analytics analytics = Analytics.getInstance();
    sharedPreferences = application.getDefaultSharedPreferences();
    timelineAnalytics = new TimelineAnalytics(analytics,
        AppEventsLogger.newLogger(getContext().getApplicationContext()), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, BuildConfig.APPLICATION_ID,
        sharedPreferences, application.getNotificationAnalytics(), navigationTracker,
        application.getReadPostsPersistence());
    storeAnalytics = new StoreAnalytics(AppEventsLogger.newLogger(getContext()), analytics);
    marketName = application.getMarketName();
    shareStoreHelper = new ShareStoreHelper(getActivity(), marketName);

    if (hasSearchFromStoreFragment()) {
      searchAnalytics = new SearchAnalytics(analytics, AppEventsLogger.newLogger(getContext()));
      searchNavigator =
          new SearchNavigator(getFragmentNavigator(), storeName, application.getDefaultStoreName());
      trendingManager = application.getTrendingManager();
      crashReport = CrashReport.getInstance();
    }

    setHasOptionsMenu(true);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    storeName = args.getString(BundleKeys.STORE_NAME.name());
    storeContext = (StoreContext) args.get(BundleKeys.STORE_CONTEXT.name());
    openType = args.containsKey(BundleKeys.OPEN_TYPE.name()) ? (OpenType) args.get(
        BundleKeys.OPEN_TYPE.name()) : OpenType.GetStore;
    storeTheme = args.getString(BundleKeys.STORE_THEME.name());
    defaultTab = (Event.Name) args.get(BundleKeys.DEFAULT_TAB_TO_OPEN.name());
    if (args.containsKey(BundleKeys.USER_ID.name())) {
      userId = args.getLong(BundleKeys.USER_ID.name());
    }
  }

  protected boolean hasSearchFromStoreFragment() {
    return true;
  }

  @Override public void onDestroyView() {

    // reset to default theme in the toolbar
    // TODO re-do this ThemeUtils methods and avoid loading resources using
    // execution-time generated ids for the desired resource
    ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(defaultTheme));
    ThemeUtils.setAptoideTheme(getActivity(), defaultTheme);

    if (pagerSlidingTabStrip != null) {
      pagerSlidingTabStrip.setOnTabReselectedListener(null);
      pagerSlidingTabStrip = null;
    }
    viewPager.removeCallbacks(registerViewpagerCurrentItem);
    super.onDestroyView();
  }

  @Override protected void setupViewPager() {
    super.setupViewPager();
    pagerSlidingTabStrip = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);

    if (pagerSlidingTabStrip != null) {
      pagerSlidingTabStrip.setViewPager(viewPager);
    }

    /*
     *  Click on tab listener
     */
    StorePagerAdapter adapter = (StorePagerAdapter) viewPager.getAdapter();
    pagerSlidingTabStrip.setOnTabReselectedListener(position -> {
      if (Event.Name.getUserTimeline.equals(adapter.getEventName(position))) {
        //TODO We should not call fragment public methods since we do NOT know about its internal
        //life cycle. A fragment A should call its activity in order to communicate with
        //fragment B. Then when fragment B is ready it should register a listener
        //with its activity in order receive external communication. Activity
        //should buffer calls if there is no listener registered and deliver them
        //after registration happens.
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
          if (fragment != null && fragment instanceof TimelineFragment) {
            ((TimelineFragment) fragment).goToTop();
          }
        }
      }
    });

    /* Be careful maintaining this code
     * this affects both the main view pager when we open app
     * and the view pager inside the StoresView
     *
     * This code was changed when FAB was migrated to a followstorewidget 23/02/2017
     */
    viewPager.addOnPageChangeListener(new AptoideViewPager.SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
        StorePagerAdapter adapter = (StorePagerAdapter) viewPager.getAdapter();
        if (Event.Name.getUserTimeline.equals(adapter.getEventName(position))) {
          Analytics.AppsTimeline.openTimeline();
          timelineAnalytics.sendTimelineTabOpened();
        } else if (Event.Name.getStore.equals(adapter.getEventName(position))
            && storeContext.equals(StoreContext.home)) {
          storeAnalytics.sendStoreTabOpenedEvent();
        }
        if (storeContext.equals(StoreContext.meta)) {
          storeAnalytics.sendStoreInteractEvent("Open Tab", adapter.getPageTitle(position)
              .toString(), storeName);
        }
      }
    });
    viewPager.addOnPageChangeListener(pageChangeListener);
    registerViewpagerCurrentItem =
        () -> pageChangeListener.onPageSelected(viewPager.getCurrentItem());
    viewPager.post(registerViewpagerCurrentItem);
    changeToTab(defaultTab);
    finishLoading();
  }

  @Override protected PagerAdapter createPagerAdapter() {
    return new StorePagerAdapter(getChildFragmentManager(), tabs, storeContext, storeId, storeTheme,
        getContext().getApplicationContext(), marketName);
  }

  @Override public int getContentViewId() {
    return R.layout.store_activity;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == PRIVATE_STORE_REQUEST_CODE) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          load(true, true, null);
          break;
      }
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (storeTheme != null) {
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(defaultTheme));
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    if (hasSearchFromStoreFragment()) {
      inflater.inflate(R.menu.fragment_store, menu);

      final MenuItem menuItem = menu.findItem(R.id.menu_item_search);
      if (appSearchSuggestionsView != null && menuItem != null) {
        appSearchSuggestionsView.initialize(menuItem);
      } else if (menuItem != null) {
        menuItem.setVisible(false);
      } else {
        menu.removeItem(R.id.menu_item_search);
      }
    }
  }

  private void handleOptionsItemSelected(Observable<MenuItem> toolbarMenuItemClick) {
    getLifecycle().filter(event -> event == LifecycleEvent.RESUME)
        .flatMap(__ -> toolbarMenuItemClick)
        .filter(menuItem -> menuItem != null && menuItem.getItemId() == R.id.menu_item_share)
        .doOnNext(__ -> {
          shareStoreHelper.shareStore(storeUrl, iconPath);
        })
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, trowable -> crashReport.log(trowable));
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final SuggestionCursorAdapter suggestionCursorAdapter = new SuggestionCursorAdapter(getContext());

    if (hasSearchFromStoreFragment()) {
      final Toolbar toolbar = getToolbar();
      final Observable<MenuItem> toolbarMenuItemClick = RxToolbar.itemClicks(toolbar)
          .publish()
          .autoConnect();

      appSearchSuggestionsView =
          new AppSearchSuggestionsView(this, RxView.clicks(toolbar), crashReport,
              suggestionCursorAdapter, PublishSubject.create(), toolbarMenuItemClick, searchAnalytics);

      final AptoideApplication application =
          (AptoideApplication) getContext().getApplicationContext();

      final SearchSuggestionsPresenter searchSuggestionsPresenter =
          new SearchSuggestionsPresenter(appSearchSuggestionsView,
              application.getSearchSuggestionManager(), AndroidSchedulers.mainThread(),
              suggestionCursorAdapter, crashReport, trendingManager, searchNavigator, false,
              searchAnalytics);

      attachPresenter(searchSuggestionsPresenter);

      handleOptionsItemSelected(toolbarMenuItemClick);
    }
  }

  @CallSuper @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    if (storeTheme != null) {
      ThemeUtils.setStoreTheme(getActivity(), storeTheme);
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(storeTheme));
    }

    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override protected int[] getViewsToShowAfterLoadingId() {
    return new int[] { R.id.pager, R.id.tabs };
  }

  @Override protected int getViewToShowAfterLoadingId() {
    return -1;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    if (create || tabs == null) {
      loadData(refresh, openType).observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(title -> {
            this.title = title;
            if (storeContext != StoreContext.home) {
              setupToolbarDetails(getToolbar());
            }
            setupViewPager();
          }, (throwable) -> handleError(throwable));
    } else {
      setupViewPager();
    }
  }

  /**
   * @return an observable with the title that should be displayed
   */
  private Observable<String> loadData(boolean refresh, OpenType openType) {
    switch (openType) {
      case GetHome:
        return GetHomeRequest.of(
            StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider), userId,
            storeContext, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            sharedPreferences, getContext().getResources(),
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
            .observe(refresh)
            .map(getHome -> {
              Store store = getHome.getNodes()
                  .getMeta()
                  .getData()
                  .getStore();
              String storeName = store != null ? store.getName() : null;
              Long storeId = store != null ? store.getId() : null;
              String avatar = store != null ? store.getAvatar() : null;
              setupVariables(parseTabs(getHome), storeId, storeName, storeUrl, avatar);
              HomeUser user = getHome.getNodes()
                  .getMeta()
                  .getData()
                  .getUser();
              return TextUtils.isEmpty(storeName) ? user.getName() : storeName;
            });
      case GetStore:
      default:
        return GetStoreRequest.of(
            StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider), storeContext,
            bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences,
            getContext().getResources(),
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
            .observe(refresh)
            .map(getStore -> {
              setupVariables(parseTabs(getStore), getStore.getNodes()
                  .getMeta()
                  .getData()
                  .getId(), getStore.getNodes()
                  .getMeta()
                  .getData()
                  .getName(), getStore.getNodes()
                  .getMeta()
                  .getData()
                  .getUrls()
                  .getMobile(), getStore.getNodes()
                  .getMeta()
                  .getData()
                  .getAvatar());
              return getStore.getNodes()
                  .getMeta()
                  .getData()
                  .getName();
            });
    }
  }

  private List<GetStoreTabs.Tab> parseTabs(StoreUserAbstraction<?> storeUserAbstraction) {
    GetStoreTabs.Tab tab = storeUserAbstraction.getNodes()
        .getTabs()
        .getList()
        .get(0);
    if (tab.getEvent()
        .getAction()
        .contains("/getStore/")) {
      tab.getEvent()
          .setName(Event.Name.getStoreWidgets);
      String parsedEventAction = tab.getEvent()
          .getAction()
          .replace("/getStore/", "/getStoreWidgets/");
      tab.getEvent()
          .setAction(parsedEventAction);
    }

    return storeUserAbstraction.getNodes()
        .getTabs()
        .getList();
  }

  private void handleError(Throwable throwable) {
    if (throwable instanceof AptoideWsV7Exception) {
      BaseV7Response baseResponse = ((AptoideWsV7Exception) throwable).getBaseResponse();

      switch (StoreUtils.getErrorType(baseResponse.getError()
          .getCode())) {
        case PRIVATE_STORE_ERROR:
        case PRIVATE_STORE_WRONG_CREDENTIALS:
          DialogFragment dialogFragment =
              (DialogFragment) getFragmentManager().findFragmentByTag(PrivateStoreDialog.TAG);
          if (dialogFragment == null) {
            dialogFragment =
                PrivateStoreDialog.newInstance(this, PRIVATE_STORE_REQUEST_CODE, storeName, true);
            dialogFragment.show(getFragmentManager(), PrivateStoreDialog.TAG);
          }
          break;
        case STORE_SUSPENDED:
          showStoreSuspendedPopup(storeName);
        default:
          finishLoading(throwable);
      }
    } else {
      finishLoading(throwable);
    }
  }

  private void setupVariables(List<GetStoreTabs.Tab> tabs, Long storeId, String storeName,
      String storeUrl, String iconPath) {
    this.tabs = tabs;
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeUrl = storeUrl;
    this.iconPath = iconPath;
  }

  protected void changeToTab(Event.Name tabToChange) {
    if (tabToChange != null) {
      StorePagerAdapter storePagerAdapter = viewPager.getAdapter() instanceof StorePagerAdapter
          ? ((StorePagerAdapter) viewPager.getAdapter()) : null;
      if (storePagerAdapter != null) {
        viewPager.setCurrentItem(
            ((StorePagerAdapter) viewPager.getAdapter()).getEventNamePosition(tabToChange));
      }
    }
  }

  private void showStoreSuspendedPopup(String storeName) {
    GenericDialogs.createGenericOkCancelMessage(getContext(), "", R.string.store_suspended_message,
        android.R.string.ok, R.string.unfollow)
        .subscribe(eResponse -> {
          switch (eResponse) {
            case NO:
              StoreUtils.unSubscribeStore(storeName, accountManager, storeCredentialsProvider,
                  AccessorFactory.getAccessorFor(
                      ((AptoideApplication) getContext().getApplicationContext()
                          .getApplicationContext()).getDatabase(),
                      cm.aptoide.pt.database.realm.Store.class));
            case YES:
            case CANCEL:
              getActivity().onBackPressed();
              break;
          }
        });
  }

  @CallSuper @Override public void setupToolbar() {
    super.setupToolbar();
  }

  protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(title);
    if (userId != null) {
      toolbar.setLogo(R.drawable.ic_user_icon);
    } else {
      toolbar.setLogo(R.drawable.ic_store);
    }
  }

  public enum OpenType {
    GetHome, GetStore
  }

  private enum BundleKeys {
    STORE_NAME, STORE_CONTEXT, STORE_THEME, DEFAULT_TAB_TO_OPEN, USER_ID, OPEN_TYPE
  }
}
