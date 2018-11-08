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
 * Created by filipegoncalves on 3/15/18.
 */

class StandByUpdateAppViewHolder extends AppsViewHolder {

  private TextView appName;
  private ImageView appIcon;
  private ProgressBar progressBar;
  private TextView updateProgress;
  private ImageView cancelButton;
  private ImageView resumeButton;
  private PublishSubject<AppClick> cancelUpdate;
  private TextView updateState;
  private LinearLayout downloadInteractButtonsLayout;
  private LinearLayout downloadAppInfoLayout;

  public StandByUpdateAppViewHolder(View itemView, PublishSubject<AppClick> cancelUpdate) {
    super(itemView);

    appName = (TextView) itemView.findViewById(R.id.apps_updates_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.apps_updates_app_icon);
    progressBar = (ProgressBar) itemView.findViewById(R.id.apps_updates_progress_bar);
    updateProgress = (TextView) itemView.findViewById(R.id.apps_updates_progress_number);
    cancelButton = (ImageView) itemView.findViewById(R.id.apps_updates_cancel_button);
    resumeButton = (ImageView) itemView.findViewById(R.id.apps_updates_resume_download);
    updateState = (TextView) itemView.findViewById(R.id.apps_updates_update_state);
    downloadInteractButtonsLayout = itemView.findViewById(R.id.apps_updates_standby_buttons_layout);
    downloadAppInfoLayout = itemView.findViewById(R.id.apps_updates_standby_app_info_layout);
    this.cancelUpdate = cancelUpdate;
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((UpdateApp) app).getIcon(), appIcon);
    appName.setText(((UpdateApp) app).getName());

    if (((UpdateApp) app).isIndeterminate()) {
      progressBar.setIndeterminate(true);
      downloadInteractButtonsLayout.setVisibility(View.GONE);

      adjustAppInfoWeightAndMargin(3, 56);
      adjustStandByDownloadButtonsWeight(0);

      cancelButton.setVisibility(View.GONE);
      resumeButton.setVisibility(View.GONE);
      updateProgress.setVisibility(View.GONE);
      updateState.setText(itemView.getResources()
          .getString(R.string.apps_short_updating));
    } else {

      adjustAppInfoWeightAndMargin(2, 8);
      adjustStandByDownloadButtonsWeight(1);

      downloadInteractButtonsLayout.setVisibility(View.VISIBLE);

      progressBar.setIndeterminate(false);
      progressBar.setProgress(((UpdateApp) app).getProgress());
      updateProgress.setText(String.format("%d%%", ((UpdateApp) app).getProgress()));
      cancelButton.setVisibility(View.VISIBLE);
      resumeButton.setVisibility(View.VISIBLE);
      updateProgress.setVisibility(View.VISIBLE);
      updateState.setText(itemView.getResources()
          .getString(R.string.apps_short_update_paused));
    }
    cancelButton.setOnClickListener(
        cancel -> cancelUpdate.onNext(new AppClick(app, AppClick.ClickType.CANCEL_UPDATE)));
    resumeButton.setOnClickListener(
        resume -> cancelUpdate.onNext(new AppClick(app, AppClick.ClickType.RESUME_UPDATE)));
  }

  private void adjustAppInfoWeightAndMargin(int weight, int margin) {
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
