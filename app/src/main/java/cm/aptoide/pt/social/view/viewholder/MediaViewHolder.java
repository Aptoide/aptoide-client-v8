package cm.aptoide.pt.social.view.viewholder;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.social.data.Media;
import cm.aptoide.pt.social.data.Post;
import cm.aptoide.pt.social.data.PostPopupMenuBuilder;
import cm.aptoide.pt.social.data.SocialCardTouchEvent;
import cm.aptoide.pt.timeline.view.LikeButtonView;
import cm.aptoide.pt.util.DateCalculator;
import cm.aptoide.pt.view.spannable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class MediaViewHolder extends PostViewHolder<Media> {
  private final TextView publisherName;
  private final TextView date;
  private final ImageView publisherAvatar;
  private final TextView articleTitle;
  private final ImageView articleThumbnail;
  private final View articleHeader;
  private final TextView relatedTo;
  private final SpannableFactory spannableFactory;
  private final DateCalculator dateCalculator;
  private final ImageView playIcon;
  private final LikeButtonView likeButton;
  private final View likeView;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final TextView commentButton;
  private final TextView shareButton;
  private final View overflowMenu;

  public MediaViewHolder(View itemView, PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      DateCalculator dateCalculator, SpannableFactory spannableFactory) {
    super(itemView, cardTouchEventPublishSubject);
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;

    publisherAvatar = (ImageView) itemView.findViewById(R.id.card_image);
    publisherName = (TextView) itemView.findViewById(R.id.card_title);
    date = (TextView) itemView.findViewById(R.id.card_subtitle);
    articleTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    articleThumbnail = (ImageView) itemView.findViewById(R.id.featured_graphic);
    articleHeader = itemView.findViewById(R.id.displayable_social_timeline_article_header);
    relatedTo = (TextView) itemView.findViewById(R.id.app_name);
    playIcon = (ImageView) itemView.findViewById(R.id.play_button);
    likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    likeView = itemView.findViewById(R.id.social_like);
    commentButton = (TextView) itemView.findViewById(R.id.social_comment);
    shareButton = (TextView) itemView.findViewById(R.id.social_share);
    overflowMenu = itemView.findViewById(R.id.overflow_menu);
  }

  @Override public void setPost(Media media, int position) {
    if (media.getType()
        .equals(CardType.ARTICLE)) {
      playIcon.setVisibility(View.GONE);
    } else if (media.getType()
        .equals(CardType.VIDEO)) {
      playIcon.setVisibility(View.VISIBLE);
    }

    publisherName.setText(spannableFactory.createColorSpan(itemView.getContext()
            .getString(R.string.timeline_title_card_title_post_past_singular, media.getPublisherName()),
        ContextCompat.getColor(itemView.getContext(), R.color.black_87_alpha),
        media.getPublisherName()));
    articleTitle.setText(media.getMediaTitle());
    relatedTo.setText(spannableFactory.createStyleSpan(itemView.getContext()
        .getString(R.string.displayable_social_timeline_article_related_to, media.getRelatedApp()
            .getName()), Typeface.BOLD, media.getRelatedApp()
        .getName()));
    date.setText(dateCalculator.getTimeSinceDate(itemView.getContext(), media.getDate()));
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(media.getPublisherAvatarURL(), publisherAvatar);
    ImageLoader.with(itemView.getContext())
        .loadWithCenterCrop(media.getMediaThumbnailUrl(), articleThumbnail);

    setupOverflowMenu(media, position);
    handleCommentsInformation(media, position);

    articleThumbnail.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(media, position, CardTouchEvent.Type.BODY)));
    articleTitle.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(media, position, CardTouchEvent.Type.BODY)));
    articleHeader.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(media, position, CardTouchEvent.Type.HEADER)));

    this.commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new SocialCardTouchEvent(media, CardTouchEvent.Type.COMMENT, position)));
    this.shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(media, position, CardTouchEvent.Type.SHARE)));

    if (media.isLiked()) {
      if (media.isLikeFromClick()) {
        likeButton.setHeartState(true);
        media.setLikedFromClick(false);
      } else {
        likeButton.setHeartStateWithoutAnimation(true);
      }
    } else {
      likeButton.setHeartState(false);
    }

    this.likeView.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new SocialCardTouchEvent(media, CardTouchEvent.Type.LIKE, position)));
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
