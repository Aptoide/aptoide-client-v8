package cm.aptoide.pt.v8engine.social.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.v8engine.social.data.Card;

/**
 * Created by jdandrade on 08/06/2017.
 */

public abstract class CardViewHolder<T extends Card> extends RecyclerView.ViewHolder {
  public CardViewHolder(View itemView) {
    super(itemView);
  }

  abstract void setCard(T card);
}
