package cm.aptoide.pt.comment.data;

import java.util.Date;

public class Comment {
  private final long id;
  private final String message;
  private final User user;
  private final int replies;
  private final Date date;

  public Comment(long id, String message, User user, int replies, Date date) {
    this.id = id;
    this.message = message;
    this.user = user;
    this.replies = replies;
    this.date = date;
  }

  public Comment() {
    this.id = -1;
    this.message = "";
    this.user = null;
    this.replies = -1;
    this.date = null;
  }

  public long getId() {
    return id;
  }

  public String getMessage() {
    return message;
  }

  public User getUser() {
    return user;
  }

  public int getReplies() {
    return replies;
  }

  public Date getDate() {
    return date;
  }
}
