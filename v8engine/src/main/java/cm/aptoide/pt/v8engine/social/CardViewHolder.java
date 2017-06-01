package cm.aptoide.pt.v8engine.social;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 31/05/2017.
 */

class CardViewHolder extends RecyclerView.ViewHolder {

  private final PublishSubject<Article> articleSubject;
  private final TextView publisherName;
  private final TextView date;
  private final ImageView publisherAvatar;
  private final TextView articleTitle;
  private final ImageView articleThumbnail;
  private final View articleHeader;
  private final TextView relatedTo;
  private final DateCalculator dateCalculator;

  public CardViewHolder(View itemView, PublishSubject<Article> articleSubject,
      DateCalculator dateCalculator) {
    super(itemView);
    this.articleSubject = articleSubject;
    this.dateCalculator = dateCalculator;

    publisherAvatar = (ImageView) itemView.findViewById(R.id.card_image);
    publisherName = (TextView) itemView.findViewById(R.id.card_title);
    date = (TextView) itemView.findViewById(R.id.card_subtitle);
    articleTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    articleThumbnail = (ImageView) itemView.findViewById(R.id.featured_graphic);
    articleHeader = itemView.findViewById(R.id.displayable_social_timeline_article_header);
    relatedTo = (TextView) itemView.findViewById(R.id.app_name);
  }

  public void setCard(Article card) {
    publisherName.setText(card.getPublisherName());
    articleTitle.setText(card.getTitle());
    relatedTo.setText(card.getRelatedApp()
        .getName());
    date.setText(dateCalculator.getTimeSinceDate(itemView.getContext(), card.getDate()));
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getPublisherAvatarURL(), publisherAvatar);
    ImageLoader.with(itemView.getContext())
        .loadWithCenterCrop(card.getThumbnailUrl(), articleThumbnail);
  }
}
