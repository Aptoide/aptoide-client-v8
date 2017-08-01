package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.LikeCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.Recommendation;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 19/06/2017.
 */

public class RecommendationViewHolder extends PostViewHolder<Recommendation> {
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final TextView headerSubTitle;
  private final ImageView appIcon;
  private final TextView appName;
  private final TextView relatedToText;
  private final TextView relatedToApp;
  private final Button getAppButton;
  private final LinearLayout like;
  private final LikeButtonView likeButton;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final TextView commentButton;
  private final TextView shareButton;

  public RecommendationViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory) {
    super(view);
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.headerIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_icon);
    this.headerTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
    this.headerSubTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_subtitle);
    this.appIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    this.appName =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
    this.relatedToText =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
    this.relatedToApp =
        (TextView) view.findViewById(R.id.social_timeline_recommendation_card_related_to_app);
    this.getAppButton =
        (Button) view.findViewById(R.id.displayable_social_timeline_recommendation_get_app_button);
    this.likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    this.like = (LinearLayout) itemView.findViewById(R.id.social_like);
    this.commentButton = (TextView) view.findViewById(R.id.social_comment);
    this.shareButton = (TextView) view.findViewById(R.id.social_share);
  }

  @Override public void setPost(Recommendation card, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getPublisherDrawableId(), headerIcon);
    this.headerTitle.setText(getStyledTitle(itemView.getContext(), card));
    this.headerSubTitle.setText(
        getTimeSinceRecommendation(itemView.getContext(), card.getTimestamp()));
    ImageLoader.with(itemView.getContext())
        .load(card.getAppIcon(), appIcon);
    this.appName.setText(card.getAppName());
    this.relatedToText.setText(itemView.getContext()
        .getString(R.string.timeline_short_related_to)
        .toLowerCase());
    this.relatedToApp.setText(card.getRelatedToAppName());

    this.getAppButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
    this.appIcon.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
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

  private Spannable getStyledTitle(Context context, Recommendation card) {
    return spannableFactory.createColorSpan(getTitle(context.getResources()),
        ContextCompat.getColor(context, R.color.appstimeline_recommends_title),
        card.getPublisherName());
  }

  public String getTimeSinceRecommendation(Context context, Date timestamp) {
    return dateCalculator.getTimeSinceDate(context, timestamp);
  }

  public String getTitle(Resources resources) {
    return AptoideUtils.StringU.getFormattedString(
        R.string.timeline_title_card_title_recommend_present_singular, resources,
        Application.getConfiguration()
            .getMarketName());
  }
}
