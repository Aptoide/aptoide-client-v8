package cm.aptoide.pt.comment;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.view.AbstractCommentViewHolder;
import cm.aptoide.pt.networking.image.ImageLoader;
import java.util.Date;
import rx.subjects.PublishSubject;

public class SubmitCommentViewHolder extends AbstractCommentViewHolder {
  private final ImageView userAvatar;
  private final EditText commentArea;
  private final PublishSubject<Comment> postComment;

  public SubmitCommentViewHolder(View view, PublishSubject<Comment> postComment) {
    super(view);
    userAvatar = view.findViewById(R.id.user_icon);
    commentArea = view.findViewById(R.id.add_comment);
    this.postComment = postComment;
  }

  @Override public void setComment(Comment comment) {
    ImageLoader.with(itemView.getContext())
        .loadWithCircleTransformAndPlaceHolderAvatarSize(comment.getUser()
            .getAvatar(), userAvatar, R.drawable.layer_1);

    userAvatar.setOnClickListener(view -> postComment.onNext(new Comment(-1, commentArea.getText()
        .toString(), comment.getUser(), -1, new Date())));
  }
}
