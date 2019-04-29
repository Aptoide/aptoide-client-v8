package cm.aptoide.pt.home.apps;

import android.support.v4.content.ContextCompat;
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
  private PublishSubject<AppClick> updateAction;
  private TextView updateState;
  private LinearLayout downloadInteractButtonsLayout;
  private LinearLayout downloadAppInfoLayout;

  private boolean isAppcUpgrade;

  public StandByUpdateAppViewHolder(View itemView, PublishSubject<AppClick> updateAction) {
    this(itemView, updateAction, false);
  }

  public StandByUpdateAppViewHolder(View itemView, PublishSubject<AppClick> updateAction,
      boolean isAppcUpgrade) {
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
    this.updateAction = updateAction;

    this.isAppcUpgrade = isAppcUpgrade;
    if (isAppcUpgrade) {
      progressBar.setProgressDrawable(
          ContextCompat.getDrawable(itemView.getContext(), R.drawable.appc_progress));
    }
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((UpdateApp) app).getIcon(), appIcon);
    appName.setText(((UpdateApp) app).getName());

    if (((UpdateApp) app).isIndeterminate()) {
      progressBar.setIndeterminate(true);
      downloadInteractButtonsLayout.setVisibility(View.GONE);

      adjustStandByDownloadAppInfoWeightAndMargin(3, 56);
      adjustStandByDownloadButtonsWeight(0);

      cancelButton.setVisibility(View.GONE);
      resumeButton.setVisibility(View.GONE);
      updateProgress.setVisibility(View.GONE);
      updateState.setText(itemView.getResources()
          .getString(R.string.apps_short_updating));
    } else {

      adjustStandByDownloadAppInfoWeightAndMargin(2, 8);
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
    cancelButton.setOnClickListener(cancel -> updateAction.onNext(new AppClick(app,
        isAppcUpgrade ? AppClick.ClickType.APPC_UPGRADE_CANCEL
            : AppClick.ClickType.CANCEL_UPDATE)));
    resumeButton.setOnClickListener(resume -> updateAction.onNext(new AppClick(app,
        isAppcUpgrade ? AppClick.ClickType.APPC_UPGRADE_RESUME
            : AppClick.ClickType.RESUME_UPDATE)));
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
