package cm.aptoide.pt.view.comments;

import cm.aptoide.pt.view.recycler.displayable.Displayable;

public interface ItemCommentAdderView<Titem, Tadapter extends CommentsAdapter>
    extends CommentAdderView<Tadapter> {
  Displayable createReadMoreDisplayable(int itemPosition, Titem item);
}
