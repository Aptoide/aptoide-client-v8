package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.RatedRecommendation;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class SocialRecommendationViewHolder extends CardViewHolder<RatedRecommendation> {
  private final int titleStringResourceId;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final ImageView headerPrimaryAvatar;
  private final ImageView headerSecondaryAvatar;
  private final TextView headerPrimaryName;
  private final TextView headerSecondaryName;
  private final TextView timestamp;
  private final ImageView appIcon;
  private final TextView appName;
  private final RatingBar appRating;
  private final Button getAppButton;

  public SocialRecommendationViewHolder(View view, int titleStringResourceId,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator) {
    super(view);
    this.titleStringResourceId = titleStringResourceId;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;

    this.headerPrimaryAvatar = (ImageView) view.findViewById(R.id.card_image);
    this.headerSecondaryAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
    this.headerPrimaryName = (TextView) view.findViewById(R.id.card_title);
    this.headerSecondaryName = (TextView) view.findViewById(R.id.card_subtitle);
    this.timestamp = (TextView) view.findViewById(R.id.card_date);
    this.appIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    this.appName =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
    this.appRating = (RatingBar) view.findViewById(R.id.rating_bar);
    this.getAppButton =
        (Button) view.findViewById(R.id.displayable_social_timeline_recommendation_get_app_button);
  }

  @Override public void setCard(RatedRecommendation card, int position) {
    // TODO: 27/06/2017 set card title correctly, now it only distinguish the installed from recommendation cards.
    this.headerPrimaryName.setText(itemView.getContext()
        .getString(titleStringResourceId, "X"));
    this.timestamp.setText(
        dateCalculator.getTimeSinceDate(itemView.getContext(), card.getTimestamp()));
    ImageLoader.with(itemView.getContext())
        .load(card.getAppIcon(), appIcon);
    this.appName.setText(card.getAppName());
    this.appRating.setRating(card.getRatingAverage());

    this.getAppButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
  }
}
