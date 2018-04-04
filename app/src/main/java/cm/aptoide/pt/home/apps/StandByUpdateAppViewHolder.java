package cm.aptoide.pt.home.apps;

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

class StandByUpdateAppViewHolder extends AppsViewHolder {

  private TextView appName;
  private ImageView appIcon;
  private ProgressBar progressBar;
  private TextView updateProgress;
  private ImageView cancelButton;
  private ImageView resumeButton;
  private PublishSubject<AppClick> cancelUpdate;

  public StandByUpdateAppViewHolder(View itemView, PublishSubject<AppClick> cancelUpdate) {
    super(itemView);

    appName = (TextView) itemView.findViewById(R.id.apps_updates_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.apps_updates_app_icon);
    progressBar = (ProgressBar) itemView.findViewById(R.id.apps_updates_progress_bar);
    updateProgress = (TextView) itemView.findViewById(R.id.apps_updates_progress_number);
    cancelButton = (ImageView) itemView.findViewById(R.id.apps_updates_cancel_button);
    resumeButton = (ImageView) itemView.findViewById(R.id.apps_updates_resume_download);
    this.cancelUpdate = cancelUpdate;
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((UpdateApp) app).getIcon(), appIcon);
    appName.setText(((UpdateApp) app).getName());
    progressBar.setProgress(((UpdateApp) app).getProgress());
    updateProgress.setText(String.format("%d%%", ((UpdateApp) app).getProgress()));
    cancelButton.setOnClickListener(
        cancel -> cancelUpdate.onNext(new AppClick(app, AppClick.ClickType.CANCEL_UPDATE)));
    resumeButton.setOnClickListener(
        resume -> cancelUpdate.onNext(new AppClick(app, AppClick.ClickType.RESUME_UPDATE)));
  }
}
