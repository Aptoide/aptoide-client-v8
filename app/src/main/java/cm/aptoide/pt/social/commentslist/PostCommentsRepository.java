package cm.aptoide.pt.social.commentslist;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 28/09/2017.
 */

class PostCommentsRepository {

  private final PostCommentsService postCommentsService;
  private final CommentsSorter commentsSorter;
  private List<Comment> cache;

  PostCommentsRepository(PostCommentsService postCommentsService, CommentsSorter commentsSorter,
      List<Comment> cache) {
    this.postCommentsService = postCommentsService;
    this.commentsSorter = commentsSorter;
    this.cache = cache;
  }

  Single<List<Comment>> getComments(String postId) {
    if (!cache.isEmpty()) {
      return Single.just(commentsSorter.sort(cache));
    }
    return postCommentsService.getComments(postId)
        .doOnSuccess(comments -> cache = comments)
        .map(comments -> commentsSorter.sort(cache));
  }

  Single<List<Comment>> getFreshComments(String postId) {
    return postCommentsService.getComments(postId)
        .doOnSuccess(comments -> cache = comments)
        .map(comments -> commentsSorter.sort(cache));
  }

  Single<List<Comment>> getNextComments(String postId) {
    return postCommentsService.getNextComments(postId)
        .doOnSuccess(comments -> cache.addAll(comments))
        .map(comments -> commentsSorter.sort(comments));
  }

  boolean hasMore() {
    return postCommentsService.hasMoreComments();
  }
}
