package cm.aptoide.pt.comment.mock;

import android.support.annotation.NonNull;
import cm.aptoide.pt.comment.CommentDetailResponseModel;
import cm.aptoide.pt.comment.CommentsDataSource;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.CommentsResponseModel;
import cm.aptoide.pt.comment.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import rx.Completable;
import rx.Single;

public class FakeCommentsDataSource implements CommentsDataSource {
  @Override
  public Single<CommentsResponseModel> loadComments(long storeId, boolean invalidateHttpCache) {
    return getFakeCommentsResponse();
  }

  @Override public Single<CommentsResponseModel> loadFreshComments(long storeId) {
    return getFakeCommentsResponse();
  }

  @Override public Single<CommentsResponseModel> loadNextComments(long storeId, int offset) {
    return getFakeCommentsResponse();
  }

  @Override public Single<CommentDetailResponseModel> loadComment(long commentId) {
    return Single.just(new CommentDetailResponseModel(getFakeComment(-1), getFakeComments()));
  }

  @Override public Completable writeComment(long storeId, String message) {
    return Completable.complete();
  }

  private Single<CommentsResponseModel> getFakeCommentsResponse() {
    List<Comment> comments = getFakeComments();
    return Single.just(new CommentsResponseModel(comments, 0));
  }

  @NonNull private List<Comment> getFakeComments() {
    List<Comment> comments = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      comments.add(getFakeComment(i));
    }
    return comments;
  }

  @NonNull private Comment getFakeComment(int i) {
    return new Comment(i, "comment " + i, new User(i, null, "user " + i), i, new Date());
  }
}
