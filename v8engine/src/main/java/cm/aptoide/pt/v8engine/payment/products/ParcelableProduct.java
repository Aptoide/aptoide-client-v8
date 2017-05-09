/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.products;

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;

/**
 * Created by marcelobenites on 8/30/16.
 */
public class ParcelableProduct implements Product, Parcelable {

  public static final Creator<ParcelableProduct> CREATOR = new Creator<ParcelableProduct>() {
    @Override public ParcelableProduct createFromParcel(Parcel in) {
      return new ParcelableProduct(in);
    }

    @Override public ParcelableProduct[] newArray(int size) {
      return new ParcelableProduct[size];
    }
  };
  private final int id;
  private final String icon;
  private final String title;
  private final String description;
  private final Price price;

  public ParcelableProduct(int id, String icon, String title, String description, Price price) {
    this.id = id;
    this.icon = icon;
    this.title = title;
    this.description = description;
    this.price = price;
  }

  protected ParcelableProduct(Parcel in) {
    id = in.readInt();
    icon = in.readString();
    title = in.readString();
    description = in.readString();
    price = new Price(in.readDouble(), in.readString(), in.readString(), in.readDouble());
  }

  @Override public int getId() {
    return id;
  }

  @Override public String getIcon() {
    return icon;
  }

  @Override public String getTitle() {
    return title;
  }

  @Override public Price getPrice() {
    return price;
  }

  @Override public String getDescription() {
    return description;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(icon);
    dest.writeString(title);
    dest.writeString(description);
    dest.writeDouble(price.getAmount());
    dest.writeString(price.getCurrency());
    dest.writeString(price.getCurrencySymbol());
    dest.writeDouble(price.getTaxRate());
  }
}
