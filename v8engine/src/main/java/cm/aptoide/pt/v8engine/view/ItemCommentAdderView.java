package cm.aptoide.pt.v8engine.view;

import cm.aptoide.pt.v8engine.view.CommentAdderView;
import cm.aptoide.pt.v8engine.view.adapters.CommentsAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

public interface ItemCommentAdderView<Titem, Tadapter extends CommentsAdapter>
    extends CommentAdderView<Tadapter> {
  Displayable createReadMoreDisplayable(int itemPosition, Titem item);
}
