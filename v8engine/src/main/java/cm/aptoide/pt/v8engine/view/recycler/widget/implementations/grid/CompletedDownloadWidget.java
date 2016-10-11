/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 7/18/16.
 */
@Displayables({ CompletedDownloadDisplayable.class }) public class CompletedDownloadWidget
    extends Widget<CompletedDownloadDisplayable> {

  private static final String TAG = CompletedDownloadWidget.class.getSimpleName();

  private TextView appName;
  private ImageView appIcon;
  private TextView status;
  private ImageView resumeDownloadButton;
  private ImageView cancelDownloadButton;
  private CompositeSubscription subscriptions;
  private Progress<Download> downloadProgress;
  private CompletedDownloadDisplayable displayable;

  public CompletedDownloadWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
    appName = (TextView) itemView.findViewById(R.id.app_name);
    status = (TextView) itemView.findViewById(R.id.speed);
    resumeDownloadButton = (ImageView) itemView.findViewById(R.id.resume_download);
    cancelDownloadButton = (ImageView) itemView.findViewById(R.id.pause_cancel_button);
  }

  @Override public void bindView(CompletedDownloadDisplayable displayable) {
    this.displayable = displayable;
    downloadProgress = displayable.getPojo();
    appName.setText(downloadProgress.getRequest().getAppName());
    if (!TextUtils.isEmpty(downloadProgress.getRequest().getIcon())) {
      ImageLoader.load(downloadProgress.getRequest().getIcon(), appIcon);
    }
    status.setText(downloadProgress.getRequest().getStatusName(itemView.getContext()));
  }

  @Override public void onViewAttached() {
    if (subscriptions == null || subscriptions.isUnsubscribed()) {
      subscriptions = new CompositeSubscription();

      subscriptions.add(RxView.clicks(itemView)
          .flatMap(click -> displayable.downloadStatus()
              .filter(status -> status == Download.COMPLETED)
              .flatMap(status -> displayable.installOrOpenDownload(getContext(),
                  (PermissionRequest) getContext())))
          .retry()
          .subscribe(success -> {
          }, throwable -> throwable.printStackTrace()));

      subscriptions.add(RxView.clicks(resumeDownloadButton)
          .flatMap(click -> displayable.downloadStatus()
              .filter(status -> status == Download.PAUSED || status == Download.ERROR)
              .flatMap(status -> displayable.resumeDownload(getContext(),
                  (PermissionRequest) getContext())))
          .retry()
          .subscribe(success -> {
          }, throwable -> throwable.printStackTrace()));

      subscriptions.add(
          RxView.clicks(cancelDownloadButton).subscribe(click -> displayable.removeDownload()));

      subscriptions.add(displayable.downloadStatus()
          .observeOn(Schedulers.computation())
          .sample(1, TimeUnit.SECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(downloadStatus -> {
            if (downloadStatus == Download.PAUSED || downloadStatus == Download.ERROR) {
              resumeDownloadButton.setVisibility(View.VISIBLE);
            } else {
              resumeDownloadButton.setVisibility(View.GONE);
            }
          }, throwable -> Logger.e(TAG, throwable)));
    }
  }

  @Override public void onViewDetached() {
    if (subscriptions != null) {
      subscriptions.unsubscribe();
    }
  }
}
