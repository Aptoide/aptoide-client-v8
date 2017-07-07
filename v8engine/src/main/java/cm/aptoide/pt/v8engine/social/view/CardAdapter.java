package cm.aptoide.pt.v8engine.social.view;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.social.data.CardViewHolderFactory;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.social.view.viewholder.CardViewHolder;
import java.util.List;

/**
 * Created by jdandrade on 2/06/17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {

  private final CardViewHolderFactory cardViewHolderFactory;
  private List<Post> cards;
  private ProgressCard progressCard;

  public CardAdapter(List<Post> cards, CardViewHolderFactory cardViewHolderFactory,
      ProgressCard progressCard) {
    this.cards = cards;
    this.cardViewHolderFactory = cardViewHolderFactory;
    this.progressCard = progressCard;
  }

  @Override public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return cardViewHolderFactory.createViewHolder(viewType, parent);
  }

  @Override public void onBindViewHolder(CardViewHolder holder, int position) {
    holder.setCard(cards.get(position), position);
  }

  @Override public int getItemViewType(int position) {
    return cards.get(position)
        .getType()
        .ordinal();
  }

  @Override public int getItemCount() {
    return this.cards.size();
  }

  public void updateCards(List<Post> cards) {
    this.cards = cards;
    notifyDataSetChanged();
  }

  public void addCards(List<Post> cards) {
    this.cards.addAll(cards);
    notifyDataSetChanged();
  }

  public void addLoadMoreProgress() {
    this.cards.add(progressCard);
    notifyDataSetChanged();
  }

  public void removeLoadMoreProgress() {
    this.cards.remove(progressCard);
    notifyDataSetChanged();
  }

  public void updateCard(Post card, int cardPosition) {
    cards.set(cardPosition, card);
    notifyItemChanged(cardPosition);
  }
}
