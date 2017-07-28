package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.AggregatedMedia;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.CardType;
import cm.aptoide.pt.v8engine.social.data.MinimalCardViewFactory;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 28/06/2017.
 */

public class AggregatedMediaViewHolder extends PostViewHolder<AggregatedMedia> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final ImageView headerAvatar1;
  private final ImageView headerAvatar2;
  private final TextView headerNames;
  private final TextView headerTimestamp;
  private final TextView mediaTitle;
  private final ImageView mediaThumbnail;
  private final TextView relatedTo;
  private final ImageView playIcon;
  private final TextView morePostersLabel;
  private final FrameLayout minimalCardContainer;
  private final LayoutInflater inflater;
  private final MinimalCardViewFactory minimalCardViewFactory;

  public AggregatedMediaViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, MinimalCardViewFactory minimalCardViewFactory) {
    super(view);
    this.minimalCardViewFactory = minimalCardViewFactory;
    this.inflater = LayoutInflater.from(itemView.getContext());
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;

    this.headerAvatar1 = (ImageView) itemView.findViewById(R.id.card_header_avatar_1);
    this.headerAvatar2 = (ImageView) itemView.findViewById(R.id.card_header_avatar_2);
    this.headerNames = (TextView) itemView.findViewById(R.id.card_title);
    this.headerTimestamp = (TextView) itemView.findViewById(R.id.card_date);
    this.mediaTitle =
        (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    this.mediaThumbnail = (ImageView) itemView.findViewById(R.id.featured_graphic);
    this.relatedTo = (TextView) itemView.findViewById(R.id.app_name);
    this.playIcon = (ImageView) itemView.findViewById(R.id.play_button);
    this.morePostersLabel =
        (TextView) itemView.findViewById(R.id.timeline_header_aditional_number_of_shares_circular);
    this.minimalCardContainer =
        (FrameLayout) itemView.findViewById(R.id.timeline_sub_minimal_card_container);
  }

  @Override public void setPost(AggregatedMedia card, int position) {
    if (card.getType()
        .equals(CardType.AGGREGATED_SOCIAL_ARTICLE)) {
      this.playIcon.setVisibility(View.GONE);
    } else if (card.getType()
        .equals(CardType.AGGREGATED_SOCIAL_VIDEO)) {
      this.playIcon.setVisibility(View.VISIBLE);
    }
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
        dateCalculator.getTimeSinceDate(itemView.getContext(), card.getDate()));
    this.mediaTitle.setText(card.getMediaTitle());
    String appName = card.getRelatedApp()
        .getName();
    this.relatedTo.setText(spannableFactory.createStyleSpan(itemView.getContext()
            .getString(R.string.displayable_social_timeline_article_related_to, appName), Typeface.BOLD,
        appName));
    ImageLoader.with(itemView.getContext())
        .load(card.getMediaThumbnailUrl(), mediaThumbnail);

    this.mediaThumbnail.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
    showMorePostersLabel(card);

    minimalCardContainer.removeAllViews();
    minimalCardContainer.addView(minimalCardViewFactory.getView(card, card.getMinimalCards(),
        MinimalCardViewFactory.MINIMUM_NUMBER_OF_VISILIBE_MINIMAL_CARDS, inflater,
        itemView.getContext(), position));
  }

  public String getCardHeaderNames(AggregatedMedia card) {
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

  private void showMorePostersLabel(AggregatedMedia card) {
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
