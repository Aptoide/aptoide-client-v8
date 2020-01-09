package cm.aptoide.pt.app.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.aptoideviews.errors.ErrorView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.editorial.CaptionBackgroundPainter;
import cm.aptoide.pt.home.ScrollableView;
import cm.aptoide.pt.home.bundles.BundlesAdapter;
import cm.aptoide.pt.home.bundles.ads.AdHomeEvent;
import cm.aptoide.pt.home.bundles.ads.AdsBundlesViewHolderFactory;
import cm.aptoide.pt.home.bundles.base.AppHomeEvent;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.home.bundles.misc.ErrorHomeBundle;
import cm.aptoide.pt.home.bundles.misc.ProgressBundle;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import static android.view.View.GONE;

/**
 * Created by D01 on 04/06/2018.
 */

public class MoreBundleFragment extends NavigationTrackFragment
    implements MoreBundleView, ScrollableView {

  private static final String MORE_LIST_STATE_KEY = "cm.aptoide.pt.more.ListState";
  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private static final int VISIBLE_THRESHOLD = 1;
  @Inject MoreBundlePresenter presenter;
  @Inject @Named("marketName") String marketName;
  @Inject CaptionBackgroundPainter captionBackgroundPainter;
  @Inject ThemeManager themeAttributeProvider;
  private RecyclerView bundlesList;
  private BundlesAdapter adapter;
  private PublishSubject<HomeEvent> uiEventsListener;
  private PublishSubject<AdHomeEvent> adClickedEvents;
  private LinearLayoutManager layoutManager;
  private DecimalFormat oneDecimalFormatter;
  private ErrorView errorView;
  private ProgressBar progressBar;
  private SwipeRefreshLayout swipeRefreshLayout;
  private Parcelable listState;
  private Toolbar toolbar;
  private View toolbarElement;
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
    bundlesList = view.findViewById(R.id.more_bundles_list);
    toolbarElement = view.findViewById(R.id.action_bar);
    errorView = view.findViewById(R.id.error_view);
    progressBar = view.findViewById(R.id.progress_bar);
    swipeRefreshLayout = view.findViewById(R.id.more_refresh_layout);
    toolbar = view.findViewById(R.id.toolbar);
    adapter = new BundlesAdapter(new ArrayList<>(), new ProgressBundle(), new ErrorHomeBundle(),
        oneDecimalFormatter, uiEventsListener,
        new AdsBundlesViewHolderFactory(uiEventsListener, adClickedEvents, oneDecimalFormatter,
            marketName, false), captionBackgroundPainter, marketName, themeAttributeProvider);
    layoutManager = new LinearLayoutManager(getContext());
    bundlesList.setLayoutManager(layoutManager);
    bundlesList.setAdapter(adapter);
    AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
    if (getArguments().getBoolean(StoreTabGridRecyclerFragment.BundleCons.TOOLBAR, true)) {
      appCompatActivity.setSupportActionBar(toolbar);
      ActionBar actionBar = appCompatActivity.getSupportActionBar();
      if (actionBar != null) {
        actionBar.setDisplayHomeAsUpEnabled(true);
      }
    } else {
      toolbarElement.setVisibility(GONE);
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
    errorView = null;
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

  @Override public void showLoading() {
    bundlesList.setVisibility(GONE);
    errorView.setVisibility(GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    bundlesList.setVisibility(View.VISIBLE);
    errorView.setVisibility(GONE);
    progressBar.setVisibility(GONE);
    swipeRefreshLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showGenericError() {
    errorView.setError(ErrorView.Error.GENERIC);
    errorView.setVisibility(View.VISIBLE);
    bundlesList.setVisibility(GONE);
    progressBar.setVisibility(GONE);
    if (swipeRefreshLayout.isRefreshing()) {
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
        .equals(HomeEvent.Type.MORE) || homeClick.getType()
        .equals(HomeEvent.Type.MORE_TOP));
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
    errorView.setError(ErrorView.Error.NO_NETWORK);
    errorView.setVisibility(View.VISIBLE);
    this.bundlesList.setVisibility(GONE);
    this.progressBar.setVisibility(GONE);
    if (this.swipeRefreshLayout.isRefreshing()) {
      this.swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public Observable<Void> retryClicked() {
    return errorView.retryClick();
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

  @Override public void updateEditorialCards() {
    adapter.updateEditorials();
    if (listState != null) {
      bundlesList.getLayoutManager()
          .onRestoreInstanceState(listState);
      listState = null;
    }
  }

  @Override public void setToolbarInfo(String title) {
    toolbar.setTitle(Translator.translate(title, getContext(), ""));
    toolbar.setLogo(R.drawable.logo_toolbar);
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= VISIBLE_THRESHOLD;
  }

  @UiThread @Override public void scrollToTop() {
    LinearLayoutManager layoutManager = ((LinearLayoutManager) bundlesList.getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      bundlesList.scrollToPosition(10);
    }
    bundlesList.smoothScrollToPosition(0);
  }

  @Override public boolean isAtTop() {
    LinearLayoutManager layoutManager = ((LinearLayoutManager) bundlesList.getLayoutManager());
    return layoutManager.findFirstVisibleItemPosition() == 0;
  }
}
