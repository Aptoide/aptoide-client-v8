package cm.aptoide.pt.home.apps.seemore;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.home.apps.App;

/**
 * Created by filipegoncalves on 3/8/18.
 */

public abstract class AppsViewHolder extends RecyclerView.ViewHolder {

  public AppsViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setApp(App app);
}
