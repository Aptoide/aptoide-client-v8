/*
 * Copyright (c) 2016.
 * Modified on 05/08/2016.
 */

package cm.aptoide.pt.v8engine.view.search;

import android.content.SharedPreferences;
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
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.ListSearchApps;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.search.SearchAnalytics;
import cm.aptoide.pt.v8engine.store.StoreUtils;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.view.fragment.BasePagerToolbarFragment;
import com.facebook.appevents.AppEventsLogger;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchFragment extends BasePagerToolbarFragment {
  private static final String TAG = SearchFragment.class.getSimpleName();

  private SharedPreferences sharedPreferences;
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
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private SearchAnalytics searchAnalytics;
  private TokenInvalidator tokenInvalidator;

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
    sharedPreferences =
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences();
    tokenInvalidator = ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    searchAnalytics = new SearchAnalytics(Analytics.getInstance(),
        AppEventsLogger.newLogger(getContext().getApplicationContext()));
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

  @Override public void onDestroyView() {
    noSearchLayoutSearchButton.setOnClickListener(null);
    noSearchLayoutSearchButton = null;
    noSearchLayoutSearchQuery = null;
    noSearchLayout = null;
    buttonsLayout = null;
    everywhereButton.setOnClickListener(null);
    everywhereButton = null;
    subscribedButton.setOnClickListener(null);
    subscribedButton = null;
    super.onDestroyView();
  }

  @Override protected void setupViewPager() {
    viewPager.setPagingEnabled(false);

    if (hasSubscribedResults || hasEverywhereResults) {
      super.setupViewPager();
    } else {
      searchAnalytics.searchNoResults(query);

      noSearchLayout.setVisibility(View.VISIBLE);
      buttonsLayout.setVisibility(View.INVISIBLE);
      noSearchLayoutSearchButton.setOnClickListener(v -> {
        String s = noSearchLayoutSearchQuery.getText()
            .toString();

        if (s.length() > 1) {
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newSearchFragment(s, storeName));
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
    everywhereButton.setTextColor(getResources().getColor(R.color.silver_dark));
    everywhereButton.setBackgroundResource(0);
  }

  @Partners protected Void everywhereButtonListener(boolean smoothScroll) {
    selectedButton = 1;
    viewPager.setCurrentItem(1, smoothScroll);
    everywhereButton.setBackgroundResource(R.drawable.search_button_background);
    everywhereButton.setTextColor(getResources().getColor(R.color.white));
    subscribedButton.setTextColor(getResources().getColor(R.color.silver_dark));
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
        // AB testing was removed from here: AN-1836: Search: remove ab testing
        // This sets the All stores tab as the selected tab of the search
        everywhereButtonListener(false);
      }
      finishLoading();
    }
  }

  @Partners protected void executeSearchRequests(String storeName, boolean create) {
    //TODO (pedro): Don't have search source (which tab)
    searchAnalytics.search(query);
    if (storeName != null) {
      shouldFinishLoading = true;
      ListSearchAppsRequest of = ListSearchAppsRequest.of(query, storeName,
          StoreUtils.getSubscribedStoresAuthMap(AccessorFactory.getAccessorFor(
              ((V8Engine) getContext().getApplicationContext()
                  .getApplicationContext()).getDatabase(), Store.class)), bodyInterceptor,
          httpClient, converterFactory, tokenInvalidator, sharedPreferences);
      of.execute(listSearchApps -> {
        List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDataList()
            .getList();

        if (list != null && hasMoreResults(listSearchApps)) {
          hasSubscribedResults = true;
          handleFinishLoading(create);
        } else {
          hasSubscribedResults = false;
          handleFinishLoading(create);
        }
      }, e -> finishLoading());
    } else {
      ListSearchAppsRequest.of(query, true, onlyTrustedApps, StoreUtils.getSubscribedStoresIds(
          AccessorFactory.getAccessorFor(((V8Engine) getContext().getApplicationContext()
              .getApplicationContext()).getDatabase(), Store.class)), bodyInterceptor, httpClient,
          converterFactory, tokenInvalidator, sharedPreferences)
          .execute(listSearchApps -> {
            List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDataList()
                .getList();

            if (list != null && hasMoreResults(listSearchApps)) {
              hasSubscribedResults = true;
              handleFinishLoading(create);
            } else {
              hasSubscribedResults = false;
              handleFinishLoading(create);
            }
          }, e -> finishLoading());

      // Other stores
      ListSearchAppsRequest.of(query, false, onlyTrustedApps, StoreUtils.getSubscribedStoresIds(
          AccessorFactory.getAccessorFor(((V8Engine) getContext().getApplicationContext()
              .getApplicationContext()).getDatabase(), Store.class)), bodyInterceptor, httpClient,
          converterFactory, tokenInvalidator, sharedPreferences)
          .execute(listSearchApps -> {
            List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDataList()
                .getList();

            if (list != null && hasMoreResults(listSearchApps)) {
              hasEverywhereResults = true;
              handleFinishLoading(create);
            } else {
              hasEverywhereResults = false;
              handleFinishLoading(create);
            }
          }, e -> finishLoading());
    }
  }

  private boolean hasMoreResults(ListSearchApps listSearchApps) {
    DataList<ListSearchApps.SearchAppsApp> datalist = listSearchApps.getDataList();

    return datalist.getList()
        .size() > 0 || listSearchApps.getTotal() > listSearchApps.getNextSize();
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

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_search, menu);

    if (storeName != null) {
      SearchUtils.setupInsideStoreSearchView(menu, getActivity(), getFragmentNavigator(),
          storeName);
    } else {
      SearchUtils.setupGlobalSearchView(menu, getActivity(), getFragmentNavigator());
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();

    if (i == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
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
