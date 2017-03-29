/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 05/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.SearchPagerAdapter;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.abtesting.ABTest;
import cm.aptoide.pt.v8engine.analytics.abtesting.ABTestManager;
import cm.aptoide.pt.v8engine.analytics.abtesting.SearchTabOptions;
import cm.aptoide.pt.v8engine.fragment.BasePagerToolbarFragment;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchFragment extends BasePagerToolbarFragment {
  private static final String TAG = SearchFragment.class.getSimpleName();
  private String query;

  transient private boolean hasSubscribedResults;
  transient private boolean hasEverywhereResults;
  transient private boolean shouldFinishLoading = false;
  // Views
  private Button subscribedButton;
  private Button everywhereButton;
  private LinearLayout buttonsLayout;
  private View noSearchLayout;
  private EditText noSearchLayoutSearchQuery;
  private ImageView noSearchLayoutSearchButton;
  private String storeName;
  private boolean onlyTrustedApps;
  private int selectedButton = 0;
  private BodyInterceptor bodyInterceptor;

  public static SearchFragment newInstance(String query) {
    return newInstance(query, false);
  }

  public static SearchFragment newInstance(String query, boolean onlyTrustedApps) {
    Bundle args = new Bundle();

    args.putString(BundleCons.QUERY, query);
    args.putBoolean(BundleCons.ONLY_TRUSTED, onlyTrustedApps);

    SearchFragment fragment = new SearchFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static SearchFragment newInstance(String query, String storeName) {
    Bundle args = new Bundle();

    args.putString(BundleCons.QUERY, query);
    args.putString(BundleCons.STORE_NAME, storeName);

    SearchFragment fragment = new SearchFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptor();
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);

    query = args.getString(BundleCons.QUERY);
    storeName = args.getString(BundleCons.STORE_NAME);
    onlyTrustedApps = args.getBoolean(BundleCons.ONLY_TRUSTED, false);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);

    subscribedButton = (Button) view.findViewById(R.id.subscribed);
    everywhereButton = (Button) view.findViewById(R.id.everywhere);
    buttonsLayout = (LinearLayout) view.findViewById(R.id.buttons_layout);
    noSearchLayout = view.findViewById(R.id.no_search_results_layout);
    noSearchLayoutSearchQuery = (EditText) view.findViewById(R.id.search_text);
    noSearchLayoutSearchButton = (ImageView) view.findViewById(R.id.ic_search_button);
    setButtonBackgrounds(selectedButton);
    setHasOptionsMenu(true);
  }

  @Override protected void setupViewPager() {
    viewPager.setPagingEnabled(false);

    if (hasSubscribedResults || hasEverywhereResults) {
      super.setupViewPager();
    } else {
      Analytics.Search.noSearchResults(query);

      noSearchLayout.setVisibility(View.VISIBLE);
      buttonsLayout.setVisibility(View.INVISIBLE);
      noSearchLayoutSearchButton.setOnClickListener(v -> {
        String s = noSearchLayoutSearchQuery.getText().toString();

        if (s.length() > 1) {
          getNavigationManager().navigateTo(
              V8Engine.getFragmentProvider().newSearchFragment(s, storeName));
        }
      });
    }
  }

  @Override protected PagerAdapter createPagerAdapter() {
    if (storeName != null) {
      return new SearchPagerAdapter(getChildFragmentManager(), query, storeName);
    } else {
      return new SearchPagerAdapter(getChildFragmentManager(), query, hasSubscribedResults,
          hasEverywhereResults);
    }
  }

  private void setButtonBackgrounds(int currentItem) {
    if (currentItem == 0) {
      subscribedButtonListener();
    } else if (currentItem == 1) {
      everywhereButtonListener(true);
    }
  }

  @Partners protected void subscribedButtonListener() {
    selectedButton = 0;
    viewPager.setCurrentItem(0);
    subscribedButton.setBackgroundResource(R.drawable.search_button_background);
    subscribedButton.setTextColor(getResources().getColor(R.color.white));
    everywhereButton.setTextColor(getResources().getColor(R.color.app_view_gray));
    everywhereButton.setBackgroundResource(0);
  }

  @Partners protected Void everywhereButtonListener(boolean smoothScroll) {
    selectedButton = 1;
    viewPager.setCurrentItem(1, smoothScroll);
    everywhereButton.setBackgroundResource(R.drawable.search_button_background);
    everywhereButton.setTextColor(getResources().getColor(R.color.white));
    subscribedButton.setTextColor(getResources().getColor(R.color.app_view_gray));
    subscribedButton.setBackgroundResource(0);
    return null;
  }

  private void setupButtonVisibility() {
    if (storeName != null) {
      subscribedButton.setText(storeName);
      subscribedButton.setVisibility(View.VISIBLE);
    } else {
      if (hasSubscribedResults) {
        subscribedButton.setVisibility(View.VISIBLE);
      }
      if (hasEverywhereResults) {
        everywhereButton.setVisibility(View.VISIBLE);
      }
    }
  }

  private void handleFinishLoading(boolean create) {

    if (!shouldFinishLoading) {
      shouldFinishLoading = true;
    } else {
      setupButtonVisibility();
      setupButtonsListeners();
      setupViewPager();
      if (create) {
        //only show the search results after choosing the tab to show
        setupAbTest().compose(bindUntilEvent(LifecycleEvent.DESTROY))
            .subscribe(setup -> finishLoading(), throwable -> {
              CrashReport.getInstance().log(throwable);
              finishLoading();
            });
      } else {
        finishLoading();
      }
    }
  }

  private Observable<Void> setupAbTest() {
    if (hasSubscribedResults && hasEverywhereResults) {
      ABTest<SearchTabOptions> searchAbTest =
          ABTestManager.getInstance().get(ABTestManager.SEARCH_TAB_TEST);
      return searchAbTest.participate()
          .observeOn(AndroidSchedulers.mainThread())
          .map(experiment -> setTabAccordingAbTest(searchAbTest));
    } else {
      return Observable.just(null);
    }
  }

  private Void setTabAccordingAbTest(ABTest<SearchTabOptions> searchAbTest) {
    if (searchAbTest.alternative().chooseTab() == 1) {
      everywhereButtonListener(false);
    }
    return null;
  }

  @Partners protected void executeSearchRequests(String storeName, boolean create) {
    Analytics.Search.searchTerm(query);

    if (storeName != null) {
      shouldFinishLoading = true;
      ListSearchAppsRequest of =
          ListSearchAppsRequest.of(query, storeName, StoreUtils.getSubscribedStoresAuthMap(),
              bodyInterceptor);
      of.execute(listSearchApps -> {
        List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDatalist().getList();

        if (list != null && list.size() > 0) {
          hasSubscribedResults = true;
          handleFinishLoading(create);
        } else {
          hasSubscribedResults = false;
          handleFinishLoading(create);
        }
      }, e -> finishLoading());
    } else {
      ListSearchAppsRequest.of(query, true, onlyTrustedApps, StoreUtils.getSubscribedStoresIds(),
          bodyInterceptor).execute(listSearchApps -> {
        List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDatalist().getList();

        if (list != null && list.size() > 0) {
          hasSubscribedResults = true;
          handleFinishLoading(create);
        } else {
          hasSubscribedResults = false;
          handleFinishLoading(create);
        }
      }, e -> finishLoading());

      // Other stores
      ListSearchAppsRequest.of(query, false, onlyTrustedApps, StoreUtils.getSubscribedStoresIds(),
          bodyInterceptor).execute(listSearchApps -> {
        List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDatalist().getList();

        if (list != null && list.size() > 0) {
          hasEverywhereResults = true;
          handleFinishLoading(create);
        } else {
          hasEverywhereResults = false;
          handleFinishLoading(create);
        }
      }, e -> finishLoading());

      // could this be a solution ?? despite the boolean flags
      //			Observable.concat(ListSearchAppsRequest.of(query, true).observe(),ListSearchAppsRequest.of(query, false).observe()).subscribe
      // (listSearchApps -> {
      //				List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDatalist().getList();
      //
      //				if (list != null && list.size() > 0) {
      //					hasEverywhereResults = true;
      //					handleFinishLoading();
      //				} else {
      //					hasEverywhereResults = false;
      //					handleFinishLoading();
      //				}
      //			}, e -> finishLoading());
    }
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(query);
  }

  private void setupButtonsListeners() {
    if (hasSubscribedResults) {
      subscribedButton.setOnClickListener(v -> subscribedButtonListener());
    }

    if (hasEverywhereResults) {
      everywhereButton.setOnClickListener(v -> everywhereButtonListener(true));
    }
  }

  @Override public int getContentViewId() {
    return R.layout.global_search_fragment;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(BundleCons.QUERY, query);
    outState.putString(BundleCons.STORE_NAME, storeName);
    outState.putInt(BundleCons.SELECTED_BUTTON, selectedButton);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();

    if (i == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_search, menu);

    if (storeName != null) {
      SearchUtils.setupInsideStoreSearchView(menu, this, storeName);
    } else {
      SearchUtils.setupGlobalSearchView(menu, this);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (savedInstanceState != null) {
      query = savedInstanceState.getString(BundleCons.QUERY);
      storeName = savedInstanceState.getString(BundleCons.STORE_NAME);
      setButtonBackgrounds(savedInstanceState.getInt(BundleCons.SELECTED_BUTTON));
    }
  }

  @Override protected int[] getViewsToShowAfterLoadingId() {
    return new int[] {};
  }

  @Override protected int getViewToShowAfterLoadingId() {
    return R.id.search_results_layout;
  }

  @Partners @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    if (create) {
      executeSearchRequests(storeName, create);
    } else {
      handleFinishLoading(create);
    }
  }

  @Partners protected static class BundleCons {

    public static final String QUERY = "query";
    public static final String STORE_NAME = "storeName";
    public static final String ONLY_TRUSTED = "onlyTrustedApps";
    public static final String SELECTED_BUTTON = "selectedbutton";
  }
}
