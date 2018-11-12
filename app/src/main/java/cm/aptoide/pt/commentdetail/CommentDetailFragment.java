package cm.aptoide.pt.commentdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comment.CommentsAdapter;
import cm.aptoide.pt.comment.SubmitInnerComment;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.ArrayList;
import java.util.Date;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class CommentDetailFragment extends NavigationTrackFragment implements CommentDetailView {

  @Inject CommentDetailPresenter presenter;
  @Inject AptoideUtils.DateTimeU dateUtils;
  private TextView userName;
  private ImageView userAvatar;
  private TextView date;
  private TextView message;
  private TextView repliesNumber;
  private RecyclerView repliesList;
  private CommentsAdapter repliesAdapter;
  private LinearLayoutManager layoutManager;
  private PublishSubject<Comment> commentClickEvent;
  private PublishSubject<Comment> postCommentClickEvent;
  private PublishSubject<Long> userClickEvent;
  private View loading;
  private View genericErrorView;
  private View commentView;
  private Toolbar toolbar;
  private ActionBar actionBar;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    commentClickEvent = PublishSubject.create();
    postCommentClickEvent = PublishSubject.create();
    userClickEvent = PublishSubject.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    userName = view.findViewById(R.id.user_name);
    userAvatar = view.findViewById(R.id.user_icon);
    date = view.findViewById(R.id.date);
    message = view.findViewById(R.id.comment);
    repliesNumber = view.findViewById(R.id.replies_number);
    repliesList = view.findViewById(R.id.replies_list);
    loading = view.findViewById(R.id.progress_bar);
    genericErrorView = view.findViewById(R.id.generic_error);
    commentView = view.findViewById(R.id.comment_view);
    toolbar = view.findViewById(R.id.action_bar)
        .findViewById(R.id.toolbar);

    layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
    repliesList.setLayoutManager(layoutManager);
    repliesAdapter = new CommentsAdapter(new ArrayList<>(), dateUtils, commentClickEvent,
        R.layout.comment_inner_layout, postCommentClickEvent, userClickEvent);
    repliesList.setAdapter(repliesAdapter);
    repliesList.addItemDecoration(new DividerItemDecoration(this.getContext(), 0));

    setHasOptionsMenu(true);
    setupToolbar();
    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onDestroyView() {
    userName = null;
    userAvatar = null;
    date = null;
    message = null;
    repliesNumber = null;
    repliesList = null;
    repliesAdapter = null;
    loading = null;
    genericErrorView = null;
    layoutManager = null;
    super.onDestroyView();
  }

  @Override public void onDestroy() {
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

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_comment_detail, container, false);
  }

  @Override public void showCommentModel(CommentDetailViewModel viewModel) {
    userName.setText(viewModel.getCommentUserName());
    ImageLoader.with(this.getContext())
        .loadWithCircleTransformAndPlaceHolderAvatarSize(viewModel.getCommentAvatar(), userAvatar,
            R.drawable.layer_1);
    date.setText(dateUtils.getTimeDiffString(this.getContext(), viewModel.getDate()
        .getTime(), this.getContext()
        .getResources()));
    message.setText(viewModel.getCommentMessage());
    if (viewModel.hasReplies()) {
      repliesNumber.setText(String.format(this.getContext()
          .getString(R.string.comment_replies_number_short), viewModel.getRepliesNumber()));
    }
    repliesAdapter.setComments(viewModel.getReplies(),
        new SubmitInnerComment(viewModel.getUserAvatar()));
  }

  @Override public void showLoading() {
    commentView.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    loading.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    commentView.setVisibility(View.VISIBLE);
    loading.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
  }

  @Override public void addLocalComment(Comment comment, Account account) {
    repliesAdapter.addSingleComment(new Comment(comment.getId(), comment.getMessage(),
        new User(-1, account.getAvatar(), account.getNickname()), 0, new Date()));
  }

  @Override public Observable<Comment> commentClicked() {
    return postCommentClickEvent;
  }

  public void setupToolbar() {

    toolbar.setTitle("Comment replies");

    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);
    actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setTitle(toolbar.getTitle());
    }
  }
}
