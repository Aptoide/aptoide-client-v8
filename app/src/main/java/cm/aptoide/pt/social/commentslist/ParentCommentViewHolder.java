package cm.aptoide.pt.social.commentslist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 02/10/2017.
 */

class ParentCommentViewHolder extends PostCommentViewHolder {

  private final PublishSubject<String> replyEventPublishSubject;
  private final ImageView userIcon;
  private final TextView userName;
  private final TextView datePos1;
  private final TextView datePos2;
  private final TextView comment;
  private final View replyLayout;

  public ParentCommentViewHolder(View view, PublishSubject<String> replyEventPublishSubject) {
    super(view);
    this.userIcon = (ImageView) view.findViewById(R.id.user_icon);
    this.userName = (TextView) view.findViewById(R.id.user_name);
    this.datePos1 = (TextView) view.findViewById(R.id.added_date_pos1);
    this.replyEventPublishSubject = replyEventPublishSubject;
    this.datePos2 = (TextView) itemView.findViewById(R.id.added_date_pos2);
    this.comment = (TextView) itemView.findViewById(R.id.comment);
    this.replyLayout = itemView.findViewById(R.id.reply_layout);
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
    datePos1.setText(date);
    datePos2.setText(date);
    this.comment.setText(comment.getBody());
    replyLayout.setVisibility(View.VISIBLE);

    replyLayout.setOnClickListener(view -> replyEventPublishSubject.onNext(comment.getBody()));
  }
}
