/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.abtesting.ABTest;
import cm.aptoide.pt.v8engine.analytics.abtesting.ABTestManager;
import cm.aptoide.pt.v8engine.analytics.abtesting.SearchTabOptions;
import cm.aptoide.pt.v8engine.view.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.repository.AdsRepository;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.EndlessRecyclerOnScrollListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.functions.Action0;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchPagerTabFragment extends GridRecyclerFragmentWithDecorator {

  private BodyInterceptor<BaseBody> bodyInterceptor;
  private AdsRepository adsRepository;

  private String query;
  private String storeName;
  private boolean addSubscribedStores;
  private boolean hasMultipleFragments;
  private boolean refreshed = false;

  private ABTest<SearchTabOptions> searchAbTest;
  private Map<String, Void> mapPackages = new HashMap<>();
  private transient EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
  private transient ListSearchAppsRequest listSearchAppsRequest;
  private transient SuccessRequestListener<ListSearchApps> listSearchAppsSuccessRequestListener =
      listSearchApps -> {

        LinkedList<Displayable> displayables = new LinkedList<>();

        List<ListSearchApps.SearchAppsApp> list = listSearchApps.getDatalist().getList();
        Observable<ListSearchApps.SearchAppsApp> from = Observable.from(list);

        if (addSubscribedStores) {
          from = from.filter(
              searchAppsApp -> !mapPackages.containsKey(searchAppsApp.getPackageName()));
        }

        from.forEach(searchAppsApp -> {
          mapPackages.put(searchAppsApp.getPackageName(), null);
          Action0 callback = () -> {
            if (isConvert(searchAbTest, addSubscribedStores)) {
              searchAbTest.convert().subscribe(success -> {
              }, throwable -> {
                CrashReport.getInstance().log(throwable);
              });
            }
          };
          displayables.add(new SearchDisplayable(searchAppsApp, callback));
        });

        addDisplayables(displayables);
      };

  public static SearchPagerTabFragment newInstance(String query, boolean subscribedStores,
      boolean hasMultipleFragments) {
    Bundle args = new Bundle();

    args.putString(BundleCons.QUERY, query);
    args.putBoolean(BundleCons.ADD_SUBSCRIBED_STORES, subscribedStores);
    args.putBoolean(BundleCons.HAS_MULTIPLE_FRAGMENTS, hasMultipleFragments);

    SearchPagerTabFragment fragment = new SearchPagerTabFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static SearchPagerTabFragment newInstance(String query, String storeName) {
    Bundle args = new Bundle();

    args.putString(BundleCons.QUERY, query);
    args.putString(BundleCons.STORE_NAME, storeName);

    SearchPagerTabFragment fragment = new SearchPagerTabFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    final AptoideAccountManager accountManager =
        ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    adsRepository = new AdsRepository(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()), accountManager);
    super.onCreate(savedInstanceState);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);

    query = args.getString(BundleCons.QUERY);
    storeName = args.getString(BundleCons.STORE_NAME);
    addSubscribedStores = args.getBoolean(BundleCons.ADD_SUBSCRIBED_STORES);
    hasMultipleFragments = args.getBoolean(BundleCons.HAS_MULTIPLE_FRAGMENTS, false);
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_fragment;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create) {
      searchAbTest = ABTestManager.getInstance().get(ABTestManager.SEARCH_TAB_TEST);
      adsRepository.getAdsFromSearch(query)
          .onErrorReturn(throwable -> null)
          .filter(minimalAd -> minimalAd != null)
          .compose(bindUntilEvent(LifecycleEvent.DESTROY))
          .subscribe(minimalAd -> {
            refreshed = true;
            addDisplayable(0, new SearchAdDisplayable(minimalAd), false);
          });

      getRecyclerView().clearOnScrollListeners();
      ListSearchAppsRequest of;
      if (storeName != null) {
        of = ListSearchAppsRequest.of(query, storeName, StoreUtils.getSubscribedStoresAuthMap(),
            bodyInterceptor);
      } else {
        of = ListSearchAppsRequest.of(query, addSubscribedStores,
            StoreUtils.getSubscribedStoresIds(), StoreUtils.getSubscribedStoresAuthMap(),
            bodyInterceptor);
      }
      endlessRecyclerOnScrollListener =
          new EndlessRecyclerOnScrollListener(this.getAdapter(), listSearchAppsRequest = of,
              listSearchAppsSuccessRequestListener, err -> err.printStackTrace(), refresh);
      getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
      endlessRecyclerOnScrollListener.onLoadMore(refresh);
    } else {
      getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(BundleCons.QUERY, query);
    outState.putString(BundleCons.STORE_NAME, storeName);
    outState.putBoolean(BundleCons.ADD_SUBSCRIBED_STORES, addSubscribedStores);
  }

  private boolean isConvert(ABTest<SearchTabOptions> searchAbTest, boolean addSubscribedStores) {
    return hasMultipleFragments && (addSubscribedStores == (searchAbTest.alternative()
        == SearchTabOptions.FOLLOWED_STORES));
  }

  protected static class BundleCons {

    public static final String QUERY = "query";
    public static final String STORE_NAME = "storeName";
    public static final String ADD_SUBSCRIBED_STORES = "addSubscribedStores";
    public static final String HAS_MULTIPLE_FRAGMENTS = "has_multiple_fragments";
  }
}
