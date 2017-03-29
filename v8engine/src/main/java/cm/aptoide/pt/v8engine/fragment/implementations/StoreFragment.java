/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 05/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetHomeRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.model.v7.store.HomeUser;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.StorePagerAdapter;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.dialog.PrivateStoreDialog;
import cm.aptoide.pt.v8engine.fragment.BasePagerToolbarFragment;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import com.astuetz.PagerSlidingTabStrip;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.List;
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
    storeFragment.getArguments().putSerializable(BundleCons.DEFAULT_TAB_TO_OPEN, defaultTab);
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
    storeCredentialsProvider = new StoreCredentialsProviderImpl();
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptor();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (storeTheme != null) {
      ThemeUtils.setStatusBarThemeColor(getActivity(),
          StoreThemeEnum.get(V8Engine.getConfiguration().getDefaultTheme()));
    }
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
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
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
    if (storeTheme != null && !storeContext.equals(StoreContext.meta)) {
      ThemeUtils.setAptoideTheme(getActivity());
    }
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
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
          if (fragment != null && fragment instanceof AppsTimelineFragment) {
            ((AppsTimelineFragment) fragment).goToTop();
          }
        }
      }
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
        }
      }
    });
    changeToTab(defaultTab);
    finishLoading();
  }

  @Override protected PagerAdapter createPagerAdapter() {
    return new StorePagerAdapter(getChildFragmentManager(), tabs, storeContext, storeId,
        storeTheme);
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

  /**
   * @return an observable with the title that should be displayed
   */
  private Observable<String> loadData(boolean refresh, OpenType openType) {
    switch (openType) {
      case GetHome:
        return GetHomeRequest.of(
            StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider), userId,
            storeContext, bodyInterceptor).observe(refresh).map(getHome -> {
          Store store = getHome.getNodes().getMeta().getData().getStore();
          String storeName = store != null ? store.getName() : null;
          Long storeId = store != null ? store.getId() : null;
          setupVariables(getHome.getNodes().getTabs().getList(), storeId, storeName);
          HomeUser user = getHome.getNodes().getMeta().getData().getUser();
          return TextUtils.isEmpty(storeName) ? user.getName() : storeName;
        });
      case GetStore:
      default:
        return GetStoreRequest.of(
            StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider), storeContext,
            bodyInterceptor).observe(refresh).map(getStore -> {
          setupVariables(getStore.getNodes().getTabs().getList(),
              getStore.getNodes().getMeta().getData().getId(),
              getStore.getNodes().getMeta().getData().getName());
          return getStore.getNodes().getMeta().getData().getName();
        });
    }
  }

  private void handleError(Throwable throwable) {
    if (throwable instanceof AptoideWsV7Exception) {
      BaseV7Response baseResponse = ((AptoideWsV7Exception) throwable).getBaseResponse();

      switch (StoreUtils.getErrorType(baseResponse.getError().getCode())) {
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

  private void setupVariables(List<GetStoreTabs.Tab> tabs, Long storeId, String storeName) {
    this.tabs = tabs;
    this.storeId = storeId;
    this.storeName = storeName;
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
        android.R.string.ok, R.string.unfollow).subscribe(eResponse -> {
      switch (eResponse) {
        case NO:
          StoreUtils.unSubscribeStore(storeName, accountManager, storeCredentialsProvider);
        case YES:
        case CANCEL:
          getActivity().onBackPressed();
          break;
      }
    });
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_search, menu);

    setupSearch(menu);
  }

  protected void setupSearch(Menu menu) {
    SearchUtils.setupInsideStoreSearchView(menu, this, storeName);
  }

  @Override public void setupViews() {
    super.setupViews();
    setHasOptionsMenu(true);
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

  @Partners @CallSuper @Override public void setupToolbar() {
    super.setupToolbar();
    // FIXME: 17/1/2017 sithengineer is this the right place to have this event ?? why ??
    Logger.d(TAG, "LOCALYTICS TESTING - STORES ACTION ENTER " + storeName);
    Analytics.Stores.enter(storeName == null ? String.valueOf(userId) : storeName);
  }

  public enum OpenType {
    GetHome, GetStore
  }

  @Partners public static class BundleCons {

    public static final String STORE_NAME = "storeName";
    public static final String STORE_CONTEXT = "storeContext";
    public static final String STORE_THEME = "storeTheme";
    public static final String DEFAULT_TAB_TO_OPEN = "default_tab_to_open";
    public static final String USER_ID = "userId";
    public static final String OPEN_TYPE = "OPEN_TYPE";
  }
}
