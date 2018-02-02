package cm.aptoide.pt.store.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.dataprovider.model.v7.store.ListStores;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class ListStoresFragment extends GetStoreEndlessFragment<ListStores> {

  @Inject AnalyticsManager analyticsManager;
  @Inject NavigationTracker navigationTracker;
  private StoreAnalytics storeAnalytics;

  public static Fragment newInstance() {
    return new ListStoresFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    storeAnalytics = new StoreAnalytics(analyticsManager, navigationTracker);
  }

  @Override protected V7<ListStores, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactoryCdnPool.newListStoresRequest(url);
  }

  @Override protected Action1<ListStores> buildAction() {
    return listStores -> {

      // Load sub nodes
      List<Store> list = listStores.getDataList()
          .getList();

      List<Displayable> displayables = new LinkedList<>();
      for (Store store : list) {
        displayables.add(new GridStoreDisplayable(store, "Home " + getToolbar().getTitle()
            .toString(), storeAnalytics));
      }

      addDisplayables(displayables);
    };
  }
}
