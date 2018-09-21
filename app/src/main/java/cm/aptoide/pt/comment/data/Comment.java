package cm.aptoide.pt.comment.data;

public class Comment {
  private final long id;
  private final String message;
  private final User user;
  private final int replies;

  public Comment(long id, String message, User user, int replies) {
    this.id = id;
    this.message = message;
    this.user = user;
    this.replies = replies;
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
}
