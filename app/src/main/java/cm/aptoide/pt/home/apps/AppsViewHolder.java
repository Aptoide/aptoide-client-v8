package cm.aptoide.pt.home.apps;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by filipegoncalves on 3/8/18.
 */

public abstract class AppsViewHolder extends RecyclerView.ViewHolder {

  public AppsViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setApp(App app);
}
