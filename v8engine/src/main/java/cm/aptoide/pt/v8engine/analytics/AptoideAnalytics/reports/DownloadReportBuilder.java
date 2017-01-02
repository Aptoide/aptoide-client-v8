package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports;

public class DownloadReportBuilder {
  private DownloadReport.Action event;
  private long timeStamp;
  private DownloadReport.Origin origin;
  private DownloadReport.Status status;
  private String packageName;
  private long size;
  private String url;
  private long obbSize;
  private DownloadReport.ObbType obbType;
  private String obbUrl;
  private long patchObbSize;
  private DownloadReport.ObbType patchObbType;
  private String patchObbUrl;
  private DownloadReport.AppContext context;

  public DownloadReportBuilder(DownloadReport.Origin origin, DownloadReport.AppContext context,
      DownloadReport.Action event, String packageName) {
    this.origin = origin;
    this.context = context;
    this.event = event;
    this.packageName = packageName;
  }

  public DownloadReportBuilder setEvent(DownloadReport.Action event) {
    this.event = event;
    return this;
  }

  public DownloadReportBuilder setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
    return this;
  }

  public DownloadReportBuilder setOrigin(DownloadReport.Origin origin) {
    this.origin = origin;
    return this;
  }

  public DownloadReportBuilder setStatus(DownloadReport.Status status) {
    this.status = status;
    return this;
  }

  public DownloadReportBuilder setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  public DownloadReportBuilder setSize(long size) {
    this.size = size;
    return this;
  }

  public DownloadReportBuilder setUrl(String url) {
    this.url = url;
    return this;
  }

  public DownloadReportBuilder setObbSize(long obbSize) {
    this.obbSize = obbSize;
    return this;
  }

  public DownloadReportBuilder setObbType(DownloadReport.ObbType obbType) {
    this.obbType = obbType;
    return this;
  }

  public DownloadReportBuilder setObbUrl(String obbUrl) {
    this.obbUrl = obbUrl;
    return this;
  }

  public DownloadReportBuilder setPatchObbSize(long patchObbSize) {
    this.patchObbSize = patchObbSize;
    return this;
  }

  public DownloadReportBuilder setPatchObbType(DownloadReport.ObbType patchObbType) {
    this.patchObbType = patchObbType;
    return this;
  }

  public DownloadReportBuilder setPatchObbUrl(String patchObbUrl) {
    this.patchObbUrl = patchObbUrl;
    return this;
  }

  public DownloadReportBuilder setContext(DownloadReport.AppContext context) {
    this.context = context;
    return this;
  }

  public DownloadReport createDownloadReport() {
    return new DownloadReport(event, timeStamp, origin, status, packageName, size, url, obbSize,
        obbType, obbUrl, patchObbSize, patchObbType, patchObbUrl, context);
  }
}