package cm.aptoide.pt.view.app;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.home.HomeBundle;

public abstract class AppViewHolder extends RecyclerView.ViewHolder {
  public AppViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setApp(Application app, HomeBundle homeBundle, int position,
      int bundlePosition);
}
