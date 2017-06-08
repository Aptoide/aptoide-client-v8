package cm.aptoide.pt.v8engine.social.view;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
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

public class ArticleViewHolder extends CardViewHolder<Article> {

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

  public ArticleViewHolder(View itemView, PublishSubject<CardTouchEvent> articleSubject,
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

  @Override public void setCard(Article article) {
    publisherName.setText(spannableFactory.createColorSpan(itemView.getContext()
            .getString(R.string.x_posted, article.getPublisherName()),
        ContextCompat.getColor(itemView.getContext(), R.color.black_87_alpha),
        article.getPublisherName()));
    articleTitle.setText(article.getTitle());
    relatedTo.setText(spannableFactory.createStyleSpan(itemView.getContext()
        .getString(R.string.displayable_social_timeline_article_related_to, article.getRelatedApp()
            .getName()), Typeface.BOLD, article.getRelatedApp()
        .getName()));
    date.setText(dateCalculator.getTimeSinceDate(itemView.getContext(), article.getDate()));
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(article.getPublisherAvatarURL(), publisherAvatar);
    ImageLoader.with(itemView.getContext())
        .loadWithCenterCrop(article.getThumbnailUrl(), articleThumbnail);

    articleThumbnail.setOnClickListener(click -> articleSubject.onNext(
        new CardTouchEvent(article, CardTouchEvent.Type.ARTICLE_BODY)));
    articleHeader.setOnClickListener(click -> articleSubject.onNext(
        new CardTouchEvent(article, CardTouchEvent.Type.ARTICLE_HEADER)));
  }
}
