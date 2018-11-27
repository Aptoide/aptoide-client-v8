package cm.aptoide.pt.comment;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;
import java.util.Date;

public class LocalCommentWrapper {

  private Comment comment;
  private Status status;

  public LocalCommentWrapper(Comment comment, Account account, long id, boolean isSent) {
    this.comment = new Comment(id, comment.getMessage(), new User(comment.getUser()
        .getId(), account.getAvatar(), account.getNickname()), 0, new Date());
    this.status = isSent ? Status.sent : Status.pending;
  }

  public void setSent() {
    status = Status.sent;
  }

  public Comment getComment() {
    return comment;
  }

  public Status getStatus() {
    return status;
  }

  public void setSentStatus() {
    this.status = Status.sent;
  }

  public enum Status {
    pending, sent
  }
}
