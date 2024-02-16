package cm.aptoide.pt.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.aptoideviews.errors.ErrorView;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.R;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.editorial.CaptionBackgroundPainter;
import cm.aptoide.pt.editorial.EditorialFragment;
import cm.aptoide.pt.home.bundles.BundlesAdapter;
import cm.aptoide.pt.home.bundles.HomeBundlesModel;
import cm.aptoide.pt.home.bundles.ads.AdHomeEvent;
import cm.aptoide.pt.home.bundles.ads.AdsBundlesViewHolderFactory;
import cm.aptoide.pt.home.bundles.base.AppComingSoonPromotionalBundle;
import cm.aptoide.pt.home.bundles.base.AppHomeEvent;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.home.bundles.editorial.EditorialBundleViewHolder;
import cm.aptoide.pt.home.bundles.editorial.EditorialHomeEvent;
import cm.aptoide.pt.home.bundles.misc.ErrorHomeBundle;
import cm.aptoide.pt.home.bundles.misc.ProgressBundle;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.reactions.ReactionsHomeEvent;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.google.android.material.snackbar.Snackbar;
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
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 05/03/2018.
 */

public class HomeFragment extends NavigationTrackFragment implements HomeView, ScrollableView {

  private static final String LIST_STATE_KEY = "cm.aptoide.pt.BottomHomeFragment.ListState";

