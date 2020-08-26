package cm.aptoide.pt.search.model;

import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.download.view.Download;
import cm.aptoide.pt.download.view.DownloadStatusModel;
import cm.aptoide.pt.view.app.AppScreenshot;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class SearchAppResult implements Download, SearchItem {
  private int rank;
  private String icon;
  private String storeName;
  private Long storeId;
  private String storeTheme;
  private long modifiedDate;
  private float averageRating;
  private long totalDownloads;
  private String appName;
  private String packageName;
  private String md5;
  private long appId;
  private boolean hasOtherVersions;
  private boolean billing;
  private boolean advertising;
  private int versionCode;
  private long size;
  private String versionName;
  private String path;
  private String pathAlt;
  private Malware malware;
  private String oemId;

  private boolean isHighlightedResult;

  // TODO: Missing from WS
  private Obb obb;
  private List<Split> splits;
  private List<String> requiredSplits;
  private DownloadStatusModel downloadModel;
  private List<AppScreenshot> screenshots;

  public SearchAppResult() {
  }

  public SearchAppResult(int rank, String icon, String storeName, Long storeId, String storeTheme,
      long modifiedDate, float averageRating, long totalDownloads, String appName,
      String packageName, String md5, long appId, int versionCode, String versionName, String path,
      String pathAlt, Malware malware, long size, boolean hasOtherVersions, boolean billing,
      boolean advertising, String oemId, boolean isHighlightedResult,
      DownloadStatusModel downloadModel, List<AppScreenshot> screenshots) {
    this.rank = rank;
    this.icon = icon;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.modifiedDate = modifiedDate;
    this.averageRating = averageRating;
    this.totalDownloads = totalDownloads;
    this.appName = appName;
    this.packageName = packageName;
    this.appId = appId;
    this.hasOtherVersions = hasOtherVersions;
    this.billing = billing;
    this.advertising = advertising;
    this.storeId = storeId;
    this.md5 = md5;
    this.versionName = versionName;
    this.versionCode = versionCode;
    this.path = path;
    this.pathAlt = pathAlt;
    this.malware = malware;
    this.size = size;
    this.oemId = oemId;
    this.isHighlightedResult = isHighlightedResult;

    this.splits = Collections.emptyList();
    this.requiredSplits = Collections.emptyList();
    this.obb = null;
    this.downloadModel = downloadModel;
    this.screenshots = screenshots;
  }

  public SearchAppResult(SearchAppResult app, DownloadStatusModel downloadModel,
      List<AppScreenshot> screenshots) {
    this(app.getRank(), app.getIcon(), app.getStoreName(), app.getStoreId(), app.getStoreTheme(),
        app.getModifiedDate(), app.getAverageRating(), app.getTotalDownloads(), app.getAppName(),
        app.getPackageName(), app.getMd5(), app.getAppId(), app.getVersionCode(),
        app.getVersionName(), app.getPath(), app.getPathAlt(), app.getMalware(), app.getSize(),
        app.hasOtherVersions(), app.hasBilling(), app.hasAdvertising(), app.getOemId(),
        app.isHighlightedResult(), downloadModel, screenshots);
  }

  public SearchAppResult(SearchApp searchApp, String oemId, boolean isHighlighted) {
    this(searchApp.getFile()
            .getMalware()
            .getRank()
            .ordinal(), searchApp.getIcon(), searchApp.getStore()
            .getName(), searchApp.getStore()
            .getId(), searchApp.getStore()
            .getAppearance()
            .getTheme(), searchApp.getModified()
            .getTime(), searchApp.getStats()
            .getRating()
            .getAvg(), searchApp.getStats()
            .getPdownloads(), searchApp.getName(), searchApp.getPackageName(), searchApp.getFile()
            .getMd5sum(), searchApp.getId(), searchApp.getFile()
            .getVercode(), searchApp.getFile()
            .getVername(), searchApp.getFile()
            .getPath(), searchApp.getFile()
            .getPathAlt(), searchApp.getFile()
            .getMalware(), searchApp.getSize(), searchApp.hasVersions(), searchApp.hasBilling(),
        searchApp.hasAdvertising(), oemId, isHighlighted, null, null);
  }

  public int getRank() {
    return rank;
  }

  public String getIcon() {
    return icon;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public long getModifiedDate() {
    return modifiedDate;
  }

  public float getAverageRating() {
    return averageRating;
  }

  public long getTotalDownloads() {
    return totalDownloads;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getAppId() {
    return appId;
  }

  public boolean hasOtherVersions() {
    return hasOtherVersions;
  }

  public boolean hasAdvertising() {
    return this.advertising;
  }

  public boolean hasBilling() {
    return this.billing;
  }

  public boolean isAppcApp() {
    return hasBilling() || hasAdvertising();
  }

  public Long getStoreId() {
    return storeId;
  }

  @Override public String getMd5() {
    return md5;
  }

  @Override public int getVersionCode() {
    return versionCode;
  }

  @Override public String getVersionName() {
    return versionName;
  }

  @Override public String getPath() {
    return path;
  }

  @Override public String getPathAlt() {
    return pathAlt;
  }

  public Obb getObb() {
    return obb;
  }

  @Override public Malware getMalware() {
    return malware;
  }

  @Override public long getSize() {
    return size;
  }

  @Override public List<Split> getSplits() {
    return splits;
  }

  @Override public List<String> getRequiredSplits() {
    return requiredSplits;
  }

  @Override public DownloadStatusModel getDownloadModel() {
    return downloadModel;
  }

  public List<AppScreenshot> getScreenshots() {
    return screenshots;
  }

  @Override public String getOemId() {
    return oemId;
  }

  public boolean isHighlightedResult() {
    return isHighlightedResult;
  }

  @NotNull @Override public Type getType() {
    return Type.APP;
  }

  @Override public long getId() {
    return SearchItem.Type.APP.ordinal() + appId;
  }
}
