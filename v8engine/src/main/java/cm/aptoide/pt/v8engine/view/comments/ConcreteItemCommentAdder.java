package cm.aptoide.pt.v8engine.view.comments;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.v8engine.comments.CommentAdder;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.ArrayList;
import java.util.List;

public class ConcreteItemCommentAdder<T> extends CommentAdder {

  private final ItemCommentAdderView commentAdderView;
  private final T review;

  public ConcreteItemCommentAdder(int reviewIndex, ItemCommentAdderView commentAdderView,
      T review) {
    super(reviewIndex);
    this.commentAdderView = commentAdderView;
    this.review = review;
  }

  @Override public void addComment(List<Comment> comments) {
    List<Displayable> displayableList = new ArrayList<>();
    commentAdderView.createDisplayableComments(comments, displayableList);
    int reviewPosition = commentAdderView.getAdapter().getItemPosition(itemIndex);
    if (comments.size() > 2) {
      displayableList.add(commentAdderView.createReadMoreDisplayable(reviewPosition, review));
    }
    commentAdderView.getAdapter().addDisplayables(reviewPosition + 1, displayableList);
  }

  @Override public void collapseComments() {
    CommentsAdapter adapter = commentAdderView.getAdapter();
    int reviewIndex = adapter.getItemPosition(this.itemIndex);
    int nextReview = adapter.getItemPosition(this.itemIndex + 1);
    nextReview = nextReview == -1 ? commentAdderView.getAdapter().getItemCount() : nextReview;
    adapter.removeDisplayables(reviewIndex + 1, nextReview - 1);
    // the -1 because we don't want to remove the next review,only until
    // the comment before the review
  }
}
