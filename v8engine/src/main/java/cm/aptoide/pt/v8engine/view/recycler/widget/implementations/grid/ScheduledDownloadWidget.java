/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ScheduledDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ScheduledDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
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

    handleLoaderLogic(displayable);
  }

  private void handleLoaderLogic(ScheduledDownloadDisplayable displayable) {
    PermissionManager permissionManager = new PermissionManager();
    DownloadServiceHelper downloadServiceHelper =
        new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);

    //DownloadAccessor scheduledAccessor = AccessorFactory.getAccessorFor(Download.class);
    subscriptions.add(downloadServiceHelper.getAllDownloads()
        .flatMapIterable(downloads -> downloads)
        .filter(download -> isCurrentScheduled(displayable, download))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::changeLoaderStatus));
  }

  private boolean isCurrentScheduled(ScheduledDownloadDisplayable displayable, Download download) {
    return TextUtils.equals(download.getMd5(), displayable.getPojo().getMd5());
  }

  private void changeLoaderStatus(Download download) {
    if (download.getOverallDownloadStatus() == Download.PROGRESS
        || download.getOverallDownloadStatus() == Download.IN_QUEUE) {
      if (progressBarIsInstalling.getVisibility() != View.VISIBLE) {
        progressBarIsInstalling.setVisibility(View.VISIBLE);
      }
    } else {
      if (progressBarIsInstalling.getVisibility() == View.VISIBLE
          || download.getOverallDownloadStatus() == Download.PAUSED) {
        progressBarIsInstalling.setVisibility(View.GONE);
      }
    }
  }

  @Override public void onViewAttached() {
  }

  @Override public void onViewDetached() {
    if (subscriptions != null && !subscriptions.isUnsubscribed()) {
      subscriptions.unsubscribe();
    }
  }
}
