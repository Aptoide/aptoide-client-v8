package cm.aptoide.pt.social.commentslist;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 28/09/2017.
 */

class Comments {

  private final PostCommentsRepository postCommentsRepository;

  Comments(PostCommentsRepository postCommentsRepository) {
    this.postCommentsRepository = postCommentsRepository;
  }

  public Single<List<Comment>> getComments(String postId) {
    return postCommentsRepository.getComments(postId);
  }
}
