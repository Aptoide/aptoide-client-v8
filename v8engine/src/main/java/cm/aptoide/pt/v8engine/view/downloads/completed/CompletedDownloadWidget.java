/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.downloads.completed;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 7/18/16.
 */
@Displayables({ CompletedDownloadDisplayable.class }) public class CompletedDownloadWidget
    extends Widget<CompletedDownloadDisplayable> {

  private TextView appName;
  private ImageView appIcon;
  private TextView status;
  private ImageView resumeDownloadButton;
  private ImageView cancelDownloadButton;
  private ColorStateList defaultTextViewColor;

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
    final FragmentActivity context = getContext();
    Download download = displayable.getDownload();
    appName.setText(download.getAppName());
    if (!TextUtils.isEmpty(download.getIcon())) {
      ImageLoader.with(context)
          .load(download.getIcon(), appIcon);
    }

    //save original colors
    if (defaultTextViewColor == null) {
      defaultTextViewColor = status.getTextColors();
    }

    updateStatus(download);

    compositeSubscription.add(RxView.clicks(itemView)
        .flatMap(click -> displayable.downloadStatus()
            .filter(status -> status == Download.COMPLETED)
            .flatMap(
                status -> displayable.installOrOpenDownload(context, (PermissionService) context)))
        .retry()
        .subscribe(success -> {
        }, throwable -> throwable.printStackTrace()));

    compositeSubscription.add(RxView.clicks(resumeDownloadButton)
        .flatMap(click -> displayable.downloadStatus()
            .filter(status -> status == Download.PAUSED || status == Download.ERROR)
            .flatMap(status -> displayable.resumeDownload(context, (PermissionService) context)))
        .retry()
        .subscribe(success -> {
        }, throwable -> throwable.printStackTrace()));

    compositeSubscription.add(RxView.clicks(cancelDownloadButton)
        .subscribe(click -> displayable.removeDownload(context), err -> {
          CrashReport.getInstance()
              .log(err);
        }));

    compositeSubscription.add(displayable.downloadStatus()
        .observeOn(Schedulers.computation())
        .sample(1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(downloadStatus -> {
          if (downloadStatus == Download.PAUSED || downloadStatus == Download.ERROR) {
            resumeDownloadButton.setVisibility(View.VISIBLE);
          } else {
            resumeDownloadButton.setVisibility(View.GONE);
          }
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }

  private void updateStatus(Download download) {
    final FragmentActivity context = getContext();
    if (download.getOverallDownloadStatus() == Download.ERROR) {
      int statusTextColor;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        statusTextColor = context.getColor(R.color.red_700);
      } else {
        statusTextColor = context.getResources()
            .getColor(R.color.red_700);
      }
      status.setTextColor(statusTextColor);
    } else {
      status.setTextColor(defaultTextViewColor);
    }
    status.setText(download.getStatusName(context));
  }
}
