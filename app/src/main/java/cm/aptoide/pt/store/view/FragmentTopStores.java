package cm.aptoide.pt.store.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.interfaces.SuccessRequestListener;
import cm.aptoide.pt.dataprovider.model.v7.store.ListStores;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.view.recycler.BaseAdapter;
import cm.aptoide.pt.view.recycler.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import com.facebook.appevents.AppEventsLogger;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

/**
 * Created by trinkes on 8/25/16.
 */
public class FragmentTopStores extends AptoideBaseFragment<BaseAdapter> implements Endless {

  public static final int STORES_LIMIT_PER_REQUEST = 10;
  public static String TAG = FragmentTopStores.class.getSimpleName();
  private int offset = 0;
  private StoreAnalytics storeAnalytics;
  private SuccessRequestListener<ListStores> listener =
      listStores -> Observable.fromCallable(() -> createDisplayables(listStores))
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(displayables -> addDisplayables(displayables), err -> {
            CrashReport.getInstance()
                .log(err);
          });

  public static FragmentTopStores newInstance() {
    return new FragmentTopStores();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    storeAnalytics =
        new StoreAnalytics(AppEventsLogger.newLogger(getContext()), Analytics.getInstance());
    setHasOptionsMenu(true);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @NonNull private List<Displayable> createDisplayables(ListStores listStores) {
    List<Displayable> displayables = new ArrayList<>();
    for (final Store store : listStores.getDataList()
        .getList()) {
      displayables.add(
          new GridStoreDisplayable(store, "Add Store Dialog Top Stores", storeAnalytics));
    }
    return displayables;
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_with_toolbar;
  }

  @Override public void setupViews() {
    super.setupViews();
    setupToolbar();
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    fetchStores();
  }

  private void fetchStores() {
    final ListStoresRequest listStoresRequest =
        requestFactoryCdnPool.newListStoresRequest(offset, STORES_LIMIT_PER_REQUEST);
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(), listStoresRequest, listener,
            err -> err.printStackTrace());
    getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(false, false);
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(R.string.top_stores_fragment_title);
    toolbar.setLogo(R.drawable.logo_toolbar);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public int getOffset() {
    return offset;
  }

  @Override public void setOffset(int offset) {
    this.offset = offset;
  }

  @Override public Integer getLimit() {
    return STORES_LIMIT_PER_REQUEST;
  }
}
