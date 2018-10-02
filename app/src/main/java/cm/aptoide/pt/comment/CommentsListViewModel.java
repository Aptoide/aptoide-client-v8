package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import java.util.List;

public class CommentsListViewModel {

  private final String avatar;
  private final List<Comment> comments;
  private final boolean loading;

  public CommentsListViewModel(String avatar, List<Comment> comments, boolean loading) {
    this.avatar = avatar;
    this.comments = comments;
    this.loading = loading;
  }

  public String getAvatar() {
    return avatar;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public boolean isLoading() {
    return loading;
  }
}
