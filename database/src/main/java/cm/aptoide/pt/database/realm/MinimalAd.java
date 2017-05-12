/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.database.realm;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import cm.aptoide.pt.model.MinimalAdInterface;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by neuro on 20-06-2016.
 */
public class MinimalAd extends RealmObject implements Parcelable, MinimalAdInterface {

  public static final Creator<MinimalAd> CREATOR = new Creator<MinimalAd>() {
    @Override public MinimalAd createFromParcel(Parcel source) {
      return new MinimalAd(source);
    }

    @Override public MinimalAd[] newArray(int size) {
      return new MinimalAd[size];
    }
  };

  @Getter private String description;
  @Getter private String packageName;
  @Getter private Long networkId;
  @Getter private String clickUrl;
  @Getter private String cpcUrl;
  @Getter @Setter private String cpdUrl;
  @Getter private Long appId;
  @Getter private Long adId;
  @Getter private String cpiUrl;
  @Getter private String name;
  @Getter private String iconPath;

  public MinimalAd() {
  }

  public MinimalAd(String packageName, long networkId, String clickUrl, String cpcUrl,
      String cpdUrl, long appId, long adId, String cpiUrl, String name, String iconPath,
      String description) {
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
  }

  public static MinimalAd from(@NonNull GetAdsResponse.Ad ad) {
    GetAdsResponse.Partner partner = ad.getPartner();
    int id = 0;
    String clickUrl = null;
    if (partner != null) {
      id = partner.getInfo()
          .getId();
      clickUrl = partner.getData()
          .getClickUrl();
    }
    return new MinimalAd(ad.getData()
        .getPackageName(), id, clickUrl, ad.getInfo()
        .getCpcUrl(), ad.getInfo()
        .getCpdUrl(), ad.getData()
        .getId(), ad.getInfo()
        .getAdId(), ad.getInfo()
        .getCpiUrl(), ad.getData()
        .getName(), ad.getData()
        .getIcon(), ad.getData()
        .getDescription());
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
  }
}
