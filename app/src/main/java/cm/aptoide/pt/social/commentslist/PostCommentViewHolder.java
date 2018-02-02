package cm.aptoide.pt.social.commentslist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.dataprovider.model.v7.Comment;

/**
 * Created by jdandrade on 02/10/2017.
 */

abstract class PostCommentViewHolder extends RecyclerView.ViewHolder {
  public PostCommentViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setComment(Comment comment, int position);
}
