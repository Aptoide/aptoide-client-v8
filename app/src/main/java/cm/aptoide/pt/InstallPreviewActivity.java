package cm.aptoide.pt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.view.ActivityView;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.DetailedApp;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Completable;

public class InstallPreviewActivity extends ActivityView {

  @Inject InstallManager installManager;
  @Inject DownloadFactory downloadFactory;
  @Inject AppCenter appCenter;
  @Inject DownloadAnalytics downloadAnalytics;
  private View button;
  private TextView appName;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
    setContentView(R.layout.activity_install_preview);

    button = findViewById(R.id.install);
    appName = findViewById(R.id.app_name);

    RxView.clicks(button)
        .flatMapSingle(__ -> appCenter.loadDetailedApp("com.appcoins.wallet", null))
        .flatMapCompletable(detailedAppRequestResult -> {
          if (!detailedAppRequestResult.hasError()) {
            DetailedApp app = detailedAppRequestResult.getDetailedApp();
            Download download =
                downloadFactory.create(Download.ACTION_INSTALL, app.getName(), app.getPackageName(),
                    app.getMd5(), app.getIcon(), app.getVersionName(), app.getVersionCode(),
                    app.getPath(), app.getPathAlt(), app.getObb());
            downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
                DownloadAnalytics.AppContext.APPS_FRAGMENT);
            return installManager.install(download)
                .doOnCompleted(() -> appName.setText(
                    "name: " + detailedAppRequestResult.getDetailedApp()
                        .toString()));
          }
          return Completable.complete();
        })
        .subscribe(detailedAppRequestResult -> {
        }, Throwable::printStackTrace);
  }
}
