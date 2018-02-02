package cm.aptoide.pt.social.commentslist;

import android.view.View;
import cm.aptoide.pt.dataprovider.model.v7.Comment;

/**
 * Created by jdandrade on 12/12/2017.
 */

class LoadingCommentViewHolder extends PostCommentViewHolder {
  LoadingCommentViewHolder(View itemView) {
    super(itemView);
  }

  @Override public void setComment(Comment comment, int position) {
    //ignore
  }
}
