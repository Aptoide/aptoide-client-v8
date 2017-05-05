/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.products;

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.v8engine.payment.Price;

/**
 * Created by marcelobenites on 8/16/16.
 */
public class PaidAppProduct extends ParcelableProduct {

  public static final Creator<PaidAppProduct> CREATOR = new Creator<PaidAppProduct>() {
    @Override public PaidAppProduct createFromParcel(Parcel in) {
      return new PaidAppProduct(in);
    }

    @Override public PaidAppProduct[] newArray(int size) {
      return new PaidAppProduct[size];
    }
  };
  private final long appId;
  private final String storeName;
  private boolean sponsored;

  public PaidAppProduct(int id, String icon, String title, String description, long appId,
      String storeName, Price price, boolean sponsored) {
    super(id, icon, title, description, price);
    this.appId = appId;
    this.storeName = storeName;
    this.sponsored = sponsored;
  }

  protected PaidAppProduct(Parcel in) {
    super(in);
    appId = in.readLong();
    storeName = in.readString();
    sponsored = in.readByte() != 0;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeLong(appId);
    dest.writeString(storeName);
    dest.writeByte((byte) (sponsored ? 1 : 0));
  }

  public long getAppId() {
    return appId;
  }

  public String getStoreName() {
    return storeName;
  }

  public boolean isSponsored() {
    return sponsored;
  }
}
