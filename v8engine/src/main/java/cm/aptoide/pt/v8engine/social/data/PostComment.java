package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 10/07/2017.
 */

public class PostComment {
  private final Post post;
  private final String commentText;
  private final int postPosition;

  public PostComment(Post post, String commentText, int postPosition) {
    this.post = post;
    this.commentText = commentText;
    this.postPosition = postPosition;
  }

  public Post getPost() {
    return post;
  }

  public String getCommentText() {
    return commentText;
  }

  public int getPostPosition() {
    return postPosition;
  }
}
