/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchAdDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import rx.Observable;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchPagerTabFragment extends GridRecyclerFragmentWithDecorator {

  private String query;
  private String storeName;
  private boolean addSubscribedStores;
  private boolean refreshed = false;

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
          displayables.add(new SearchDisplayable(searchAppsApp));
        });

        addDisplayables(displayables);
      };

  public static SearchPagerTabFragment newInstance(String query, boolean subscribedStores) {
    Bundle args = new Bundle();

    args.putString(BundleCons.QUERY, query);
    args.putBoolean(BundleCons.ADD_SUBSCRIBED_STORES, subscribedStores);

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

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create) {
      GetAdsRequest.ofSearch(query).execute(getAdsResponse -> {
        if (getAdsResponse.getAds().size() > 0) {
          refreshed = true;
          addDisplayable(0, new SearchAdDisplayable(getAdsResponse.getAds().get(0)));
        }
      });

      recyclerView.clearOnScrollListeners();
      ListSearchAppsRequest of;
      if (storeName != null) {
        of = ListSearchAppsRequest.of(query, storeName, StoreUtils.getSubscribedStoresAuthMap(),
            AptoideAccountManager.getAccessToken(), AptoideAccountManager.getUserEmail());
      } else {
        of = ListSearchAppsRequest.of(query, addSubscribedStores,
            StoreUtils.getSubscribedStoresIds(), StoreUtils.getSubscribedStoresAuthMap(),
            AptoideAccountManager.getAccessToken(), AptoideAccountManager.getUserEmail());
      }
      endlessRecyclerOnScrollListener =
          new EndlessRecyclerOnScrollListener(this.getAdapter(), listSearchAppsRequest = of,
              listSearchAppsSuccessRequestListener, errorRequestListener, refresh);
      recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
      endlessRecyclerOnScrollListener.onLoadMore(refresh);
    } else {
      recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_fragment;
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);

    query = args.getString(BundleCons.QUERY);
    storeName = args.getString(BundleCons.STORE_NAME);
    addSubscribedStores = args.getBoolean(BundleCons.ADD_SUBSCRIBED_STORES);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(BundleCons.QUERY, query);
    outState.putString(BundleCons.STORE_NAME, storeName);
    outState.putBoolean(BundleCons.ADD_SUBSCRIBED_STORES, addSubscribedStores);
  }

  protected static class BundleCons {

    public static final String QUERY = "query";
    public static final String STORE_NAME = "storeName";
    public static final String ADD_SUBSCRIBED_STORES = "addSubscribedStores";
  }
}
