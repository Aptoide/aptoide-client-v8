package cm.aptoide.pt.analytics.analytics;

class AnalyticsBaseBody {
  private String aptoideId;
  private String accessToken;
  private int aptoideVercode;
  private String aptoideMd5sum;
  private String aptoidePackage;
  private String lang;
  private boolean mature;
  private String q;

  public String getAptoideId() {
    return aptoideId;
  }

  public void setAptoideId(String aptoideId) {
    this.aptoideId = aptoideId;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public int getAptoideVercode() {
    return aptoideVercode;
  }

  public void setAptoideVercode(int aptoideVercode) {
    this.aptoideVercode = aptoideVercode;
  }

  public String getAptoideMd5sum() {
    return aptoideMd5sum;
  }

  public void setAptoideMd5sum(String aptoideMd5sum) {
    this.aptoideMd5sum = aptoideMd5sum;
  }

  public String getAptoidePackage() {
    return aptoidePackage;
  }

  public void setAptoidePackage(String aptoidePackage) {
    this.aptoidePackage = aptoidePackage;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public boolean isMature() {
    return mature;
  }

  public void setMature(boolean mature) {
    this.mature = mature;
  }

  public String getQ() {
    return q;
  }

  public void setQ(String q) {
    this.q = q;
  }
}
