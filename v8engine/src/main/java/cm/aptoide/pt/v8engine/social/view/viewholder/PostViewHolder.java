package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.Post;

/**
 * Created by jdandrade on 08/06/2017.
 */

public abstract class PostViewHolder<T extends Post> extends RecyclerView.ViewHolder {
  private final ImageView latestCommentMainAvatar;
  private final TextView socialCommentBody;
  private final LinearLayout socialCommentBar;
  private final TextView socialCommentUsername;

  public PostViewHolder(View itemView) {
    super(itemView);
    this.socialCommentBar = (LinearLayout) itemView.findViewById(R.id.social_latest_comment_bar);
    this.socialCommentUsername =
        (TextView) itemView.findViewById(R.id.social_latest_comment_user_name);
    this.latestCommentMainAvatar =
        (ImageView) itemView.findViewById(R.id.card_last_comment_main_icon);
    this.socialCommentBody = (TextView) itemView.findViewById(R.id.social_latest_comment_body);
  }

  public abstract void setPost(T card, int position);

  void handleCommentsInformation(Post post) {
    if (post.getCommentsNumber() > 0) {
      socialCommentBar.setVisibility(View.VISIBLE);
      ImageLoader.with(itemView.getContext())
          .loadWithShadowCircleTransform(post.getComments()
              .get(0)
              .getAvatar(), latestCommentMainAvatar);
      socialCommentUsername.setText(post.getComments()
          .get(0)
          .getName());
      socialCommentBody.setText(post.getComments()
          .get(0)
          .getBody());
    } else {
      socialCommentBar.setVisibility(View.GONE);
    }
  }
}
