package cm.aptoide.pt.view.app;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by trinkes on 19/10/2017.
 */

public abstract class ListStoreAppViewHolder extends RecyclerView.ViewHolder {
  public ListStoreAppViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setApp(Application app);
}
