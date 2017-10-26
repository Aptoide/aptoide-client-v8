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
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.StorePagerAdapter;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.behavior.ScrollAwareFABBehavior;
import cm.aptoide.pt.v8engine.dialog.AddStoreDialog;
import cm.aptoide.pt.v8engine.dialog.PrivateStoreDialog;
import cm.aptoide.pt.v8engine.fragment.BasePagerToolbarFragment;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import com.astuetz.PagerSlidingTabStrip;
import com.trello.rxlifecycle.android.FragmentEvent;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 06-05-2016.
 */
public class StoreFragment extends BasePagerToolbarFragment {

  private static final String TAG = StoreFragment.class.getSimpleName();

  private final int PRIVATE_STORE_REQUEST_CODE = 20;
  private final AptoideClientUUID aptoideClientUUID;
  protected PagerSlidingTabStrip pagerSlidingTabStrip;
  private String storeName;
  private StoreContext storeContext;
  private GetStore getStore;
  private String storeTheme;

  public StoreFragment() {
    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());
  }

  public static StoreFragment newInstance(String storeName, String storeTheme) {
    return newInstance(storeName, StoreContext.store, storeTheme);
  }

  public static StoreFragment newInstance(String storeName, StoreContext storeContext,
      String storeTheme) {
    Bundle args = new Bundle();
    args.putString(BundleCons.STORE_NAME, storeName);
    args.putSerializable(BundleCons.STORE_CONTEXT, storeContext);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    StoreFragment fragment = new StoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  //Delete after completing themes implementation
  public static StoreFragment newInstance(String storeName) {
    return newInstance(storeName, StoreContext.store);
  }

  //Delete after completing themes implementation
  public static StoreFragment newInstance(String storeName, StoreContext storeContext) {
    Bundle args = new Bundle();
    args.putString(BundleCons.STORE_NAME, storeName);
    args.putSerializable(BundleCons.STORE_CONTEXT, storeContext);
    StoreFragment fragment = new StoreFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    if (storeTheme != null) {
      ThemeUtils.setStoreTheme(getActivity(), storeTheme);
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
    }

    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override protected int getViewToShowAfterLoadingId() {
    return R.id.app_bar_layout;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    if (create || getStore == null) {
      GetStoreRequest.of(StoreUtils.getStoreCredentials(storeName), storeContext,
          AptoideAccountManager.getAccessToken(), aptoideClientUUID.getUniqueIdentifier())
          .observe(refresh)
          .observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe((getStore) -> {
            this.getStore = getStore;
            setupViewPager();
          }, (throwable) -> {
            if (throwable instanceof AptoideWsV7Exception) {
              BaseV7Response baseResponse = ((AptoideWsV7Exception) throwable).getBaseResponse();

              if (StoreUtils.PRIVATE_STORE_ERROR.equals(baseResponse.getError().getCode())
                  || StoreUtils.PRIVATE_STORE_WRONG_CREDENTIALS.equals(
                  baseResponse.getError().getCode())) {
                DialogFragment dialogFragment =
                    (DialogFragment) getFragmentManager().findFragmentByTag(PrivateStoreDialog.TAG);
                if (dialogFragment == null) {
                  dialogFragment =
                      PrivateStoreDialog.newInstance(this, PRIVATE_STORE_REQUEST_CODE, storeName,
                          true);
                  dialogFragment.show(getFragmentManager(), PrivateStoreDialog.TAG);
                }
              }
            } else {
              finishLoading(throwable);
            }
          });
    } else {
      setupViewPager();
    }
  }

  @Override public void onDestroyView() {
    if (storeTheme != null && !storeContext.equals(StoreContext.store)) {
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

    /*
     *  On Orientation change keep the fab up on subscribed stores
     */
    if (viewPager.getCurrentItem() == adapter.getEventNamePosition(Event.Name.myStores)) {
      FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
      floatingActionButton.setOnClickListener(
          v -> new AddStoreDialog().show(fragmentManager, "addStoreDialog"));
      floatingActionButton.show();
    } else {
      floatingActionButton.hide();
    }

    /* Be careful maintaining this code
     * this affects both the main ViewPager when we open app
     * and the ViewPager inside the StoresView
     */
    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
        StorePagerAdapter adapter = (StorePagerAdapter) viewPager.getAdapter();

        if (Event.Name.getUserTimeline.equals(adapter.getEventName(position))) {
          Analytics.AppsTimeline.openTimeline();
        }

        if (Integer.valueOf(position).equals(adapter.getEventNamePosition(Event.Name.myStores))) {
          FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

          CoordinatorLayout.LayoutParams params =
              (CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams();
          params.setBehavior(new ScrollAwareFABBehavior());
          floatingActionButton.setLayoutParams(params);

          floatingActionButton.setOnClickListener(
              v -> new AddStoreDialog().show(fragmentManager, "addStoreDialog"));
          floatingActionButton.show();
        } else {
          CoordinatorLayout.LayoutParams layoutParams =
              (CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams();
          layoutParams.setBehavior(null);
          floatingActionButton.setLayoutParams(layoutParams);

          floatingActionButton.hide();
          floatingActionButton.setOnClickListener(null);
        }
      }
    });

    finishLoading();
  }

  @Override protected PagerAdapter createPagerAdapter() {
    return new StorePagerAdapter(getChildFragmentManager(), getStore);
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
    storeTheme = args.getString(BundleCons.STORE_THEME);
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

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_search, menu);

    setupSearch(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();

    if (i == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  protected void setupSearch(Menu menu) {
    SearchUtils.setupInsideStoreSearchView(menu, getNavigationManager(), storeName);
  }

  @Override public void setupViews() {
    super.setupViews();
    setHasOptionsMenu(true);
  }

  protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(storeName);
    toolbar.setLogo(R.drawable.ic_store);
  }

  @Partners @CallSuper @Override public void setupToolbar() {
    super.setupToolbar();
    Analytics.Stores.enter(storeName);
  }

  @Partners public static class BundleCons {

    public static final String STORE_NAME = "storeName";
    public static final String STORE_CONTEXT = "storeContext";
    public static final String STORE_THEME = "storeTheme";
  }
}
