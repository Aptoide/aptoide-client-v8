package cm.aptoide.pt.v8engine.view.downloads.active;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.DownloadsPresenter;
import cm.aptoide.pt.v8engine.presenter.DownloadsView;
import cm.aptoide.pt.v8engine.view.recycler.RecyclerViewHolder;
import com.jakewharton.rxbinding.view.RxView;

public class ActiveDownloadViewHolder extends RecyclerViewHolder<DownloadsView.DownloadViewModel> {

  private final DownloadsPresenter presenter;

  private final TextView appName;
  private final ProgressBar progressBar;
  private final TextView downloadSpeedTv;
  private final TextView downloadProgressTv;
  private final ImageView appIcon;

  public ActiveDownloadViewHolder(View itemView, DownloadsPresenter presenter) {
    super(itemView);
    this.presenter = presenter;
    appName = (TextView) itemView.findViewById(R.id.app_name);
    downloadSpeedTv = (TextView) itemView.findViewById(R.id.speed);
    downloadProgressTv = (TextView) itemView.findViewById(R.id.progress);
    progressBar = (ProgressBar) itemView.findViewById(R.id.downloading_progress);
    appIcon = (ImageView) itemView.findViewById(R.id.app_icon);

    // button to pause / resume download
    ImageView pauseCancelButton = (ImageView) itemView.findViewById(R.id.pause_cancel_button);
    addSubscription(RxView.clicks(pauseCancelButton)
        .subscribe(click -> presenter.pauseInstall(itemView.getContext(), getViewModel()),
            throwable -> CrashReport.getInstance()
                .log(throwable)));
  }

  @Override protected void update(Context context, DownloadsView.DownloadViewModel download) {
    appName.setText(download.getAppName());
    if (!TextUtils.isEmpty(download.getIcon())) {
      ImageLoader.with(context)
          .load(download.getIcon(), appIcon);
    }
    if (download.getStatus() == DownloadsView.DownloadViewModel.Status.STAND_BY) {
      progressBar.setIndeterminate(true);
    } else {
      progressBar.setIndeterminate(false);
      progressBar.setProgress(download.getProgress());
    }
    downloadProgressTv.setText(String.format("%d%%", download.getProgress()));
    downloadSpeedTv.setText(
        String.valueOf(AptoideUtils.StringU.formatBytesToBits((long) download.getSpeed(), true)));
  }

  @Override public int getViewResource() {
    return R.layout.active_download_row_layout;
  }
}
