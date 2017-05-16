package cm.aptoide.pt.v8engine.view.account.store;

import android.text.TextUtils;
import org.parceler.Parcel;

@Parcel class CreateStoreModel {
  long storeId;
  String storeName;
  String storeDescription;
  String storeAvatarPath;
  String storeThemeName;
  String storeFrom;
  String storeRemoteUrl;

  public CreateStoreModel() {
  }

  public CreateStoreModel(long storeId, String storeFrom, String storeRemoteUrl,
      String storeThemeName, String storeDescription) {
    this.storeId = storeId;
    this.storeName = "";
    this.storeDescription = storeDescription;
    this.storeAvatarPath = "";
    this.storeThemeName = storeThemeName;
    this.storeFrom = storeFrom;
    this.storeRemoteUrl = storeRemoteUrl;
  }

  public CreateStoreModel(CreateStoreModel otherStoreModel, String storeName,
      String storeDescription) {
    this.storeId = otherStoreModel.getStoreId();
    this.storeName = storeName;
    this.storeDescription = storeDescription;
    this.storeAvatarPath = otherStoreModel.getStoreAvatarPath();
    this.storeThemeName = otherStoreModel.getStoreThemeName();
    this.storeFrom = otherStoreModel.getStoreFrom();
    this.storeRemoteUrl = otherStoreModel.getStoreRemoteUrl();
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public String getStoreDescription() {
    return storeDescription;
  }

  public String getStoreAvatarPath() {
    return storeAvatarPath;
  }

  public void setStoreAvatarPath(String storeAvatarPath) {
    this.storeAvatarPath = storeAvatarPath;
  }

  public long getStoreId() {
    return storeId;
  }

  public String getStoreThemeName() {
    return storeThemeName;
  }

  public void setStoreThemeName(String storeTheme) {
    this.storeThemeName = storeTheme;
  }

  public String getStoreFrom() {
    return storeFrom;
  }

  public String getStoreRemoteUrl() {
    return storeRemoteUrl;
  }

  /**
   * This method sets the required non existing store data as null to prepare this object a
   * network request.
   */
  public void prepareToSendRequest() {
    if (TextUtils.isEmpty(storeName)) {
      storeName = null;
    }

    if (TextUtils.isEmpty(storeThemeName)) {
      storeThemeName = null;
    }

    if (TextUtils.isEmpty(storeDescription)) {
      storeDescription = null;
    }
  }

  public boolean hasStoreDescription() {
    return !TextUtils.isEmpty(getStoreDescription());
  }

  public boolean hasThemeName() {
    return !TextUtils.isEmpty(getStoreThemeName());
  }

  public boolean hasStoreAvatar() {
    return !TextUtils.isEmpty(getStoreAvatarPath());
  }

  public boolean hasStoreName() {
    return !TextUtils.isEmpty(getStoreName());
  }
}
