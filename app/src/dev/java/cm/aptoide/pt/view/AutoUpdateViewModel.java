package cm.aptoide.pt.view;

public class AutoUpdateViewModel {
  private final String packageName;
  private final int localVersionCode;
  private String md5;
  private int vercode;
  private String infoPackageName;
  private int appId;
  private String path;
  private int mindsdk;
  private int minAptoideVercode;

  public AutoUpdateViewModel(String packageName, int localVersionCode) {
    this.packageName = packageName;
    this.localVersionCode = localVersionCode;
    this.md5 = "";
    this.vercode = -1;
    this.infoPackageName = "";
    this.appId = -1;
    this.path = "";
    this.mindsdk = 0;
    this.minAptoideVercode = 0;
  }

  public String getPackageName() {
    return packageName;
  }

  public int getLocalVersionCode() {
    return localVersionCode;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public int getVercode() {
    return vercode;
  }

  public void setVercode(int vercode) {
    this.vercode = vercode;
  }

  public String getInfoPackageName() {
    return infoPackageName;
  }

  public void setInfoPackageName(String infoPackageName) {
    this.infoPackageName = infoPackageName;
  }

  public int getAppId() {
    return appId;
  }

  public void setAppId(int appId) {
    this.appId = appId;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getMindsdk() {
    return mindsdk;
  }

  public void setMindsdk(int mindsdk) {
    this.mindsdk = mindsdk;
  }

  public int getMinAptoideVercode() {
    return minAptoideVercode;
  }

  public void setMinAptoideVercode(int minAptoideVercode) {
    this.minAptoideVercode = minAptoideVercode;
  }
}
