package cm.aptoide.pt.v8engine.social.data;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 28/06/2017.
 */

public class MinimalCardViewFactory {

  public static final int MINIMUM_NUMBER_OF_VISILIBE_MINIMAL_CARDS = 2;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private TextView morePostersLabel;
  private LinearLayout socialInfoBar;
  private TextView numberLikes;
  private TextView numberComments;
  private TextView numberLikesOneLike;
  private RelativeLayout likePreviewContainer;
  private LinearLayout socialCommentBar;
  private TextView socialCommentUsername;
  private TextView socialCommentBody;
  private ImageView latestCommentMainAvatar;
  private LayoutInflater inflater;
  private int marginOfTheNextLikePreview = 60;

  public MinimalCardViewFactory(DateCalculator dateCalculator, SpannableFactory spannableFactory,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
  }

  public View getView(Post originalPost, List<MinimalPost> minimalPosts, LayoutInflater inflater,
      Context context, int position) {
    LinearLayout minimalCardContainer = new LinearLayout(context);
    minimalCardContainer.setOrientation(LinearLayout.VERTICAL);

    for (MinimalPost post : minimalPosts) {
      minimalCardContainer.addView(
          getMinimalCardView(originalPost, post, inflater, context, minimalCardContainer,
              position));
    }
    return minimalCardContainer;
  }

  private View getMinimalCardView(Post originalPost, MinimalPost post, LayoutInflater inflater,
      Context context, ViewGroup minimalCardContainer, int position) {
    this.inflater = inflater;
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

    /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */

    socialInfoBar = (LinearLayout) subCardView.findViewById(R.id.social_info_bar);
    numberLikes = (TextView) subCardView.findViewById(R.id.social_number_of_likes);
    numberComments = (TextView) subCardView.findViewById(R.id.social_number_of_comments);
    numberLikesOneLike = (TextView) subCardView.findViewById(R.id.social_one_like);
    likePreviewContainer = (RelativeLayout) subCardView.findViewById(
        R.id.displayable_social_timeline_likes_preview_container);
    socialCommentBar = (LinearLayout) subCardView.findViewById(R.id.social_latest_comment_bar);
    socialCommentUsername =
        (TextView) subCardView.findViewById(R.id.social_latest_comment_user_name);
    socialCommentBody = (TextView) subCardView.findViewById(R.id.social_latest_comment_body);
    latestCommentMainAvatar =
        (ImageView) subCardView.findViewById(R.id.card_last_comment_main_icon);
    /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */

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
      if (post.isLikeFromClick()) {
        likeButton.setHeartState(true);
        post.setLikedFromClick(false);
      } else {
        likeButton.setHeartStateWithoutAnimation(true);
      }
    } else {
      likeButton.setHeartState(false);
    }

    /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
    showSocialInformationBar(post, context);
    showLikesPreview(post, context);
    /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */

