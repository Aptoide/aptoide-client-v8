package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.LikeCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.PopularApp;
import cm.aptoide.pt.v8engine.social.data.PopularAppTouchEvent;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class PopularAppViewHolder extends PostViewHolder<PopularApp> {
  private final LayoutInflater inflater;
  private final DateCalculator dateCalculator;
  private final TextView headerSubTitle;
  private final ViewGroup headerUsersContainer;
  private final ImageView appIcon;
  private final TextView appName;
  private final RatingBar appRating;
  private final Button getAppButton;
  private final LinearLayout like;
  private final LikeButtonView likeButton;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final TextView commentButton;
  private final TextView shareButton;

  public PopularAppViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator) {
    super(view);
    this.inflater = LayoutInflater.from(itemView.getContext());
    this.dateCalculator = dateCalculator;
    this.headerSubTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_popular_app_card_timestamp);
    this.appIcon = (ImageView) view.findViewById(R.id.displayable_social_timeline_popular_app_icon);
    this.appName =
        (TextView) view.findViewById(R.id.displayable_social_timeline_popular_app_body_title);
    this.appRating = (RatingBar) view.findViewById(R.id.rating_bar);
    this.getAppButton =
        (Button) view.findViewById(R.id.displayable_social_timeline_popular_app_get_app_button);
    this.headerUsersContainer =
        (ViewGroup) view.findViewById(R.id.displayable_social_timeline_popular_app_users_container);
    this.likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    this.like = (LinearLayout) itemView.findViewById(R.id.social_like);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.commentButton = (TextView) view.findViewById(R.id.social_comment);
    this.shareButton = (TextView) itemView.findViewById(R.id.social_share);
  }

  @Override public void setPost(PopularApp card, int position) {
    this.headerSubTitle.setText(
        dateCalculator.getTimeSinceDate(itemView.getContext(), card.getTimestamp()));
    ImageLoader.with(itemView.getContext())
        .load(card.getAppIcon(), appIcon);
    this.appName.setText(card.getAppName());
    this.appRating.setRating(card.getAppAverageRating());
    showFriendsAvatars(card, itemView.getContext());
    this.getAppButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
    if (card.isLiked()) {
      if (card.isLikeFromClick()) {
        likeButton.setHeartState(true);
        card.setLikedFromClick(false);
      } else {
        likeButton.setHeartStateWithoutAnimation(true);
      }
    } else {
      likeButton.setHeartState(false);
    }
    this.like.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new LikeCardTouchEvent(card, CardTouchEvent.Type.LIKE, position)));

    this.commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.COMMENT)));
    this.shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.SHARE)));
  }

  private void showFriendsAvatars(PopularApp card, Context context) {
    headerUsersContainer.removeAllViews();
    View friendView;
    ImageView friendAvatar;
    for (UserSharerTimeline.User friend : card.getUsers()) {
      friendView = inflater.inflate(R.layout.social_timeline_friend, headerUsersContainer, false);
      friendAvatar = (ImageView) friendView.findViewById(R.id.social_timeline_friend_avatar);
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(friend.getAvatar(), friendAvatar);

      friendView.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
          new PopularAppTouchEvent(card, friend.getId(), "DEFAULT", CardTouchEvent.Type.HEADER)));

      headerUsersContainer.addView(friendView);
    }
  }
}
