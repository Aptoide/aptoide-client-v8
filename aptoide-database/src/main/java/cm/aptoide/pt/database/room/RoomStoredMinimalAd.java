package cm.aptoide.pt.database.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "storedMinimalAd") public class RoomStoredMinimalAd {

  @NonNull @PrimaryKey private String packageName;
  private String referrer;
  private String cpcUrl;
  private String cpdUrl;
  private String cpiUrl;
  private Long timestamp;
  private Long adId;

  public RoomStoredMinimalAd(String packageName, String referrer, String cpcUrl, String cpdUrl,
      String cpiUrl, Long adId) {
    this.packageName = packageName;
    this.referrer = referrer;
    this.cpcUrl = cpcUrl;
    this.cpdUrl = cpdUrl;
    this.cpiUrl = cpiUrl;
    this.adId = adId;
    this.timestamp = System.currentTimeMillis();
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

  public String getCpcUrl() {
    return cpcUrl;
  }

  public void setCpcUrl(String cpcUrl) {
    this.cpcUrl = cpcUrl;
  }

  public String getCpdUrl() {
    return cpdUrl;
  }

  public void setCpdUrl(String cpdUrl) {
    this.cpdUrl = cpdUrl;
  }

  public String getCpiUrl() {
    return cpiUrl;
  }

  public void setCpiUrl(String cpiUrl) {
    this.cpiUrl = cpiUrl;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Long getAdId() {
    return adId;
  }

  public void setAdId(Long adId) {
    this.adId = adId;
  }
}
