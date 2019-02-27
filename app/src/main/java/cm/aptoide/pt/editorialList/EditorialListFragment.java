package cm.aptoide.pt.editorialList;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.home.EditorialHomeEvent;
import cm.aptoide.pt.home.HomeEvent;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class EditorialListFragment extends NavigationTrackFragment implements EditorialListView {

  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private static final int VISIBLE_THRESHOLD = 1;
  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.CURATION;
  @Inject public EditorialListPresenter presenter;
  private BottomNavigationActivity bottomNavigationActivity;
  private RecyclerView editorialList;
  private EditorialListAdapter adapter;
  private PublishSubject<HomeEvent> uiEventsListener;
  private LinearLayoutManager layoutManager;
  private SwipeRefreshLayout swipeRefreshLayout;

  //Error views
  private View genericErrorView;
  private View noNetworkErrorView;
  private ProgressBar progressBar;
  private View noNetworkRetryButton;
  private View retryButton;

  private ImageView userAvatar;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    uiEventsListener = PublishSubject.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    userAvatar = view.findViewById(R.id.user_actionbar_icon);
    layoutManager = new LinearLayoutManager(getContext());
    adapter = new EditorialListAdapter(new ArrayList<>(), new ProgressCard(), uiEventsListener);
    editorialList = view.findViewById(R.id.editorial_list);
    editorialList.setLayoutManager(layoutManager);
    editorialList.setAdapter(adapter);
    swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);

    //Error views
    genericErrorView = view.findViewById(R.id.generic_error);
    noNetworkErrorView = view.findViewById(R.id.no_network_connection);
    retryButton = genericErrorView.findViewById(R.id.retry);
    noNetworkRetryButton = noNetworkErrorView.findViewById(R.id.retry);
    progressBar = view.findViewById(R.id.progress_bar);
    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onDestroy() {
    uiEventsListener = null;
    super.onDestroy();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_editorial_list, container, false);
  }

  @Override public Observable<EditorialHomeEvent> editorialCardClicked() {
    return uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.EDITORIAL))
        .cast(EditorialHomeEvent.class);
  }

  @Override public void showLoading() {
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
    swipeRefreshLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showGenericError() {
    genericErrorView.setVisibility(View.VISIBLE);
    noNetworkErrorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
    if (this.swipeRefreshLayout.isRefreshing()) {
      this.swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public void showNetworkError() {
    this.noNetworkErrorView.setVisibility(View.VISIBLE);
    this.genericErrorView.setVisibility(View.GONE);
    this.progressBar.setVisibility(View.GONE);
    if (this.swipeRefreshLayout.isRefreshing()) {
      this.swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public Observable<Void> retryClicked() {
    return Observable.merge(RxView.clicks(retryButton), RxView.clicks(noNetworkRetryButton));
  }

  @Override public Observable<Void> refreshes() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public void hideRefresh() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public Observable<Void> imageClick() {
    return RxView.clicks(userAvatar);
  }

  @Override public void showAvatar() {
    userAvatar.setVisibility(View.VISIBLE);
  }

  @Override public void setDefaultUserImage() {
    ImageLoader.with(getContext())
        .loadUsingCircleTransform(R.drawable.ic_account_circle, userAvatar);
  }

  @Override public void setUserImage(String userAvatarUrl) {
    ImageLoader.with(getContext())
        .loadWithShadowCircleTransformWithPlaceholder(userAvatarUrl, userAvatar,
            R.drawable.ic_account_circle);
  }

  @Override public Observable<Object> reachesBottom() {
    return RxRecyclerView.scrollEvents(editorialList)
        .map(scroll -> isEndReached())
        .distinctUntilChanged()
        .filter(isEnd -> isEnd)
        .cast(Object.class);
  }

  @Override public void populateView(EditorialListViewModel editorialListViewModel) {
    adapter.add(editorialListViewModel.getCurationCards());
  }

  @Override public void showError(EditorialListViewModel.Error error) {
    switch (error) {
      case NETWORK:
        noNetworkErrorView.setVisibility(View.VISIBLE);
        break;
      case GENERIC:
        genericErrorView.setVisibility(View.VISIBLE);
        break;
    }
  }

  @Override public void showLoadMore() {
    if (adapter != null) {
      adapter.addLoadMore();
    }
  }

  @Override public void hideLoadMore() {
    if (adapter != null) {
      adapter.removeLoadMore();
    }
  }

  @Override public void update(List<CurationCard> curationCards) {
    if (adapter != null) {
      adapter.update(curationCards);
    }
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= VISIBLE_THRESHOLD;
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
  }

  @Override public void onDestroyView() {
    editorialList = null;
    adapter = null;
    layoutManager = null;
    genericErrorView = null;
    noNetworkErrorView = null;
    progressBar = null;
    noNetworkRetryButton = null;
    retryButton = null;
    userAvatar = null;
    swipeRefreshLayout = null;
    super.onDestroyView();
  }
}
