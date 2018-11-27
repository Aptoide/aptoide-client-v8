package cm.aptoide.pt.comments.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comment.CommentMapper;
import cm.aptoide.pt.comment.CommentsAdapter;
import cm.aptoide.pt.comment.CommentsListManager;
import cm.aptoide.pt.comment.CommentsNavigator;
import cm.aptoide.pt.comment.SubmitComment;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;
import cm.aptoide.pt.store.view.StoreLatestCommentsDisplayable;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.widget.Widget;
import java.util.ArrayList;
import java.util.Date;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subjects.PublishSubject;

public class StoreLatestCommentsWidget extends Widget<StoreLatestCommentsDisplayable> {

  private RecyclerView recyclerView;
  private TextView emptyState;

  private long storeId;
  private String storeName;
  private AptoideAccountManager accountManager;
  private PublishSubject<Comment> postComment;
  private PublishSubject<Comment> commentClickEvent;
  private PublishSubject<Long> userClickEvent;
  private CommentMapper commentMapper;
  private CommentsAdapter commentsAdapter;
  private CommentsNavigator commentsNavigator;
  private CommentsListManager commentsListManager;

  public StoreLatestCommentsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    recyclerView = (RecyclerView) itemView.findViewById(R.id.comments);
    emptyState = itemView.findViewById(R.id.empty_state);
    postComment = PublishSubject.create();
    commentClickEvent = PublishSubject.create();
    userClickEvent = PublishSubject.create();
  }

  @Override public void bindView(StoreLatestCommentsDisplayable displayable) {
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();

    LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
    recyclerView.setLayoutManager(layoutManager);
    commentsAdapter =
        new CommentsAdapter(new ArrayList<>(), AptoideUtils.DateTimeU.getInstance(getContext()),
            commentClickEvent, R.layout.comment_item, postComment, userClickEvent);
    storeId = displayable.getStoreId();
    storeName = displayable.getStoreName();
    commentMapper = displayable.getCommentMapper();
    commentsListManager = displayable.getCommentsListManager();
    commentsNavigator = displayable.getCommentsNavigator();

    // TODO: 9/12/2016 create load and store methods when fragment is destroyed

    setAdapterWithComments(displayable);
    setAdapterWithoutComments(displayable);
    handleClicksOnPost();
    handleClickOnComment();
    handleClickOnUser();
  }

  private void handleClicksOnPost() {
    compositeSubscription.add(postComment.doOnNext(__ -> hideKeyboard())
        .flatMap(comment -> accountManager.accountStatus()
            .map(account -> {
              if (account.isLoggedIn()) {
                return account;
              } else {
                return null;
              }
            })
            .filter(account -> account != null)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(account -> {
              if (emptyState.getVisibility() == View.VISIBLE) {
                hideEmptyState();
              }
              addLocalComment(comment, account);
              return commentsListManager.postComment(comment, storeId);
            }))
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
  }

  private void handleClickOnComment() {
    compositeSubscription.add(commentClickEvent.doOnNext(
        comment -> commentsNavigator.navigateToCommentView(comment, comment.getId()))
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
  }

  private void handleClickOnUser() {
    compositeSubscription.add(userClickEvent.doOnNext(id -> commentsNavigator.navigateToStore(id))
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
  }

  private void hideKeyboard() {
    View view = getActivityNavigator().getActivity()
        .getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getActivityNavigator().getActivity()
          .getSystemService(Context.INPUT_METHOD_SERVICE);
      assert imm != null;
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  private void addLocalComment(Comment comment, Account account) {
    commentsAdapter.addSingleComment(new Comment(comment.getId(), comment.getMessage(), new User(
        (account.getStore()
            .getId()), account.getAvatar(), account.getNickname()), 0, new Date()));
  }

  private void setAdapterWithComments(StoreLatestCommentsDisplayable displayable) {
    compositeSubscription.add(accountManager.accountStatus()
        .observeOn(AndroidSchedulers.mainThread())
        .filter(account -> displayable.getComments()
            .size() > 0)
        .doOnNext(account -> {
          String avatar;
          if (account.isLoggedIn()) {
            avatar = account.getAvatar();
          } else {
            avatar = null;
          }
          recyclerView.setAdapter(commentsAdapter);
          commentsAdapter.setComments(commentMapper.map(displayable.getComments()),
              new SubmitComment(avatar));
        })
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
    recyclerView.setAdapter(commentsAdapter);
  }

  private void setAdapterWithoutComments(StoreLatestCommentsDisplayable displayable) {
    compositeSubscription.add(accountManager.accountStatus()
        .observeOn(AndroidSchedulers.mainThread())
        .filter(account -> displayable.getComments()
            .size() <= 0)
        .doOnNext(account -> {
          String avatar;
          if (account.isLoggedIn()) {
            avatar = account.getAvatar();
          } else {
            avatar = null;
          }
          showEmptyState();
          recyclerView.setAdapter(commentsAdapter);
          commentsAdapter.setComments(commentMapper.map(displayable.getComments()),
              new SubmitComment(avatar));
        })
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
    recyclerView.setAdapter(commentsAdapter);
  }

  private void showEmptyState() {
    int handsEmojiCode = 0x1F64C;
    String handsEmoji = new String(Character.toChars(handsEmojiCode));
    String text = getContext().getResources()
        .getString(R.string.comment_widget_no_comment);
    emptyState.setText(String.format(text, handsEmoji));
    emptyState.setVisibility(View.VISIBLE);
  }

  private void hideEmptyState() {
    emptyState.setVisibility(View.GONE);
  }
}
