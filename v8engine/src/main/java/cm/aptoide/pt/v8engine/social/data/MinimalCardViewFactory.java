package cm.aptoide.pt.v8engine.social.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
  private TextView morePostersLabel;

  public MinimalCardViewFactory(DateCalculator dateCalculator,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    this.dateCalculator = dateCalculator;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
  }

  private View getMinimalCardView(Post originalPost, MinimalPost post, LayoutInflater inflater,
      Context context, ViewGroup minimalCardContainer) {
    View subCardView =
        inflater.inflate(R.layout.timeline_sub_minimal_card, minimalCardContainer, false);
    TextView minimalCardHeaderMainName = (TextView) subCardView.findViewById(R.id.card_title);
    ImageView minimalCardHeaderMainAvatar =
        (ImageView) subCardView.findViewById(R.id.card_header_avatar_1);
    ImageView minimalCardHeaderMainAvatar2 =
        (ImageView) subCardView.findViewById(R.id.card_header_avatar_2);
    TextView cardHeaderTimestamp = (TextView) subCardView.findViewById(R.id.card_date);
    morePostersLabel = (TextView) subCardView.findViewById(
        R.id.timeline_header_aditional_number_of_shares_circular);
    LikeButtonView likeButton = (LikeButtonView) subCardView.findViewById(R.id.social_like_button);
    LinearLayout like = (LinearLayout) subCardView.findViewById(R.id.social_like);
    TextView commentButton = (TextView) subCardView.findViewById(R.id.social_comment);
    TextView shareButton = (TextView) subCardView.findViewById(R.id.social_share);
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(post.getMinimalPostPosters()
            .get(0)
            .getPrimaryAvatar(), minimalCardHeaderMainAvatar);

    if (post.getMinimalPostPosters()
        .size() > 1) {
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(post.getMinimalPostPosters()
              .get(1)
              .getPrimaryAvatar(), minimalCardHeaderMainAvatar2);
    } else {
      minimalCardHeaderMainAvatar2.setVisibility(View.GONE);
    }

    showMorePostersLabel(post.getMinimalPostPosters()
        .size(), context);

    minimalCardHeaderMainName.setText(getCardHeaderNames(post.getMinimalPostPosters()));

    cardHeaderTimestamp.setText(dateCalculator.getTimeSinceDate(context, post.getDate()));

    if (post.isLiked()) {
      likeButton.setHeartState(true);
    } else {
      likeButton.setHeartState(false);
    }

    like.setOnClickListener(click -> likeButton.performClick());

    likeButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, CardTouchEvent.Type.LIKE)));
    commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, CardTouchEvent.Type.COMMENT)));
    shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(originalPost, CardTouchEvent.Type.SHARE)));
    return subCardView;
  }

  private void showMorePostersLabel(int posters, Context context) {
    if (posters > 2) {
      morePostersLabel.setText(String.format(context.getString(R.string.timeline_short_plus),
          String.valueOf(posters - 2)));
      morePostersLabel.setVisibility(View.VISIBLE);
    } else {
      morePostersLabel.setVisibility(View.INVISIBLE);
    }
  }

  public String getCardHeaderNames(List<Poster> sharers) {
    StringBuilder headerNamesStringBuilder = new StringBuilder();
    if (sharers.size() == 1) {
      return headerNamesStringBuilder.append(sharers.get(0)
          .getStore()
          .getName())
          .toString();
    }
    List<Poster> firstSharers = sharers.subList(0, 2);
    for (Poster poster : firstSharers) {
      headerNamesStringBuilder.append(poster.getPrimaryName())
          .append(", ");
    }
    headerNamesStringBuilder.setLength(headerNamesStringBuilder.length() - 2);
    return headerNamesStringBuilder.toString();
  }

  public View getView(Post originalPost, List<MinimalPost> minimalPosts, LayoutInflater inflater,
      Context context) {
    LinearLayout minimalCardContainer = new LinearLayout(context);

    for (MinimalPost post : minimalPosts) {
      minimalCardContainer.addView(
          getMinimalCardView(originalPost, post, inflater, context, minimalCardContainer));
    }
    return minimalCardContainer;
  }

  public View getView(Post originalPost, List<Post> minimalCards, int numberOfCardsToShow,
      LayoutInflater inflater, Context context) {
    LinearLayout minimalCardContainer = new LinearLayout(context);

    for (int i = 0; i < numberOfCardsToShow && i < minimalCards.size(); i++) {
      minimalCardContainer.addView(
          getMinimalCardView(originalPost, (MinimalPost) minimalCards.get(i), inflater, context,
              minimalCardContainer));
    }

    return minimalCardContainer;
  }
}
