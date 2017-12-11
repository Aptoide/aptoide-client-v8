package cm.aptoide.pt.social.commentslist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;
import cm.aptoide.pt.view.recycler.RecyclerViewPositionHelper;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 28/09/2017.
 */

public class PostCommentsFragment extends BaseToolbarFragment implements PostCommentsView {
  public static final String POST_ID_KEY = "POST_ID_KEY";
  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private final int visibleThreshold = 5;
  /**
   * Flag to control whether or not bottomReached should emit to the presenter.
   */
  private boolean bottomAlreadyReached;
  private RecyclerView list;
  private PostCommentsAdapter adapter;
  private FloatingActionButton floatingActionButton;
  private RecyclerViewPositionHelper helper;

  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;

  private PublishSubject<String> replyEventPublishSubject;
  private SwipeRefreshLayout swipeRefreshLayout;

  public static Fragment newInstance(String postId) {
    Fragment fragment = new PostCommentsFragment();
    final Bundle args = new Bundle();
    args.putString(POST_ID_KEY, postId);
    fragment.setArguments(args);
    return fragment;
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
    adapter = new PostCommentsAdapter(new ArrayList<>(), replyEventPublishSubject);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    list = (RecyclerView) view.findViewById(R.id.recycler_view);
    list.setAdapter(adapter);
    list.addItemDecoration(new ItemDividerDecoration(this));
    list.setLayoutManager(new LinearLayoutManager(getContext()));
    helper = RecyclerViewPositionHelper.createHelper(list);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabAdd);
    setHasOptionsMenu(true);
    attachPresenter(new PostCommentsPresenter(this, new Comments(
        new PostCommentsRepository(bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            sharedPreferences)),
        getArguments().containsKey(POST_ID_KEY) ? getArguments().getString(POST_ID_KEY) : null));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(R.string.comments_title_comments);
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

  @Override public Observable<Void> refreshes() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public void showLoadMoreProgressIndicator() {
    Logger.d(this.getClass()
        .getName(), "show indicator called");
  }

  @Override public void hideLoadMoreProgressIndicator() {
    Logger.d(this.getClass()
        .getName(), "hide indicator called");
    bottomAlreadyReached = false;
  }

  @Override public void showComments(List<Comment> comments) {
    adapter.updateComments(comments);
  }

  @Override public void hideRefresh() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }
}
