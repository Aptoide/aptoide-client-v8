package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.PopularApp;
import cm.aptoide.pt.v8engine.social.data.publisher.Publisher;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class PopularAppViewHolder extends CardViewHolder<PopularApp> {
  private final LayoutInflater inflater;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final TextView headerSubTitle;
  private final ViewGroup headerUsersContainer;
  private final ImageView appIcon;
  private final TextView appName;
  private final RatingBar appRating;
  private final Button getAppButton;

  public PopularAppViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator) {
    super(view);
    this.inflater = LayoutInflater.from(itemView.getContext());
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
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
  }

  @Override public void setCard(PopularApp card, int position) {
    this.headerSubTitle.setText(
        dateCalculator.getTimeSinceDate(itemView.getContext(), card.getTimestamp()));
    ImageLoader.with(itemView.getContext())
        .load(card.getAppIcon(), appIcon);
    this.appName.setText(card.getAppName());
    this.appRating.setRating(card.getRatingAverage());
    showFriendsAvatars(card, itemView.getContext());
    this.getAppButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
  }

  private void showFriendsAvatars(PopularApp card, Context context) {
    headerUsersContainer.removeAllViews();
    View friendView;
    ImageView friendAvatar;
    for (Publisher friend : card.getPublishers()) {
      friendView = inflater.inflate(R.layout.social_timeline_friend, headerUsersContainer, false);
      friendAvatar = (ImageView) friendView.findViewById(R.id.social_timeline_friend_avatar);
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(friend.getPublisherAvatar()
              .getAvatarUrl(), friendAvatar);
      headerUsersContainer.addView(friendView);
    }
  }
}
