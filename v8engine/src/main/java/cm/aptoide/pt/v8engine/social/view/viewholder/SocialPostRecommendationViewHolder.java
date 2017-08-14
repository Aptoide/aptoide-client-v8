package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.LikesCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.RatedRecommendation;
import cm.aptoide.pt.v8engine.social.data.SocialCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.SocialHeaderCardTouchEvent;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 24/07/2017.
 */

public class SocialPostRecommendationViewHolder extends PostViewHolder<RatedRecommendation> {
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
  private final SpannableFactory spannableFactory;
  private final RelativeLayout cardHeader;
  private final LinearLayout like;
  private final LikeButtonView likeButton;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final TextView commentButton;
  private final TextView shareButton;

  /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
  private final LinearLayout socialInfoBar;
  private final TextView numberLikes;
  private final TextView numberComments;
  private final TextView numberLikesOneLike;
  private final RelativeLayout likePreviewContainer;
  private final LayoutInflater inflater;
  private final LinearLayout socialCommentBar;
  private final TextView socialCommentUsername;
  private final TextView socialCommentBody;
  private final ImageView latestCommentMainAvatar;
  /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */

  private final TextView postContent;
  private int marginOfTheNextLikePreview = 60;

  public SocialPostRecommendationViewHolder(View view, DateCalculator dateCalculator,
      SpannableFactory spannableFactory,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(view);
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
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
    this.cardHeader = (RelativeLayout) view.findViewById(R.id.social_header);
    this.likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    this.like = (LinearLayout) itemView.findViewById(R.id.social_like);
    this.commentButton = (TextView) itemView.findViewById(R.id.social_comment);
    this.shareButton = (TextView) itemView.findViewById(R.id.social_share);
    /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
    this.socialInfoBar = (LinearLayout) itemView.findViewById(R.id.social_info_bar);
    this.numberLikes = (TextView) itemView.findViewById(R.id.social_number_of_likes);
    this.numberComments = (TextView) itemView.findViewById(R.id.social_number_of_comments);
    this.numberLikesOneLike = (TextView) itemView.findViewById(R.id.social_one_like);
    this.likePreviewContainer = (RelativeLayout) itemView.findViewById(
        R.id.displayable_social_timeline_likes_preview_container);
    this.socialCommentBar = (LinearLayout) itemView.findViewById(R.id.social_latest_comment_bar);
    this.socialCommentUsername =
        (TextView) itemView.findViewById(R.id.social_latest_comment_user_name);
    this.socialCommentBody = (TextView) itemView.findViewById(R.id.social_latest_comment_body);
    this.latestCommentMainAvatar =
        (ImageView) itemView.findViewById(R.id.card_last_comment_main_icon);
    this.inflater = LayoutInflater.from(itemView.getContext());
    /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
    this.postContent = (TextView) itemView.findViewById(R.id.post_content);
  }

