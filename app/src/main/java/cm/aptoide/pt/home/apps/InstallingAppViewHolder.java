package cm.aptoide.pt.home.apps;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;

class InstallingAppViewHolder extends AppsViewHolder {
  private TextView appName;
  private ImageView appIcon;

  InstallingAppViewHolder(View view) {
    super(view);
    appName = itemView.findViewById(R.id.apps_installing_app_name);
    appIcon = itemView.findViewById(R.id.apps_installing_icon);
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((UpdateApp) app).getIcon(), appIcon);
    appName.setText(((UpdateApp) app).getName());
  }
}
