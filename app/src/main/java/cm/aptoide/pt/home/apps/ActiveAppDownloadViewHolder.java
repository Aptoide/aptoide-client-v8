package cm.aptoide.pt.home.apps;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 3/12/18.
 */

class ActiveAppDownloadViewHolder extends AppsViewHolder {

  private TextView appName;
  private ImageView appIcon;
  private ProgressBar progressBar;
  private TextView downloadProgress;
  private ImageView pauseButton;
  private PublishSubject<AppClick> pauseDownload;

  public ActiveAppDownloadViewHolder(View itemView, PublishSubject<AppClick> pauseDownload) {
    super(itemView);

    appName = (TextView) itemView.findViewById(R.id.apps_downloads_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.apps_downloads_icon);
    progressBar = (ProgressBar) itemView.findViewById(R.id.apps_downloads_progress_bar);
    downloadProgress = (TextView) itemView.findViewById(R.id.apps_download_progress_number);
    pauseButton = (ImageView) itemView.findViewById(R.id.apps_download_pause_button);
    this.pauseDownload = pauseDownload;
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((DownloadApp) app).getIcon(), appIcon);
    appName.setText(((DownloadApp) app).getAppName());

    progressBar.setProgress(((DownloadApp) app).getProgress());
    downloadProgress.setText(String.format("%d%%", ((DownloadApp) app).getProgress()));

    progressBar.setIndeterminate(((DownloadApp) app).isIndeterminate());

    pauseButton.setOnClickListener(
        pause -> pauseDownload.onNext(new AppClick(app, AppClick.ClickType.PAUSE_DOWNLOAD)));
  }
}
