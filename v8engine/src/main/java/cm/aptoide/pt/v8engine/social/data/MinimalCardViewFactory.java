package cm.aptoide.pt.v8engine.social.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 28/06/2017.
 */

public class MinimalCardViewFactory {

  public static final int MINIMUM_NUMBER_OF_VISILIBE_MINIMAL_CARDS = 2;
  private final DateCalculator dateCalculator;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private TextView minimalCardHeaderMainName;
  private ImageView minimalCardHeaderMainAvatar;
  private ImageView minimalCardHeaderMainAvatar2;
  private TextView cardHeaderTimestamp;
  private TextView morePostersLabel;
  private LikeButtonView likeButton;
  private LinearLayout like;

  public MinimalCardViewFactory(DateCalculator dateCalculator,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    this.dateCalculator = dateCalculator;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
  }

  private View getMinimalCardView(MinimalCard minimalCard, LayoutInflater inflater, Context context,
      ViewGroup minimalCardContainer) {
    View subCardView =
        inflater.inflate(R.layout.timeline_sub_minimal_card, minimalCardContainer, false);
    minimalCardHeaderMainName = (TextView) subCardView.findViewById(R.id.card_title);
    minimalCardHeaderMainAvatar = (ImageView) subCardView.findViewById(R.id.card_header_avatar_1);
    minimalCardHeaderMainAvatar2 = (ImageView) subCardView.findViewById(R.id.card_header_avatar_2);
    cardHeaderTimestamp = (TextView) subCardView.findViewById(R.id.card_date);
    morePostersLabel = (TextView) subCardView.findViewById(
        R.id.timeline_header_aditional_number_of_shares_circular);
    this.likeButton = (LikeButtonView) subCardView.findViewById(R.id.social_like_button);
    this.like = (LinearLayout) subCardView.findViewById(R.id.social_like_layout);
    Poster poster1 = new Poster(minimalCard.getSharers()
        .get(0)
        .getUser(), minimalCard.getSharers()
        .get(0)
        .getStore());

    ImageLoader.with(context)
        .loadWithShadowCircleTransform(poster1.getPrimaryAvatar(), minimalCardHeaderMainAvatar);

    if (minimalCard.getSharers()
        .size() > 1) {
      Poster poster2 = new Poster(minimalCard.getSharers()
          .get(1)
          .getUser(), minimalCard.getSharers()
          .get(1)
          .getStore());
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(poster2.getPrimaryAvatar(), minimalCardHeaderMainAvatar2);
    } else {
      minimalCardHeaderMainAvatar2.setVisibility(View.GONE);
    }

    showMorePostersLabel(minimalCard.getSharers(), context);

    minimalCardHeaderMainName.setText(getCardHeaderNames(minimalCard.getSharers()));

    cardHeaderTimestamp.setText(dateCalculator.getTimeSinceDate(context, minimalCard.getDate()));

    this.like.setOnClickListener(click -> {
      this.likeButton.performClick();
      this.cardTouchEventPublishSubject.onNext(new CardTouchEvent(new Post() {
        @Override public String getCardId() {
          return minimalCard.getCardId();
        }

        @Override public CardType getType() {
          return CardType.MINIMAL_CARD;
        }
      }, CardTouchEvent.Type.LIKE));
    });

    return subCardView;
  }

  private void showMorePostersLabel(List<UserSharerTimeline> sharers, Context context) {
    if (sharers.size() > 2) {
      morePostersLabel.setText(String.format(context.getString(R.string.timeline_short_plus),
          String.valueOf(sharers.size() - 2)));
      morePostersLabel.setVisibility(View.VISIBLE);
    } else {
      morePostersLabel.setVisibility(View.INVISIBLE);
    }
  }

  public String getCardHeaderNames(List<UserSharerTimeline> sharers) {
    StringBuilder headerNamesStringBuilder = new StringBuilder();
    if (sharers.size() == 1) {
      return headerNamesStringBuilder.append(sharers.get(0)
          .getStore()
          .getName())
          .toString();
    }
    List<UserSharerTimeline> firstSharers = sharers.subList(0, 2);
    for (UserSharerTimeline user : firstSharers) {
      headerNamesStringBuilder.append(user.getStore()
          .getName())
          .append(", ");
    }
    headerNamesStringBuilder.setLength(headerNamesStringBuilder.length() - 2);
    return headerNamesStringBuilder.toString();
  }

  public View getView(List<MinimalCard> minimalCards, LayoutInflater inflater, Context context) {
    LinearLayout minimalCardContainer = new LinearLayout(context);

    for (MinimalCard minimalCard : minimalCards) {
      minimalCardContainer.addView(
          getMinimalCardView(minimalCard, inflater, context, minimalCardContainer));
    }
    return minimalCardContainer;
  }

  public View getView(List<MinimalCard> minimalCards, int numberOfCardsToShow,
      LayoutInflater inflater, Context context) {
    LinearLayout minimalCardContainer = new LinearLayout(context);

    for (int i = 0; i < numberOfCardsToShow && i < minimalCards.size(); i++) {
      minimalCardContainer.addView(
          getMinimalCardView(minimalCards.get(i), inflater, context, minimalCardContainer));
    }

    return minimalCardContainer;
  }
}
