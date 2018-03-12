package cm.aptoide.pt.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jdandrade on 07/03/2018.
 */

abstract class AppBundleViewHolder extends RecyclerView.ViewHolder {
  public AppBundleViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setBundle(AppBundle appBundle, int position);
}
