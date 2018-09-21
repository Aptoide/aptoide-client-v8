package cm.aptoide.pt.comment.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;

public class CommentViewHolder extends RecyclerView.ViewHolder {

  private final View outerLayout;
  private final ImageView userAvatar;
  private final TextView userName;
  private final TextView date;
  private final TextView comment;
  private final TextView replies;
  private final AptoideUtils.DateTimeU dateUtils;

  public CommentViewHolder(View view, AptoideUtils.DateTimeU dateUtils) {
    super(view);
    userAvatar = view.findViewById(R.id.user_icon);
    outerLayout = view.findViewById(R.id.outer_layout);
    userName = view.findViewById(R.id.user_name);
    date = view.findViewById(R.id.date);
    comment = view.findViewById(R.id.comment);
    replies = view.findViewById(R.id.replies);
    this.dateUtils = dateUtils;
  }

  public void setComment(Comment comment) {
    ImageLoader.with(itemView.getContext())
        .loadWithCircleTransformAndPlaceHolderAvatarSize(comment.getUser()
            .getAvatar(), userAvatar, R.drawable.layer_1);
    userName.setText(comment.getUser()
        .getName());

    String dateDiff = dateUtils.getTimeDiffString(itemView.getContext(), comment.getDate()
        .getTime(), itemView.getContext()
        .getResources());
    this.date.setText(dateDiff);
    this.comment.setText(comment.getMessage());

    if (comment.getReplies() > 0) {
      String repliesText = String.format(itemView.getContext()
          .getString(R.string.comment_replies_number_short), comment.getReplies());
      this.replies.setText(repliesText);
      this.replies.setVisibility(View.VISIBLE);
    } else {
      this.replies.setVisibility(View.INVISIBLE);
    }
  }
}
