package cm.aptoide.pt.search.view.item;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SearchResultItemView<T> extends RecyclerView.ViewHolder {

  SearchResultItemView(View itemView) {
    super(itemView);
  }

  public void setup(T item) {
  }

  public void prepareToRecycle() {
  }
}
