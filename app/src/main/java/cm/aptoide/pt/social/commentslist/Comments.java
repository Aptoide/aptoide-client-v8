package cm.aptoide.pt.social.commentslist;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 19/12/2017.
 */

class Comments {

  private final PostCommentsRepository postCommentsRepository;
  private final CommentMapper mapper;

  public Comments(PostCommentsRepository postCommentsRepository, CommentMapper mapper) {
    this.postCommentsRepository = postCommentsRepository;
    this.mapper = mapper;
  }

  Single<List<Comment>> getComments(String postId) {
    return postCommentsRepository.getComments(postId);
  }

  Single<List<Comment>> getFreshComments(String postId) {
    return postCommentsRepository.getFreshComments(postId);
  }

  Single<List<Comment>> getNextComments(String postId) {
    return postCommentsRepository.getNextComments(postId);
  }

  boolean hasMore() {
    return postCommentsRepository.hasMore();
  }

  Comment mapToComment(CommentDataWrapper data, Account account) {
    return mapper.mapToComment(data, account);
  }
}
