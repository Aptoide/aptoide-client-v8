package cm.aptoide.pt.social.view.viewholder;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.Post;
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
  private final View overflowMenu;

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
    this.overflowMenu = itemView.findViewById(R.id.overflow_menu);
  }

  public abstract void setPost(T card, int position);

  protected void setupOverflowMenu(Post post, int position) {
    overflowMenu.setOnClickListener(view -> {
      final PopupMenu popup = new PopupMenu(itemView.getContext(), overflowMenu);
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.menu_post_overflow, popup.getMenu());

      MenuItem menuItemDelete = popup.getMenu()
          .findItem(R.id.delete);
      menuItemDelete.setOnMenuItemClickListener(menuItem -> {
        cardTouchEventPublishSubject.onNext(
            new CardTouchEvent(post, position, CardTouchEvent.Type.DELETE_POST));
        return false;
      });

      MenuItem menuItemReport = popup.getMenu()
          .findItem(R.id.report);
      menuItemReport.setOnMenuItemClickListener(menuItem -> {
        cardTouchEventPublishSubject.onNext(
            new CardTouchEvent(post, position, CardTouchEvent.Type.REPORT_ABUSE));
        return false;
      });
      MenuItem menuItemUnfollow = popup.getMenu()
          .findItem(R.id.unfollow);
      menuItemUnfollow.setOnMenuItemClickListener(menuItem -> {
        cardTouchEventPublishSubject.onNext(
            new CardTouchEvent(post, position, CardTouchEvent.Type.UNFOLLOW_STORE));
        return false;
      });
      popup.show();
    });
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
