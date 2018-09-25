package cm.aptoide.pt.comment.data;

import java.util.List;

public class CommentsResponseModel {
  private final List<Comment> comments;
  private final int offset;

  public CommentsResponseModel(List<Comment> comments, int offset) {
    this.comments = comments;
    this.offset = offset;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public int getOffset() {
    return offset;
  }
}
