package cm.aptoide.pt.comment.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.comment.data.Comment;

public abstract class AbstractCommentViewHolder extends RecyclerView.ViewHolder {
  public AbstractCommentViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setComment(Comment comment);
}