  @Override public void setPost(RatedRecommendation card, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getPoster()
            .getPrimaryAvatar(), headerPrimaryAvatar);
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getPoster()
            .getSecondaryAvatar(), headerSecondaryAvatar);
    this.headerPrimaryName.setText(getStyledTitle(itemView.getContext(), card.getPoster()
        .getPrimaryName()));
    showHeaderSecondaryName(card);
    this.timestamp.setText(
        dateCalculator.getTimeSinceDate(itemView.getContext(), card.getTimestamp()));
    ImageLoader.with(itemView.getContext())
        .load(card.getAppIcon(), appIcon);
    this.appName.setText(card.getAppName());
    this.appRating.setRating(card.getAppAverageRating());
    this.postContent.setText(card.getContent());

    this.getAppButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
    this.appIcon.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
    this.cardHeader.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new SocialHeaderCardTouchEvent(card, card.getPoster()
            .getStore()
            .getName(), card.getPoster()
            .getStore()
            .getStoreTheme(), card.getPoster()
            .getUser()
            .getId(), CardTouchEvent.Type.HEADER)));
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
    /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
    showSocialInformationBar(card);
    showLikesPreview(card);
    /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
    this.like.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new SocialCardTouchEvent(card, CardTouchEvent.Type.LIKE, position)));

    this.commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new SocialCardTouchEvent(card, CardTouchEvent.Type.COMMENT, position)));
    this.shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.SHARE)));
    this.likePreviewContainer.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new LikesCardTouchEvent(card, card.getLikesNumber(), CardTouchEvent.Type.LIKES_PREVIEW)));
    this.numberComments.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.COMMENT_NUMBER)));
  }

  public Spannable getStyledTitle(Context context, String title) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_share_past_singular, title),
        ContextCompat.getColor(context, R.color.black_87_alpha), title);
  }

  private void showHeaderSecondaryName(RatedRecommendation card) {
    if (TextUtils.isEmpty(card.getPoster()
        .getSecondaryName())) {
      this.headerSecondaryName.setVisibility(View.GONE);
    } else {
      this.headerSecondaryName.setText(card.getPoster()
          .getSecondaryName());
      this.headerSecondaryName.setVisibility(View.VISIBLE);
    }
  }

  /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
  private void showSocialInformationBar(RatedRecommendation card) {
    if (card.getLikesNumber() > 0 || card.getCommentsNumber() > 0) {
      socialInfoBar.setVisibility(View.VISIBLE);
    } else {
      socialInfoBar.setVisibility(View.GONE);
    }

    handleLikesInformation(card);
    handleCommentsInformation(card);
  }

  private void showLikesPreview(RatedRecommendation post) {
    likePreviewContainer.removeAllViews();
    marginOfTheNextLikePreview = 60;
    for (int j = 0; j < post.getLikesNumber(); j++) {

      UserTimeline user = null;
      if (post.getLikes() != null && j < post.getLikes()
          .size()) {
        user = post.getLikes()
            .get(j);
      }
      addUserToPreview(marginOfTheNextLikePreview, user);
      if (marginOfTheNextLikePreview < 0) {
        break;
      }
    }
  }

  private void handleLikesInformation(RatedRecommendation card) {
    if (card.getLikesNumber() > 0) {
      if (card.getLikesNumber() > 1) {
        showNumberOfLikes(card.getLikesNumber());
      } else if (card.getLikes() != null
          && card.getLikes()
          .size() != 0) {
        String firstLikeName = card.getLikes()
            .get(0)
            .getName();
        if (firstLikeName != null) {
          numberLikesOneLike.setText(spannableFactory.createColorSpan(itemView.getContext()
                  .getString(R.string.timeline_short_like_present_singular, firstLikeName),
              ContextCompat.getColor(itemView.getContext(), R.color.black_87_alpha),
              firstLikeName));
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
            numberLikesOneLike.setText(spannableFactory.createColorSpan(itemView.getContext()
                    .getString(R.string.timeline_short_like_present_singular, firstStoreName),
                ContextCompat.getColor(itemView.getContext(), R.color.black_87_alpha),
                firstStoreName));
            numberLikes.setVisibility(View.INVISIBLE);
            numberLikesOneLike.setVisibility(View.VISIBLE);
          } else {
            showNumberOfLikes(card.getLikesNumber());
          }
        }
      }
    } else {
      numberLikes.setVisibility(View.INVISIBLE);
      numberLikesOneLike.setVisibility(View.INVISIBLE);
    }
  }

  private void handleCommentsInformation(RatedRecommendation post) {
    if (post.getCommentsNumber() > 0) {
      numberComments.setVisibility(View.VISIBLE);
      numberComments.setText(itemView.getContext()
          .getResources()
          .getQuantityString(R.plurals.timeline_short_comment, (int) post.getCommentsNumber(),
              (int) post.getCommentsNumber()));
      socialCommentBar.setVisibility(View.VISIBLE);
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
      numberComments.setVisibility(View.INVISIBLE);
      socialCommentBar.setVisibility(View.GONE);
    }
  }
  /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */

  private void addUserToPreview(int i, UserTimeline user) {
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
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(user.getAvatar(), likeUserPreviewIcon);
      } else if (user.getStore()
          .getAvatar() != null) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(user.getStore()
                .getAvatar(), likeUserPreviewIcon);
      }
      likePreviewContainer.addView(likeUserPreviewView);
      marginOfTheNextLikePreview -= 20;
    }
  }

  private void showNumberOfLikes(long likesNumber) {
    numberLikes.setVisibility(View.VISIBLE);
    numberLikes.setText(itemView.getContext()
        .getString(R.string.timeline_short_like_present_plural, likesNumber)
        .toLowerCase());
    numberLikesOneLike.setVisibility(View.INVISIBLE);
  }
}
