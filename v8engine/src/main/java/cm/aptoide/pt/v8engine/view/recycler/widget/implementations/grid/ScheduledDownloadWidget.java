/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ScheduledDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * created by SithEngineer
 */
@Displayables({ ScheduledDownloadDisplayable.class }) public class ScheduledDownloadWidget
    extends Widget<ScheduledDownloadDisplayable> {

  private ImageView appIcon;
  private TextView appName;
  private TextView appVersion;
  private CheckBox isSelected;
  private ProgressBar progressBarIsInstalling;
  private CompositeSubscription subscriptions;

  public ScheduledDownloadWidget(View itemView) {
    super(itemView);

    subscriptions = new CompositeSubscription();
  }

  @Override protected void assignViews(View itemView) {
    appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
    appName = (TextView) itemView.findViewById(R.id.app_name);
    appVersion = (TextView) itemView.findViewById(R.id.app_version);
    isSelected = (CheckBox) itemView.findViewById(R.id.is_selected);
    progressBarIsInstalling = (ProgressBar) itemView.findViewById(R.id.progress_bar_is_installing);
  }

  @Override public void bindView(ScheduledDownloadDisplayable displayable) {
    if (subscriptions == null || subscriptions.isUnsubscribed()) {
      subscriptions = new CompositeSubscription();
    }

    Scheduled scheduled = displayable.getPojo();
    ImageLoader.load(scheduled.getIcon(), appIcon);
    appName.setText(scheduled.getName());
    appVersion.setText(scheduled.getVersionName());

    isSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
      displayable.setSelected(isChecked);
    });

    isSelected.setChecked(displayable.isSelected());
    itemView.setOnClickListener(v -> isSelected.setChecked(!isSelected.isChecked()));

    displayable.setProgressBarIsInstalling(progressBarIsInstalling);
    displayable.setIsSelected(isSelected);

    isDownloading(displayable);
  }

  private void isDownloading(ScheduledDownloadDisplayable displayable) {
    AptoideDownloadManager aptoideDownloadManager = AptoideDownloadManager.getInstance();
    aptoideDownloadManager.initDownloadService(getContext());
    Installer installer = new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK);
    InstallManager installManager = new InstallManager(aptoideDownloadManager, installer,
        AccessorFactory.getAccessorFor(Download.class),
        AccessorFactory.getAccessorFor(Installed.class));

    Observable<Progress<Download>> installation =
        installManager.getInstallation(displayable.getPojo().getMd5());

    subscriptions.add(installation.map(
        downloadProgress -> installManager.isInstalling(downloadProgress)
            || installManager.isPending(downloadProgress))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((isDownloading) -> {
          displayable.updateUi(isDownloading);
        }, throwable -> {
          displayable.updateUi(false);
          throwable.printStackTrace();
        }));
  }

  @Override public void onViewAttached() {

  }

  @Override public void onViewDetached() {
    subscriptions.clear();
  }
}
