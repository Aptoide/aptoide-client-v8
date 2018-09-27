package cm.aptoide.pt.commentdetail;

import cm.aptoide.pt.comment.data.Comment;
import java.util.List;

public class CommentDetailViewModel {
  private final String commentUserName;
  private final String commentAvatar;
  private final String commentMessage;
  private final String repliesNumber;
  private final boolean hasReplies;
  private final List<Comment> replies;

  public CommentDetailViewModel(String commentUserName, String commentAvatar, String commentMessage,
      String repliesNumber, List<Comment> replies) {
    this.commentUserName = commentUserName;
    this.commentAvatar = commentAvatar;
    this.commentMessage = commentMessage;
    this.repliesNumber = repliesNumber;
    this.replies = replies;
    this.hasReplies = replies.size() > 0;
  }

  public String getCommentUserName() {
    return commentUserName;
  }

  public String getCommentAvatar() {
    return commentAvatar;
  }

  public String getCommentMessage() {
    return commentMessage;
  }

  public String getRepliesNumber() {
    return repliesNumber;
  }

  public boolean isHasReplies() {
    return hasReplies;
  }

  public List<Comment> getReplies() {
    return replies;
  }
}
