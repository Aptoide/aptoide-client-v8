package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports;

import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.Report;
import lombok.Data;
import lombok.ToString;

/**
 * Created by trinkes on 02/01/2017.
 */

public @Data @ToString class DownloadReport extends Report {
  private Action action;
  private long timeStamp;
  private Origin origin;
  private Status status;
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

  public DownloadReport(Action action, long timeStamp, Origin origin, Status status,
      String packageName, long size, String url, long obbSize, ObbType obbType, String obbUrl,
      long patchObbSize, ObbType patchObbType, String patchObbUrl, AppContext context) {
    this.action = action;
    this.timeStamp = timeStamp;
    this.origin = origin;
    this.status = status;
    this.packageName = packageName;
    this.size = size;
    this.url = url;
    this.obbSize = obbSize;
    this.obbType = obbType;
    this.obbUrl = obbUrl;
    this.patchObbSize = patchObbSize;
    this.patchObbType = patchObbType;
    this.patchObbUrl = patchObbUrl;
    this.name = "download";
    this.context = context;
  }

  public enum Action {
    CLICK, AUTO
  }

  public enum Origin {
    install, update, downgrade, update_all
  }

  public enum Status {
    start, pause, resume, stop
  }

  enum ObbType {
    main, patch
  }

  public enum AppContext {
    timeline, appview, updatetab, scheduled, rollback, RateComments
  }
}
