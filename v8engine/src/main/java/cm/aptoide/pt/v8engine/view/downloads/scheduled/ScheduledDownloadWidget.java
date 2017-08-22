/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.downloads.scheduled;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created
 */
public class ScheduledDownloadWidget extends Widget<ScheduledDownloadDisplayable> {

  private ImageView appIcon;
  private TextView appName;
  private TextView appVersion;
  private CheckBox isSelected;
  private ProgressBar progressBarIsInstalling;
  private InstallManager installManager;

  public ScheduledDownloadWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
    appName = (TextView) itemView.findViewById(R.id.app_name);
    appVersion = (TextView) itemView.findViewById(R.id.app_version);
    isSelected = (CheckBox) itemView.findViewById(R.id.is_selected);
    progressBarIsInstalling = (ProgressBar) itemView.findViewById(R.id.progress_bar_is_installing);
  }

  @Override public void bindView(ScheduledDownloadDisplayable displayable) {
    installManager = ((V8Engine) getContext().getApplicationContext()).getInstallManager(
        InstallerFactory.ROLLBACK);
    Scheduled scheduled = displayable.getPojo();
    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .load(scheduled.getIcon(), appIcon);
    appName.setText(scheduled.getName());
    appVersion.setText(scheduled.getVersionName());

    isSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
      displayable.setSelected(isChecked);
    });

    isSelected.setChecked(displayable.isSelected());
    itemView.setOnClickListener(v -> isSelected.setChecked(!isSelected.isChecked()));

    compositeSubscription.add(displayable.isDownloading()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isDownloading -> updateUi(isDownloading), throwable -> CrashReport.getInstance()
            .log(throwable)));
  }

  private void updateUi(boolean isDownloading) {
    if (isSelected != null) {
      isSelected.setVisibility(isDownloading ? View.GONE : View.VISIBLE);
    }

    if (progressBarIsInstalling != null) {
      progressBarIsInstalling.setVisibility(isDownloading ? View.VISIBLE : View.GONE);
    }
  }
}
