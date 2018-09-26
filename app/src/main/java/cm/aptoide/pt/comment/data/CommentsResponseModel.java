package cm.aptoide.pt.comment.data;

import java.util.Collections;
import java.util.List;

public class CommentsResponseModel {
  private final List<Comment> comments;
  private final int offset;
  private final boolean loading;

  public CommentsResponseModel(List<Comment> comments, int offset) {
    this.comments = comments;
    this.offset = offset;
    this.loading = false;
  }

  public CommentsResponseModel(boolean loading) {
    this.loading = loading;
    this.comments = Collections.emptyList();
    this.offset = -1;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public int getOffset() {
    return offset;
  }

  public boolean isLoading() {
    return loading;
  }
}
