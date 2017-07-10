package cm.aptoide.pt.v8engine.social.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.PackageRepository;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.CardViewHolderFactory;
import cm.aptoide.pt.v8engine.social.data.MinimalCardViewFactory;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.social.data.SharePreviewFactory;
import cm.aptoide.pt.v8engine.social.data.Timeline;
import cm.aptoide.pt.v8engine.social.data.TimelineResponseCardMapper;
import cm.aptoide.pt.v8engine.social.data.TimelineService;
import cm.aptoide.pt.v8engine.social.presenter.TimelineNavigator;
import cm.aptoide.pt.v8engine.social.presenter.TimelinePresenter;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import cm.aptoide.pt.v8engine.view.recycler.RecyclerViewPositionHelper;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelineFragment extends FragmentView implements TimelineView {

  public static final int LATEST_PACKAGES_COUNT = 20;
  public static final int RANDOM_PACKAGES_COUNT = 10;
  private static final String ACTION_KEY = "action";
  private static final String USER_ID_KEY = "USER_ID_KEY";
  private static final String STORE_ID = "STORE_ID";
  private static final String STORE_CONTEXT = "STORE_CONTEXT";

  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private final int visibleThreshold = 5;
  private CardAdapter adapter;
  private PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private PublishSubject<Post> sharePreviewPublishSubject;
  private RecyclerView list;
  private ProgressBar progressBar;
  private SwipeRefreshLayout swipeRefreshLayout;
  private View coordinatorLayout;
  private RecyclerViewPositionHelper helper;
  private View genericError;
  private View retryButton;
  private TokenInvalidator tokenInvalidator;
  private LinksHandlerFactory linksHandlerFactory;
  private SharedPreferences sharedPreferences;
  private FloatingActionButton floatingActionButton;
  private InstallManager installManager;
  private boolean newRefresh;
  private Long userId;
  private Long storeId;
  private StoreContext storeContext;
  private AptoideAccountManager accountManager;
  private AlertDialog shareDialog;
  private SharePreviewFactory sharePreviewFactory;
  private SpannableFactory spannableFactory;

  public static Fragment newInstance(String action, Long userId, Long storeId,
      StoreContext storeContext) {
    final Bundle args = new Bundle();
    if (userId != null) {
      args.putLong(USER_ID_KEY, userId);
    }
    if (storeId != null) {
      args.putLong(STORE_ID, storeId);
    }
    args.putSerializable(STORE_CONTEXT, storeContext);
    Fragment fragment = new TimelineFragment();
    args.putString(ACTION_KEY, action);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    newRefresh = true;
    userId = getArguments().containsKey(USER_ID_KEY) ? getArguments().getLong(USER_ID_KEY) : null;
    storeId = getArguments().containsKey(STORE_ID) ? getArguments().getLong(STORE_ID) : null;
    storeContext = (StoreContext) getArguments().getSerializable(STORE_CONTEXT);
    accountManager = ((V8Engine) getActivity().getApplicationContext()).getAccountManager();
    linksHandlerFactory = new LinksHandlerFactory(getContext());
    tokenInvalidator = ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    sharePreviewFactory = new SharePreviewFactory(accountManager);
    sharedPreferences =
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences();
    cardTouchEventPublishSubject = PublishSubject.create();
    sharePreviewPublishSubject = PublishSubject.create();
    final DateCalculator dateCalculator = new DateCalculator(getContext().getApplicationContext(),
        getContext().getApplicationContext()
            .getResources());
    spannableFactory = new SpannableFactory();
    adapter = new CardAdapter(Collections.emptyList(),
        new CardViewHolderFactory(cardTouchEventPublishSubject, dateCalculator, spannableFactory,
            new MinimalCardViewFactory(dateCalculator, spannableFactory,
                cardTouchEventPublishSubject)), new ProgressCard());
    installManager = ((V8Engine) getContext().getApplicationContext()).getInstallManager(
        InstallerFactory.ROLLBACK);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_timeline, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    genericError = view.findViewById(R.id.generic_error);
    retryButton = genericError.findViewById(R.id.retry);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    list = (RecyclerView) view.findViewById(R.id.fragment_cards_list);
    list.setAdapter(adapter);
    list.setLayoutManager(new LinearLayoutManager(getContext()));
    helper = RecyclerViewPositionHelper.createHelper(list);
    // Pull-to-refresh
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
    coordinatorLayout = view.findViewById(R.id.coordinator_layout);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);

    attachPresenter(new TimelinePresenter(this,
        new Timeline(new TimelineService(getArguments().getString(ACTION_KEY), userId,
            ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultClient(),
            WebService.getDefaultConverter(),
            new PackageRepository(getContext().getPackageManager()), LATEST_PACKAGES_COUNT,
            RANDOM_PACKAGES_COUNT, new TimelineResponseCardMapper(), linksHandlerFactory, 20, 0,
            Integer.MAX_VALUE, tokenInvalidator, sharedPreferences), installManager,
        new DownloadFactory()), CrashReport.getInstance(),
        new TimelineNavigator(getFragmentNavigator(), accountManager,
            getContext().getString(R.string.likes)), new PermissionManager(),
        (PermissionService) getContext(), installManager, RepositoryFactory.getStoreRepository(),
        new StoreUtilsProxy(((V8Engine) getContext().getApplicationContext()).getAccountManager(),
            ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7(),
            new StoreCredentialsProviderImpl(), AccessorFactory.getAccessorFor(Store.class),
            ((V8Engine) getContext().getApplicationContext()).getDefaultClient(),
            WebService.getDefaultConverter(),
            ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences()),
        new StoreCredentialsProviderImpl(), accountManager, userId, storeId, storeContext,
        getContext().getResources(), getFragmentNavigator()), savedInstanceState);
  }

  @Override public void showCards(List<Post> cards) {
    adapter.updateCards(cards);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
    list.setVisibility(View.VISIBLE);
  }

  @Override public void showProgressIndicator() {
    list.setVisibility(View.GONE);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideProgressIndicator() {
    list.setVisibility(View.VISIBLE);
    swipeRefreshLayout.setVisibility(View.VISIBLE);
    coordinatorLayout.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.GONE);
  }

  @Override public void hideRefresh() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void showMoreCards(List<Post> cards) {
    adapter.addCards(cards);
  }

  @Override public void showGenericError() {
    this.genericError.setVisibility(View.VISIBLE);
    this.list.setVisibility(View.GONE);
    this.progressBar.setVisibility(View.GONE);
    this.swipeRefreshLayout.setVisibility(View.GONE);
    this.coordinatorLayout.setVisibility(View.GONE);
    if (this.swipeRefreshLayout.isRefreshing()) {
      this.swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public rx.Observable<Void> refreshes() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public Observable<Void> reachesBottom() {
    return RxRecyclerView.scrollEvents(list)
        .filter(event -> helper != null
            && event.view()
            .isAttachedToWindow()
            && (helper.getItemCount() - event.view()
            .getChildCount()) <= ((helper.findFirstVisibleItemPosition() == -1 ? 0
            : helper.findFirstVisibleItemPosition()) + visibleThreshold))
        .map(event -> null)
        .cast(Void.class);
  }

  @Override public Observable<CardTouchEvent> postClicked() {
    return cardTouchEventPublishSubject;
  }

  @Override public Observable<Post> shareConfirmation() {
    return sharePreviewPublishSubject;
  }

  @Override public Observable<Void> retry() {
    return RxView.clicks(retryButton);
  }

  @Override public void showLoadMoreProgressIndicator() {
    adapter.addLoadMoreProgress();
  }

  @Override public void hideLoadMoreProgressIndicator() {
    adapter.removeLoadMoreProgress();
  }

  @Override public boolean isNewRefresh() {
    boolean b = newRefresh;
    newRefresh = false;
    return b;
  }

  @Override public Observable<Void> floatingActionButtonClicked() {
    return RxView.clicks(floatingActionButton);
  }

  @Override public Completable showFloatingActionButton() {
    return Completable.fromAction(() -> {
      // todo up transition
      //floatingActionButton.animate().yBy(-100f);
      floatingActionButton.setVisibility(View.VISIBLE);
    });
  }

  @Override public Completable hideFloatingActionButton() {
    return Completable.fromAction(() -> {
      // todo down transition
      //floatingActionButton.animate().yBy(100f);
      floatingActionButton.setVisibility(View.GONE);
    });
  }

  @Override public Observable<Direction> scrolled() {
    return RxRecyclerView.scrollEvents(list)
        .map(event -> new Direction(event.dx(), event.dy()));
  }

  @Override public void showRootAccessDialog() {
    GenericDialogs.createGenericYesNoCancelMessage(getContext(), null,
        AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog,
            getContext().getResources()))
        .subscribe(eResponse -> {
          switch (eResponse) {
            case YES:
              installManager.rootInstallAllowed(true);
              break;
            case NO:
              installManager.rootInstallAllowed(false);
              break;
          }
        });
  }

  @Override public void updateInstallProgress(Post card, int cardPosition) {
    adapter.updateCard(card, cardPosition);
  }

  @Override public void showStoreSubscribedMessage(String storeName) {
    ShowMessage.asSnack(getView(), AptoideUtils.StringU.getFormattedString(R.string.store_followed,
        getContext().getResources(), storeName));
  }

  @Override public void showStoreUnsubscribedMessage(String storeName) {
    ShowMessage.asSnack(getView(),
        AptoideUtils.StringU.getFormattedString(R.string.unfollowing_store_message,
            getContext().getResources(), storeName));
  }

  @Override public void showSharePreview(Post post) {
    shareDialog =
        new AlertDialog.Builder(getContext()).setTitle(R.string.timeline_title_shared_card_preview)
            .setMessage(R.string.social_timeline_you_will_share)
            .setView(sharePreviewFactory.getSharePreviewView(post, getContext()))
            .setPositiveButton(R.string.share,
                (dialogInterface, i) -> sharePreviewPublishSubject.onNext(post))
            .setNegativeButton(android.R.string.cancel, null)
            .create();
    shareDialog.show();
  }

  @Override public void showShareSuccessMessage() {
    ShowMessage.asSnack(getView(), R.string.social_timeline_share_dialog_title);
  }

  // TODO: 07/07/2017 migrate this behaviour to mvp
  @UiThread public void goToTop() {
    LinearLayoutManager layoutManager = ((LinearLayoutManager) list.getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      list.scrollToPosition(10);
    }
    list.smoothScrollToPosition(0);
  }
}