    like.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new LikeCardTouchEvent(post, CardTouchEvent.Type.LIKE, position)));
    commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, CardTouchEvent.Type.COMMENT)));
    shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(originalPost, CardTouchEvent.Type.SHARE)));
    this.likePreviewContainer.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new LikesPreviewCardTouchEvent(post, post.getLikesNumber(),
            CardTouchEvent.Type.LIKES_PREVIEW)));
    this.numberComments.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, CardTouchEvent.Type.COMMENT_NUMBER)));
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
          .getPrimaryName())
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

  /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
  private void showSocialInformationBar(MinimalPost card, Context context) {
    if (card.getLikesNumber() > 0 || card.getCommentsNumber() > 0) {
      socialInfoBar.setVisibility(View.VISIBLE);
    } else {
      socialInfoBar.setVisibility(View.GONE);
    }

    handleLikesInformation(card, context);
    handleCommentsInformation(card, context);
  }

  private void showLikesPreview(MinimalPost post, Context context) {
    likePreviewContainer.removeAllViews();
    marginOfTheNextLikePreview = 60;
    for (int j = 0; j < post.getLikesNumber(); j++) {

      UserTimeline user = null;
      if (post.getLikes() != null && j < post.getLikes()
          .size()) {
        user = post.getLikes()
            .get(j);
      }
      addUserToPreview(marginOfTheNextLikePreview, user, context);
      if (marginOfTheNextLikePreview < 0) {
        break;
      }
    }
  }

  private void handleLikesInformation(MinimalPost card, Context context) {
    if (card.getLikesNumber() > 0) {
      if (card.getLikesNumber() > 1) {
        showNumberOfLikes(card.getLikesNumber(), context);
      } else if (card.getLikes() != null
          && card.getLikes()
          .size() != 0) {
        String firstLikeName = card.getLikes()
            .get(0)
            .getName();
        if (firstLikeName != null) {
          numberLikesOneLike.setText(spannableFactory.createColorSpan(
              context.getString(R.string.timeline_short_like_present_singular, firstLikeName),
              ContextCompat.getColor(context, R.color.black_87_alpha), firstLikeName));
          numberLikes.setVisibility(View.INVISIBLE);
          numberLikesOneLike.setVisibility(View.VISIBLE);
        } else {
          String firstStoreName = card.getLikes()
              .get(0)
              .getStore()
              .getName();
          if (card.getLikes()
              .get(0)
              .getStore() != null && firstStoreName != null) {
            numberLikesOneLike.setText(spannableFactory.createColorSpan(
                context.getString(R.string.timeline_short_like_present_singular, firstStoreName),
                ContextCompat.getColor(context, R.color.black_87_alpha), firstStoreName));
            numberLikes.setVisibility(View.INVISIBLE);
            numberLikesOneLike.setVisibility(View.VISIBLE);
          } else {
            showNumberOfLikes(card.getLikesNumber(), context);
          }
        }
      }
    } else {
      numberLikes.setVisibility(View.INVISIBLE);
      numberLikesOneLike.setVisibility(View.INVISIBLE);
    }
  }
  /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */

  private void handleCommentsInformation(MinimalPost post, Context context) {
    if (post.getCommentsNumber() > 0) {
      numberComments.setVisibility(View.VISIBLE);
      numberComments.setText(context.getResources()
          .getQuantityString(R.plurals.timeline_short_comment, (int) post.getCommentsNumber(),
              (int) post.getCommentsNumber()));
      socialCommentBar.setVisibility(View.VISIBLE);
      ImageLoader.with(context)
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
      numberComments.setVisibility(View.INVISIBLE);
      socialCommentBar.setVisibility(View.GONE);
    }
  }

  private void addUserToPreview(int i, UserTimeline user, Context context) {
    View likeUserPreviewView;
    ImageView likeUserPreviewIcon;
    likeUserPreviewView =
        inflater.inflate(R.layout.social_timeline_like_user_preview, likePreviewContainer, false);
    likeUserPreviewIcon =
        (ImageView) likeUserPreviewView.findViewById(R.id.social_timeline_like_user_preview);
    ViewGroup.MarginLayoutParams p =
        (ViewGroup.MarginLayoutParams) likeUserPreviewView.getLayoutParams();
    p.setMargins(i, 0, 0, 0);
    likeUserPreviewView.requestLayout();

    if (user != null) {
      if (user.getAvatar() != null) {
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(user.getAvatar(), likeUserPreviewIcon);
      } else if (user.getStore()
          .getAvatar() != null) {
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(user.getStore()
                .getAvatar(), likeUserPreviewIcon);
      }
      likePreviewContainer.addView(likeUserPreviewView);
      marginOfTheNextLikePreview -= 20;
    }
  }

  private void showNumberOfLikes(long likesNumber, Context context) {
    numberLikes.setVisibility(View.VISIBLE);
    numberLikes.setText(context.getString(R.string.timeline_short_like_present_plural, likesNumber)
        .toLowerCase());
    numberLikesOneLike.setVisibility(View.INVISIBLE);
  }

  public View getView(Post originalPost, List<Post> minimalCards, int numberOfCardsToShow,
      LayoutInflater inflater, Context context, int position) {
    LinearLayout minimalCardContainer = new LinearLayout(context);
    minimalCardContainer.setOrientation(LinearLayout.VERTICAL);

    for (int i = 0; i < numberOfCardsToShow && i < minimalCards.size(); i++) {
      minimalCardContainer.addView(
          getMinimalCardView(originalPost, (MinimalPost) minimalCards.get(i), inflater, context,
              minimalCardContainer, position));
    }

    return minimalCardContainer;
  }
}
