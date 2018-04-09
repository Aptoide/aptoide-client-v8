package cm.aptoide.pt.home.apps;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 3/12/18.
 */

class ErrorAppDownloadViewHolder extends AppsViewHolder {

  private TextView appName;
  private ImageView appIcon;
  private ImageView retryButton;
  private PublishSubject<AppClick> retryDownload;

  public ErrorAppDownloadViewHolder(View itemView, PublishSubject<AppClick> retryDownload) {
    super(itemView);
    appName = (TextView) itemView.findViewById(R.id.apps_downloads_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.apps_downloads_icon);
    retryButton = (ImageView) itemView.findViewById(R.id.apps_download_retry_button);
    this.retryDownload = retryDownload;
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((DownloadApp) app).getIcon(), appIcon);
    appName.setText(((DownloadApp) app).getAppName());
    retryButton.setOnClickListener(
        install -> retryDownload.onNext(new AppClick(app, AppClick.ClickType.RETRY_DOWNLOAD)));
  }
}
