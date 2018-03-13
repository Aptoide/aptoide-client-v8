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

class StandByAppDownloadViewHolder extends AppsViewHolder {

  private TextView appName;
  private ImageView appIcon;
  private ProgressBar progressBar;

  private TextView downloadProgress;
  private ImageView cancelButton;
  private ImageView resumeButton;
  private PublishSubject<App> cancelDownload;
  private PublishSubject<App> resumeDownload;

  public StandByAppDownloadViewHolder(View itemView, PublishSubject<App> cancelDownload,
      PublishSubject<App> resumeDownload) {
    super(itemView);

    appName = (TextView) itemView.findViewById(R.id.app_downloads_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.app_downloads_icon);
    progressBar = (ProgressBar) itemView.findViewById(R.id.app_downloads_progress_bar);
    downloadProgress = (TextView) itemView.findViewById(R.id.app_download_progress_number);
    cancelButton = (ImageView) itemView.findViewById(R.id.app_download_cancel_button);
    resumeButton = (ImageView) itemView.findViewById(R.id.app_download_resume_download);
    this.cancelDownload = cancelDownload;
    this.resumeDownload = resumeDownload;
  }

  @Override public void setApp(App app) {

    ImageLoader.with(itemView.getContext())
        .load(((DownloadApp) app).getIcon(), appIcon);
    appName.setText(((DownloadApp) app).getAppName());

    progressBar.setProgress(((DownloadApp) app).getProgress());
    downloadProgress.setText(String.format("%d%%", ((DownloadApp) app).getProgress()));

    cancelButton.setOnClickListener(cancel -> cancelDownload.onNext(app));
    resumeButton.setOnClickListener(resume -> resumeDownload.onNext(app));
  }
}
