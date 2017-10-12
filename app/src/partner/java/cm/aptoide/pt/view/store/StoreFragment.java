/*
 * Copyright (c) 2016.
 * Modified on 05/07/2016.
 */

package cm.aptoide.pt.view.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
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
import cm.aptoide.pt.notification.NotificationAnalytics;
//import cm.aptoide.pt.social.view.TimelineFragment;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.util.SearchUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.ThemeUtils;
import cm.aptoide.pt.view.fragment.BasePagerToolbarFragment;
import cm.aptoide.pt.view.share.ShareStoreHelper;
import com.astuetz.PagerSlidingTabStrip;
import com.facebook.appevents.AppEventsLogger;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 06-05-2016.
 */
public class StoreFragment extends BasePagerToolbarFragment {

  private static final String TAG = StoreFragment.class.getSimpleName();

  private final int PRIVATE_STORE_REQUEST_CODE = 20;
  protected PagerSlidingTabStrip pagerSlidingTabStrip;
  private AptoideAccountManager accountManager;
  private String storeName;
  private String title;
  private StoreContext storeContext;
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
  private PageViewsAnalytics pageViewAnalytics;

  public static StoreFragment newInstance(long userId, String storeTheme, OpenType openType) {
    return newInstance(userId, storeTheme, null, openType);
  }

  public static StoreFragment newInstance(long userId, String storeTheme, Event.Name defaultTab,
      OpenType openType) {
    Bundle args = new Bundle();
    args.putLong(BundleCons.USER_ID, userId);
    args.putSerializable(BundleCons.STORE_CONTEXT, StoreContext.meta);
    args.putSerializable(BundleCons.OPEN_TYPE, openType);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    args.putSerializable(BundleCons.DEFAULT_TAB_TO_OPEN, defaultTab);
    StoreFragment fragment = new StoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static StoreFragment newInstance(String storeName, String storeTheme,
      Event.Name defaultTab, OpenType openType) {
    StoreFragment storeFragment = newInstance(storeName, storeTheme, openType);
    storeFragment.getArguments()
        .putSerializable(BundleCons.DEFAULT_TAB_TO_OPEN, defaultTab);
    return storeFragment;
  }

  public static StoreFragment newInstance(String storeName, String storeTheme,
      StoreFragment.OpenType openType) {
    Bundle args = new Bundle();
    args.putString(BundleCons.STORE_NAME, storeName);
    args.putSerializable(BundleCons.OPEN_TYPE, openType);
    args.putSerializable(BundleCons.STORE_CONTEXT, StoreContext.meta);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    StoreFragment fragment = new StoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static StoreFragment newInstance(String storeName, String storeTheme) {
    return newInstance(storeName, storeTheme, OpenType.GetStore);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    defaultTheme = ((AptoideApplication) getContext().getApplicationContext()).getDefaultTheme();
    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), cm.aptoide.pt.database.realm.Store.class));
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    Analytics analytics = Analytics.getInstance();
    timelineAnalytics = new TimelineAnalytics(analytics,
        AppEventsLogger.newLogger(getContext().getApplicationContext()), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, BuildConfig.APPLICATION_ID,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        new NotificationAnalytics(httpClient, analytics), navigationTracker);
    storeAnalytics = new StoreAnalytics(AppEventsLogger.newLogger(getContext()), analytics);
    marketName = ((AptoideApplication) getContext().getApplicationContext()).getMarketName();
    shareStoreHelper = new ShareStoreHelper(getActivity(), marketName);
    pageViewAnalytics =
        new PageViewsAnalytics(AppEventsLogger.newLogger(getContext().getApplicationContext()),
            Analytics.getInstance(), navigationTracker);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    storeName = args.getString(BundleCons.STORE_NAME);
    storeContext = (StoreContext) args.get(BundleCons.STORE_CONTEXT);
    openType = args.containsKey(BundleCons.OPEN_TYPE) ? (OpenType) args.get(BundleCons.OPEN_TYPE)
        : OpenType.GetStore;
    storeTheme = args.getString(BundleCons.STORE_THEME);
    defaultTab = (Event.Name) args.get(BundleCons.DEFAULT_TAB_TO_OPEN);
    if (args.containsKey(BundleCons.USER_ID)) {
      userId = args.getLong(BundleCons.USER_ID);
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
    super.onDestroyView();
  }

  @Override protected void setupViewPager() {
    super.setupViewPager();
    viewPager.setAptoideNavigationTracker(navigationTracker);
    viewPager.setPageViewAnalytics(pageViewAnalytics);
    pagerSlidingTabStrip = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);

    if (pagerSlidingTabStrip != null) {
      pagerSlidingTabStrip.setViewPager(viewPager);
    }

    /*
     *  Click on tab listener
     */
    StorePagerAdapter adapter = (StorePagerAdapter) viewPager.getAdapter();
    pagerSlidingTabStrip.setOnTabReselectedListener(position -> {
      //if (Event.Name.getUserTimeline.equals(adapter.getEventName(position))) {
      //  //TODO We should not call fragment public methods since we do NOT know about its internal
      //  //life cycle. A fragment A should call its activity in order to communicate with
      //  //fragment B. Then when fragment B is ready it should register a listener
      //  //with its activity in order receive external communication. Activity
      //  //should buffer calls if there is no listener registered and deliver them
      //  //after registration happens.
      //  for (Fragment fragment : getChildFragmentManager().getFragments()) {
      //    if (fragment != null && fragment instanceof TimelineFragment) {
      //      ((TimelineFragment) fragment).goToTop();
      //    }
      //  }
      //}
    });

    /* Be careful maintaining this code
     * this affects both the main ViewPager when we open app
     * and the ViewPager inside the StoresView
     *
     * This code was changed when FAB was migrated to a followstorewidget 23/02/2017
     */
    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
    inflater.inflate(R.menu.menu_search, menu);

    setupSearch(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();

    if (i == R.id.menu_share) {
      shareStoreHelper.shareStore(storeUrl, iconPath);

      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  protected void setupSearch(Menu menu) {
    SearchUtils.setupInsideStoreSearchView(menu, getActivity(), getFragmentNavigator(), storeName);
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
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            getContext().getResources(),
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
            bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
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

  @Override public void setupViews() {
    super.setupViews();
    setHasOptionsMenu(true);
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

  public static class BundleCons {

    public static final String STORE_NAME = "storeName";
    public static final String STORE_CONTEXT = "storeContext";
    public static final String STORE_THEME = "storeTheme";
    public static final String DEFAULT_TAB_TO_OPEN = "default_tab_to_open";
    public static final String USER_ID = "userId";
    public static final String OPEN_TYPE = "OPEN_TYPE";
  }
}
