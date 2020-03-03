package cm.aptoide.pt.database.room;

public class MinimalAd {

  private String cpdUrl;
  private String description;
  private String packageName;
  private Long networkId;
  private String clickUrl;
  private String cpcUrl;
  private Long appId;
  private Long adId;
  private String cpiUrl;
  private String name;
  private String iconPath;
  private Integer downloads;
  private Integer stars;
  private Long modified;

  private boolean hasAppc;
  private double appcAmount;
  private double currencyAmount;
  private String currency;
  private String currencySymbol;

  public MinimalAd(String packageName, long networkId, String clickUrl, String cpcUrl,
      String cpdUrl, long appId, long adId, String cpiUrl, String name, String iconPath,
      String description, int downloads, int stars, Long modified, boolean hasAppc,
      double appcAmount, double currencyAmount, String currency, String currencySymbol) {
    this.packageName = packageName;
    this.networkId = networkId;
    this.clickUrl = clickUrl;
    this.cpcUrl = cpcUrl;
    this.cpdUrl = cpdUrl;
    this.appId = appId;
    this.adId = adId;
    this.cpiUrl = cpiUrl;
    this.name = name;
    this.iconPath = iconPath;
    this.description = description;
    this.downloads = downloads;
    this.stars = stars;
    this.modified = modified;
    this.hasAppc = hasAppc;
    this.appcAmount = appcAmount;
    this.currencyAmount = currencyAmount;
    this.currency = currency;
    this.currencySymbol = currencySymbol;
  }

  public String getCpdUrl() {
    return cpdUrl;
  }

  public void setCpdUrl(String cpdUrl) {
    this.cpdUrl = cpdUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public Long getNetworkId() {
    return networkId;
  }

  public void setNetworkId(Long networkId) {
    this.networkId = networkId;
  }

  public String getClickUrl() {
    return clickUrl;
  }

  public void setClickUrl(String clickUrl) {
    this.clickUrl = clickUrl;
  }

  public String getCpcUrl() {
    return cpcUrl;
  }

  public void setCpcUrl(String cpcUrl) {
    this.cpcUrl = cpcUrl;
  }

  public Long getAppId() {
    return appId;
  }

  public void setAppId(Long appId) {
    this.appId = appId;
  }

  public Long getAdId() {
    return adId;
  }

  public void setAdId(Long adId) {
    this.adId = adId;
  }

  public String getCpiUrl() {
    return cpiUrl;
  }

  public void setCpiUrl(String cpiUrl) {
    this.cpiUrl = cpiUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIconPath() {
    return iconPath;
  }

  public void setIconPath(String iconPath) {
    this.iconPath = iconPath;
  }

  public Integer getDownloads() {
    return downloads;
  }

  public void setDownloads(Integer downloads) {
    this.downloads = downloads;
  }

  public Integer getStars() {
    return stars;
  }

  public void setStars(Integer stars) {
    this.stars = stars;
  }

  public Long getModified() {
    return modified;
  }

  public void setModified(Long modified) {
    this.modified = modified;
  }

  public boolean isHasAppc() {
    return hasAppc;
  }

  public void setHasAppc(boolean hasAppc) {
    this.hasAppc = hasAppc;
  }

  public double getAppcAmount() {
    return appcAmount;
  }

  public void setAppcAmount(double appcAmount) {
    this.appcAmount = appcAmount;
  }

  public double getCurrencyAmount() {
    return currencyAmount;
  }

  public void setCurrencyAmount(double currencyAmount) {
    this.currencyAmount = currencyAmount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }
}
