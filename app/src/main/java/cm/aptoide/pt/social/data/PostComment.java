package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 10/07/2017.
 */

public class PostComment {
  private final String cardId;
  private final String commentText;

  public PostComment(String cardId, String commentText) {
    this.cardId = cardId;
    this.commentText = commentText;
  }

  public String getCardId() {
    return cardId;
  }

  public String getCommentText() {
    return commentText;
  }
}
