package cm.aptoide.pt.app.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.AdClick;
import cm.aptoide.pt.home.AdHomeEvent;
import cm.aptoide.pt.home.AdsBundlesViewHolderFactory;
import cm.aptoide.pt.home.AppHomeEvent;
import cm.aptoide.pt.home.BundlesAdapter;
import cm.aptoide.pt.home.HomeBundle;
import cm.aptoide.pt.home.HomeEvent;
import cm.aptoide.pt.home.ProgressBundle;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 04/06/2018.
 */

public class MoreBundleFragment extends NavigationTrackFragment implements MoreBundleView {

  private static final String MORE_LIST_STATE_KEY = "cm.aptoide.pt.more.ListState";
  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private static final int VISIBLE_THRESHOLD = 1;
  @Inject MoreBundlePresenter presenter;
  @Inject @Named("marketName") String marketName;
  private RecyclerView bundlesList;
  private BundlesAdapter adapter;
  private PublishSubject<HomeEvent> uiEventsListener;
  private PublishSubject<AdHomeEvent> adClickedEvents;
  private LinearLayoutManager layoutManager;
  private DecimalFormat oneDecimalFormatter;
  private View genericErrorView;
  private View noNetworkErrorView;
  private ProgressBar progressBar;
  private SwipeRefreshLayout swipeRefreshLayout;
  private Parcelable listState;
  private View noNetworkRetryButton;
  private View retryButton;
  private Toolbar toolbar;
  private PublishSubject<Boolean> notifyItemsAdded;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    uiEventsListener = PublishSubject.create();
    adClickedEvents = PublishSubject.create();
    notifyItemsAdded = PublishSubject.create();
    oneDecimalFormatter = new DecimalFormat("0.0");
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(MORE_LIST_STATE_KEY)) {
        listState = savedInstanceState.getParcelable(MORE_LIST_STATE_KEY);
        savedInstanceState.putParcelable(MORE_LIST_STATE_KEY, null);
      }
    }
    bundlesList = (RecyclerView) view.findViewById(R.id.more_bundles_list);
    genericErrorView = view.findViewById(R.id.generic_error);
    noNetworkErrorView = view.findViewById(R.id.no_network_connection);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    retryButton = genericErrorView.findViewById(R.id.retry);
    noNetworkRetryButton = noNetworkErrorView.findViewById(R.id.retry);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.more_refresh_layout);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
    adapter = new BundlesAdapter(new ArrayList<>(), new ProgressBundle(), uiEventsListener,
        oneDecimalFormatter, adClickedEvents, marketName,
        new AdsBundlesViewHolderFactory(uiEventsListener, adClickedEvents, oneDecimalFormatter,
            marketName, false));
    layoutManager = new LinearLayoutManager(getContext());
    bundlesList.setLayoutManager(layoutManager);
    bundlesList.setAdapter(adapter);
    AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
    appCompatActivity.setSupportActionBar(toolbar);
    ActionBar actionBar = appCompatActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", StoreContext.home.name());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.more_bundles_view, container, false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (bundlesList != null) {
      outState.putParcelable(MORE_LIST_STATE_KEY, bundlesList.getLayoutManager()
          .onSaveInstanceState());
    }
  }

  @Override public void onDestroyView() {
    listState = bundlesList.getLayoutManager()
        .onSaveInstanceState();
    bundlesList = null;
    adapter = null;
    layoutManager = null;
    swipeRefreshLayout = null;
    genericErrorView = null;
    noNetworkErrorView = null;
    progressBar = null;
    toolbar = null;
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    uiEventsListener = null;
    oneDecimalFormatter = null;
    adClickedEvents = null;
    super.onDestroy();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void showBundles(List<HomeBundle> bundles) {
    adapter.update(bundles);
    if (listState != null) {
      bundlesList.getLayoutManager()
          .onRestoreInstanceState(listState);
      listState = null;
    }
  }

  @Override public void addHighlightedAd(AdClick click) {
    adapter.addHighlightedAd(click);
  }

  @Override public void showLoading() {
    bundlesList.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    bundlesList.setVisibility(View.VISIBLE);
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
    swipeRefreshLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showGenericError() {
    this.genericErrorView.setVisibility(View.VISIBLE);
    this.noNetworkErrorView.setVisibility(View.GONE);
    this.bundlesList.setVisibility(View.GONE);
    this.progressBar.setVisibility(View.GONE);
    if (this.swipeRefreshLayout.isRefreshing()) {
      this.swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public Observable<Void> refreshes() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public Observable<Object> reachesBottom() {
    return Observable.merge(RxRecyclerView.scrollEvents(bundlesList)
        .map(scroll -> isEndReached()), notifyItemsAdded)
        .distinctUntilChanged()
        .filter(isEnd -> isEnd)
        .cast(Object.class);
  }

  @Override public Observable<HomeEvent> moreClicked() {
    return uiEventsListener.filter(homeClick -> homeClick.getType()
        .equals(HomeEvent.Type.MORE));
  }

  @Override public Observable<AppHomeEvent> appClicked() {
    return uiEventsListener.filter(homeClick -> homeClick.getType()
        .equals(HomeEvent.Type.APP))
        .cast(AppHomeEvent.class);
  }

  @Override public Observable<AdHomeEvent> adClicked() {
    return adClickedEvents;
  }

  @Override public void showLoadMore() {
    adapter.addLoadMore();
  }

  @Override public void hideShowMore() {
    if (adapter != null) {
      adapter.removeLoadMore();
    }
  }

  @Override public void showMoreHomeBundles(List<HomeBundle> bundles) {
    adapter.add(bundles);
    notifyItemsAdded.onNext(false);
  }

  @Override public void hideRefresh() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void showNetworkError() {
    this.noNetworkErrorView.setVisibility(View.VISIBLE);
    this.genericErrorView.setVisibility(View.GONE);
    this.bundlesList.setVisibility(View.GONE);
    this.progressBar.setVisibility(View.GONE);
    if (this.swipeRefreshLayout.isRefreshing()) {
      this.swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public Observable<Void> retryClicked() {
    return Observable.merge(RxView.clicks(retryButton), RxView.clicks(noNetworkRetryButton));
  }

  @Override public Observable<HomeEvent> bundleScrolled() {
    return uiEventsListener.filter(click -> click.getType()
        .equals(HomeEvent.Type.SCROLL_RIGHT))
        .debounce(200, TimeUnit.MILLISECONDS);
  }

  @Override public Observable<HomeEvent> visibleBundles() {
    return RxRecyclerView.scrollEvents(bundlesList)
        .subscribeOn(AndroidSchedulers.mainThread())
        .map(recyclerViewScrollEvent -> layoutManager.findFirstVisibleItemPosition())
        .filter(position -> position != RecyclerView.NO_POSITION)
        .distinctUntilChanged()
        .map(visibleItem -> new HomeEvent(adapter.getBundle(visibleItem), visibleItem, null));
  }

  @Override public void setToolbarInfo(String title) {
    toolbar.setTitle(Translator.translate(title, getContext(), ""));
    toolbar.setLogo(R.drawable.logo_toolbar);
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= VISIBLE_THRESHOLD;
  }
}
