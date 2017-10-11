package cm.aptoide.pt.social.view.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.Post;
import cm.aptoide.pt.social.data.PostPopupMenuBuilder;
import cm.aptoide.pt.social.data.Recommendation;
import cm.aptoide.pt.social.data.SocialCardTouchEvent;
import cm.aptoide.pt.timeline.view.LikeButtonView;
import cm.aptoide.pt.util.DateCalculator;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.spannable.SpannableFactory;
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
  private final String marketName;
  private final View overflowMenu;

  public RecommendationViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, String marketName) {
    super(view, cardTouchEventPublishSubject);
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
    this.marketName = marketName;
    this.overflowMenu = itemView.findViewById(R.id.overflow_menu);
  }

  @Override public void setPost(Recommendation post, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(post.getPublisherDrawableId(), headerIcon);
    this.headerTitle.setText(getStyledTitle(itemView.getContext(), post));
    this.headerSubTitle.setText(
        getTimeSinceRecommendation(itemView.getContext(), post.getTimestamp()));
    ImageLoader.with(itemView.getContext())
        .load(post.getAppIcon(), appIcon);
    this.appName.setText(post.getAppName());
    this.relatedToText.setText(itemView.getContext()
        .getString(R.string.timeline_short_related_to)
        .toLowerCase());
    this.relatedToApp.setText(post.getRelatedToAppName());

    this.getAppButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, position, CardTouchEvent.Type.BODY)));
    this.appIcon.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, position, CardTouchEvent.Type.BODY)));
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

    setupOverflowMenu(post, position);
    handleCommentsInformation(post, position);

    this.like.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new SocialCardTouchEvent(post, CardTouchEvent.Type.LIKE, position)));

    this.commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new SocialCardTouchEvent(post, CardTouchEvent.Type.COMMENT, position)));
    this.shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, position, CardTouchEvent.Type.SHARE)));
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
        R.string.timeline_title_card_title_recommend_present_singular, resources, marketName);
  }

  private void setupOverflowMenu(Post post, int position) {
    overflowMenu.setOnClickListener(view -> {
      PopupMenu popupMenu = new PostPopupMenuBuilder().prepMenu(itemView.getContext(), overflowMenu)
          .addReportAbuse(menuItem -> {
            cardTouchEventPublishSubject.onNext(
                new CardTouchEvent(post, position, CardTouchEvent.Type.REPORT_ABUSE));
            return false;
          })
          .getPopupMenu();
      popupMenu.show();
    });
  }
}
