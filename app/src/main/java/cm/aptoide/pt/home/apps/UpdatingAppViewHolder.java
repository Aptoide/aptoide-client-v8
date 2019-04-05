package cm.aptoide.pt.home.apps;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 3/15/18.
 */

class UpdatingAppViewHolder extends AppsViewHolder {

  private TextView appName;
  private ImageView appIcon;
  private ProgressBar progressBar;
  private TextView updateState;
  private TextView updateProgress;
  private ImageView pauseButton;
  private PublishSubject<AppClick> pauseUpdate;

  private boolean isAppcUpgrade;

  public UpdatingAppViewHolder(View itemView, PublishSubject<AppClick> pauseUpdate) {
    this(itemView, pauseUpdate, false);
  }

  public UpdatingAppViewHolder(View itemView, PublishSubject<AppClick> pauseUpdate,
      boolean isAppcUpgrade) {
    super(itemView);

    appName = (TextView) itemView.findViewById(R.id.apps_updates_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.apps_updates_app_icon);
    progressBar = (ProgressBar) itemView.findViewById(R.id.apps_updates_progress_bar);
    updateState = (TextView) itemView.findViewById(R.id.apps_updates_update_state);
    updateProgress = (TextView) itemView.findViewById(R.id.apps_updates_progress_number);
    pauseButton = (ImageView) itemView.findViewById(R.id.apps_updates_pause_button);
    this.pauseUpdate = pauseUpdate;

    this.isAppcUpgrade = isAppcUpgrade;
  }

  @Override public void setApp(App app) {

    ImageLoader.with(itemView.getContext())
        .load(((UpdateApp) app).getIcon(), appIcon);
    appName.setText(((UpdateApp) app).getName());

    progressBar.setProgress(((UpdateApp) app).getProgress());
    updateProgress.setText(String.format("%d%%", ((UpdateApp) app).getProgress()));

    pauseButton.setOnClickListener(pause -> pauseUpdate.onNext(new AppClick(app,
        isAppcUpgrade ? AppClick.ClickType.APPC_UPGRADE_PAUSE : AppClick.ClickType.PAUSE_UPDATE)));
  }
}
