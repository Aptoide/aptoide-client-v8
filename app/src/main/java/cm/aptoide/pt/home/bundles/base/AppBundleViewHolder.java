package cm.aptoide.pt.home.bundles.base;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jdandrade on 07/03/2018.
 */

public abstract class AppBundleViewHolder extends RecyclerView.ViewHolder {
  public AppBundleViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setBundle(HomeBundle homeBundle, int position);
}
