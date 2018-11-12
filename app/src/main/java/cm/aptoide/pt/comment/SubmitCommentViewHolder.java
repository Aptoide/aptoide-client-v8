package cm.aptoide.pt.comment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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
  private final View postView;
  private final Button postButton;
  private final PublishSubject<Comment> postComment;

  public SubmitCommentViewHolder(View view, PublishSubject<Comment> postComment,
      boolean isInnerComment) {
    super(view);
    userAvatar = view.findViewById(R.id.user_icon);
    commentArea = view.findViewById(R.id.add_comment);
    postView = view.findViewById(R.id.post_layout);
    postButton = view.findViewById(R.id.post_button);
    if (isInnerComment) {
      view.findViewById(R.id.inner_comment_separator)
          .setVisibility(View.VISIBLE);
    }
    this.postComment = postComment;
  }

  @Override public void setComment(Comment comment) {
    ImageLoader.with(itemView.getContext())
        .loadWithCircleTransformAndPlaceHolderAvatarSize(comment.getUser()
            .getAvatar(), userAvatar, R.drawable.layer_1);

    commentArea.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        postView.setVisibility(View.VISIBLE);
      } else {
        postView.setVisibility(View.GONE);
      }
    });
    commentArea.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        if (s.toString()
            .trim()
            .length() > 2) {
          postButton.setClickable(true);
          postButton.setEnabled(true);
        } else {
          postButton.setClickable(false);
          postButton.setEnabled(false);
        }
      }
    });

    postButton.setOnClickListener(view -> {
      String message = commentArea.getText()
          .toString();
      commentArea.getText()
          .clear();
      commentArea.clearFocus();
      postComment.onNext(new Comment(-1, message, comment.getUser(), -1, new Date()));
    });
  }
}
