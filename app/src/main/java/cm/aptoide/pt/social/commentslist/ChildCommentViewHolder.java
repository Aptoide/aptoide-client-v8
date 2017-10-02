package cm.aptoide.pt.social.commentslist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;

/**
 * Created by jdandrade on 02/10/2017.
 */

class ChildCommentViewHolder extends PostCommentViewHolder {
  private final ImageView userIcon;
  private final TextView userName;
  private final TextView date;
  private final TextView comment;
  private final View replyLayout;

  public ChildCommentViewHolder(View view) {
    super(view);
    userIcon = (ImageView) view.findViewById(R.id.user_icon);
    userName = (TextView) view.findViewById(R.id.user_name);
    date = (TextView) itemView.findViewById(R.id.added_date_pos2);
    comment = (TextView) itemView.findViewById(R.id.comment);
    replyLayout = itemView.findViewById(R.id.reply_layout);
  }

  @Override public void setComment(Comment comment, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithCircleTransformAndPlaceHolderAvatarSize(comment.getUser()
            .getAvatar(), userIcon, R.drawable.layer_1);
    userName.setText(comment.getUser()
        .getName());
    String date = AptoideUtils.DateTimeU.getInstance(itemView.getContext())
        .getTimeDiffString(itemView.getContext(), comment.getAdded()
            .getTime(), itemView.getContext()
            .getResources());
    this.date.setText(date);
    this.comment.setText(comment.getBody());
    replyLayout.setVisibility(View.GONE);
    this.date.setVisibility(View.VISIBLE);
  }
}
