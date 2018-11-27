package cm.aptoide.pt.comment;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.CommentsResponseModel;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class CommentsListManager {
  private final AptoideAccountManager accountManager;
  private final Comments comments;
  private final long storeId;
  private int offset;
  private List<LocalCommentWrapper> localComments;

  public CommentsListManager(AptoideAccountManager accountManager, long storeId, Comments comments,
      int offset) {
    this.accountManager = accountManager;
    this.storeId = storeId;
    this.comments = comments;
    this.offset = offset;
    this.localComments = new ArrayList<>();
  }

  public Single<CommentsListViewModel> loadComments() {
    return accountManager.accountStatus()
        .first()
        .flatMapSingle(account -> comments.loadComments(storeId)
            .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset())
            .flatMap(commentsResponseModel -> {
              if (hasPendingPosts()) {
                return comments.writeComment(storeId, localComments.get(0)
                    .getComment()
                    .getMessage())
                    .doOnSuccess(__ -> localComments.get(0)
                        .setSent())
                    .map(__ -> commentsResponseModel);
              } else {
                return Single.just(commentsResponseModel);
              }
            })
            .flatMap(this::map)
            .map(comments -> handleLocalComments(comments, account.isLoggedIn())))
        .toSingle();
  }

  public Single<CommentsListViewModel> loadFreshComments() {
    return accountManager.accountStatus()
        .first()
        .flatMapSingle(account -> comments.loadFreshComments(storeId)
            .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset())
            .flatMap(this::map)
            .map(comments -> handleLocalComments(comments, account.isLoggedIn())))
        .toSingle();
  }

  public Single<CommentsListViewModel> loadMoreComments() {
    return comments.loadNextComments(storeId, offset)
        .doOnSuccess(commentsResponseModel -> offset = commentsResponseModel.getOffset())
        .flatMap(this::map);
  }

  private Single<CommentsListViewModel> map(CommentsResponseModel commentsResponseModel) {
    return accountManager.accountStatus()
        .first()
        .flatMap(account -> Observable.just(
            new CommentsListViewModel(account.getAvatar(), commentsResponseModel.getComments(),
                commentsResponseModel.isLoading())))
        .toSingle();
  }

  public Observable<Long> postComment(Comment comment) {
    return postComment(comment, storeId);
  }

  public Observable<Long> postComment(Comment comment, long storeId) {
    return accountManager.accountStatus()
        .first()
        .filter(account -> account.isLoggedIn())
        .flatMapSingle(account -> comments.writeComment(storeId, comment.getMessage())
            .map(response -> response.getData()
                .getId())
            .doOnSuccess(id -> saveLocalComment(comment, account, id, true)));
  }

  public Completable replyComment(Comment comment, long parentId) {
    return comments.writeComment(storeId, comment.getMessage(), parentId);
  }

  public long getStoreId() {
    return storeId;
  }

  private void saveLocalComment(Comment comment, Account account, long id, boolean isSent) {
    localComments.add(new LocalCommentWrapper(comment, account, id, isSent));
  }

  private CommentsListViewModel handleLocalComments(CommentsListViewModel comments,
      boolean isLoggedIn) {
    if (!isLoggedIn) {
      handleLoggedOutLocalComments();
    } else {
      handleLoggedInLocalComments(comments);
    }
    return comments;
  }

  private void handleLoggedOutLocalComments() {
    for (LocalCommentWrapper wrapper : localComments) {
      if (wrapper.getStatus() == LocalCommentWrapper.Status.pending) {
        localComments.remove(wrapper);
      }
    }
  }

  private void handleLoggedInLocalComments(CommentsListViewModel comments) {
    List<Comment> result = new ArrayList<>();
    for (Comment comment : comments.getComments()) {
      for (LocalCommentWrapper wrapper : localComments) {
        if (wrapper.getComment()
            .getId() == comment.getId()) {
          localComments.remove(wrapper);
        } else {
          if (wrapper.getStatus()
              .equals(LocalCommentWrapper.Status.sent) && !result.contains(wrapper.getComment())) {
            result.add(0, wrapper.getComment());
          }
        }
      }
    }
    comments.getComments()
        .addAll(0, result);
  }

  private boolean hasPendingPosts() {
    if (localComments.size() > 0) {
      return localComments.get(0)
          .getStatus()
          .equals(LocalCommentWrapper.Status.pending);
    } else {
      return false;
    }
  }
}
