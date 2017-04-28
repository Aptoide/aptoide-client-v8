/*
 * Copyright (c) 2016.
 * Modified on 21/07/2016.
 */

package cm.aptoide.pt.permissions;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA. User: rmateus Date: 15-07-2013 Time: 15:52 To change this template
 * use File | Settings | File Templates.
 */
public class ApkPermission implements Parcelable {

  public static final Creator<ApkPermission> CREATOR = new Creator<ApkPermission>() {
    @Override public ApkPermission createFromParcel(Parcel in) {
      return new ApkPermission(in);
    }

    @Override public ApkPermission[] newArray(int size) {
      return new ApkPermission[size];
    }
  };
  private String name;
  private String description;

  public ApkPermission(String name, String description) {
    this.name = name;
    this.description = description;
  }

  protected ApkPermission(Parcel in) {
    name = in.readString();
    description = in.readString();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {

    dest.writeString(name);
    dest.writeString(description);
  }
}
