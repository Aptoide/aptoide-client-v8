package cm.aptoide.pt.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ItemView<T> extends RecyclerView.ViewHolder {

  public ItemView(View itemView) {
    super(itemView);
  }

  public abstract void setup(T item);
}
