package cm.aptoide.pt.social.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.InstallManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallerFactory;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.CardViewHolderFactory;
import cm.aptoide.pt.social.data.MinimalCardViewFactory;
import cm.aptoide.pt.social.data.Post;
import cm.aptoide.pt.social.data.PostComment;
import cm.aptoide.pt.social.data.SocialAction;
import cm.aptoide.pt.social.data.SocialCardTouchEvent;
import cm.aptoide.pt.social.data.Timeline;
import cm.aptoide.pt.social.data.TimelinePostsRepository;
import cm.aptoide.pt.social.data.TimelineResponseCardMapper;
import cm.aptoide.pt.social.data.TimelineService;
import cm.aptoide.pt.social.data.share.ShareDialogFactory;
import cm.aptoide.pt.social.data.share.ShareDialogInterface;
import cm.aptoide.pt.social.data.share.ShareEvent;
import cm.aptoide.pt.social.data.share.SharePostViewSetup;
import cm.aptoide.pt.social.data.viewedposts.PostReadReporter;
import cm.aptoide.pt.social.presenter.TimelineNavigator;
import cm.aptoide.pt.social.presenter.TimelinePresenter;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.util.DateCalculator;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.comments.CommentDialogFragment;
import cm.aptoide.pt.view.fragment.FragmentView;
import cm.aptoide.pt.view.navigator.TabNavigator;
import cm.aptoide.pt.view.recycler.RecyclerViewPositionHelper;
import cm.aptoide.pt.view.recycler.displayable.SpannableFactory;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelineFragment extends FragmentView implements TimelineView {

  private static final String ACTION_KEY = "action";
  private static final String USER_ID_KEY = "USER_ID_KEY";
  private static final String STORE_ID = "STORE_ID";
  private static final String STORE_CONTEXT = "STORE_CONTEXT";
  private static final String LIST_STATE_KEY = "LIST_STATE";

  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private final int visibleThreshold = 5;
  private Converter.Factory defaultConverter;
  private BodyInterceptor<BaseBody> baseBodyInterceptorV7;
  private boolean bottomAlreadyReached;
  private PostAdapter adapter;
  private PublishSubject<CardTouchEvent> postTouchEventPublishSubject;
  private PublishSubject<PostComment> commentPostResponseSubject;
  private RecyclerView list;
  private Parcelable listState;
  private ProgressBar progressBar;
  private SwipeRefreshLayout swipeRefreshLayout;
  private View coordinatorLayout;
  private RecyclerViewPositionHelper helper;
  private View genericError;
  private View retryButton;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;
  private FloatingActionButton floatingActionButton;
  private InstallManager installManager;
  private Long userId;
  private Long storeId;
  private StoreContext storeContext;
  private AptoideAccountManager accountManager;
  private ShareDialogInterface<Object> shareDialog;
  private TabNavigator tabNavigator;
  private ShareDialogFactory shareDialogFactory;
  private PublishSubject<ShareEvent> sharePostPublishSubject;
  private PublishRelay<View> loginPrompt;
  private TimelineService timelineService;
  private TimelinePostsRepository timelinePostsRepository;
  private DateCalculator dateCalculator;
  private boolean postIndicator;
  private boolean progressIndicator;
  private PostReadReporter postReadReporter;
  private LinearLayoutManager layoutManager;
  private TimelineAnalytics timelineAnalytics;
  private OkHttpClient defaultClient;

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

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof TabNavigator) {
      tabNavigator = (TabNavigator) activity;
    } else {
      throw new IllegalStateException(
          "Activity must implement " + TabNavigator.class.getSimpleName());
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    userId = getArguments().containsKey(USER_ID_KEY) ? getArguments().getLong(USER_ID_KEY) : null;
    storeId = getArguments().containsKey(STORE_ID) ? getArguments().getLong(STORE_ID) : null;
    storeContext = (StoreContext) getArguments().getSerializable(STORE_CONTEXT);
    baseBodyInterceptorV7 =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    defaultConverter = WebService.getDefaultConverter();
    defaultClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    timelineAnalytics = new TimelineAnalytics(Analytics.getInstance(),
        AppEventsLogger.newLogger(getContext().getApplicationContext()), baseBodyInterceptorV7,
        defaultClient, defaultConverter, tokenInvalidator, V8Engine.getConfiguration()
        .getAppId(), sharedPreferences);
    accountManager = ((V8Engine) getActivity().getApplicationContext()).getAccountManager();
    tokenInvalidator = ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    sharedPreferences =
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences();
    postTouchEventPublishSubject = PublishSubject.create();
    sharePostPublishSubject = PublishSubject.create();
    commentPostResponseSubject = PublishSubject.create();
    dateCalculator = new DateCalculator(getContext().getApplicationContext(),
        getContext().getApplicationContext()
            .getResources());
    shareDialogFactory =
        new ShareDialogFactory(getContext(), new SharePostViewSetup(dateCalculator));
    installManager = ((V8Engine) getContext().getApplicationContext()).getInstallManager(
        InstallerFactory.ROLLBACK);

    timelinePostsRepository =
        ((V8Engine) getContext().getApplicationContext()).getTimelineRepository(
            getArguments().getString(ACTION_KEY));

    OkHttpClient okhttp = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    postReadReporter =
        new PostReadReporter(baseBodyInterceptorV7, okhttp, defaultConverter, tokenInvalidator,
            sharedPreferences);
    timelineService = new TimelineService(userId, baseBodyInterceptorV7, okhttp, defaultConverter,
        new TimelineResponseCardMapper(), tokenInvalidator, sharedPreferences);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if (list != null) {
      outState.putParcelable(LIST_STATE_KEY, list.getLayoutManager()
          .onSaveInstanceState());
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_timeline, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(LIST_STATE_KEY)) {
        listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        savedInstanceState.putParcelable(LIST_STATE_KEY, null);
      }
    }
    genericError = view.findViewById(R.id.generic_error);
    retryButton = genericError.findViewById(R.id.retry);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    list = (RecyclerView) view.findViewById(R.id.fragment_cards_list);
    layoutManager = new LinearLayoutManager(getContext());
    list.setLayoutManager(layoutManager);
    loginPrompt = PublishRelay.create();
    helper = RecyclerViewPositionHelper.createHelper(list);
    // Pull-to-refresh
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
    coordinatorLayout = view.findViewById(R.id.coordinator_layout);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);

    SpannableFactory spannableFactory = new SpannableFactory();
    adapter = new PostAdapter(Collections.emptyList(),
        new CardViewHolderFactory(postTouchEventPublishSubject, dateCalculator, spannableFactory,
            new MinimalCardViewFactory(dateCalculator, spannableFactory,
                postTouchEventPublishSubject)), new ProgressCard());
    list.setAdapter(adapter);

    final StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(
        ((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class);
    StoreCredentialsProviderImpl storeCredentialsProvider =
        new StoreCredentialsProviderImpl(storeAccessor);

    Timeline timeline =
        new Timeline(timelineService, installManager, new DownloadFactory(), timelineAnalytics,
            timelinePostsRepository, postReadReporter);

    TimelineNavigator timelineNavigation = new TimelineNavigator(getFragmentNavigator(),
        getContext().getString(R.string.timeline_title_likes), tabNavigator);

    StoreUtilsProxy storeUtilsProxy =
        new StoreUtilsProxy(((V8Engine) getContext().getApplicationContext()).getAccountManager(),
            baseBodyInterceptorV7, storeCredentialsProvider, storeAccessor, defaultClient,
            defaultConverter, tokenInvalidator, sharedPreferences);

    attachPresenter(
        new TimelinePresenter(this, timeline, CrashReport.getInstance(), timelineNavigation,
            new PermissionManager(), (PermissionService) getContext(), installManager,
            RepositoryFactory.getStoreRepository(getContext()), storeUtilsProxy,
            storeCredentialsProvider, accountManager, timelineAnalytics, userId, storeId,
            storeContext, getContext().getResources(), getFragmentNavigator()), savedInstanceState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    progressBar = null;
    listState = list.getLayoutManager()
        .onSaveInstanceState();
    adapter.clearPosts();
    adapter = null;
    genericError = null;
    list = null;
    swipeRefreshLayout = null;
    coordinatorLayout = null;
    helper = null;
    retryButton = null;
    floatingActionButton = null;
    bottomAlreadyReached = false;
    timelinePostsRepository.clearLoading();
  }

  @Override public void showCards(List<Post> cards) {
    adapter.updatePosts(cards);
    if (listState != null) {
      list.getLayoutManager()
          .onRestoreInstanceState(listState);
      listState = null;
    }
  }

  @Override public void showGeneralProgressIndicator() {
    progressIndicator = true;
    list.setVisibility(View.GONE);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideGeneralProgressIndicator() {
    progressIndicator = false;
    hideProgressIndicator();
  }

  @Override public void hideRefresh() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void showMoreCards(List<Post> cards) {
    adapter.addPosts(cards);
  }

  @Override public void showGenericViewError() {
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
        .filter(event -> !bottomAlreadyReached
            && helper.getItemCount() > visibleThreshold
            && helper != null
            && event.view()
            .isAttachedToWindow()
            && (helper.getItemCount() - event.view()
            .getChildCount()) <= ((helper.findFirstVisibleItemPosition() == -1 ? 0
            : helper.findFirstVisibleItemPosition()) + visibleThreshold))
        .map(event -> null)
        .doOnNext(__ -> bottomAlreadyReached = true)
        .cast(Void.class);
  }

  @Override public Observable<CardTouchEvent> postClicked() {
    return postTouchEventPublishSubject;
  }

  @Override public Observable<ShareEvent> shareConfirmation() {
    return sharePostPublishSubject;
  }

  @Override public Observable<PostComment> commentPosted() {
    return commentPostResponseSubject;
  }

  @Override public Observable<Void> retry() {
    return RxView.clicks(retryButton);
  }

  @Override public void showLoadMoreProgressIndicator() {
    Logger.d(this.getClass()
        .getName(), "show indicator called");
    adapter.addLoadMoreProgress();
  }

  @Override public void hideLoadMoreProgressIndicator() {
    Logger.d(this.getClass()
        .getName(), "hide indicator called");
    bottomAlreadyReached = false;
    if (adapter != null) {
      adapter.removeLoadMoreProgress();
    }
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

  @Override public void updatePost(int cardPosition) {
    adapter.updatePost(cardPosition);
  }

  @Override public void swapPost(Post post, int postPosition) {
    adapter.swapPost(post, postPosition);
  }

  @Override public void showStoreSubscribedMessage(String storeName) {
    final String msg = AptoideUtils.StringU.getFormattedString(R.string.store_followed,
        getContext().getResources(), storeName);
    Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public void showStoreUnsubscribedMessage(String storeName) {
    final String msg = AptoideUtils.StringU.getFormattedString(R.string.unfollowing_store_message,
        getContext().getResources(), storeName);
    Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public void showSharePreview(Post post, Account account) {
    shareDialog = shareDialogFactory.createDialogFor(post, account);
    shareDialog.setup(post);

    handleSharePreviewAnswer();
  }

  @Override public void showSharePreview(Post originalPost, Post card, Account account) {
    shareDialog = shareDialogFactory.createDialogFor(originalPost, account);
    shareDialog.setupMinimalPost(originalPost, card);

    handleSharePreviewAnswer();
  }

  @Override public void showShareSuccessMessage() {
    ShowMessage.asSnack(getView(), R.string.social_timeline_share_dialog_title);
  }

  @Override public void showCommentDialog(SocialCardTouchEvent touchEvent) {
    FragmentManager fm = getFragmentManager();
    CommentDialogFragment commentDialogFragment =
        CommentDialogFragment.newInstanceTimelineArticleComment(touchEvent.getCard()
            .getCardId());
    commentDialogFragment.setCommentBeforeSubmissionCallbackContract((inputText) -> {
      PostComment postComment =
          new PostComment(touchEvent.getCard(), inputText, touchEvent.getPostPosition());
      commentPostResponseSubject.onNext(postComment);
    });
    commentDialogFragment.show(fm, "fragment_comment_dialog");
  }

  @Override public void showGenericError() {
    Snackbar.make(getView(), R.string.all_message_general_error, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showLoginPromptWithAction() {
    Snackbar.make(getView(), R.string.you_need_to_be_logged_in, Snackbar.LENGTH_LONG)
        .setAction(R.string.login, view -> loginPrompt.call(view))
        .show();
  }

  @Override public Observable<Void> loginActionClick() {
    return loginPrompt.map(__ -> null);
  }

  @Override public void showCreateStoreMessage(SocialAction socialAction) {
    Snackbar.make(getView(),
        R.string.timeline_message_error_you_need_to_create_store_with_social_action,
        Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showPostProgressIndicator() {
    postIndicator = true;
    list.setVisibility(View.GONE);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hidePostProgressIndicator() {
    postIndicator = false;
    hideProgressIndicator();
  }

  @Override public Observable<Post> getVisibleItems() {
    return RxRecyclerView.scrollEvents(list)
        .subscribeOn(AndroidSchedulers.mainThread())
        .map(recyclerViewScrollEvent -> layoutManager.findFirstCompletelyVisibleItemPosition())
        .filter(position -> position != RecyclerView.NO_POSITION)
        .distinctUntilChanged()
        .map(visibleItem -> adapter.getPost(visibleItem));
  }

  private void handleSharePreviewAnswer() {
    shareDialog.cancels()
        .doOnNext(__ -> shareDialog.dismiss())
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe();

    shareDialog.shares()
        .doOnNext(event -> sharePostPublishSubject.onNext(event))
        .compose(bindUntilEvent(LifecycleEvent.PAUSE))
        .subscribe();
    shareDialog.show();
  }

  private void hideProgressIndicator() {
    if (!postIndicator && !progressIndicator) {
      list.setVisibility(View.VISIBLE);
      swipeRefreshLayout.setVisibility(View.VISIBLE);
      coordinatorLayout.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.GONE);
    }
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
