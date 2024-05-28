package cm.aptoide.pt.search.model;

import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.download.view.Download;
import cm.aptoide.pt.download.view.DownloadStatusModel;
import cm.aptoide.pt.view.app.AppScreenshot;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class SearchAppResult implements SearchItem {
  private int rank;
  private Long storeId;
  private String storeTheme;
  private long modifiedDate;
  private float averageRating;
  private long totalDownloads;
  private boolean hasOtherVersions;
  private boolean isHighlightedResult;

  private List<AppScreenshot> screenshots;

  private Download download;

  private boolean isInCatappult;
  private String appCategory;

  public SearchAppResult() {
  }

  public SearchAppResult(int rank, String icon, String storeName, Long storeId, String storeTheme,
      long modifiedDate, float averageRating, long totalDownloads, String appName,
      String packageName, String md5, long appId, int versionCode, String versionName, String path,
      String pathAlt, Malware malware, long size, boolean hasOtherVersions, boolean billing,
      boolean advertising, String oemId, boolean isHighlightedResult, Obb obb,
      List<String> requiredSplits, List<Split> splits, DownloadStatusModel downloadModel,
      List<AppScreenshot> screenshots, boolean isInCatappult, String appCategory) {
    this.rank = rank;
    this.storeTheme = storeTheme;
    this.modifiedDate = modifiedDate;
    this.averageRating = averageRating;
    this.totalDownloads = totalDownloads;
    this.hasOtherVersions = hasOtherVersions;
    this.storeId = storeId;
    this.isHighlightedResult = isHighlightedResult;
    this.screenshots = screenshots;
    this.isInCatappult = isInCatappult;
    this.appCategory = appCategory;

    this.download =
        new Download(appId, appName, packageName, md5, versionName, versionCode, icon, path,
            pathAlt, size, obb, storeName, advertising, billing, malware, splits, requiredSplits,
            oemId, downloadModel, appCategory);
  }

  public SearchAppResult(SearchAppResult app, DownloadStatusModel downloadModel,
      List<AppScreenshot> screenshots, boolean isAppInCatappult, String appCategory) {
    this(app.getRank(), app.getIcon(), app.getStoreName(), app.getStoreId(), app.getStoreTheme(),
        app.getModifiedDate(), app.getAverageRating(), app.getTotalDownloads(), app.getAppName(),
        app.getPackageName(), app.getMd5(), app.getAppId(), app.getVersionCode(),
        app.getVersionName(), app.getPath(), app.getPathAlt(), app.getMalware(), app.getSize(),
        app.hasOtherVersions(), app.hasBilling(), app.hasAdvertising(), app.getOemId(),
        app.isHighlightedResult(), app.getObb(), app.getRequiredSplits(), app.getSplits(),
        downloadModel, screenshots, isAppInCatappult, appCategory);
  }

  public int getRank() {
    return rank;
  }

  public String getIcon() {
    return download.getIcon();
  }

  public String getStoreName() {
    return download.getStoreName();
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
    return download.getAppName();
  }

  public String getPackageName() {
    return download.getPackageName();
  }

  public long getAppId() {
    return download.getAppId();
  }

  public boolean hasOtherVersions() {
    return hasOtherVersions;
  }

  public boolean hasAdvertising() {
    return download.getHasAdvertising();
  }

  public boolean hasBilling() {
    return download.getHasBilling();
  }

  public boolean isAppcApp() {
    return hasBilling() || hasAdvertising();
  }

  public Long getStoreId() {
    return storeId;
  }

  public String getMd5() {
    return download.getMd5();
  }

  public int getVersionCode() {
    return download.getVersionCode();
  }

  public String getVersionName() {
    return download.getVersionName();
  }

  public String getPath() {
    return download.getPath();
  }

  public String getPathAlt() {
    return download.getPathAlt();
  }

  public Obb getObb() {
    return download.getObb();
  }

  public Malware getMalware() {
    return download.getMalware();
  }

  public long getSize() {
    return download.getSize();
  }

  public List<Split> getSplits() {
    return download.getSplits();
  }

  public List<String> getRequiredSplits() {
    return download.getRequiredSplits();
  }

  public DownloadStatusModel getDownloadModel() {
    return download.getDownloadModel();
  }

  public List<AppScreenshot> getScreenshots() {
    return screenshots;
  }

  public String getOemId() {
    return download.getOemId();
  }

  public Download getDownload() {
    return download;
  }

  public boolean isHighlightedResult() {
    return isHighlightedResult;
  }

  public boolean isInCatappult() {
    return isInCatappult;
  }

  @NotNull @Override public Type getType() {
    return Type.APP;
  }

  @Override public long getId() {
    return SearchItem.Type.APP.ordinal() + download.getAppId();
  }

  public String getAppCategory() {
    return appCategory;
  }
}
