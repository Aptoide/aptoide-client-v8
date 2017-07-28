package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.AggregatedRecommendation;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.MinimalCardViewFactory;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 29/06/2017.
 */

public class AggregatedRecommendationViewHolder extends PostViewHolder<AggregatedRecommendation> {
  private final LayoutInflater inflater;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final MinimalCardViewFactory minimalCardViewFactory;
  private final ImageView headerAvatar1;
  private final ImageView headerAvatar2;
  private final TextView headerNames;
  private final TextView headerTimestamp;
  private final ImageView appIcon;
  private final TextView appName;
  private final RatingBar appRating;
  private final Button getAppButton;
  private final TextView morePostersLabel;
  private final FrameLayout minimalCardContainer;

  public AggregatedRecommendationViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, MinimalCardViewFactory minimalCardViewFactory) {
    super(view);
    this.inflater = LayoutInflater.from(itemView.getContext());
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.minimalCardViewFactory = minimalCardViewFactory;
    this.headerAvatar1 = (ImageView) view.findViewById(R.id.card_header_avatar_1);
    this.headerAvatar2 = (ImageView) view.findViewById(R.id.card_header_avatar_2);
    this.headerNames = (TextView) view.findViewById(R.id.card_title);
    this.headerTimestamp = (TextView) view.findViewById(R.id.card_date);
    this.appIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    this.appName =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
    this.appRating = (RatingBar) view.findViewById(R.id.rating_bar);
    this.getAppButton =
        (Button) view.findViewById(R.id.displayable_social_timeline_recommendation_get_app_button);
    this.morePostersLabel =
        (TextView) itemView.findViewById(R.id.timeline_header_aditional_number_of_shares_circular);
    this.minimalCardContainer =
        (FrameLayout) itemView.findViewById(R.id.timeline_sub_minimal_card_container);
  }

  @Override public void setPost(AggregatedRecommendation card, int position) {
    if (card.getPosters() != null) {
      if (card.getPosters()
          .size() > 0) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(card.getPosters()
                .get(0)
                .getPrimaryAvatar(), this.headerAvatar1);
      }
      if (card.getPosters()
          .size() > 1) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(card.getPosters()
                .get(1)
                .getPrimaryAvatar(), this.headerAvatar2);
      }
    }
    this.headerNames.setText(getCardHeaderNames(card));
    this.headerTimestamp.setText(
        dateCalculator.getTimeSinceDate(itemView.getContext(), card.getTimestamp()));
    ImageLoader.with(itemView.getContext())
        .load(card.getAppIcon(), appIcon);
    this.appName.setText(card.getAppName());
    this.appRating.setRating(card.getAppAverageRating());
    this.getAppButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
    this.appIcon.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
    showMorePostersLabel(card);
    minimalCardContainer.removeAllViews();
    minimalCardContainer.addView(minimalCardViewFactory.getView(card, card.getMinimalPosts(),
        MinimalCardViewFactory.MINIMUM_NUMBER_OF_VISILIBE_MINIMAL_CARDS, inflater,
        itemView.getContext(), position));
  }

  public String getCardHeaderNames(AggregatedRecommendation card) {
    StringBuilder headerNamesStringBuilder = new StringBuilder();
    if (card.getPosters()
        .size() >= 2) {
      List<Poster> posters = card.getPosters()
          .subList(0, 2);
      for (Poster poster : posters) {
        headerNamesStringBuilder.append(poster.getPrimaryName())
            .append(", ");
      }
      headerNamesStringBuilder.setLength(headerNamesStringBuilder.length() - 2);
    }
    return headerNamesStringBuilder.toString();
  }

  private void showMorePostersLabel(AggregatedRecommendation card) {
    if (card.getPosters()
        .size() > 2) {
      morePostersLabel.setText(String.format(itemView.getContext()
          .getString(R.string.timeline_short_plus), String.valueOf(card.getPosters()
          .size() - 2)));
      morePostersLabel.setVisibility(View.VISIBLE);
    } else {
      morePostersLabel.setVisibility(View.INVISIBLE);
    }
  }
}
