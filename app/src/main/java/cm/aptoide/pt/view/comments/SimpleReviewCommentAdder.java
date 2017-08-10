package cm.aptoide.pt.view.comments;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.comments.CommentAdder;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.ArrayList;
import java.util.List;

public class SimpleReviewCommentAdder extends CommentAdder {

  private final CommentAdderView commentAdderView;

  public SimpleReviewCommentAdder(int reviewIndex, CommentAdderView commentAdderView) {
    super(reviewIndex);
    this.commentAdderView = commentAdderView;
  }

  @Override public void addComment(List<Comment> comments) {
    int nextReviewPosition = commentAdderView.getAdapter()
        .getItemPosition(itemIndex + 1);
    nextReviewPosition = nextReviewPosition == -1 ? commentAdderView.getAdapter()
        .getItemCount() : nextReviewPosition;
    commentAdderView.getAdapter()
        .removeDisplayable(nextReviewPosition - 1);
    List<Displayable> displayableList = new ArrayList<>();
    commentAdderView.createDisplayableComments(comments, displayableList);
    commentAdderView.getAdapter()
        .addDisplayables(nextReviewPosition - 1, displayableList);
  }
}
