package cm.aptoide.pt.social.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.Post;
import cm.aptoide.pt.social.data.PostOverflowArrayAdapter;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 08/06/2017.
 */

public abstract class PostViewHolder<T extends Post> extends RecyclerView.ViewHolder {
  private final ImageView latestCommentMainAvatar;
  private final TextView socialCommentBody;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final LinearLayout socialCommentBar;
  private final TextView socialCommentUsername;
  private final Spinner overflowMenu;

  public PostViewHolder(View itemView,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(itemView);
    this.socialCommentBar = (LinearLayout) itemView.findViewById(R.id.social_latest_comment_bar);
    this.socialCommentUsername =
        (TextView) itemView.findViewById(R.id.social_latest_comment_user_name);
    this.latestCommentMainAvatar =
        (ImageView) itemView.findViewById(R.id.card_last_comment_main_icon);
    this.socialCommentBody = (TextView) itemView.findViewById(R.id.social_latest_comment_body);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.overflowMenu = (Spinner) itemView.findViewById(R.id.overflow_menu);
  }

  public abstract void setPost(T card, int position);

  protected void setupOverflowMenu() {
    PostOverflowArrayAdapter<String> adapter =
        new PostOverflowArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item,
            itemView.getContext()
                .getResources()
                .getStringArray(R.array.timeline_button_cards_overflow_menu_options));
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
    overflowMenu.setAdapter(adapter);
  }

  protected void handleCommentsInformation(Post post, int position) {
    if (post.getCommentsNumber() > 0) {
      socialCommentBar.setVisibility(View.VISIBLE);
      socialCommentBar.setOnClickListener(view -> cardTouchEventPublishSubject.onNext(
          new CardTouchEvent(post, position, CardTouchEvent.Type.LAST_COMMENT)));
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
