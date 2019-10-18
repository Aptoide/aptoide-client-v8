package cm.aptoide.pt.home.bundles.editorial;

import android.view.View;
import androidx.annotation.NonNull;
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder;
import cm.aptoide.pt.home.bundles.base.HomeBundle;

public abstract class EditorialViewHolder extends AppBundleViewHolder {
  public EditorialViewHolder(@NonNull View itemView) {
    super(itemView);
  }

  public abstract void setBundle(HomeBundle homeBundle, int position);
}
