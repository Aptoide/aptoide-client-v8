package cm.aptoide.pt.v8engine.social.data;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.view.ArticleViewHolder;
import cm.aptoide.pt.v8engine.social.view.CardViewHolder;
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
        return new ArticleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_article_item, parent, false), articleSubject, dateCalculator,
            spannableFactory);
      default:
        return new ArticleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_article_item, parent, false), articleSubject, dateCalculator,
            spannableFactory);
    }
  }
}
