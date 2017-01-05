package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.DownloadAnalyticsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Event;
import lombok.Data;
import lombok.ToString;

/**
 * Created by trinkes on 02/01/2017.
 */

public @Data @ToString class DownloadInstallBaseEvent extends Event {
  private Action action;
  private int versionCode;
  private Origin origin;
  private String packageName;
  private String url;
  private ObbType obbType;
  private String obbUrl;
  private ObbType patchObbType;
  private String patchObbUrl;
  private String name;
  private AppContext context;
  private DownloadInstallEventConverter downloadInstallEventConverter;
  private DownloadInstallAnalyticsBaseBody.ResultStatus resultStatus;
  private Throwable error;

  public DownloadInstallBaseEvent(Action action, Origin origin, String packageName, String url,
      String obbUrl, String patchObbUrl, AppContext context, int versionCode,
      DownloadInstallEventConverter downloadInstallEventConverter, String eventName) {
    this.action = action;
    this.versionCode = versionCode;
    this.origin = origin;
    this.packageName = packageName;
    this.url = url;
    this.obbType = ObbType.main;
    this.obbUrl = obbUrl;
    this.patchObbType = ObbType.patch;
    this.patchObbUrl = patchObbUrl;
    this.name = eventName;
    this.context = context;
    this.downloadInstallEventConverter = downloadInstallEventConverter;
  }

  @Override public void send() {
    DownloadAnalyticsRequest.of(AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID(),
        downloadInstallEventConverter.convert(this, resultStatus, error), action.name(), name,
        context.name())
        .observe()
        .subscribe(baseV7Response -> Logger.d(this, "onResume: " + baseV7Response),
            throwable -> throwable.printStackTrace());
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  public enum Action {
    CLICK, AUTO
  }

  public enum Origin {
    install, update, downgrade, update_all
  }

  protected enum ObbType {
    main, patch
  }

  public enum AppContext {
    timeline, appview, updatetab, scheduled, downloads
  }
}
