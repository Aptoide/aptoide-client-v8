package cm.aptoide.pt.v8engine.view.comments;

import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

public interface ItemCommentAdderView<Titem, Tadapter extends CommentsAdapter>
    extends CommentAdderView<Tadapter> {
  Displayable createReadMoreDisplayable(int itemPosition, Titem item);
}
