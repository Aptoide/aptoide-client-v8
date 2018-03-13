package cm.aptoide.pt.home.apps;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 3/12/18.
 */

class CompletedAppDownloadViewHolder extends AppsViewHolder {

  private TextView appName;
  private ImageView appIcon;
  private PublishSubject<App> installApp;
  private CardView card;

  public CompletedAppDownloadViewHolder(View itemView, PublishSubject<App> installApp) {
    super(itemView);
    appName = (TextView) itemView.findViewById(R.id.app_downloads_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.app_downloads_icon);
    card = (CardView) itemView.findViewById(R.id.app_completed_download_card);
    this.installApp = installApp;
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((DownloadApp) app).getIcon(), appIcon);
    appName.setText(((DownloadApp) app).getAppName());
    card.setOnClickListener(install -> installApp.onNext(app));
  }
}
