package cm.aptoide.pt.home.apps;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by filipegoncalves on 3/8/18.
 */

public abstract class AppsViewHolder extends RecyclerView.ViewHolder {

  public AppsViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setApp(App app);
}
