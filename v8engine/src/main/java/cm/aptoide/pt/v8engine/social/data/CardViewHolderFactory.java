package cm.aptoide.pt.v8engine.social.data;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.view.viewholder.AppUpdateViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.CardViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.MediaViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.ProgressViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.RecommendationViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.StoreLatestAppsViewHolder;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 08/06/2017.
 */

public class CardViewHolderFactory {

  private final PublishSubject<CardTouchEvent> articleSubject;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;

  public CardViewHolderFactory(PublishSubject<CardTouchEvent> articleSubject,
      DateCalculator dateCalculator, SpannableFactory spannableFactory) {
    this.articleSubject = articleSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
  }

  public CardViewHolder createViewHolder(int cardViewType, ViewGroup parent) {
    if (cardViewType > CardType.values().length) {
      throw new IllegalStateException("Wrong card type " + cardViewType);
    }
    CardType cardType = CardType.values()[cardViewType];
    switch (cardType) {
      case ARTICLE:
      case VIDEO:
        return new MediaViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_article_item, parent, false), articleSubject, dateCalculator,
            spannableFactory);
      case RECOMMENDATION:
        return new RecommendationViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_recommendation_item, parent, false), articleSubject,
            dateCalculator, spannableFactory);
      case STORE:
        return new StoreLatestAppsViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_store_item, parent, false), articleSubject, dateCalculator,
            spannableFactory);
      case UPDATE:
        return new AppUpdateViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_update_item, parent, false), articleSubject, dateCalculator,
            spannableFactory);
      case PROGRESS:
        return new ProgressViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_progress_item, parent, false));
      default:
        throw new IllegalStateException("Wrong cardType" + cardType.name());
    }
  }
}
