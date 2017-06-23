package cm.aptoide.pt.database.realm;

import cm.aptoide.pt.model.MinimalAdInterface;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by neuro on 28-07-2016.
 */
public class StoredMinimalAd extends RealmObject implements MinimalAdInterface {

  public static final String PACKAGE_NAME = "packageName";
  public static final String REFERRER = "referrer";
  public static final String CPI_URL = "cpiUrl";

  @PrimaryKey @Required private String packageName;
  private String referrer;
  private String cpcUrl;
  private String cpdUrl;
  private String cpiUrl;
  private Long timestamp;

  private Long adId;

  public StoredMinimalAd() {
  }

  // apagar
  public StoredMinimalAd(String packageName, String referrer, String cpcUrl, String cpdUrl,
      String cpiUrl, long adId) {
    this.packageName = packageName;
    this.referrer = referrer;
    this.cpcUrl = cpcUrl;
    this.cpdUrl = cpdUrl;
    this.cpiUrl = cpiUrl;
    this.adId = adId;
    this.timestamp = System.currentTimeMillis();
  }

  public static StoredMinimalAd from(MinimalAd minimalAd, String referrer) {

    String packageName = minimalAd.getPackageName();
    String cpcUrl = minimalAd.getCpcUrl();
    String cpdUrl = minimalAd.getCpdUrl();
    String cpiUrl = minimalAd.getCpiUrl();
    Long adId = minimalAd.getAdId();

    return new StoredMinimalAd(packageName, referrer, cpcUrl, cpdUrl, cpiUrl, adId);
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getReferrer() {
    return referrer;
  }

  public void setReferrer(String referrer) {
    this.referrer = referrer;
  }

  @Override public String getCpcUrl() {
    return cpcUrl;
  }

  @Override public String getCpdUrl() {
    return cpdUrl;
  }

  @Override public void setCpdUrl(String cpdUrl) {
    this.cpdUrl = cpdUrl;
  }

  @Override public String getCpiUrl() {
    return cpiUrl;
  }

  public void setCpiUrl(String cpiUrl) {
    this.cpiUrl = cpiUrl;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public long getAdId() {
    return adId;
  }

  public void setAdId(long adId) {
    this.adId = adId;
  }
}
