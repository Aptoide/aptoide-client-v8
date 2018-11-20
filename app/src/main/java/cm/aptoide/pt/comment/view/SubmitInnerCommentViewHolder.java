package cm.aptoide.pt.comment.view;

import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comment.SubmitCommentViewHolder;
import cm.aptoide.pt.comment.data.Comment;
import rx.subjects.PublishSubject;

public class SubmitInnerCommentViewHolder extends SubmitCommentViewHolder {

  public SubmitInnerCommentViewHolder(View view, PublishSubject<Comment> postComment) {
    super(view, postComment);
    view.findViewById(R.id.inner_comment_separator)
        .setVisibility(View.VISIBLE);
  }
}
