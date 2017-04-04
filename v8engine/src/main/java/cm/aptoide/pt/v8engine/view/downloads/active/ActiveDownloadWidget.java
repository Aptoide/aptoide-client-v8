package cm.aptoide.pt.v8engine.view.downloads.active;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 7/18/16.
 */
@Displayables({ ActiveDownloadDisplayable.class }) public class ActiveDownloadWidget
    extends Widget<ActiveDownloadDisplayable> {

  private TextView appName;
  private ProgressBar progressBar;
  private TextView downloadSpeedTv;
  private TextView downloadProgressTv;
  private ImageView pauseCancelButton;
  private ImageView appIcon;

  public ActiveDownloadWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    appName = (TextView) itemView.findViewById(R.id.app_name);
    downloadSpeedTv = (TextView) itemView.findViewById(R.id.speed);
    downloadProgressTv = (TextView) itemView.findViewById(R.id.progress);
    progressBar = (ProgressBar) itemView.findViewById(R.id.downloading_progress);
    pauseCancelButton = (ImageView) itemView.findViewById(R.id.pause_cancel_button);
    appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
  }

  @Override public void bindView(ActiveDownloadDisplayable displayable) {
    compositeSubscription.add(RxView.clicks(pauseCancelButton)
        .subscribe(click -> displayable.pauseInstall(getContext()),
            throwable -> CrashReport.getInstance().log(throwable)));

    compositeSubscription.add(displayable.getDownloadObservable()
        .observeOn(Schedulers.computation())
        .distinctUntilChanged()
        .map(download -> download)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((download) -> updateUi(download),
            throwable -> CrashReport.getInstance().log(throwable)));
  }

  private Void updateUi(Download download) {
    appName.setText(download.getAppName());
    if (!TextUtils.isEmpty(download.getIcon())) {
      ImageLoader.with(getContext()).load(download.getIcon(), appIcon);
    }
    if (download.getOverallDownloadStatus() == Download.IN_QUEUE) {
      progressBar.setIndeterminate(true);
    } else {
      progressBar.setIndeterminate(false);
      progressBar.setProgress(download.getOverallProgress());
    }
    downloadProgressTv.setText(String.format("%d%%", download.getOverallProgress()));
    downloadSpeedTv.setText(String.valueOf(
        AptoideUtils.StringU.formatBytesToBits((long) download.getDownloadSpeed(), true)));
    return null;
  }
}
