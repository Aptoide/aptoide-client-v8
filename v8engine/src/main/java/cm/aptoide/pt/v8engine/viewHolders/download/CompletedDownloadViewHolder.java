package cm.aptoide.pt.v8engine.viewHolders.download;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.DownloadsView;
import cm.aptoide.pt.v8engine.viewHolders.RecyclerViewHolder;
import com.jakewharton.rxbinding.view.RxView;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CompletedDownloadViewHolder
    extends RecyclerViewHolder<DownloadsView.DownloadViewModel> {

  private final TextView appName;
  private final ImageView appIcon;
  private final TextView status;
  private final ImageView resumeDownloadButton;
  private final ImageView cancelDownloadButton;
  private ColorStateList defaultTextViewColor;

  protected CompletedDownloadViewHolder(View itemView) {
    super(itemView);

    appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
    appName = (TextView) itemView.findViewById(R.id.app_name);
    status = (TextView) itemView.findViewById(R.id.speed);
    resumeDownloadButton = (ImageView) itemView.findViewById(R.id.resume_download);
    cancelDownloadButton = (ImageView) itemView.findViewById(R.id.pause_cancel_button);

    addSubscription(RxView.clicks(itemView)
        .flatMap(click -> displayable.downloadStatus()
            .filter(status -> status == Download.COMPLETED)
            .flatMap(
                status -> displayable.installOrOpenDownload(context, (PermissionRequest) context)))
        .retry()
        .subscribe(success -> {
        }, throwable -> throwable.printStackTrace()));

    addSubscription(RxView.clicks(resumeDownloadButton)
        .flatMap(click -> displayable.downloadStatus()
            .filter(status -> status == Download.PAUSED || status == Download.ERROR)
            .flatMap(status -> displayable.resumeDownload(context, (PermissionRequest) context)))
        .retry()
        .subscribe(success -> {
        }, throwable -> throwable.printStackTrace()));

    addSubscription(RxView.clicks(cancelDownloadButton)
        .subscribe(click -> displayable.removeDownload(context), err -> {
          CrashReport.getInstance().log(err);
        }));

    addSubscription(displayable.downloadStatus()
        .observeOn(Schedulers.computation())
        .sample(1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(downloadStatus -> {
          if (downloadStatus == Download.PAUSED || downloadStatus == Download.ERROR) {
            resumeDownloadButton.setVisibility(View.VISIBLE);
          } else {
            resumeDownloadButton.setVisibility(View.GONE);
          }
        }, throwable -> CrashReport.getInstance().log(throwable)));
  }

  @Override protected void update(Context context, DownloadsView.DownloadViewModel download) {

    appName.setText(download.getAppName());
    if (!TextUtils.isEmpty(download.getIcon())) {
      ImageLoader.with(context).load(download.getIcon(), appIcon);
    }

    //save original colors
    if (defaultTextViewColor == null) {
      defaultTextViewColor = status.getTextColors();
    }

    //
    // update download status
    //
    if (download.getStatus() == DownloadsView.DownloadViewModel.Status.ERROR) {
      int statusTextColor;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        statusTextColor = context.getColor(R.color.red_700);
      } else {
        statusTextColor = context.getResources().getColor(R.color.red_700);
      }
      status.setTextColor(statusTextColor);
    } else {
      status.setTextColor(defaultTextViewColor);
    }
    status.setText(download.getStatusName(context));
  }

  @Override public int getViewResource() {
    return R.layout.completed_donwload_row_layout;
  }
}
