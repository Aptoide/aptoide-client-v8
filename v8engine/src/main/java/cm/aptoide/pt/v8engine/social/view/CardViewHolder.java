package cm.aptoide.pt.v8engine.social.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.data.Article;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class CardViewHolder extends RecyclerView.ViewHolder {

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

  public CardViewHolder(View itemView, PublishSubject<CardTouchEvent> articleSubject,
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
  }

  public void setCard(Article card) {
    publisherName.setText(spannableFactory.createColorSpan(itemView.getContext()
            .getString(R.string.x_posted, card.getPublisherName()),
        ContextCompat.getColor(itemView.getContext(), R.color.black_87_alpha),
        card.getPublisherName()));
    articleTitle.setText(card.getTitle());
    relatedTo.setText(card.getRelatedApp()
        .getName());
    date.setText(dateCalculator.getTimeSinceDate(itemView.getContext(), card.getDate()));
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getPublisherAvatarURL(), publisherAvatar);
    ImageLoader.with(itemView.getContext())
        .loadWithCenterCrop(card.getThumbnailUrl(), articleThumbnail);

    articleThumbnail.setOnClickListener(
        click -> articleSubject.onNext(new CardTouchEvent(card, CardTouchEvent.Type.ARTICLE_BODY)));
    articleHeader.setOnClickListener(click -> articleSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.ARTICLE_HEADER)));
  }
}
