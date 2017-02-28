package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.AptoideBaseFragment;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 8/25/16.
 */
public class FragmentTopStores extends AptoideBaseFragment<BaseAdapter> implements Endless {

  public static final int STORES_LIMIT_PER_REQUEST = 10;
  public static String TAG = FragmentTopStores.class.getSimpleName();
  private int offset = 0;
  private SuccessRequestListener<ListStores> listener =
      listStores -> Observable.fromCallable(() -> createDisplayables(listStores))
          .subscribeOn(Schedulers.computation())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(displayables -> addDisplayables(displayables), err -> {
            CrashReport.getInstance().log(err);
          });

  public static FragmentTopStores newInstance() {
    return new FragmentTopStores();
  }

  @NonNull private List<Displayable> createDisplayables(ListStores listStores) {
    List<Displayable> displayables = new ArrayList<>();
    for (final Store store : listStores.getDatalist().getList()) {
      displayables.add(new GridStoreDisplayable(store));
    }
    return displayables;
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_with_toolbar;
  }

  @Override public void setupViews() {
    super.setupViews();
    setupToolbar();
    setHasOptionsMenu(true);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    fetchStores();
  }

  private void fetchStores() {
    final ListStoresRequest listStoresRequest =
        requestFactory.newListStoresRequest(offset, STORES_LIMIT_PER_REQUEST);
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(), listStoresRequest, listener,
            Throwable::printStackTrace);
    getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(false);
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
