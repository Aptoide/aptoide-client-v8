package cm.aptoide.pt.v8engine.comments;

import cm.aptoide.pt.model.v7.Comment;
import java.util.ArrayList;
import java.util.List;

/**
 * created by SithEngineer
 */
public class CommentNode {
  private final List<CommentNode> childComments;
  private Comment comment;
  private int level = 1;

  public CommentNode() {
    this(null);
  }

  public CommentNode(Comment comment) {
    this.comment = comment;
    childComments = new ArrayList<>();
  }

  public Comment getComment() {
    return comment;
  }

  public void setComment(Comment comment) {
    this.comment = comment;
  }

  public void addChild(CommentNode comment) {
    childComments.add(comment);
  }

  public boolean hasChild() {
    return childComments.size() > 0;
  }

  public List<CommentNode> getChildComments() {
    return childComments;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }
}
