package cm.aptoide.pt.download;

public class ApkPaths {

  private final String path;
  private final String altPath;

  public ApkPaths(String path, String altPath) {
    this.path = path;
    this.altPath = altPath;
  }

  public String getPath() {
    return path;
  }

  public String getAltPath() {
    return altPath;
  }
}
