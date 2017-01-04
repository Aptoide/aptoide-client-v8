package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.DownloadAnalyticsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Report;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by trinkes on 02/01/2017.
 */

public @Data @ToString class DownloadReport extends Report {
  private Action action;
  private int versionCode;
  private long timeStamp;
  private Origin origin;
  private String packageName;
  private long size;
  private String url;
  private long obbSize;
  private ObbType obbType;
  private String obbUrl;
  private long patchObbSize;
  private ObbType patchObbType;
  private String patchObbUrl;
  private String name;
  private AppContext context;
  private DownloadReportConverter downloadReportConverter;
  private DownloadInstallAnalyticsBaseBody.ResultStatus resultStatus;
  /**
   * this variable should be activated when the download progress starts, this will prevent the
   * event to be sent if download was cached
   */
  @Setter private boolean downloadHadProgress;

  public DownloadReport(Action action, Origin origin, String packageName, long size, String url,
      long obbSize, String obbUrl, long patchObbSize, String patchObbUrl, AppContext context,
      int versionCode, DownloadReportConverter downloadReportConverter) {
    this.action = action;
    this.versionCode = versionCode;
    this.timeStamp = System.currentTimeMillis();
    this.origin = origin;
    this.packageName = packageName;
    this.size = size;
    this.url = url;
    this.obbSize = obbSize;
    this.obbType = ObbType.main;
    this.obbUrl = obbUrl;
    this.patchObbSize = patchObbSize;
    this.patchObbType = ObbType.patch;
    this.patchObbUrl = patchObbUrl;
    this.name = "download";
    this.context = context;
    this.downloadReportConverter = downloadReportConverter;
    downloadHadProgress = false;
  }

  @Override public void send() {
    if (downloadHadProgress) {
      if (resultStatus == null) {
        throw new IllegalArgumentException(
            "The Result status should be added before send the event");
      } else {
        DownloadAnalyticsRequest.of(AptoideAccountManager.getAccessToken(),
            new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                DataProvider.getContext()).getAptoideClientUUID(),
            downloadReportConverter.convert(this, resultStatus), action.name(), name,
            context.name())
            .observe()
            .subscribe(baseV7Response -> Logger.d(this, "onResume: " + baseV7Response),
                throwable -> throwable.printStackTrace());
      }
    }
  }

  public enum Action {
    CLICK, AUTO
  }

  public enum Origin {
    install, update, downgrade, update_all
  }

  enum ObbType {
    main, patch
  }

  public enum AppContext {
    timeline, appview, updatetab, scheduled, rollback, RateComments
  }
}
