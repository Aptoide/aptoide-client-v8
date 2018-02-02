package cm.aptoide.pt.social.commentslist;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 28/09/2017.
 */

public class PostCommentsFragment extends BaseToolbarFragment implements PostCommentsView {
  public static final String POST_ID_KEY = "POST_ID_KEY";
  public static final String SHOW_COMMENT_DIALOG = "SHOW_COMMENT_DIALOG";
  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private final int visibleThreshold = 5;
  @Inject AptoideAccountManager accountManager;
  private RecyclerView list;
  private PostCommentsAdapter adapter;
  private FloatingActionButton floatingActionButton;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;
  private PublishSubject<Long> replyEventPublishSubject;
  private SwipeRefreshLayout swipeRefreshLayout;
  private LinearLayoutManager layoutManager;
  private ProgressBar progressBar;

  private View genericError;
  private TabNavigator tabNavigator;

  public static PostCommentsFragment newInstance(String postId) {
    PostCommentsFragment fragment = new PostCommentsFragment();
    final Bundle args = new Bundle();
    args.putString(POST_ID_KEY, postId);
    fragment.setArguments(args);
    return fragment;
  }

  public static Fragment newInstanceWithCommentDialog(String postId) {
    Fragment fragment = new PostCommentsFragment();
    final Bundle args = new Bundle();
    args.putString(POST_ID_KEY, postId);
    args.putBoolean(SHOW_COMMENT_DIALOG, true);
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

  @Override public int getContentViewId() {
    return R.layout.post_comments_fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    sharedPreferences =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences();
    replyEventPublishSubject = PublishSubject.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    list = (RecyclerView) view.findViewById(R.id.recycler_view);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    genericError = view.findViewById(R.id.generic_error);
    adapter =
        new PostCommentsAdapter(new ArrayList<>(), new ProgressComment(), replyEventPublishSubject);
    list.setAdapter(adapter);
    list.addItemDecoration(new ItemDividerDecoration(getContext().getResources()
        .getDisplayMetrics()));
    layoutManager = new LinearLayoutManager(getContext());
    list.setLayoutManager(layoutManager);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabAdd);
    floatingActionButton.setVisibility(View.VISIBLE);
    setHasOptionsMenu(true);

    Boolean shouldShowCommentDialog;
    if (savedInstanceState != null && savedInstanceState.containsKey(SHOW_COMMENT_DIALOG)) {
      shouldShowCommentDialog = savedInstanceState.getBoolean(SHOW_COMMENT_DIALOG);
    } else {
      shouldShowCommentDialog = getArguments().getBoolean(SHOW_COMMENT_DIALOG);
    }

    attachPresenter(new PostCommentsPresenter(this, new Comments(new PostCommentsRepository(
        new PostCommentsService(10, 0, Integer.MAX_VALUE, bodyInterceptor, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences), new CommentsSorter(),
        new ArrayList<>()), new CommentMapper()),
        new CommentsNavigator(getActivity().getSupportFragmentManager(), PublishSubject.create(),
            PublishSubject.create(), tabNavigator), AndroidSchedulers.mainThread(),
        CrashReport.getInstance(), getArguments().getString(POST_ID_KEY), accountManager,
        shouldShowCommentDialog));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getFragmentComponent(savedInstanceState).inject(this);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public Observable<Object> reachesBottom() {
    return RxRecyclerView.scrollEvents(list)
        .distinctUntilChanged()
        .filter(scroll -> isEndReached())
        .cast(Object.class);
  }

  @Override public Observable<Void> refreshes() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public Observable<Long> repliesComment() {
    return replyEventPublishSubject;
  }

  @Override public Observable<Void> repliesPost() {
    return RxView.clicks(floatingActionButton);
  }

  @Override public void showLoadMoreProgressIndicator() {
    adapter.addLoadMoreProgress();
  }

  @Override public void hideLoadMoreProgressIndicator() {
    adapter.removeLoadMoreProgress();
  }

  @Override public void showComments(List<Comment> comments) {
    adapter.updateComments(comments);
  }

  @Override public void hideRefresh() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void showMoreComments(List<Comment> comments) {
    adapter.addComments(comments);
  }

  @Override public void showLoading() {
    list.setVisibility(View.GONE);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    list.setVisibility(View.VISIBLE);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
  }

  @Override public void showNewComment(Comment comment) {
    adapter.addNewComment(comment);
  }

  @Override public void showCommentSubmittedMessage() {
    Snackbar.make(getView(), R.string.comment_submitted, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(SHOW_COMMENT_DIALOG, false);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    list = null;
    adapter = null;
    progressBar = null;
    genericError = null;
    layoutManager = null;
    swipeRefreshLayout = null;
    floatingActionButton = null;
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(R.string.comments_title_comments);
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= visibleThreshold;
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }
}
