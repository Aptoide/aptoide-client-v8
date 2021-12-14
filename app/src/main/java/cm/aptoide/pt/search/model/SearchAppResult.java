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

  public SearchAppResult() {
  }

  public SearchAppResult(int rank, String icon, String storeName, Long storeId, String storeTheme,
      long modifiedDate, float averageRating, long totalDownloads, String appName,
      String packageName, String md5, long appId, int versionCode, String versionName, String path,
      String pathAlt, Malware malware, long size, boolean hasOtherVersions, boolean billing,
      boolean advertising, String oemId, boolean isHighlightedResult, Obb obb,
      List<String> requiredSplits, List<Split> splits, DownloadStatusModel downloadModel,
      List<AppScreenshot> screenshots) {
    this.rank = rank;
    this.storeTheme = storeTheme;
    this.modifiedDate = modifiedDate;
    this.averageRating = averageRating;
    this.totalDownloads = totalDownloads;
    this.hasOtherVersions = hasOtherVersions;
    this.storeId = storeId;
    this.isHighlightedResult = isHighlightedResult;
    this.screenshots = screenshots;

    this.download =
        new Download(appId, appName, packageName, md5, versionName, versionCode, icon, path,
            pathAlt, size, obb, storeName, advertising, billing, malware, splits, requiredSplits,
            oemId, downloadModel);
  }

  public SearchAppResult(SearchAppResult app, DownloadStatusModel downloadModel,
      List<AppScreenshot> screenshots) {
    this(app.getRank(), app.getIcon(), app.getStoreName(), app.getStoreId(), app.getStoreTheme(),
        app.getModifiedDate(), app.getAverageRating(), app.getTotalDownloads(), app.getAppName(),
        app.getPackageName(), app.getMd5(), app.getAppId(), app.getVersionCode(),
        app.getVersionName(), app.getPath(), app.getPathAlt(), app.getMalware(), app.getSize(),
        app.hasOtherVersions(), app.hasBilling(), app.hasAdvertising(), app.getOemId(),
        app.isHighlightedResult(), app.getObb(), app.getRequiredSplits(), app.getSplits(),
        downloadModel, screenshots);
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

  @NotNull @Override public Type getType() {
    return Type.APP;
  }

  @Override public long getId() {
    return SearchItem.Type.APP.ordinal() + download.getAppId();
  }

  @Override public int hashCode() {
    int result = rank;
    result = 31 * result + (storeId != null ? storeId.hashCode() : 0);
    result = 31 * result + (storeTheme != null ? storeTheme.hashCode() : 0);
    result = 31 * result + (int) (modifiedDate ^ (modifiedDate >>> 32));
    result = 31 * result + (averageRating != +0.0f ? Float.floatToIntBits(averageRating) : 0);
    result = 31 * result + (int) (totalDownloads ^ (totalDownloads >>> 32));
    result = 31 * result + (hasOtherVersions ? 1 : 0);
    result = 31 * result + (isHighlightedResult ? 1 : 0);
    result = 31 * result + (screenshots != null ? screenshots.hashCode() : 0);
    result = 31 * result + (download != null ? download.hashCode() : 0);
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SearchAppResult that = (SearchAppResult) o;

    if (rank != that.rank) return false;
    if (modifiedDate != that.modifiedDate) return false;
    if (Float.compare(that.averageRating, averageRating) != 0) return false;
    if (totalDownloads != that.totalDownloads) return false;
    if (hasOtherVersions != that.hasOtherVersions) return false;
    if (isHighlightedResult != that.isHighlightedResult) return false;
    if (storeId != null ? !storeId.equals(that.storeId) : that.storeId != null) return false;
    if (storeTheme != null ? !storeTheme.equals(that.storeTheme) : that.storeTheme != null) {
      return false;
    }
    if (screenshots != null ? !screenshots.equals(that.screenshots) : that.screenshots != null) {
      return false;
    }
    return download != null ? download.equals(that.download) : that.download == null;
  }
}
