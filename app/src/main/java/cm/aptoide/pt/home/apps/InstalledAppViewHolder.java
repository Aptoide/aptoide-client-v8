package cm.aptoide.pt.home.apps;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;

/**
 * Created by filipegoncalves on 3/9/18.
 */

class InstalledAppViewHolder extends AppsViewHolder {

  private TextView appName;
  private TextView appVersion;
  private ImageView appIcon;

  public InstalledAppViewHolder(View itemView) {
    super(itemView);

    appVersion = (TextView) itemView.findViewById(R.id.app_installed_app_version);
    appName = (TextView) itemView.findViewById(R.id.app_installed_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.app_installed_icon);
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((InstalledApp) app).getIcon(), appIcon);
    appName.setText(((InstalledApp) app).getAppName());
    appVersion.setText(((InstalledApp) app).getVersion());
  }
}
