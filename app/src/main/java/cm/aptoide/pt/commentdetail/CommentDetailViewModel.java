package cm.aptoide.pt.commentdetail;

import cm.aptoide.pt.comment.data.Comment;
import java.util.Date;
import java.util.List;

public class CommentDetailViewModel {
  private final String commentUserName;
  private final long userId;
  private final String commentAvatar;
  private final String commentMessage;
  private final String userAvatar;
  private final int repliesNumber;
  private final boolean hasReplies;
  private final List<Comment> replies;
  private final Date date;

  public CommentDetailViewModel(String commentUserName, long userId, String commentAvatar,
      String commentMessage,
      String userAvatar, int repliesNumber, Date date, List<Comment> replies) {
    this.commentUserName = commentUserName;
    this.userId = userId;
    this.commentAvatar = commentAvatar;
    this.commentMessage = commentMessage;
    this.userAvatar = userAvatar;
    this.repliesNumber = repliesNumber;
    this.replies = replies;
    this.hasReplies = replies.size() > 0;
    this.date = date;
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

  public int getRepliesNumber() {
    return repliesNumber;
  }

  public boolean hasReplies() {
    return hasReplies;
  }

  public List<Comment> getReplies() {
    return replies;
  }

  public Date getDate() {
    return date;
  }

  public String getUserAvatar() {
    return userAvatar;
  }

  public long getUserId() {
    return userId;
  }
}
