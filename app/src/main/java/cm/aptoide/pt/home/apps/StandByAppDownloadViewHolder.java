package cm.aptoide.pt.home.apps;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
  private PublishSubject<AppClick> downloadAction;
  private LinearLayout downloadInteractButtonsLayout;
  private LinearLayout downloadAppInfoLayout;
  private TextView downloadState;

  public StandByAppDownloadViewHolder(View itemView, PublishSubject<AppClick> downloadAction) {
    super(itemView);

    appName = itemView.findViewById(R.id.apps_downloads_app_name);
    appIcon = itemView.findViewById(R.id.apps_downloads_icon);
    progressBar = itemView.findViewById(R.id.apps_downloads_progress_bar);
    downloadProgress = itemView.findViewById(R.id.apps_download_progress_number);
    cancelButton = itemView.findViewById(R.id.apps_download_cancel_button);
    resumeButton = itemView.findViewById(R.id.apps_download_resume_download);

    downloadInteractButtonsLayout =
        itemView.findViewById(R.id.apps_downloads_standby_buttons_layout);
    downloadAppInfoLayout = itemView.findViewById(R.id.apps_downloads_standby_app_info_layout);
    downloadState = itemView.findViewById(R.id.apps_downloads_download_state);
    this.downloadAction = downloadAction;
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((DownloadApp) app).getIcon(), appIcon);
    appName.setText(((DownloadApp) app).getName());

    if (((DownloadApp) app).isIndeterminate()) {
      progressBar.setIndeterminate(true);

      adjustStandByDownloadAppInfoWeightAndMargin(3, 56);
      adjustStandByDownloadButtonsWeight(0);

      cancelButton.setVisibility(View.GONE);
      resumeButton.setVisibility(View.GONE);
      downloadProgress.setVisibility(View.GONE);
      downloadState.setText(itemView.getResources()
          .getString(R.string.apps_short_downloading));
    } else {
      adjustStandByDownloadAppInfoWeightAndMargin(2, 8);
      adjustStandByDownloadButtonsWeight(1);
      downloadInteractButtonsLayout.setVisibility(View.VISIBLE);

      progressBar.setIndeterminate(false);
      progressBar.setProgress(((DownloadApp) app).getProgress());

      downloadProgress.setText(String.format("%d%%", ((DownloadApp) app).getProgress()));
      cancelButton.setVisibility(View.VISIBLE);
      resumeButton.setVisibility(View.VISIBLE);
      downloadProgress.setVisibility(View.VISIBLE);
      downloadState.setText(itemView.getResources()
          .getString(R.string.apps_short_download_paused));
    }
    itemView.setOnClickListener(
        __ -> downloadAction.onNext(new AppClick(app, AppClick.ClickType.UPDATE_CARD_CLICK)));
    cancelButton.setOnClickListener(
        cancel -> downloadAction.onNext(new AppClick(app, AppClick.ClickType.CANCEL_DOWNLOAD)));
    resumeButton.setOnClickListener(
        resume -> downloadAction.onNext(new AppClick(app, AppClick.ClickType.RESUME_DOWNLOAD)));
  }

  private void adjustStandByDownloadAppInfoWeightAndMargin(int weight, int margin) {
    LinearLayout.LayoutParams appInfoParams =
        (LinearLayout.LayoutParams) downloadAppInfoLayout.getLayoutParams();
    appInfoParams.weight = weight;
    appInfoParams.rightMargin = margin;
    downloadAppInfoLayout.setLayoutParams(appInfoParams);
  }

  private void adjustStandByDownloadButtonsWeight(int weight) {
    LinearLayout.LayoutParams buttonsLayoutParams =
        (LinearLayout.LayoutParams) downloadInteractButtonsLayout.getLayoutParams();
    buttonsLayoutParams.weight = weight;
    downloadInteractButtonsLayout.setLayoutParams(buttonsLayoutParams);
  }
}
