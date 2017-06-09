/*
 * Copyright (c) 2016.
 * Modified on 27/07/2016.
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
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.InstallationProgress;
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
    InstallationProgress installation = displayable.getInstallation();
    appName.setText(installation.getAppName());
    if (!TextUtils.isEmpty(installation.getIcon())) {
      ImageLoader.with(context)
          .load(installation.getIcon(), appIcon);
    }

    //save original colors
    if (defaultTextViewColor == null) {
      defaultTextViewColor = status.getTextColors();
    }

    updateStatus(installation, displayable);

    compositeSubscription.add(RxView.clicks(itemView)
        .flatMap(click -> displayable.downloadStatus()
            .first()
            .filter(status -> status == InstallationProgress.InstallationStatus.UNINSTALLED
                || status == InstallationProgress.InstallationStatus.INSTALLED)
            .flatMap(
                status -> displayable.installOrOpenDownload(context, (PermissionService) context)))
        .retry()
        .subscribe(success -> {
        }, throwable -> throwable.printStackTrace()));

    compositeSubscription.add(RxView.clicks(resumeDownloadButton)
        .flatMap(click -> displayable.downloadStatus()
            .first()
            .filter(status -> status == InstallationProgress.InstallationStatus.PAUSED
                || status == InstallationProgress.InstallationStatus.FAILED)
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
        .first()
        .observeOn(Schedulers.computation())
        .sample(1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(downloadStatus -> {
          if (downloadStatus == InstallationProgress.InstallationStatus.PAUSED
              || downloadStatus == InstallationProgress.InstallationStatus.FAILED) {
            resumeDownloadButton.setVisibility(View.VISIBLE);
          } else {
            resumeDownloadButton.setVisibility(View.GONE);
          }
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }

  private void updateStatus(InstallationProgress installation,
      CompletedDownloadDisplayable displayable) {
    final FragmentActivity context = getContext();
    if (installation.getState() == InstallationProgress.InstallationStatus.FAILED) {
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
    status.setText(displayable.getStatusName(getContext()));
  }
}