  private static final String TAG = EditorialFragment.class.getName();
  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private static final int VISIBLE_THRESHOLD = 2;
  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.HOME;
  @Inject HomePresenter presenter;
  @Inject @Named("marketName") String marketName;
  @Inject CaptionBackgroundPainter captionBackgroundPainter;
  @Inject ThemeManager themeManager;
  private RecyclerView bundlesList;
  private BundlesAdapter adapter;
  private PublishSubject<HomeEvent> uiEventsListener;
  private PublishSubject<Void> snackListener;
  private PublishSubject<Boolean> firstBundleLoadListener;
  private PublishSubject<AdHomeEvent> adClickedEvents;
  private LinearLayoutManager layoutManager;
  private DecimalFormat oneDecimalFormatter;
  private ProgressBar progressBar;
  private SwipeRefreshLayout swipeRefreshLayout;
  private Parcelable listState;
  private ImageView userAvatar;
  private BottomNavigationActivity bottomNavigationActivity;
  private ErrorView errorView;

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
    snackListener = null;
    super.onDestroy();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);

    uiEventsListener = PublishSubject.create();
    adClickedEvents = PublishSubject.create();
    snackListener = PublishSubject.create();
    firstBundleLoadListener = PublishSubject.create();
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
    bundlesList.getItemAnimator()
        .setChangeDuration(0);
    errorView = view.findViewById(R.id.error_view);
    progressBar = view.findViewById(R.id.progress_bar);
    swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
    layoutManager = new LinearLayoutManager(getContext());
    bundlesList.setLayoutManager(layoutManager);

    adapter = new BundlesAdapter(new ArrayList<>(), new ProgressBundle(), new ErrorHomeBundle(),
        oneDecimalFormatter, uiEventsListener,
        new AdsBundlesViewHolderFactory(uiEventsListener, adClickedEvents, oneDecimalFormatter,
            marketName), captionBackgroundPainter, marketName, themeManager);
    bundlesList.setAdapter(adapter);

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
    errorView = null;
    progressBar = null;
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

  @Override public void showLoading() {
    bundlesList.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    bundlesList.setVisibility(View.VISIBLE);
    errorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
    swipeRefreshLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showGenericError() {
    errorView.setError(ErrorView.Error.GENERIC);
    errorView.setVisibility(View.VISIBLE);
    bundlesList.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
    if (swipeRefreshLayout.isRefreshing()) {
      swipeRefreshLayout.setRefreshing(false);
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
        .equals(HomeEvent.Type.REWARD_APP) || homeClick.getType()
        .equals(HomeEvent.Type.INSTALL_PROMOTIONAL) || homeClick.getType()
        .equals(HomeEvent.Type.ESKILLS_APP))
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
    errorView.setError(ErrorView.Error.NO_NETWORK);
    errorView.setVisibility(View.VISIBLE);
    this.bundlesList.setVisibility(View.GONE);
    this.progressBar.setVisibility(View.GONE);
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
    return Observable.merge(RxRecyclerView.scrollEvents(bundlesList),
        firstBundleLoadListener.filter(isLoaded -> isLoaded)
            .map(aBoolean -> 0))
        .map(recyclerViewScrollEvent -> layoutManager.findFirstVisibleItemPosition())
        .filter(position -> position != RecyclerView.NO_POSITION)
        .filter(position -> adapter.getBundle(position)
            .getContent() != null)
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

  @Override public Observable<EditorialHomeEvent> editorialCardClicked() {
    return uiEventsListener.filter(homeClick -> homeClick.getType()
        .equals(HomeEvent.Type.EDITORIAL))
        .cast(EditorialHomeEvent.class);
  }

  @Override public Observable<HomeEvent> infoBundleKnowMoreClicked() {
    return this.uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.APPC_KNOW_MORE));
  }

  @Override public Observable<EditorialHomeEvent> reactionsButtonClicked() {
    return uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.REACT_SINGLE_PRESS))
        .cast(EditorialHomeEvent.class);
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

  @Override public void setAdsTest(boolean showNatives) {

  }

  @Override public Observable<HomeEvent> walletOfferCardInstallWalletClick() {
    return this.uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.INSTALL_WALLET));
  }

  @Override public void sendDeeplinkToWalletAppView(String url) {
    Intent intent = new Intent(this.getContext(), DeepLinkIntentReceiver.class);
    intent.setData(Uri.parse(url));
    startActivity(intent);
  }

  @Override public void showConsentDialog() {
  }

  @Override public Observable<ReactionsHomeEvent> reactionClicked() {
    return uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.REACTION))
        .cast(ReactionsHomeEvent.class);
  }

  @Override public Observable<EditorialHomeEvent> reactionButtonLongPress() {
    return uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.REACT_LONG_PRESS))
        .cast(EditorialHomeEvent.class);
  }

  @Override public void showReactionsPopup(String cardId, String groupId, int bundlePosition) {
    EditorialBundleViewHolder editorialBundleViewHolder =
        getViewHolderForAdapterPosition(bundlePosition);
    if (editorialBundleViewHolder != null) {
      editorialBundleViewHolder.showReactions(cardId, groupId, bundlePosition);
    }
  }

  @Override public void showLogInDialog() {
    ShowMessage.asSnack(getActivity(), R.string.editorial_reactions_login_short, R.string.login,
        snackView -> snackListener.onNext(null), Snackbar.LENGTH_SHORT);
  }

  @Override public Observable<Void> snackLogInClick() {
    return snackListener;
  }

  @Override public void showGenericErrorToast() {
    Snackbar.make(getView(), getString(R.string.error_occured), Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showNetworkErrorToast() {
    Snackbar.make(getView(), getString(R.string.connection_error), Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showLoadMoreError() {
    adapter.showLoadMoreError();
  }

  @Override public void removeLoadMoreError() {
    adapter.removeLoadMoreError();
  }

  @Override public Observable<HomeEvent> onLoadMoreRetryClicked() {
    return uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.LOAD_MORE_RETRY));
  }

  @Override public void showBundlesSkeleton(HomeBundlesModel homeBundles) {
    fireFirstBundleLoadedEvent(homeBundles);
    adapter.update(homeBundles.getList());
    if (listState != null) {
      bundlesList.getLayoutManager()
          .onRestoreInstanceState(listState);
      listState = null;
    }
    hideLoading();
  }

  @Override public Observable<HomeEvent> eSkillsKnowMoreClick() {
    return this.uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.ESKILLS_MORE));
  }

  @Override public Observable<HomeEvent> eSkillsClick() {
    return this.uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.ESKILLS));
  }

  @Override public Observable<HomeEvent> notifyMeClicked() {
    return this.uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.NOTIFY_ME));
  }

  @Override public Observable<HomeEvent> cancelNotifyMeClicked() {
    return this.uiEventsListener.filter(homeEvent -> homeEvent.getType()
        .equals(HomeEvent.Type.CANCEL_NOTIFY_ME));
  }

  @Override public void updateAppComingSoonStatus(AppComingSoonPromotionalBundle homeBundle,
      boolean isRegisteredForNotification) {
    adapter.updateAppComingSoonCard(homeBundle, isRegisteredForNotification);
  }

  private void fireFirstBundleLoadedEvent(HomeBundlesModel homeBundles) {
    try {
      if (homeBundles.getList()
          .get(0)
          .getContent() != null) {
        firstBundleLoadListener.onNext(true);
      }
    } catch (Exception ignored) {
    }
  }

  @Override public boolean isAtTop() {
    LinearLayoutManager layoutManager = ((LinearLayoutManager) bundlesList.getLayoutManager());
    return layoutManager.findFirstVisibleItemPosition() == 0;
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= VISIBLE_THRESHOLD && adapter.isLoaded();
  }

  private EditorialBundleViewHolder getViewHolderForAdapterPosition(int placeHolderPosition) {
    try {
      EditorialBundleViewHolder placeHolderViewHolder =
          ((EditorialBundleViewHolder) bundlesList.findViewHolderForAdapterPosition(
              placeHolderPosition));
      return placeHolderViewHolder;
    } catch (Exception e) {
      Log.e(TAG, "Unable to find editorialViewHolder");
    }
    return null;
  }
}
