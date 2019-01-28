package cm.aptoide.pt.home;

import android.app.Activity;
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
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.promotions.PromotionsHomeDialog;
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
 * Created by jdandrade on 05/03/2018.
 */

public class HomeFragment extends NavigationTrackFragment implements HomeView {

  private static final String LIST_STATE_KEY = "cm.aptoide.pt.BottomHomeFragment.ListState";

  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private static final int VISIBLE_THRESHOLD = 2;
  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.HOME;
  @Inject HomePresenter presenter;
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
  private ImageView userAvatar;
  private ImageView promotionsIcon;
  private TextView promotionsTicker;
  private BottomNavigationActivity bottomNavigationActivity;
  private LoggedInTermsAndConditionsDialog gdprDialog;
  private PromotionsHomeDialog promotionsHomeDialog;

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onDestroy() {
    uiEventsListener = null;
    oneDecimalFormatter = null;
    adClickedEvents = null;
    userAvatar = null;
    super.onDestroy();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);

    uiEventsListener = PublishSubject.create();
    adClickedEvents = PublishSubject.create();
    oneDecimalFormatter = new DecimalFormat("0.0");
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(LIST_STATE_KEY)) {
        listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        savedInstanceState.putParcelable(LIST_STATE_KEY, null);
      }
    }
    userAvatar = view.findViewById(R.id.user_actionbar_icon);
    bundlesList = view.findViewById(R.id.bundles_list);
    genericErrorView = view.findViewById(R.id.generic_error);
    noNetworkErrorView = view.findViewById(R.id.no_network_connection);
    retryButton = genericErrorView.findViewById(R.id.retry);
    noNetworkRetryButton = noNetworkErrorView.findViewById(R.id.retry);
    progressBar = view.findViewById(R.id.progress_bar);
    swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
    layoutManager = new LinearLayoutManager(getContext());
    bundlesList.setLayoutManager(layoutManager);
    gdprDialog = new LoggedInTermsAndConditionsDialog(getContext());
    promotionsHomeDialog = new PromotionsHomeDialog(getContext());
    promotionsIcon = view.findViewById(R.id.promotions_icon);
    promotionsTicker = view.findViewById(R.id.promotions_ticker);
    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", StoreContext.home.name());
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
    if (gdprDialog != null) {
      gdprDialog.destroyDialog();
      gdprDialog = null;
    }
    if (promotionsHomeDialog != null) {
      promotionsHomeDialog.destroyDialog();
      promotionsHomeDialog = null;
    }
    promotionsIcon = null;
    super.onDestroyView();
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
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
    return RxRecyclerView.scrollEvents(bundlesList)
        .map(scroll -> isEndReached())
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
        .equals(HomeEvent.Type.APP) || homeClick.getType()
        .equals(HomeEvent.Type.REWARD_APP))
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

  @Override public Observable<AppHomeEvent> recommendedAppClicked() {
    return uiEventsListener.filter(homeClick -> homeClick.getType()
        .equals(HomeEvent.Type.SOCIAL_CLICK) || homeClick.getType()
        .equals(HomeEvent.Type.SOCIAL_INSTALL))
        .cast(AppHomeEvent.class);
  }

  @Override public Observable<EditorialHomeEvent> editorialCardClicked() {
    return uiEventsListener.filter(homeClick -> homeClick.getType()
        .equals(HomeEvent.Type.EDITORIAL))
        .cast(EditorialHomeEvent.class);
  }

  @Override public Observable<String> gdprDialogClicked() {
    return gdprDialog.dialogClicked();
  }

  @Override public Observable<String> promotionsHomeDialogClicked() {
    return promotionsHomeDialog.dialogClicked();
  }

  @Override public Observable<HomeEvent> infoBundleKnowMoreClicked() {
    return this.uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.KNOW_MORE));
  }

  @UiThread @Override public void scrollToTop() {
    LinearLayoutManager layoutManager = ((LinearLayoutManager) bundlesList.getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      bundlesList.scrollToPosition(10);
    }
    bundlesList.smoothScrollToPosition(0);
  }

  @Override public void setUserImage(String userAvatarUrl) {
    ImageLoader.with(getContext())
        .loadWithShadowCircleTransformWithPlaceholder(userAvatarUrl, userAvatar,
            R.drawable.ic_account_circle);
  }

  @Override public Observable<Void> imageClick() {
    return RxView.clicks(userAvatar);
  }

  @Override public Observable<HomeEvent> dismissBundleClicked() {
    return this.uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.DISMISS_BUNDLE));
  }

  @Override public void hideBundle(int bundlePosition) {
    adapter.remove(bundlePosition);
  }

  @Override public void showAvatar() {
    userAvatar.setVisibility(View.VISIBLE);
  }

  @Override public void setDefaultUserImage() {
    ImageLoader.with(getContext())
        .loadUsingCircleTransform(R.drawable.ic_account_circle, userAvatar);
  }

  @Override public void showTermsAndConditionsDialog() {
    gdprDialog.showDialog();
  }

  @Override public Observable<Void> promotionsClick() {
    return RxView.clicks(promotionsIcon);
  }

  @Override public void showPromotionsHomeDialog(HomePromotionsWrapper wrapper) {
    promotionsHomeDialog.showDialog(getContext(), wrapper);
  }

  @Override public void showPromotionsHomeIcon(HomePromotionsWrapper homeWrapper) {
    promotionsIcon.setVisibility(View.VISIBLE);
    if (homeWrapper.getPromotions() > 0) {
      if (homeWrapper.getPromotions() < 10) {
        promotionsTicker.setText(Integer.toString(homeWrapper.getPromotions()));
      } else {
        promotionsTicker.setText("9+");
      }
      promotionsTicker.setVisibility(View.VISIBLE);
    }
  }

  @Override public void dismissPromotionsDialog() {
    promotionsHomeDialog.dismissDialog();
  }

  @Override public void setPromotionsTickerWithValue(int value) {
    promotionsTicker.setText(Integer.toString(value));
    promotionsTicker.setVisibility(View.VISIBLE);
  }

  @Override public void setEllipsizedPromotionsTicker() {
    promotionsTicker.setText("9+");
    promotionsTicker.setVisibility(View.VISIBLE);
  }

  @Override public void hidePromotionsIcon() {
    promotionsIcon.setVisibility(View.GONE);
    promotionsTicker.setVisibility(View.GONE);
  }

  @Override public void setAdsTest(boolean showNatives) {
    adapter = new BundlesAdapter(new ArrayList<>(), new ProgressBundle(), uiEventsListener,
        oneDecimalFormatter, adClickedEvents, marketName,
        new AdsBundlesViewHolderFactory(uiEventsListener, adClickedEvents, oneDecimalFormatter,
            marketName, showNatives));
    bundlesList.setAdapter(adapter);
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= VISIBLE_THRESHOLD;
  }
}
