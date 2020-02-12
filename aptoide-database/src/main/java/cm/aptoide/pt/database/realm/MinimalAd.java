/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.database.realm;

import android.os.Parcel;
import android.os.Parcelable;
import io.realm.RealmObject;

public class MinimalAd extends RealmObject implements Parcelable {

  public static final Creator<MinimalAd> CREATOR = new Creator<MinimalAd>() {
    @Override public MinimalAd createFromParcel(Parcel source) {
      return new MinimalAd(source);
    }

    @Override public MinimalAd[] newArray(int size) {
      return new MinimalAd[size];
    }
  };

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

  public MinimalAd() {
  }

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

  protected MinimalAd(Parcel in) {
    this.description = in.readString();
    this.packageName = in.readString();
    this.networkId = in.readLong();
    this.clickUrl = in.readString();
    this.cpcUrl = in.readString();
    this.cpdUrl = in.readString();
    this.appId = in.readLong();
    this.adId = in.readLong();
    this.cpiUrl = in.readString();
    this.name = in.readString();
    this.iconPath = in.readString();
    this.downloads = in.readInt();
    this.stars = in.readInt();
    this.modified = in.readLong();
    this.hasAppc = in.readInt() == 1;
    this.appcAmount = in.readDouble();
    this.currencyAmount = in.readDouble();
    this.currency = in.readString();
    this.currencySymbol = in.readString();
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

  public String getPackageName() {
    return packageName;
  }

  public Long getNetworkId() {
    return networkId;
  }

  public String getClickUrl() {
    return clickUrl;
  }

  public String getCpcUrl() {
    return cpcUrl;
  }

  public Long getAppId() {
    return appId;
  }

  public Long getAdId() {
    return adId;
  }

  public String getCpiUrl() {
    return cpiUrl;
  }

  public String getName() {
    return name;
  }

  public String getIconPath() {
    return iconPath;
  }

  public Integer getDownloads() {
    return downloads;
  }

  public Integer getStars() {
    return stars;
  }

  public Long getModified() {
    return modified;
  }

  public boolean isHasAppc() {
    return hasAppc;
  }

  public String getCurrency() {
    return currency;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public double getCurrencyAmount() {
    return currencyAmount;
  }

  public double getAppcAmount() {
    return appcAmount;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.description);
    dest.writeString(this.packageName);
    dest.writeLong(this.networkId);
    dest.writeString(this.clickUrl);
    dest.writeString(this.cpcUrl);
    dest.writeString(this.cpdUrl);
    dest.writeLong(this.appId);
    dest.writeLong(this.adId);
    dest.writeString(this.cpiUrl);
    dest.writeString(this.name);
    dest.writeString(this.iconPath);
    dest.writeInt(this.downloads);
    dest.writeInt(this.stars);
    dest.writeLong(this.modified);
    dest.writeInt(this.hasAppc ? 1 : 0);
    dest.writeDouble(this.appcAmount);
    dest.writeDouble(this.currencyAmount);
    dest.writeString(this.currency);
    dest.writeString(this.currencySymbol);
  }
}
