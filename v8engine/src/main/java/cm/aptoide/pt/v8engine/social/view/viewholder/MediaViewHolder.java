package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.CardType;
import cm.aptoide.pt.v8engine.social.data.Media;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class MediaViewHolder extends CardViewHolder<Media> {

  private final PublishSubject<CardTouchEvent> articleSubject;
  private final TextView publisherName;
  private final TextView date;
  private final ImageView publisherAvatar;
  private final TextView articleTitle;
  private final ImageView articleThumbnail;
  private final View articleHeader;
  private final TextView relatedTo;
  private final SpannableFactory spannableFactory;
  private final DateCalculator dateCalculator;
  private final ImageView cardIcon;
  private final ImageView playIcon;

  public MediaViewHolder(View itemView, PublishSubject<CardTouchEvent> articleSubject,
      DateCalculator dateCalculator, SpannableFactory spannableFactory) {
    super(itemView);
    this.articleSubject = articleSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;

    publisherAvatar = (ImageView) itemView.findViewById(R.id.card_image);
    publisherName = (TextView) itemView.findViewById(R.id.card_title);
    date = (TextView) itemView.findViewById(R.id.card_subtitle);
    articleTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    articleThumbnail = (ImageView) itemView.findViewById(R.id.featured_graphic);
    articleHeader = itemView.findViewById(R.id.displayable_social_timeline_article_header);
    relatedTo = (TextView) itemView.findViewById(R.id.app_name);
    cardIcon = (ImageView) itemView.findViewById(R.id.timeline_header_card_type_icon);
    playIcon = (ImageView) itemView.findViewById(R.id.play_button);
  }

  @Override public void setCard(Media media, int position) {
    if (media.getType()
        .equals(CardType.ARTICLE)) {
      setIcon(R.drawable.appstimeline_article_icon);
      playIcon.setVisibility(View.GONE);
    } else if (media.getType()
        .equals(CardType.VIDEO)) {
      setIcon(R.drawable.appstimeline_video_play_icon);
      playIcon.setVisibility(View.VISIBLE);
    }
    publisherName.setText(spannableFactory.createColorSpan(itemView.getContext()
            .getString(R.string.x_posted, media.getPublisherName()),
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

    articleThumbnail.setOnClickListener(
        click -> articleSubject.onNext(new CardTouchEvent(media, CardTouchEvent.Type.BODY)));
    articleHeader.setOnClickListener(
        click -> articleSubject.onNext(new CardTouchEvent(media, CardTouchEvent.Type.HEADER)));
  }

  private void setIcon(int drawableId) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      cardIcon.setImageDrawable(itemView.getContext()
          .getDrawable(drawableId));
    } else {
      cardIcon.setImageDrawable(itemView.getResources()
          .getDrawable(drawableId));
    }
  }
}
