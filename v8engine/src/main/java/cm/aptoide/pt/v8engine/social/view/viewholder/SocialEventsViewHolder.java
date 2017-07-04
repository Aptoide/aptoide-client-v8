package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 04/07/2017.
 */

abstract class SocialEventsViewHolder<T extends Post> extends CardViewHolder<T> {
  protected final LinearLayout like;
  protected final LikeButtonView likeButton;
  protected final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;

  public SocialEventsViewHolder(View itemView,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(itemView);
    this.likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    this.like = (LinearLayout) itemView.findViewById(R.id.social_like_layout);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
  }

  @Override public void setCard(T post, int position) {
    this.like.setOnClickListener(click -> {
      this.likeButton.performClick();
      this.cardTouchEventPublishSubject.onNext(new CardTouchEvent(post, CardTouchEvent.Type.LIKE));
    });
  }
}
