package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;

public class SubmitComment extends Comment {
  public SubmitComment(String avatar) {
    super(-1, "", new User(-1, avatar, ""), -1, null);
  }
}
