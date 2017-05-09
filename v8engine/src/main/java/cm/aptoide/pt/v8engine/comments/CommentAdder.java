package cm.aptoide.pt.v8engine.comments;

import cm.aptoide.pt.model.v7.Comment;
import java.util.List;

public abstract class CommentAdder {

  protected final int itemIndex;

  public CommentAdder(int itemIndex) {
    this.itemIndex = itemIndex;
  }

  public abstract void addComment(List<Comment> comments);

  public void collapseComments() {
    // non-mandatory method
  }
}
