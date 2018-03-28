package cm.aptoide.pt.home;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 05/03/2018.
 */

public class BottomHomeFragment extends NavigationTrackFragment implements HomeView {

  private static final String LIST_STATE_KEY = "cm.aptoide.pt.BottomHomeFragment.ListState";

  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private static final int VISIBLE_THRESHOLD = 2;
  @Inject Home home;
  @Inject HomePresenter presenter;
  private RecyclerView bundlesList;
  private BundlesAdapter adapter;
  private PublishSubject<HomeMoreClick> uiEventsListener;
  private PublishSubject<Application> appClickedEvents;
  private PublishSubject<AppClick> recommendsClickedEvents;
  private PublishSubject<GetAdsResponse.Ad> adClickedEvents;
  private LinearLayoutManager layoutManager;
  private DecimalFormat oneDecimalFormatter;
  private View genericErrorView;
  private View noNetworkErrorView;
  private ProgressBar progressBar;
  private SwipeRefreshLayout swipeRefreshLayout;
  private Parcelable listState;
  private View noNetworkRetryButton;
  private View retryButton;
  private ImageView userAvatar;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    uiEventsListener = PublishSubject.create();
    appClickedEvents = PublishSubject.create();
    recommendsClickedEvents = PublishSubject.create();
    adClickedEvents = PublishSubject.create();
    oneDecimalFormatter = new DecimalFormat("#.#");
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(LIST_STATE_KEY)) {
        listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        savedInstanceState.putParcelable(LIST_STATE_KEY, null);
      }
    }
    userAvatar = (ImageView) view.findViewById(R.id.user_actionbar_icon);
    bundlesList = (RecyclerView) view.findViewById(R.id.bundles_list);
    genericErrorView = view.findViewById(R.id.generic_error);
    noNetworkErrorView = view.findViewById(R.id.no_network_connection);
    retryButton = genericErrorView.findViewById(R.id.retry);
    noNetworkRetryButton = noNetworkErrorView.findViewById(R.id.retry);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
    adapter = new BundlesAdapter(new ArrayList<>(), new ProgressBundle(), uiEventsListener,
        oneDecimalFormatter, appClickedEvents, adClickedEvents, recommendsClickedEvents);
    layoutManager = new LinearLayoutManager(getContext());
    bundlesList.setLayoutManager(layoutManager);
    bundlesList.setAdapter(adapter);
    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", null);
  }

  @Override public void onDestroy() {
    uiEventsListener = null;
    oneDecimalFormatter = null;
    adClickedEvents = null;
    appClickedEvents = null;
    recommendsClickedEvents = null;
    userAvatar = null;
    super.onDestroy();
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
    super.onDestroyView();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_home, container, false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (bundlesList != null) {
      outState.putParcelable(LIST_STATE_KEY, bundlesList.getLayoutManager()
          .onSaveInstanceState());
    }
  }

  @Override public void showHomeBundles(List<HomeBundle> bundles) {
    adapter.update(bundles);
    if (listState != null) {
      bundlesList.getLayoutManager()
          .onRestoreInstanceState(listState);
      listState = null;
    }
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
    return RxRecyclerView.scrollEvents(bundlesList)
        .filter(scroll -> isEndReached())
        .distinctUntilChanged()
        .cast(Object.class);
  }

  @Override public Observable<HomeMoreClick> moreClicked() {
    return uiEventsListener;
  }

  @Override public Observable<Application> appClicked() {
    return appClickedEvents;
  }

  @Override public Observable<AppClick> recommendedAppClicked() {
    return recommendsClickedEvents;
  }

  @Override public Observable<GetAdsResponse.Ad> adClicked() {
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
  }

  @UiThread @Override public void scrollToTop() {
    LinearLayoutManager layoutManager = ((LinearLayoutManager) bundlesList.getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      bundlesList.scrollToPosition(10);
    }
    bundlesList.smoothScrollToPosition(0);
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

  @Override public void setUserImage(String userAvatarUrl) {
    ImageLoader.with(getContext())
        .loadWithCircleTransformAndPlaceHolder(userAvatarUrl, userAvatar,
            R.drawable.my_account_placeholder);
  }

  @Override public Observable<Void> imageClick() {
    return RxView.clicks(userAvatar);
  }

  @Override public void showAvatar() {
    userAvatar.setVisibility(View.VISIBLE);
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= VISIBLE_THRESHOLD;
  }
}
