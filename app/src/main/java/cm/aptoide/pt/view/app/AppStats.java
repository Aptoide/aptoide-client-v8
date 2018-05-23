package cm.aptoide.pt.view.app;

/**
 * Created by D01 on 21/05/2018.
 */

public class AppStats {

  private final AppRating rating;
  private final AppRating globalRating;
  private final int downloads;
  private final int packageDownloads;

  public AppStats(AppRating rating, AppRating globalRating, int downloads, int packageDownloads) {
    this.rating = rating;
    this.globalRating = globalRating;
    this.downloads = downloads;
    this.packageDownloads = packageDownloads;
  }

  public AppRating getRating() {
    return rating;
  }

  public AppRating getGlobalRating() {
    return globalRating;
  }

  public int getDownloads() {
    return downloads;
  }

  public int getPackageDownloads() {
    return packageDownloads;
  }
}
