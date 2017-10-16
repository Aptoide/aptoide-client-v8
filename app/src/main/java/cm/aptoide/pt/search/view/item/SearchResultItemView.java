package cm.aptoide.pt.search.view.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class SearchResultItemView<T> extends RecyclerView.ViewHolder {

  public SearchResultItemView(View itemView) {
    super(itemView);
  }

  public void setup(T item) {
  }
}
