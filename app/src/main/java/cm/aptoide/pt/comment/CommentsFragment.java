package cm.aptoide.pt.comment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class CommentsFragment extends NavigationTrackFragment implements CommentsView {

  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private static final int VISIBLE_THRESHOLD = 2;
  @Inject CommentsPresenter commentsPresenter;
  @Inject AptoideUtils.DateTimeU dateUtils;
  private PublishSubject<Comment> postComment;
  private PublishSubject<Comment> commentClickEvent;
  private PublishSubject<Long> userClickEvent;
  private RecyclerView commentsList;
  private CommentsAdapter commentsAdapter;
  private SwipeRefreshLayout swipeRefreshLayout;
  private View loading;
  private View genericErrorView;
  private LinearLayoutManager layoutManager;
  private Toolbar toolbar;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_comments, container, false);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    commentClickEvent = PublishSubject.create();
    postComment = PublishSubject.create();
    userClickEvent = PublishSubject.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = view.findViewById(R.id.action_bar)
        .findViewById(R.id.toolbar);
    loading = view.findViewById(R.id.progress_bar);
    genericErrorView = view.findViewById(R.id.generic_error);
    swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
    swipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
        R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
    commentsList = view.findViewById(R.id.comments_list);
    layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
    commentsList.setLayoutManager(layoutManager);
    commentsAdapter =
        new CommentsAdapter(new ArrayList<>(), dateUtils, commentClickEvent, R.layout.comment_item,
            postComment, userClickEvent);
    commentsList.setAdapter(commentsAdapter);

    setHasOptionsMenu(true);
    handleStatusBar();
    setupToolbar();
    attachPresenter(commentsPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void showComments(CommentsListViewModel viewModel) {
    commentsAdapter.setComments(viewModel.getComments(), new SubmitComment(viewModel.getAvatar()));
  }

  @Override public void showLoading() {
    commentsList.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    loading.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    commentsList.setVisibility(View.VISIBLE);
    loading.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
  }

  @Override public void showGeneralError() {
    this.genericErrorView.setVisibility(View.VISIBLE);
    this.commentsList.setVisibility(View.GONE);
    this.loading.setVisibility(View.GONE);
    this.swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void hideRefreshLoading() {
    this.swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void addComments(List<Comment> comments) {
    commentsAdapter.addComments(comments);
  }

  @Override public void showLoadMore() {
    commentsAdapter.addLoadMore();
  }

  @Override public void hideLoadMore() {
    if (commentsAdapter != null) {
      commentsAdapter.removeLoadMore();
    }
  }

  @Override public void addLocalComment(Comment comment, Account account, long id) {
    commentsAdapter.addSingleComment(new Comment(id, comment.getMessage(), new User(
        comment.getUser()
            .getId(), account.getAvatar(), account.getNickname()), 0, new Date()));
  }

  @Override public Observable<Void> refreshes() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public Observable<Object> reachesBottom() {
    return RxRecyclerView.scrollEvents(commentsList)
        .map(scroll -> isEndReached())
        .distinctUntilChanged()
        .filter(isEnd -> isEnd)
        .cast(Object.class);
  }

  @Override public Observable<Comment> commentClick() {
    return commentClickEvent;
  }

  @Override public Observable<Comment> commentPost() {
    return postComment;
  }

  @Override public Observable<Long> userClickEvent() {
    return userClickEvent;
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= VISIBLE_THRESHOLD;
  }

  @Override public void onDestroyView() {
    commentsList = null;
    commentsAdapter = null;
    swipeRefreshLayout = null;
    genericErrorView = null;
    loading = null;
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    postComment = null;
    commentClickEvent = null;
    super.onDestroy();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        hideKeyboard();
        getActivity().onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void hideKeyboard() {
    super.hideKeyboard();
  }

  public void setupToolbar() {

    toolbar.setTitle(getResources().getString(R.string.comment_fragment_title));

    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
      actionBar.setTitle(toolbar.getTitle());
    }
  }

  private void handleStatusBar() {
    Window window = getActivity().getWindow();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      window.setStatusBarColor(getResources().getColor(R.color.grey_medium));
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      window.getDecorView()
          .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      window.setStatusBarColor(getResources().getColor(R.color.white));
    }
  }
}
