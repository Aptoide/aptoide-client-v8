package cm.aptoide.pt.social.data;

/**
 * Created by franciscocalado on 1/18/18.
 */

public class PostCommentDataWrapper {
  private final String postId;
  private final String commentText;
  private final boolean error;

  public PostCommentDataWrapper(String postId, String commentText, boolean error) {
    this.postId = postId;
    this.commentText = commentText;
    this.error = error;
  }

  public String getCommentText() {
    return commentText;
  }

  public String getPostId() {
    return postId;
  }

  public boolean hasError() {
    return error;
  }
}
