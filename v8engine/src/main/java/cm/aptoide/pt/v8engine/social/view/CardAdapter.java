package cm.aptoide.pt.v8engine.social.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.data.Article;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 2/06/17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {

  private List<Article> cards;
  private PublishSubject<CardTouchEvent> articleSubject;
  private DateCalculator dateCalculator;
  private SpannableFactory spannableFactory;

  public CardAdapter(List<Article> cards, PublishSubject<CardTouchEvent> articleSubject,
      DateCalculator dateCalculator, SpannableFactory spannableFactory) {
    this.cards = cards;
    this.articleSubject = articleSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
  }

  @Override public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new CardViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(viewType, parent, false), articleSubject, dateCalculator, spannableFactory);
  }

  @Override public void onBindViewHolder(CardViewHolder holder, int position) {
    holder.setCard(cards.get(position));
  }

  @Override public int getItemViewType(int position) {
    return R.layout.timeline_article_item;
  }

  @Override public int getItemCount() {
    return cards.size();
  }

  public void updateCards(List<Article> cards) {
    this.cards = cards;
    notifyDataSetChanged();
  }

  public void addCards(List<Article> cards) {
    this.cards.addAll(cards);
    notifyDataSetChanged();
  }
}
