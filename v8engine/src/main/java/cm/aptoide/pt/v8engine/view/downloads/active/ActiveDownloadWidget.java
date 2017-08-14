package cm.aptoide.pt.v8engine.view.downloads.active;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 7/18/16.
 */
public class ActiveDownloadWidget extends Widget<ActiveDownloadDisplayable> {

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
        .subscribe(click -> displayable.pauseInstall(), throwable -> CrashReport.getInstance()
            .log(throwable)));

    compositeSubscription.add(displayable.getInstallationObservable()
        .observeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installation -> updateUi(installation), throwable -> CrashReport.getInstance()
            .log(throwable)));
  }

  private Void updateUi(Install installation) {
    appName.setText(installation.getAppName());
    if (!TextUtils.isEmpty(installation.getIcon())) {
      ImageLoader.with(getContext())
          .load(installation.getIcon(), appIcon);
    }
    if (installation.getState()
        .equals(Install.InstallationStatus.INSTALLING) && installation.isIndeterminate()) {
      pauseCancelButton.setVisibility(View.INVISIBLE);
    } else {
      pauseCancelButton.setVisibility(View.VISIBLE);
    }

    progressBar.setIndeterminate(installation.isIndeterminate());
    progressBar.setProgress(installation.getProgress());
    downloadProgressTv.setText(String.format("%d%%", installation.getProgress()));
    if (installation.isIndeterminate()
        || installation.getState() == Install.InstallationStatus.INSTALLED) {
      downloadSpeedTv.setText("");
    } else {
      downloadSpeedTv.setText(String.valueOf(
          AptoideUtils.StringU.formatBytesToBits((long) installation.getSpeed(), true)));
    }
    return null;
  }
}
